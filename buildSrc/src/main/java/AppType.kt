enum class AppType {
    TestApp,
    YafitMoveApp,
    ZzalApp;
}

fun AppType.getAppInfo(): AppInfo {
    return when (this) {
        AppType.TestApp -> AppInfo(
            appName = "App-XView-ADCash(product)",
            appId = "6da4ad56527f409d9e3f97700437abc0",
            appSecret = "09ff5525d0e44ed8",
            placementBanner320x50 = "3a59dfbe-da78-4c43-8178-8ad6b4865f24",
//            placementBanner320x50 = "e05a80ec-2dba-4ab2-b93c-b3bfc2e40efe",
            placementBanner320x100 = "1f357fdb-d444-45c7-b291-958268c93871",
            placementBanner300x250 = "569afc91-1ff9-45c7-90d3-4922bbb4f08b",
            placementBannerDynamic = "43617f5a-1c2b-4c7b-a1e7-79f7cbaf62cc",
            placementInterstitial = "cfc84457-0a08-4c53-aed5-7c880d665251",
            placementInterstitialVideo = "79f60e87-32aa-4310-9edb-24d1ab6441d7",
            placementNative = ""
        )
        AppType.YafitMoveApp -> AppInfo(
            appName = "App-XView-ADCash(YafitMove)",
            appId = "d323c8be76884ceba9559f2be2a87171",
            appSecret = "fc20c973133d4a34b3de00b09e546eae982b183d50d641459c6fb7cb7c180183",
            placementBanner320x50 = "",
            placementBanner320x100 = "",
            placementBanner300x250 = "",
            placementBannerDynamic = "",
            placementInterstitial = "",
            placementInterstitialVideo = "8a7ad598-cf53-4526-bc12-f46fb125d62d",
            placementNative = ""
        )
        AppType.ZzalApp -> AppInfo(
            appName = "App-XView-ADCash(ZzalApp)",
            appId = "175664270a40490ea21ad1c3aea66312",
            appSecret = "4c1eeb6189024a6c",
            placementBanner320x50 = "",
            placementBanner320x100 = "",
            placementBanner300x250 = "",
            placementBannerDynamic = "",
            placementInterstitial = "3642e912-d8d3-4d87-8868-f3790ad0da27",
            placementInterstitialVideo = "8a7ad598-cf53-4526-bc12-f46fb125d62d",
            placementNative = ""
        )
    }

}

data class AppInfo(
    val appName: String,
    val appId: String,
    val appSecret: String,
    val placementBanner320x50: String,
    val placementBanner320x100: String,
    val placementBanner300x250: String,
    val placementBannerDynamic: String,
    val placementInterstitial: String,
    val placementInterstitialVideo: String,
    val placementNative: String
)