buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Dependencies.comAndroidToolsBuild_gradle.notation())
        classpath(Dependencies.orgJetbrainsKotlin_kotlinGradlePlugin.notation())
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
