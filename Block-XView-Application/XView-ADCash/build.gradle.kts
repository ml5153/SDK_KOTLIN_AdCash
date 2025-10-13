plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.avatye.sample.adcash"
    compileSdk = Version.Android.compileSdk
    defaultConfig {
        applicationId = "com.avatye.sample.adcash"
        minSdk = Version.Android.minSdk
        targetSdk = Version.Android.targetSdk
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
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
    flavorDimensions += "default"
    productFlavors {
        create("develop") {
            dimension = "default"
            manifestPlaceholders["config_app_host"] = "dev"
            manifestPlaceholders["config_app_icon"] = "ic_launcher_develop"
            manifestPlaceholders["config_app_icon_round"] = "ic_launcher_develop_round"
            manifestPlaceholders["config_doyouad_islive"] = false

            resValue(type = "string", name = "app_name", value = "App-XView-ADCash(develop)")

            resValue(type = "string", name = "appId", value = "c6f70ff9e59b4642842c319e07528edf")
            resValue(type = "string", name = "appSecret", value = "6315c0a058a24519")
            resValue(type = "string", name = "placement_banner_320x50", value = "6a7d7d23-bce5-422a-bbd4-60f6c219c0c2")
            resValue(type = "string", name = "placement_banner_320x100", value = "6c80c2af-8793-474e-bbc7-b1b4c6f7ea66")
            resValue(type = "string", name = "placement_banner_300x250", value = "17fdd332-742f-4f50-88fd-fd6a24438660")
            resValue(type = "string", name = "placement_banner_dynamic", value = "e052939a-0099-40af-baea-2a8fbfcfbf26")

            resValue(type = "string", name = "placement_interstitial", value = "acbed82b-4df0-4f94-8c97-97fdeb828a4a")
            resValue(type = "string", name = "placement_interstitial_video", value = "5588e434-f400-405d-8d4e-55f602a76957")

            resValue(type = "string", name = "placement_native", value = "c61f4f0c-667f-4615-b4b6-e4c9f7398c4a")
        }
        create("qa") {
            dimension = "default"
            manifestPlaceholders["config_app_host"] = "qa"
            manifestPlaceholders["config_app_icon"] = "ic_launcher_test"
            manifestPlaceholders["config_app_icon_round"] = "ic_launcher_test_round"
            manifestPlaceholders["config_doyouad_islive"] =  false

            resValue(type = "string", name = "app_name", value = "App-XView-ADCash(qa)")

            resValue(type = "string", name = "appId", value = "01ab8b688bdb4022877f9e7b189fb58d")
            resValue(type = "string", name = "appSecret", value = "5ecf155055f24006")
            resValue(type = "string", name = "placement_banner_320x50", value = "3e97ed43-11c8-44e4-b29f-f6c2e73fa904")
//            resValue(type = "string", name = "placement_banner_320x50", value = "33bf3d0b-9d59-4286-ad2b-9173de23fdcd")
            resValue(type = "string", name = "placement_banner_320x100", value = "4a514ef9-cbc6-49a7-a57f-094997d64bd5")
            resValue(type = "string", name = "placement_banner_300x250", value = "6ad720d9-836e-4773-8541-c05b126f2112")
            resValue(type = "string", name = "placement_banner_dynamic", value = "93366dc4-f60e-428e-9750-696a9eade488")

            resValue(type = "string", name = "placement_interstitial", value = "2fb53a5c-58c0-485b-aff2-347b4b8dd191")
            resValue(type = "string", name = "placement_interstitial_video", value = "14b6ac48-c860-4ca1-9b74-9e0cb43e7aa1")

            resValue(type = "string", name = "placement_native", value = "cb368f55-897b-4357-af17-2dcdc78b2afe")
        }
        create("product") {
            dimension = "default"
            manifestPlaceholders["config_app_host"] = "live"
            manifestPlaceholders["config_app_icon"] = "ic_launcher_live"
            manifestPlaceholders["config_app_icon_round"] = "ic_launcher_test_round"
            manifestPlaceholders["config_doyouad_islive"] =  true

            val appInfo = AppType.TestApp.getAppInfo()

            resValue(type = "string", name = "app_name", value = appInfo.appName)

            resValue(type = "string", name = "appId", value = appInfo.appId)
            resValue(type = "string", name = "appSecret", value = appInfo.appSecret)

            resValue(type = "string", name = "placement_banner_320x50", value = appInfo.placementBanner320x50)
            resValue(type = "string", name = "placement_banner_320x100", value = appInfo.placementBanner320x100)
            resValue(type = "string", name = "placement_banner_300x250", value = appInfo.placementBanner300x250)
            resValue(type = "string", name = "placement_banner_dynamic", value = appInfo.placementBannerDynamic)

            resValue(type = "string", name = "placement_interstitial", value = appInfo.placementInterstitial)
            resValue(type = "string", name = "placement_interstitial_video", value = appInfo.placementInterstitialVideo)

            resValue(type = "string", name = "placement_native", value = appInfo.placementNative)
        }
    }
}

dependencies {
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    implementation(project(":Block-Product:Product-ADCash"))
    // util
    implementation(project(":Block-Platform-Library:Platform-Library-PrintOut"))
    // archive
    implementation(project(":Block-Mediation-Archive:Archive-Adfit"))
    implementation(project(":Block-Mediation-Archive:Archive-AdMob"))
    implementation(project(":Block-Mediation-Archive:Archive-AppLovin"))
    implementation(project(":Block-Mediation-Archive:Archive-Cauly"))
    implementation(project(":Block-Mediation-Archive:Archive-Facebook-Audience"))
    implementation(project(":Block-Mediation-Archive:Archive-Fyber"))
    //implementation(project(":Block-Mediation-Archive:Archive-Mobon"))
    implementation(project(":Block-Mediation-Archive:Archive-Mobwith"))
    implementation(project(":Block-Mediation-Archive:Archive-NAM"))
    implementation(project(":Block-Mediation-Archive:Archive-Pangle"))
    implementation(project(":Block-Mediation-Archive:Archive-Unity"))
    implementation(project(":Block-Mediation-Archive:Archive-Vungle"))
    implementation("androidx.activity:activity:1.10.1")
}