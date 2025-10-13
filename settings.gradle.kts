pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://artifact.bytedance.com/repository/pangle") }
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
        maven { url = uri("https://dl.cloudsmith.io/public/avatye/android-adcash-internal/maven/") }
        maven { url = uri("https://nexus.wisernd.co.kr/repository/sdk/") }
        maven { url = uri("https://cauly.github.io/cauly-sdk-android-maven/maven-repo") }
    }
}
rootProject.name = "Project.Android.SDK.ADCash"
//include(":Block-Mediation-Archive:Archive-MezzoMedia")
include(":Block-Mediation-Archive:Archive-Mobwith")
include(":Block-Mediation-Archive:Archive-Adfit")
include(":Block-Mediation-Archive:Archive-AdMob")
include(":Block-Mediation-Archive:Archive-AppLovin")
include(":Block-Mediation-Archive:Archive-Cauly")
include(":Block-Mediation-Archive:Archive-Facebook-Audience")
include(":Block-Mediation-Archive:Archive-Fyber")
//include(":Block-Mediation-Archive:Archive-Mobon")
include(":Block-Mediation-Archive:Archive-NAM")
include(":Block-Mediation-Archive:Archive-Pangle")
include(":Block-Mediation-Archive:Archive-Unity")
include(":Block-Mediation-Archive:Archive-Vungle")
// platform
include(":Block-Platform-Library:Platform-Library-Support")
include(":Block-Platform-Library:Platform-Library-PrintOut")
// ssp
include(":Block-Platform-SSP:Platform-SSP-Basement")
include(":Block-Platform-SSP:Platform-SSP-AdHouse")
include(":Block-Platform-SSP:Platform-SSP-AdPopcorn")
//include(":Block-Platform-SSP:Platform-SSP-AdMixer")
include(":Block-Platform-SSP:Platform-SSP-MezzoMedia")
include(":Block-Platform-SSP:Platform-SSP-DoYouAd")
include(":Block-Product:Product-ADCash")
// view
include(":Block-XView-Application:XView-ADCash")
include(":Block-XView-Application:xview-adcash-qa")
