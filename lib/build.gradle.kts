plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    `maven-publish`
}

android {
    compileSdkVersion(Versions.compileSdkVersion)
    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
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
    project.getStringExtra("lib.resourcePrefix")?.let {
        resourcePrefix(it)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        moduleName = "dev.ebnbin.${project.requireStringExtra("lib.id")}"
    }
    buildFeatures {
        viewBinding = project.getStringExtra("lib.viewBinding")?.toBoolean() ?: false
    }
}

afterEvaluate {
    publishing {
        publications {
            val publication = project.getStringExtraOrDefault("lib.publication", "release")
            create<MavenPublication>(publication) {
                from(components[publication])
            }
        }
    }
}

//*********************************************************************************************************************

dependencies {
    debugApi(Dependencies.devEbnbin_ebui.notation(project, "0.0.20"))

    debugImplementation(Dependencies.comSquareupLeakcanary_leakcanaryAndroid.notation())
    debugImplementation(Dependencies.catEreza_customactivityoncrash.notation())
}
