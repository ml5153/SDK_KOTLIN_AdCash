package com.avatye.adcash

import com.avatye.adcash.platform.provider.basement.AdsviserMediationExtra

object BannerMediationExtra {


    object DoYouAd {
        /** 광고 interval 사용여부 */

        /** 시간(초) */
        val useInterval = AdsviserMediationExtra.EXTRA_INTERSTITIAL_ADMIXER_USER_PHONE_NUMBER





    }

    object Cauly {
        /**
         * 광고 노출 주기를 카울리측에서 컨트롤 함
         * 기본값: true
         */
        const val DYNAMIC_RELOAD_INTERVAL = "CAULY_DYNAMIC_RELOAD_INTERVAL"

        /**
         * 광고노출 주기를 매체에서 컨트롤 함
         * CAULY_DAYNAMIC_RELOAD_INTERVAL 를 False 로 변경 후 사용가능
         * 기본값: 20s
         * 설정범위: 10~120
         */
        const val RELOAD_INTERVAL = "CAULY_RELOAD_INTERVAL"

        /**
         * 스레드 우선순위 설정
         * 기본값: 5
         * 설정범위: 1~10
         */
        const val THREAD_PRIORITY = "CAULY_THREAD_PRIORITY"

        /**
         * 카울리 광고 잠금 화면 노출 원할 경우 true 설정
         * 기본값: false
         */
        const val ENABLE_LOCK = "CAULY_ENABLE_LOCK"
    }

    object Mopub {
        /**
         * Mopub 광고 자동 갱신 기능 사용 여부
         * 기본값: true
         */
        const val AUTO_REFRESH_ENABLED = "MOPUB_AUTO_REFRESH_ENABLED"
    }

    object Mezzo {
        /**
         * 앱의 Store URL
         */
        const val STORE_URL = "MEZZO_STORE_URL"

        /**
         * 백그라운드에서 배너가 동작할 수 있게 체크 하는 옵션
         */
        const val IS_USED_BACKGROUND_CHECK = "MEZZO_IS_USED_BACKGROUND_CHECK"

        /**
         * 메조 연령 정보를 세팅하는 옵션
         * 기본값: -1
         * -1: 알수없음
         * 0: 13세미만
         * 1: 13세이상
         */
        const val AGE_LEVEL = "MEZZO_AGE_LEVEL"
    }
}