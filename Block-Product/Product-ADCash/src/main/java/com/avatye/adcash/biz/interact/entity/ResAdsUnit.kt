package com.avatye.adcash.biz.interact.entity

import com.avatye.adcash.ADCashSettings.DEFAULT_LOADER_TIMEOUT
import com.avatye.adcash.biz.entity.app.AdsNetworkData
import com.avatye.adcash.biz.entity.app.AdsNetworkName
import com.avatye.adcash.biz.entity.app.AdsPlacementUnit
import com.avatye.adcash.biz.entity.app.AdsUnitData
import com.avatye.adcash.biz.entity.app.AdsUnitHouseData
import com.avatye.adcash.biz.entity.app.AdsUnitPlacementData
import com.avatye.adcash.platform.library.network.capacitor.CapacitorResponseFactory
import com.avatye.adcash.platform.library.extension.produce
import com.avatye.adcash.platform.library.extension.toBooleanValue
import com.avatye.adcash.platform.library.extension.toJSONArrayValue
import com.avatye.adcash.platform.library.extension.toLongValue
import com.avatye.adcash.platform.library.extension.toStringValue
import com.avatye.adcash.platform.library.extension.until
import org.json.JSONArray
import org.json.JSONObject

class ResAdsUnit : CapacitorResponseFactory() {

    var result = AdsUnitData()
        private set

    override fun mapper(responseValue: String) {
        JSONObject(responseValue).produce {
            result = AdsUnitData(
                placementId = it.toStringValue("placementID"),
                placementName = it.toStringValue("placementName"),
                placementUnit = it.toStringValue("unitType"),
                timeout = it.toLongValue("timeout", DEFAULT_LOADER_TIMEOUT),
                networks = makeNetworks(it.toJSONArrayValue("networks")),
            )
        }
    }

    private fun makeNetworks(jsonArray: JSONArray?): MutableList<AdsNetworkData> {
        val networks = mutableListOf<AdsNetworkData>()
        jsonArray?.until {
            val networkName = AdsNetworkName.from(it.toStringValue("networkName"))
            val keyValue = it.toStringValue("keyValue")
            val keyName = it.toStringValue("keyName")
            val placement = makePlacements(it.toJSONArrayValue("placements"))
            val isHouseUnit = it.toBooleanValue("isHouseUnit")
            networks.add(
                AdsNetworkData(
                    keyName = keyName,
                    keyValue = keyValue,
                    networkName = networkName,
                    isHouseUnit = isHouseUnit,
                    placements = placement,
                )
            )
        }
        return networks
    }

    private fun makePlacements(jsonArray: JSONArray?): MutableList<AdsUnitPlacementData> {
        val placements = mutableListOf<AdsUnitPlacementData>()
        jsonArray?.until {
            val unitId = AdsPlacementUnit.from(it.toStringValue("unitID"))
            val unitValue = it.toStringValue("unitValue")
            val imageUrl = it.toStringValue("imageUrl")
            val landingUrl = it.toStringValue("landingUrl")
            val isHouseUnit = it.toBooleanValue("isHouseUnit")
            val houseUnit = makeAdUnitHouse(isHouseUnit = isHouseUnit, json = it)
            placements.add(
                AdsUnitPlacementData(
                    unitId = unitId,
                    unitValue = unitValue,
                    imageUrl = imageUrl,
                    landingUrl = landingUrl,
                    houseUnit = houseUnit
                )
            )
        }
        return placements
    }

    private fun makeAdUnitHouse(isHouseUnit: Boolean, json: JSONObject): AdsUnitHouseData? {
        return if (isHouseUnit) {
            return AdsUnitHouseData(
                imageUrl = json.toStringValue("imageUrl"),
                landingUrl = json.toStringValue("landingUrl")
            )
        } else {
            null
        }
    }

}