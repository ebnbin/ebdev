import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    `maven-publish`
}

inline fun <reified T> rootProjectExtra(key: String): T {
    return rootProject.extra.properties.getValue(key) as T
}

inline fun <reified T> projectExtra(key: String): T {
    return project.extra.properties.getValue(key) as T
}

fun version(key: String): String {
    return rootProjectExtra<Map<String, String>>("versionMap").getValue(key)
}

fun dependency(id: String): String {
    return "$id:${rootProjectExtra<Map<String, String>>("dependencyMap").getValue(id)}"
}

fun devDependency(id: String): Any {
    return if (gradleLocalProperties(rootDir)["devEnabled"] == "true") {
        project(":$id")
    } else {
        "com.github.ebnbin:$id:${rootProjectExtra<String>("dev.$id")}"
    }
}

android {
    compileSdkVersion(version("compileSdkVersion").toInt())
    defaultConfig {
        minSdkVersion(version("minSdkVersion").toInt())
        targetSdkVersion(version("targetSdkVersion").toInt())
        val proguardFiles = project.file("proguard").listFiles() ?: emptyArray()
        consumerProguardFiles(*proguardFiles)
    }
    sourceSets {
        configureEach {
            val srcDirs = project.file("src/$name")
                .listFiles { file -> file.isDirectory && file.name.startsWith("res-") }
                ?: emptyArray()
            res.srcDirs(*srcDirs)
        }
    }
    resourcePrefix(projectExtra("resourcePrefix"))
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        moduleName = "dev.ebnbin.${projectExtra<String>("libId")}"
    }
    buildFeatures {
        viewBinding = projectExtra<String>("viewBinding").toBoolean()
        dataBinding = projectExtra<String>("dataBinding").toBoolean()
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
            }
        }
    }
}

//*********************************************************************************************************************

dependencies {
    api(devDependency("ebui"))
}
