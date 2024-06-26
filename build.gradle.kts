// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
    }
}
plugins {
    id("com.android.application") version "8.2.0-rc01" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false

    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false


    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"

}