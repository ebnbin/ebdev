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
    (project.extraProperties()["resourcePrefix"] as String?)?.let {
        resourcePrefix(it)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        moduleName = "dev.ebnbin.${project.extraProperties().getValue("libId") as String}"
    }
    buildFeatures {
        viewBinding = (project.extraProperties().getOrDefault("viewBinding", "false") as String).toBoolean()
        dataBinding = (project.extraProperties().getOrDefault("dataBinding", "false") as String).toBoolean()
    }
}

afterEvaluate {
    publishing {
        publications {
            val publish = project.extraProperties().getOrDefault("publish", "release") as String
            create<MavenPublication>(publish) {
                from(components[publish])
            }
        }
    }
}

//*********************************************************************************************************************

dependencies {
    debugApi(Dependencies.comGithubEbnbin_ebui.devNotation(project))

    debugImplementation(Dependencies.comSquareupLeakcanary_leakcanaryAndroid.notation())
    debugImplementation(Dependencies.catEreza_customactivityoncrash.notation())
}
