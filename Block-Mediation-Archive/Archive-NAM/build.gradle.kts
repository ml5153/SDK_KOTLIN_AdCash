plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}
android {
    namespace = "com.avatye.adcash.mediation.archive.nam"
    compileSdk = Version.Android.compileSdk
    defaultConfig {
        minSdk = Version.Android.minSdk
        targetSdk = Version.Android.targetSdk
        consumerProguardFiles("consumer-rules.pro")
        // buildConfigField
        buildConfigField(type = "int", name = "X_BUILD_SDK_VERSION_CODE", value = "${Version.versionCode}")
        buildConfigField(type = "String", name = "X_BUILD_SDK_VERSION_NAME", value = "\"${Version.versionName}\"")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
    lint {
        abortOnError = false
    }
}
dependencies {
    implementation(project(":Block-Platform-Library:Platform-Library-PrintOut"))
    api(platform("com.naver.gfpsdk:nam-bom:${Version.Advertisement.nam}"))
    api("com.naver.gfpsdk:nam-core")
    api("com.naver.gfpsdk.mediation:nam-nda")
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                Publish.printLog()
                name = Publish.name
                url = Publish.url
                credentials {
                    username = Publish.Credentials.username
                    password = Publish.Credentials.password
                }
            }
        }
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = Publish.groupId
                artifactId = Publish.ArtifactIds.Mediation.archiveNam
                version = Version.versionName
            }
        }
    }
}