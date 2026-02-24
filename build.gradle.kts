/*
* Copyright 2024 Joel Kanyi.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import java.net.URI
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlin.compatibility) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.nmcp.aggregation)
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            licenseHeaderFile(
                rootProject.file("${project.rootDir}/spotless/copyright.kt"),
                "^(package|object|import|interface)",
            )
            trimTrailingWhitespace()
            endWithNewline()

            ktlint(
                "1.8.0"
            ).customRuleSets(
                listOf(
                    "io.nlopez.compose.rules:ktlint:0.5.6",
                ),
            ).editorConfigOverride(
                mapOf(
                    "ktlint_standard_no-wildcard-imports" to "disabled",
                ),
            )
        }
        format("kts") {
            target("**/*.kts")
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\*).*$)")
        }
        format("misc") {
            target("**/*.md", "**/.gitignore")
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
        }
    }
}

dokka {
    moduleName.set("Sain")

    dokkaPublications.html {
        outputDirectory.set(rootDir.resolve("docs/kdoc"))
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
    }

    dokkaSourceSets.configureEach {
        includes.from("README.md")
        documentedVisibilities.set(setOf(VisibilityModifier.Public))

        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl.set(URI("https://github.com/joelkanyi/sain"))
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLinks.register("kotlin") {
            url.set(URI("https://kotlinlang.org/api/latest/jvm/stdlib/"))
        }
    }
}

dependencies {
    dokka(project(":sain"))
}

nmcpAggregation {
    centralPortal {
        username = System.getenv("MAVEN_CENTRAL_USERNAME")
        password = System.getenv("MAVEN_CENTRAL_PASSWORD")
        publishingType = "AUTOMATIC"
    }

    publishAllProjectsProbablyBreakingProjectIsolation()
}

dependencies {
    nmcpAggregation(project(":sain"))
}

