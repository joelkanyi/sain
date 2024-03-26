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

import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compatibility)
    alias(libs.plugins.dokka)
    alias(libs.plugins.nmcp)
    alias(libs.plugins.gradleMavenPublish)
}

kotlin {
    applyDefaultHierarchyTemplate()

    explicitApi() // https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release")
    }

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.joelkanyi.sain"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

group = "io.github.joelkanyi"
version = "2.0.0-RC2"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    pom {
        name.set("Sain")
        description.set("Compose Multiplatform Signature Library")
        url.set("https://github.com/joelkanyi/sain")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/joelkanyi/sain/issues")
        }

        developers {
            developer {
                id.set("joelkanyi")
                name.set("Joel Kanyi")
                email.set("joelkanyi98@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com:joelkanyi/sain.git")
            developerConnection.set("scm:git:ssh://github.com:joelkanyi/sain.git")
            url.set("https://github.com/joelkanyi/sain")
        }
    }
}

nmcp {
    publishAllPublications {
        // get from ~/.gradle/gradle.properties (HOME/.gradle/gradle.properties)
        username = System.getenv("MAVEN_CENTRAL_USERNAME")
        password = System.getenv("MAVEN_CENTRAL_PASSWORD")
        publicationType = "AUTOMATIC"
    }
}
