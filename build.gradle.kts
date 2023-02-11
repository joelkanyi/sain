buildscript {
    val compose_version by extra("1.2.0")
}

plugins {
    id("com.android.application") version ("7.4.0") apply false
    id("com.android.library") version ("7.4.0") apply false
    id("org.jetbrains.kotlin.android") version ("1.7.0") apply false
}