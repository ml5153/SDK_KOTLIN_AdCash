import java.net.URI

object Version {

    const val SNAPSHOT = false
    const val versionCode = 25
    val versionName = "3.0.0.$versionCode${getVersionNameSuffix()}"

    private fun getVersionNameSuffix(): String {
        return if (SNAPSHOT) {
            "_SNAPSHOT"
        } else {
            ""
        }
    }

    object Android {
        const val kotlin = "1.9.25"
        const val library = "8.8.0"
        const val application = "8.8.0"
        const val compileSdk = 35
        const val targetSdk = 35
        const val minSdk = 21
    }

    object Core {
        const val ktx = "1.8.0"
        const val appCompat = "1.4.2"
        const val coroutines = "1.7.1"
        const val material = "1.6.1"
        const val workRuntime = "2.7.1"
        const val playServiceBasement = "18.1.0"
        const val playServiceAdsIdentifier = "18.0.1"
        const val glide = "4.16.0"
        const val jodaTime = "2.12.5"
    }

    object Advertisement {
        const val adpopcorn = "3.8.6"

        //Mediation
        const val nam = "8.6.1"
        const val adfit = "3.19.5"
        const val applovin = "13.3.1"
        const val unityads = "4.16.0"
        const val vungle = "7.5.0"
        const val facebook = "6.20.0"
        const val admob = "23.3.0"
        const val fyber = "8.3.7"
        const val cauly = "3.5.39"
        //const val mobon = "1.0.0.65"
        const val mobwith  = "1.0.58"
        const val pangle = "7.3.0.4"

        //SSP
        const val mezzomedia = "3.1.1"
        //const val admixer = "3.0.11"
        const val doyouad = "1.0.35"

    }

}

object Publish {

    const val groupId = "com.avatye.adcash"
    const val name = "cloudsmith"
    const val releasesRepoUrl = "https://maven.cloudsmith.io/avatye/android-adcash/"
    const val internalRepoUrl = "https://maven.cloudsmith.io/avatye/android-adcash-internal/"

    val url = if (Version.versionName.endsWith("SNAPSHOT")) {
        URI(internalRepoUrl)
    } else {
        URI(releasesRepoUrl)
    }

    object ArtifactIds {
        const val productAdcash = "product-adcash"

        object Platform {
            const val sspBasement = "platform-ssp-basement"
            const val sspAdpopcorn = "platform-ssp-adpopcorn"
            //const val sspAdmixer = "platform-ssp-admixer"
            const val sspAdhouse = "platform-ssp-adhouse"
            const val sspMezzoMedia = "platform-ssp-mezzomedia"
            const val sspDoYouAd = "platform-ssp-doyouad"

            const val librarySupport = "platform-library-support"
            const val libraryPrintout = "platform-library-printout"
        }

        object Mediation {
            const val archiveAdfit = "archive-adfit"
            const val archiveAdMob = "archive-admob"
            const val archiveAppLovin = "archive-applovin"
            const val archiveCauly = "archive-cauly"
            const val archiveFacebookAudience = "archive-facebook-audience"
            const val archiveFyber = "archive-fyber"
            //const val archiveMobon = "archive-mobon"
            const val archiveMobwith = "archive-mobwith"
            //const val archiveMezzoMedia = "archive-mezzomedia"
            const val archiveNam = "archive-nam"
            const val archivePangle = "archive-pangle"
            const val archiveUnity = "archive-unity"
            const val archiveVungle = "archive-vungle"
        }

    }

    object Credentials {
        const val username = "developer-avatye"
        const val password = "2dfcd313f462bd7f4ca93a9ef5fd5b668de08295"
    }

    fun printLog() {
        println("name : $name")
        println("url : $url")
        println("Credentials.username : ${Credentials.username}")
        println("Credentials.password : ${Credentials.password}")
        println("Version.versionName : ${Version.versionName}")
    }

}