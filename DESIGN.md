# Sain API redesign

Status: proposed. Target: `2.3.0` (additive, non-breaking). Removal of the old
surface is deferred to a future `3.0.0`.

This document is written before any code. It defines the new public surface, the
state contract, the rendering model, and the compatibility strategy, and traces
each decision to its source.

## 1. Motivation

The current `Sain` composable works but has three structural problems, confirmed
by two independent senior reviews and, earlier, by a real user in the field.

### 1.1 Field evidence (the decision anchor)

Issue [#72](https://github.com/joelkanyi/sain/issues/72), March 2025. A user
(`syru88`) upgraded `2.0.6` to `2.0.7` and could no longer size the signature pad
to the available space. Their words:

> "The bigger issue for me is the `signatureHeight` parameter. From the calling
> site, I'd need to wait for the available size using `onGloballyPositioned`
> before calling the Sain function, and that doesn't feel like best practice."

This is a Compose sizing-guideline violation. A component must let the caller's
`Modifier` drive its size. The `2.0.7` change replaced caller-controlled sizing
(weight, `fillMaxSize`) with an internal `signatureHeight` plus a forced
`fillMaxWidth()`. The pad stopped obeying the modifier.

The fix shipped in `2.0.8` deprecated the flexible overload rather than the rigid
one. The deprecated overload we ship today is in fact the more correct one on
sizing (it applies the caller's `modifier` directly to the pad `Box`), which is
why it carries the `modifier-not-used-at-root` ktlint suppression.

### 1.2 Design problems (both reviews agreed)

1. Retrieving the signature is inverted control. The caller renders buttons
   inside an `actions` slot, invokes a library callback with
   `SignatureAction.COMPLETE`, and the library calls back via `onComplete` with a
   bitmap the caller's own hoisted `state` already holds. Three mechanisms for
   one job.
2. The bitmap is re-rendered on every draw pass inside `drawWithContent`, and
   snapshot state is written from the draw phase while that same state is read in
   composition. A full-size `ImageBitmap` is allocated per frame.
3. The stroke width ignores density (`Density(1f, 1f)` is hardcoded) and is then
   silently tripled to compensate, documented as a feature.

Plus: 17 flat parameters, a `FloatArray` in the public API, a public
`SignatureState.updateSignature` that lets any caller inject an arbitrary bitmap,
and `pointerInput(true)` that never rebinds when `state` changes.

## 2. Goals and non-goals

Goals:

- Sizing is driven by the caller's `Modifier`. Fixes #72.
- One way to get the signature out: read it from the hoisted state.
- No bitmap allocation or snapshot write in the draw phase.
- Honest, density-correct stroke width.
- A small, grouped, misuse-resistant parameter surface.
- Ship additively. No existing user's code breaks on upgrade.

Non-goals for `2.3.0`:

- Removing the old API (deferred to `3.0.0`).
- Changing the stroke data model from segments to strokes (see §9).
- Pressure or velocity capture (future).

## 3. Requirements, traced to source

| # | Requirement | Source |
| - | ----------- | ------ |
| R1 | The pad fills the caller's `Modifier` constraints; no internal size. | #72 (`syru88`) |
| R2 | Border and pad customization is preserved. | #72 (`syru88` valued it) |
| R3 | The caller retrieves the signature from the hoisted state, not a callback. | Compose component API guidelines; Haze/`ScrollState` shape |
| R4 | No per-frame bitmap; no state write in draw. | Compose draw-phase guidance |
| R5 | Stroke width is density-correct, no hidden multiplier. | Doctrine section 5 (no hidden behavior) |
| R6 | No unstable types (`FloatArray`) in the public API. | KB kotlin-public-api-abi |
| R7 | State mutators that are internal mechanics are not public. | Doctrine section 5 (hard to misuse) |
| R8 | Upgrading to `2.3.0` compiles unchanged and renders identically. | Backward-compat policy (this doc, section 10) |
| R9 | The caller places the clear and confirm controls anywhere (top, bottom, left, right, overlay). | User request (button placement) |

## 4. The new entry point

```kotlin
@Composable
public fun Sain(
    state: SignatureState,
    modifier: Modifier = Modifier,
    colors: SainColors = SainDefaults.colors(),
    thickness: Dp = SainDefaults.Thickness,
    shape: Shape = SainDefaults.PadShape,
    border: BorderStroke? = SainDefaults.Border,
    guideline: GuidelineStyle? = SainDefaults.guideline(),
    hint: SignatureHint? = SainDefaults.hint(),
)
```

Parameter order follows the Compose component guidelines: the required contract
param (`state`) first, then the single `modifier`, then optional params grouped
by concern (pad appearance, then optional features).

Sizing (R1): the pad occupies the constraints handed in through `modifier`. The
composable applies `modifier` to its single root element and does not call
`fillMaxWidth()` or fix a height. A caller sizes it however they like:

```kotlin
Sain(state, Modifier.fillMaxWidth().height(250.dp))   // fixed height
Sain(state, Modifier.weight(1f))                        // fill remaining space (the #72 case)
```

Nullable optional features (R6, guidelines from KB): `border`, `guideline`, and
`hint` are nullable, where `null` means the feature is absent. This removes the
`showGuideline` boolean and its hidden coupling to the hint text. The hint shows
when `hint != null && state.isEmpty`, and only that. The guideline shows when
`guideline != null`, independent of the hint. The old code gated the hint on the
guideline being visible; that coupling is gone.

There is no `onComplete`, no `actions` slot, and no `SignatureAction` in the new
surface. The caller renders their own buttons and talks to `state`.

### 4.1 Usage

```kotlin
val state = rememberSignatureState()

Column {
    Sain(state, Modifier.weight(1f))
    Row {
        Button(onClick = { state.clear() }) { Text("Clear") }
        Button(
            enabled = !state.isEmpty,
            onClick = {
                val signature = state.toImageBitmap()   // null only if empty
                if (signature != null) onSigned(signature)
            },
        ) { Text("Done") }
    }
}
```

The caller controls button placement (top bar, bottom, anywhere), which the old
`actions` slot forced into an internal `Column` below the pad.

### 4.2 Control placement (R9)

Because `Sain` no longer owns the controls, placement is ordinary Compose layout.
The library adds no `buttonPosition` enum or slot; a placement flag would re-import
the very coupling this redesign removes. `Sain` is only the pad. The controls are
the caller's composables, positioned by wrapping the pad in a `Column`, `Row`, or
`Box`.

```kotlin
// Buttons below (the old forced layout, now a choice)
Column {
    Sain(state, Modifier.weight(1f))
    Controls(state)
}

// Buttons above
Column {
    Controls(state)
    Sain(state, Modifier.weight(1f))
}

// Buttons to the side
Row {
    Sain(state, Modifier.weight(1f))
    Controls(state)
}

// Overlaid in a corner
Box {
    Sain(state, Modifier.fillMaxSize())
    Controls(state, Modifier.align(Alignment.BottomEnd))
}
```

`Controls` is whatever the caller writes; it reads `state.isEmpty` and calls
`state.clear()` / `state.toImageBitmap()`. The library ships no buttons, keeping
it free of any Material dependency.

## 5. State contract

```kotlin
@Stable
public class SignatureState {
    /** True while no stroke has been drawn. Cheap; no allocation. */
    public val isEmpty: Boolean

    /** Erases all strokes. */
    public fun clear()

    /**
     * Renders the current strokes to an [ImageBitmap]. Returns null if and only
     * if [isEmpty]. This is the only place a bitmap is produced.
     *
     * Call this from an event handler (a button's onClick), never from
     * composition. Calling it during composition rasterizes on every
     * recomposition, which is the per-frame allocation this design removes.
     */
    public fun toImageBitmap(): ImageBitmap?

    public companion object {
        /**
         * Persists the strokes AND the last render context (size, density,
         * signature color, thickness), so a state restored by [rememberSaveable]
         * can render via [toImageBitmap] before the pad's first layout (the
         * restore-then-immediate-Done case). Restore tolerates the `2.2.x` format
         * (strokes only, no render context); see section 12.
         */
        public val Saver: Saver<SignatureState, Any>
    }
}

@Composable
public fun rememberSignatureState(): SignatureState
```

Internal (R7): the stroke list and the `add(stroke)` used by the gesture handler
are `internal`. Callers cannot inject strokes or overwrite a bitmap.

The state also holds an internal **render context**: the measured pad size, the
density, the signature color, and the thickness. A bitmap is a pure function of
(strokes, render context), and `toImageBitmap()` has no other inputs, so all four
must live in the state, not only on the composable. Both composables report their
render context into the shared state on layout (an invisible internal write). The
old composable reports its `signatureColor` and `signatureThickness`; the new one
reports `colors.signature` and `thickness`.

This report is required, not optional: `SignatureState` is shared across old and
new pads (all overloads accept it), so a half-migrated caller who pairs an OLD
pad with the new `state.toImageBitmap()` must still get a real, styled bitmap.
Without it, that combination returns null forever, or renders uncolored. See
section 10.2.

`toImageBitmap()` (R4) renders on demand. The pad no longer stores a bitmap and
no longer renders during the draw phase. `isEmpty` answers the common query
without the `signatureLines.toList()` defensive copy the old holder paid on every
read.

## 6. Value types

Each is an `@Immutable` class with an `internal` constructor and a factory on
`SainDefaults`. Internal constructors let us add fields later without breaking
the ABI (the OkHttp value-type convention, and Kepler's).

```kotlin
@Immutable
public class SainColors internal constructor(
    public val pad: Color,
    public val signature: Color,
)

@Immutable
public class GuidelineStyle internal constructor(
    public val color: Color,
    public val strokeWidth: Dp,
    public val dash: Dp,
    public val gap: Dp,
    public val padding: Dp,
    public val cornerRadius: Dp,
)

@Immutable
public class SignatureHint internal constructor(
    public val text: String,
    public val textStyle: TextStyle,
)

public object SainDefaults {
    public val Thickness: Dp = 3.dp
    public val PadShape: Shape = RoundedCornerShape(8.dp)
    public val Border: BorderStroke = BorderStroke(0.5.dp, Color.Black)

    public fun colors(
        pad: Color = Color.White,
        signature: Color = Color.Black,
    ): SainColors

    public fun guideline(
        color: Color = Color.Gray,
        strokeWidth: Dp = 1.dp,
        dash: Dp = 6.dp,
        gap: Dp = 6.dp,
        padding: Dp = 16.dp,
        cornerRadius: Dp = 8.dp,
    ): GuidelineStyle

    public fun hint(
        text: String = "Sign within this area",
        textStyle: TextStyle = TextStyle(
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        ),
    ): SignatureHint
}
```

The dash pattern is two `Dp` values (`dash`, `gap`) instead of a `FloatArray`
(R6). This is stable, typed, and covers the real use case; it is converted to the
px `PathEffect` internally. It loses multi-interval patterns (dot-dash), which a
signature guideline does not need. If one is ever requested, the `internal`
constructor plus the `SainDefaults.guideline()` factory let us add an overload
without an ABI break.

Hint color has a single owner: `SignatureHint.textStyle.color`. `SainColors`
deliberately holds only `pad` and `signature`, so there is no second place to set
the hint color and no conflict to resolve.

The hint default string stays English but is now overridable and decoupled from
the guideline. A caller localizes by passing `SainDefaults.hint(text = ...)`, or
disables it with `hint = null`.

## 7. Rendering model

Old: `drawWithContent` re-rasterized all strokes into a new full-size
`ImageBitmap` every frame, wrote it to snapshot state, then a separate `Image`
composable drew that bitmap. The state write in the draw phase, read in
composition, is a self-feeding invalidation.

New:

1. The pad draws strokes directly in the draw phase with `drawLine` (round caps),
   reading state, never writing it. No bitmap, no allocation on the hot path.
2. The gesture handler appends segments to the state (`pointerInput(state)`, R:
   rebinds when the state instance changes).
3. A bitmap is produced only when the caller calls `state.toImageBitmap()`, using
   the real pad density and size (R5: no `Density(1f,1f)`, no `* 3`).

```
Gesture --> state.add(segment) --> draw phase reads strokes --> draws lines
                                          |
                caller taps Done --> state.toImageBitmap() (one render, on demand)
```

## 8. Sizing contract and acceptance test

R1 is the headline fix and gets an explicit test.

- Given `Sain(state, Modifier.weight(1f))` inside a `Column`, the pad fills the
  remaining height. (The exact #72 scenario.)
- Given `Sain(state, Modifier.size(w, h))`, the pad measures to `w` by `h`.
- The composable never overrides incoming size constraints.

## 9. Explicitly deferred

- `SignatureLine` stays a `data class` for `2.3.0` (the deprecated overloads and
  the old `signatureLines` still expose it). The new surface never exposes it, so
  `3.0.0` deletes it rather than reworking it into an `@Immutable` class. Removing
  it is binary-breaking, hence deferred to the major. `rememberSaveable`
  serialization is unchanged.
- Segment model over stroke (`Path`-per-pointer-down) model. Direct segment
  drawing with round caps is adequate now. A stroke model enables bezier
  smoothing later and belongs to its own design.
- `SignatureAction` remains (the deprecated overloads use it). It is deprecated
  and removed in `3.0.0`.

## 10. Backward compatibility and migration

Policy: additive in a minor, removal in a major. Introducing the new API and
deprecating the old is non-breaking, so it ships in `2.3.0`, not `3.0.0`. Nothing
is removed and no existing signature changes.

Guarantee (R8): a user who bumps to `2.3.0` compiles unchanged and their
signatures render identically. The old `Sain` overloads keep their exact current
rendering, including the `Density(1f,1f)` and `* 3` behavior, so upgrading does
not shift a single pixel. The corrected rendering applies only to the new path,
opted into by moving to the new composable.

The implementation seam this rests on, stated so it is not left to instinct: the
old **rendering** is frozen. The `ImageBitmapUtils.toImageBitmap` helper, every
call into it, the draw-phase rasterize, and the `Density(1f,1f)` and `* 3`
behavior stay exactly as they are. The new renderer is NEW internal code inside
`SignatureState`, not a modified shared helper. The natural instinct (add a
density parameter to the existing helper and call it from both paths) would break
R8 silently and is prohibited.

What is NOT frozen is the rest of the old composable bodies: they gain the
additive, non-rendering render-context write (section 5) and the `2.2.1`
`pointerInput(state)` key fix. These do not alter a single rendered pixel of the
old path, which is what R8 protects. The arbiter of R8 is the golden-bitmap test
(section 12), not a literal byte diff of the source.

Deprecations added in `2.3.0`, all at `DeprecationLevel.WARNING`:

- Both old `Sain` overloads.
- `SignatureState.updateSignature`, `addSignatureLine`, `clearSignatureLines`
  (replaced by internal `add` and public `clear`), `signatureLines`, `signature`.
  `clearSignatureLines` gets `ReplaceWith("clear()")`.

`ReplaceWith` on the old `Sain` is limited: the state-hoisting change is
structural (the caller moves buttons out of the `actions` slot and reads
`state.toImageBitmap()`), which a mechanical `ReplaceWith` cannot express. The
existing broken `ReplaceWith` (it references `MaterialTheme.colorScheme`, absent
from this library) is removed. `MIGRATION.md` carries the before/after.

### 10.1 Overload disambiguation

The new `Sain` coexists with the two old overloads without ambiguity:

- New `Sain` requires `state: SignatureState` first and has no trailing lambda.
- Both old overloads require `onComplete: (ImageBitmap?) -> Unit` and a trailing
  `actions` lambda, neither with a default.

So `Sain(state)` resolves only to the new one (the old ones cannot be called
without `onComplete` and `actions`), and `Sain(onComplete = { ... }) { ... }`
resolves only to an old one. The first positional parameter differs in type
(`SignatureState` vs a lambda), so positional calls are unambiguous too. A caller
reaching for old muscle memory, `Sain(state) { ... }`, fails to compile (the
trailing lambda cannot bind to the new overload's non-function last parameter,
and `state` cannot bind to the old overloads' function first parameter). A hard
error, not a silent wrong resolution, is the correct failure mode.

Two caveats, neither of which breaks the claim:

- The two OLD overloads are already mutually ambiguous for a defaults-only call
  such as `Sain(onComplete = {}) { }` (both are applicable via defaults with
  identical types in every shared position). This is pre-existing in `2.2.x`, not
  introduced here, and the new overload does not enter that resolution.
- `DeprecationLevel.WARNING` does not remove a candidate from overload
  resolution; only `HIDDEN` does. Disambiguation therefore rests entirely on the
  signature shapes above, never on deprecation.

### 10.2 Cross-path behavior (shared `SignatureState`)

Every overload accepts a `SignatureState`, so old and new pads can be mixed
during migration. The behavior of each state member is defined for both pads:

| Member | New-path pad | Old-path pad |
| ------ | ----------- | ------------ |
| `isEmpty` | true until first stroke | same |
| `clear()` | clears strokes | clears strokes |
| `toImageBitmap()` | renders strokes with the new renderer and the reported render context | same renderer and context; the old pad reports its `signatureColor` / `signatureThickness` (see note) |
| `signature` (deprecated) | always null; the new path never writes it | last per-frame render (old behavior), unchanged |

The load-bearing case is `toImageBitmap()` on an old-path pad: it works because
both composables report their full render context (size, density, color,
thickness) into the state. If they did not, that combination would return null
forever, or render uncolored, in the exact intermediate state a migration
produces. This is a hard requirement, tested in section 12.

Note (WYSIWYG in the straddle): on an old-path pad the screen shows the old
`Density(1f,1f)` + `* 3` rendering, but `toImageBitmap()` uses the new, corrected
renderer. The captured bitmap therefore does not pixel-match what the user watched
themselves sign, and `onComplete` (old capture) and `toImageBitmap()` (new
capture) on the same state yield two visually different bitmaps. This mismatch is
inherent to mixing an old pad with new capture and is resolved by finishing the
migration (move to the new `Sain`). We deliberately do NOT make `toImageBitmap()`
emulate the old rendering on old pads; that would re-import the density bug behind
a heuristic. `toImageBitmap()` is one renderer, always the correct one.

`toImageBitmap()` does not mutate state: it is a pure read that returns a bitmap
and never writes the deprecated `signature` property. That is why `signature` on a
new-path pad is always null.

## 11. Rollout

1. `2.2.1` (patch, invisible fixes only): `pointerInput(state)` key fix, `Saver`
   restore hardening. No API change, no pixel change. The density and draw-loop
   fixes are not slipped in here because they alter rendered output; they belong
   to the new path in `2.3.0`.
2. `2.3.0` (minor, additive): the new `Sain`, `SignatureState.clear` /
   `isEmpty` / `toImageBitmap`, the value types and `SainDefaults`, the corrected
   rendering on the new path, the deprecations above, `MIGRATION.md`, README
   before/after. Old API fully working at `WARNING`.
3. `3.0.0` (major, far off, burden-driven): remove the deprecated overloads,
   internalize the state mutators, drop `SignatureAction`, and delete
   `SignatureLine` from the public API. The new surface never exposes
   `SignatureLine` (`add` is internal, `signatureLines` is deprecated), so `3.0.0`
   removes it rather than reworking it into an `@Immutable` class. The Saver keeps
   the segment wire format internally.

## 12. Validation

- Unit: `SignatureState` add / clear / `isEmpty` / `toImageBitmap` (null if and
  only if empty, non-null after a stroke), `Saver` round-trip including the
  persisted render context (size, density, color, thickness), and
  restore-then-immediate `toImageBitmap` (strokes restored, pad not yet laid out,
  still renders styled).
- Saver back-compat: restoring a `2.2.x` bundle (strokes only, no render context)
  must not crash; the missing context falls back to defaults until first layout.
  This is an explicit test case, since `SignatureState.Saver` is public API.
- Cross-path (10.2): an OLD-path pad plus `state.toImageBitmap()` returns a real,
  colored bitmap, not null and not uncolored. This is the migration-straddle guard.
- R8 golden: a set of strokes rendered by the OLD path is compared against a
  committed golden PNG asset in the repo. The asset is captured from `2.2.x` and
  the test fails if the old path's output drifts. Without a committed asset the
  test cannot compare across versions, so R8 stays an unverified claim.
- Behavior: the §8 sizing cases.
- ABI: `apiCheck` green; the `2.3.0` diff is additions only, no removals or
  changes to existing entries.
- Manual: a stroke drawn on the new path renders at the requested `thickness` on
  a high-density device (no more device-dependent width).

## 13. Resolved decisions

1. `toImageBitmap()` is synchronous. A few hundred `drawLine` calls is
   sub-millisecond; `suspend` would be ceremony forcing every caller into a
   coroutine scope. The KDoc (section 5) forbids calling it from composition,
   which is the only way the cost would matter. It becomes `suspend` only if a
   real large-pad case is measured to jank a frame.
2. Hint color lives only on `SignatureHint.textStyle.color`. `SainColors` holds
   `pad` and `signature` only. This removes the two-sources-of-truth conflict
   (resolved in section 6).

## 14. Implementation note

Internal library code (the old composables) calls the newly-deprecated state
members (`updateSignature`, `signature`), so those files carry
`@Suppress("DEPRECATION")`. This is expected for a deprecate-in-place release and
is not a smell.
