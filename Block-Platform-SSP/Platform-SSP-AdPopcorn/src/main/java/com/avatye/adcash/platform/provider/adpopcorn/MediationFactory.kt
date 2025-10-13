package com.avatye.adcash.platform.provider.adpopcorn

import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect.Mediation.Companion.connectorName
import com.avatye.adcash.platform.provider.adpopcorn.Settings.printout
import com.avatye.adcash.platform.provider.basement.nativeviewbinder.IMediationConnect


internal object MediationFactory {

    private const val SourceName = "MediationConnectFactory"

    fun makeConnector(mediation: IMediationConnect.Mediation): IMediationConnect? {
        var connector: IMediationConnect? = null
        if (hasConnector(mediation = mediation)) {
            try {
                Class.forName(mediation.connectorName).let { clazz ->
                    clazz.getConstructor().newInstance().let {
                        connector = (it as IMediationConnect)
                        printout.info(sourceName = SourceName) {
                            "makeMediationConnector { connectorName:${mediation.name}, success: true }"
                        }
                    }
                }
            } catch (t: Throwable) {
                printout.error(sourceName = SourceName, throwable = t) {
                    "makeMediationConnector -> exception"
                }
                connector = null
            }
        }
        return connector
    }

    fun hasConnector(mediation: IMediationConnect.Mediation): Boolean {
        val success = kotlin.runCatching { Class.forName(mediation.connectorName) }.isSuccess
        printout.info(sourceName = mediation.name) {
            "MediationConnectFactory::hasConnector { connectorName: ${mediation.name}, success: $success }"
        }
        return success
    }
}