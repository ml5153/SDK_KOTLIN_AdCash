plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("maven-publish")
}
android {
    namespace = "com.avatye.adcash.platform.provider.adpopcorn"
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
        viewBinding = true
    }
}
dependencies {
    api("com.igaworks.ssp:IgawAdPopcornSSP:${Version.Advertisement.adpopcorn}")
//    implementation(project(":Block-Mediation-Archive:Archive-NAM"))

    implementation(project(":Block-Platform-SSP:Platform-SSP-Basement"))
    implementation(project(":Block-Platform-Library:Platform-Library-Support"))
    implementation(project(":Block-Platform-Library:Platform-Library-PrintOut"))
    implementation("joda-time:joda-time:${Version.Core.jodaTime}")
    implementation("com.google.android.material:material:${Version.Core.material}")
    implementation("com.google.android.gms:play-services-ads-identifier:${Version.Core.playServiceAdsIdentifier}")
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
                artifactId = Publish.ArtifactIds.Platform.sspAdpopcorn
                version = Version.versionName
            }
        }
    }
}