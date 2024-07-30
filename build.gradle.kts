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

            ktlint().customRuleSets(
                listOf(
                    "io.nlopez.compose.rules:ktlint:0.4.8",
                ),
            )
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("$buildDir/**/*.kts")
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\*).*$)")
        }
        format("misc") {
            target("**/*.md", "**/.gitignore")
            trimTrailingWhitespace()
            indentWithTabs()
            endWithNewline()
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>().configureEach {
    outputDirectory.set(file("$rootDir/docs/kdoc"))
}
