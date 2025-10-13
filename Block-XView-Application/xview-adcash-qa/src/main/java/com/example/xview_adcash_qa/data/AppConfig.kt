package com.example.xview_adcash_qa.data

import com.google.gson.annotations.SerializedName

enum class OsType {
    ANDROID,
    IOS
}

enum class AppBuildEnvironment {
    DEVELOPMENT,
    PRODUCTION
}

enum class AccountType {
    GUEST,
    CHANNELING
}
data class Application(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("environment")
    val environment: AppBuildEnvironment,

    @SerializedName("accountType")
    val accountType: AccountType,

    @SerializedName("osType")
    val osType: OsType,

    @SerializedName("appId")
    val appId: String,

    @SerializedName("appSecret")
    val appSecret: String
)