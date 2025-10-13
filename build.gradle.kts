plugins {
    id("com.android.application") version Version.Android.application apply false
    id("com.android.library") version Version.Android.library apply false
    id("org.jetbrains.kotlin.android") version Version.Android.kotlin apply false
}

tasks.register("generateReleaseNotes") {
    group = "documentation"
    description = "Generates release notes for the current SDK versions."

    doLast {
        println(
            """
            ### SDK Version Information
            ---
            
            com.igaworks.ssp:IgawAdPopcornSSP: ${Version.Advertisement.adpopcorn}
            com.naver.gfpsdk:nam-bom: ${Version.Advertisement.nam}
            com.kakao.adfit:ads-base: ${Version.Advertisement.adfit}
            com.google.android.gms:play-services-ads: ${Version.Advertisement.admob}
            com.applovin:applovin-sdk: ${Version.Advertisement.applovin}
            com.fsn.cauly:cauly-sdk: ${Version.Advertisement.cauly}
            com.facebook.android:audience-network-sdk: ${Version.Advertisement.facebook}
            com.fyber:marketplace-sdk: ${Version.Advertisement.fyber}
            io.github.mobon:mobwithSDK:${Version.Advertisement.mobwith}
            com.pangle.global:ads-sdk: ${Version.Advertisement.pangle}
            com.unity3d.ads:unity-ads: ${Version.Advertisement.unityads}
            com.vungle:publisher-sdk-android: ${Version.Advertisement.vungle}
            com.wisernd:doyouad: ${Version.Advertisement.doyouad}
            io.github.mezzo-platform:mezzo-sdk:${Version.Advertisement.mezzomedia}
            
            ---
        """.trimIndent()
        )
    }
}