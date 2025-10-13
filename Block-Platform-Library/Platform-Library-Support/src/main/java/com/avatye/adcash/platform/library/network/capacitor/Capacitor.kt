package com.avatye.adcash.platform.library.network.capacitor

import com.avatye.adcash.platform.library.CoroutineTask
import com.avatye.adcash.platform.library.Settings.printout
import com.avatye.adcash.platform.library.extension.toIntValue
import com.avatye.adcash.platform.library.extension.toStringValue
import com.avatye.adcash.platform.library.extension.toUrlEncode
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLHandshakeException

object Capacitor {

    // region ## enums
    enum class Auth(val value: String) {
        BASIC("basic"),
        BEARER("bearer");
    }

    enum class Method(val value: String) {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE")
    }
    // endregion

    private const val FAILURE_UNKNOWN = "capacitor@failure:unknown"
    private const val FAILURE_HOST_UNKNOWN = "capacitor@failure:host-unknown"
    private const val FAILURE_PARSING = "capacitor@failure:parsing"
    private const val FAILURE_INSPECTION = "capacitor@failure:inspection"
    private const val FAILURE_UNAUTHENTICATED = "capacitor@failure:unauthenticated"
    private const val FAILURE_FORBIDDEN = "capacitor@failure:forbidden"
    private const val FAILURE_SSL_HANDSHAKE = "capacitor@failure:ssl-handshake"
    private const val FAILURE_MALFORMED_URL = "capacitor@failure:malformed-url"
    private const val FAILURE_SOCKET_TIMEOUT = "capacitor@failure:socket-timeout"
    private const val FAILURE_IO = "capacitor@failure:io"
    private const val FAILURE_BODY_EMPTY = "capacitor@failure:body-empty"
    private const val FAILURE_PARSER = "capacitor@failure:json-parse-error"

    // region ## task
    internal class Task<T : CapacitorResponseFactory>(
        private val reqType: Capacitor.Method,
        private val reqUrl: String,
        private val reqHeader: HashMap<String, String>? = null,
        private val reqInfo: HashMap<String, String>? = null,
        private val reqBody: HashMap<String, Any>? = null,
        private val responseClass: Class<T>
    ) {
        private val sourceName = "Capacitor@Task"


//        init {
//            if (!reqUrl.startsWith("https")) {
//                throw Exception("support only https protocol")
//            }
//        }

        private val contentTypeValue: String = "application/json; charset=utf-8"
        private var capacitorCallback: ICapacitorCallback<T>? = null

        private fun headerValue(repHeader: Map<String, List<String>>?, keyName: String): String? {
            return repHeader?.get(keyName)?.get(0)
        }

        private val requestURL: URL = when (reqType) {
            Method.POST, Method.PUT -> {
                URL(reqUrl)
            }

            Method.GET, Method.DELETE -> {
                val query = makeQueryBodyString()
                if (query.isNotEmpty()) {
                    URL("$reqUrl?$query")
                } else {
                    URL(reqUrl)
                }
            }
        }

        private fun makeQueryBodyString(): String {
            var query = ""
            reqBody?.let {
                query = try {
                    val sb = StringBuilder()
                    for ((key, value) in it) {
                        if (sb.isNotEmpty()) {
                            sb.append("&")
                        }
                        sb.append(key.toUrlEncode)
                        sb.append('=')
                        sb.append(value.toString().toUrlEncode)
                    }
                    sb.toString()
                } catch (e: Exception) {
                    ""
                }
            }
            return query
        }

        private fun onHandleResponse(entity: T) = capacitorCallback?.onSuccess(entity)

        private fun onHandleFailure(entity: CapacitorFailure) = capacitorCallback?.onFailure(entity)

        private fun makeJSONBodyString(): String {
            var jsonString = "{}"
            reqBody?.let {
                val jsonObject = JSONObject()
                for ((key, value) in it) {
                    jsonObject.put(key, value)
                }
                jsonString = jsonObject.toString()
            }
            return jsonString
        }

        private fun makeURLConnection(): HttpsURLConnection? {
            return try {
                (requestURL.openConnection() as HttpsURLConnection).apply {
                    useCaches = false
                    connectTimeout = 30000
                    readTimeout = 30000
                    requestMethod = reqType.value
                    reqHeader?.let {
                        for ((key, value) in it.entries) {
                            setRequestProperty(key, value)
                        }
                    }
                    when (reqType) {
                        // post & put
                        Method.POST, Method.PUT -> {
                            setRequestProperty("Content-type", contentTypeValue)
                            doInput = true
                            doOutput = true
                            val bodyParams = makeJSONBodyString()
                            if (bodyParams.isNotEmpty()) {
                                OutputStreamWriter(outputStream).run {
                                    write(bodyParams)
                                    flush()
                                    close()
                                }
                            }
                        }
                        // get & delete
                        Method.GET, Method.DELETE -> {
                            setRequestProperty("Content-type", contentTypeValue)
                            doInput = true
                        }
                    }
                }
            } catch (e: Exception) {
                printout.error(sourceName = sourceName, throwable = e)
                null
            }
        }

        inner class RequestCoroutineTask : CoroutineTask<Void, RequestCoroutineTask.TaskResult>() {

            override fun doInBackground(vararg params: Void): TaskResult {
                makeURLConnection()?.let { connection ->
                    return try {
                        val responseCode = connection.responseCode
                        val bufferedReader = when (responseCode in 200..299) {
                            true -> BufferedReader(InputStreamReader(connection.inputStream))
                            false -> BufferedReader(InputStreamReader(connection.errorStream))
                        }

                        var inputLine: String?
                        val responseBody = StringBuffer()
                        while (bufferedReader.readLine().also { inputLine = it } != null) {
                            responseBody.append(inputLine)
                        }
                        bufferedReader.close()
                        TaskResult().apply {
                            this.responseCode = responseCode
                            this.responseBody = responseBody.toString()
                            responseHeaders = connection.headerFields
                        }
                    } catch (e: SSLHandshakeException) {
                        return TaskResult().apply {
                            failureMessage = "$FAILURE_SSL_HANDSHAKE: ${e.message}"
                        }
                    } catch (e: MalformedURLException) {
                        return TaskResult().apply {
                            failureMessage = "$FAILURE_MALFORMED_URL: ${e.message}"
                        }
                    } catch (e: SocketTimeoutException) {
                        return TaskResult().apply {
                            failureMessage = "$FAILURE_SOCKET_TIMEOUT: ${e.message}"
                        }
                    } catch (e: IOException) {
                        return TaskResult().apply {
                            responseCode = 2000
                            failureMessage = "$FAILURE_IO: ${e.message}"
                        }
                    } catch (e: Exception) {
                        return TaskResult().apply {
                            failureMessage = "$FAILURE_UNKNOWN: ${e.message}"
                        }
                    } finally {
                        connection.disconnect()
                    }
                } ?: run {
                    return TaskResult().apply {
                        failureMessage = "$FAILURE_UNKNOWN: connection is null"
                    }
                }
            }

            override fun onPostExecute(result: TaskResult) {
                when (result.responseCode) {
                    in 200..299 -> processSuccess(task = result)
                    401 -> processUnAuthenticated(task = result)
                    403 -> processForbidden(task = result)
                    503, 504 -> processInspection(task = result)
                    else -> processFailure(task = result)
                }
            }

            private fun processSuccess(task: TaskResult) {
                val responseEntity = responseClass.getConstructor().newInstance()
                val isSuccess = try {
                    responseEntity.of(responseValue = task.responseBody)
                    true
                } catch (e: Exception) {
                    logException(task.responseCode, task.responseBody, e)
                    false
                }
                if (isSuccess) {
                    logSuccess(task.responseCode, task.responseBody)
                    onHandleResponse(responseEntity)
                } else {
                    logFailure(task.responseCode, task.responseBody)
                    val failure = CapacitorFailure(
                        status = task.responseCode,
                        code = FAILURE_PARSER,
                        message = "response mapper error"
                    )
                    onHandleFailure(failure)
                }
            }

            private fun processUnAuthenticated(task: TaskResult) {
                logFailure(task.responseCode, task.responseBody)
                onHandleFailure(
                    entity = CapacitorFailure(
                        status = task.responseCode,
                        code = FAILURE_UNAUTHENTICATED,
                        message = parseJSONError(task.responseBody)?.toStringValue("message") ?: ""
                    )
                )
            }

            private fun processForbidden(task: TaskResult) {
                logFailure(task.responseCode, task.responseBody)
                onHandleFailure(
                    entity = CapacitorFailure(
                        status = task.responseCode,
                        code = FAILURE_FORBIDDEN,
                        message = parseJSONError(task.responseBody)?.toStringValue("message") ?: ""
                    )
                )
            }

            private fun processFailure(task: TaskResult) {
                logFailure(task.responseCode, task.responseBody)
                if (task.responseBody.isNotEmpty()) {
                    runCatching {
                        JSONObject(task.responseBody).let {
                            val envelopeFailure = CapacitorFailure(
                                status = it.toIntValue("status", task.responseCode),
                                code = it.toStringValue("code"),
                                message = it.toStringValue("message")
                            )
                            onHandleFailure(envelopeFailure)
                        }
                    }.onFailure {
                        onHandleFailure(
                            entity = CapacitorFailure(
                                status = task.responseCode,
                                code = FAILURE_UNKNOWN,
                                message = it.message ?: FAILURE_UNKNOWN
                            )
                        )
                    }
                } else {
                    onHandleFailure(
                        entity = CapacitorFailure(
                            status = task.responseCode,
                            code = FAILURE_BODY_EMPTY,
                            message = FAILURE_BODY_EMPTY
                        )
                    )
                }
            }

            private fun processInspection(task: TaskResult) {
                logFailure(task.responseCode, task.responseBody)
                onHandleFailure(
                    entity = CapacitorFailure(
                        status = task.responseCode,
                        code = FAILURE_INSPECTION,
                        message = FAILURE_INSPECTION
                    )
                )
            }

            private fun parseJSONError(responseBody: String): JSONObject? {
                return try {
                    JSONObject(responseBody)
                } catch (e: Exception) {
                    printout.error(sourceName = sourceName, throwable = e) {
                        "json parsing(error message) -> throw exception"
                    }
                    null
                }
            }

            inner class TaskResult {
                var responseCode: Int = 0
                var responseHeaders: Map<String, List<String>>? = null
                var responseBody: String = ""
                var failureMessage: String? = null
            }
        }

        // region ## format-log
        private fun logSuccess(resCode: Int, resValue: String) {
            runCatching {
                printout.info(sourceName = sourceName) {
                    "-->\n----------------------------------------------------------------------------------------------------\n"
                        .plus("EnvelopeTask -> onPostExecute -> Success {\n")
                        .plus("\trequestUrl: ${reqType.value}#${reqUrl}\n")
                        .plus("\t, requestHeader: ${reqHeader?.toString() ?: "null"}\n")
                        .plus("\t, requestInfo: ${reqInfo?.toString() ?: "null"}\n")
                        .plus("\t, requestBody: ${reqBody?.toString() ?: "null"}\n")
                        .plus("\t, responseCode: $resCode\n")
                        .plus("\t, responseBody: $resValue\n")
                        .plus("}\n")
                        .plus("----------------------------------------------------------------------------------------------------")
                }
            }
        }

        private fun logException(resCode: Int, resValue: String, e: Exception) {
            runCatching {
                printout.info(sourceName = sourceName) {
                    "-->\n----------------------------------------------------------------------------------------------------\n"
                        .plus("EnvelopeTask -> onPostExecute -> Exception {\n")
                        .plus("\trequestUrl: ${reqType.value}#${reqUrl}\n")
                        .plus("\t, requestHeader: ${reqHeader?.toString() ?: "null"}\n")
                        .plus("\t, requestInfo: ${reqInfo?.toString() ?: "null"}\n")
                        .plus("\t, requestBody: ${reqBody?.toString() ?: "null"}\n")
                        .plus("\t, responseCode: $resCode\n")
                        .plus("\t, responseBody: $resValue\n")
                        .plus("\t, exception: ${e.message}\n")
                        .plus("}\n")
                        .plus("----------------------------------------------------------------------------------------------------")
                }
            }
        }

        private fun logFailure(resCode: Int, resValue: String) {
            runCatching {
                printout.info(sourceName = sourceName) {
                    "-->\n----------------------------------------------------------------------------------------------------\n"
                        .plus("EnvelopeTask -> onPostExecute -> Failure {\n")
                        .plus("\trequestUrl: ${reqType.value}#${reqUrl}\n")
                        .plus("\t, requestHeader: ${reqHeader?.toString()}\n")
                        .plus("\t, requestInfo: ${reqInfo?.toString() ?: "null"}\n")
                        .plus("\t, requestBody: ${reqBody?.toString()}\n")
                        .plus("\t, responseCode: $resCode\n")
                        .plus("\t, responseBody: $resValue\n")
                        .plus("}\n")
                        .plus("----------------------------------------------------------------------------------------------------")
                }
            }
        }
        // endregion

        fun execute(callback: ICapacitorCallback<T>? = null) {
            capacitorCallback = callback
            runCatching {
                RequestCoroutineTask().execute()
            }.onFailure {
                printout.error(sourceName = sourceName, throwable = it)
            }
        }
    }
    // endregion
}