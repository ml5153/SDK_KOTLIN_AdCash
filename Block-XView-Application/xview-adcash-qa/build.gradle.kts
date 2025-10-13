plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.xview_adcash_qa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.xview_adcash_qa"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":Block-Product:Product-ADCash"))
    implementation(project(":Block-Mediation-Archive:Archive-Adfit"))
    //implementation(project(":Block-Mediation-Archive:Archive-AdMob"))
    implementation(project(":Block-Mediation-Archive:Archive-AppLovin"))
    implementation(project(":Block-Mediation-Archive:Archive-Cauly"))
    implementation(project(":Block-Mediation-Archive:Archive-Facebook-Audience"))
    implementation(project(":Block-Mediation-Archive:Archive-Fyber"))
    implementation(project(":Block-Mediation-Archive:Archive-Mobwith"))
    implementation(project(":Block-Mediation-Archive:Archive-NAM"))
    implementation(project(":Block-Mediation-Archive:Archive-Pangle"))
    implementation(project(":Block-Mediation-Archive:Archive-Unity"))
    implementation(project(":Block-Mediation-Archive:Archive-Vungle"))
    //implementation(project(":Block-Mediation-Archive:Archive-MezzoMedia"))

    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
}