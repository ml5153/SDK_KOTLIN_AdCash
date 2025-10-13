package com.avatye.adcash.platform.library.printout

import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

class PrintOut(private val moduleName: String) {

    companion object {
        fun print(throwable: Throwable? = null, moduleName: String? = null, viewName: String? = null, trace: () -> String) {
            println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
            println("${PrintOutSettings.SourceName}:[$moduleName#$viewName] => ${trace()}")
            println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        }
    }

    private enum class LEVEL { VERBOSE, DEBUG, INFO, WARN, ERROR }

    val allowLog: Boolean
        get() {
            return PrintOutSettings.allowLog
        }

    fun info(throwable: Throwable? = null, sourceName: String? = null, args: Array<String>) = logWriter(
        logLevel = LEVEL.INFO, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = makeTrace(*args)
    )

    fun debug(throwable: Throwable? = null, sourceName: String? = null, vararg args: String) = logWriter(
        logLevel = LEVEL.DEBUG, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = makeTrace(*args)
    )

    fun verbose(throwable: Throwable? = null, sourceName: String? = null, vararg args: String) = logWriter(
        logLevel = LEVEL.VERBOSE, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = makeTrace(*args)
    )

    fun warn(throwable: Throwable? = null, sourceName: String? = null, vararg args: String) = logWriter(
        logLevel = LEVEL.WARN, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = makeTrace(*args)
    )

    fun error(throwable: Throwable? = null, sourceName: String? = null, vararg args: String) = logWriter(
        logLevel = LEVEL.ERROR, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = makeTrace(*args)
    )

    private fun makeTrace(vararg trace: String): () -> String {
        return fun(): String {
            return trace.joinToString()
        }
    }

    fun info(throwable: Throwable? = null, sourceName: String? = null, trace: () -> String) = logWriter(
        logLevel = LEVEL.INFO, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = trace
    )

    fun debug(throwable: Throwable? = null, sourceName: String? = null, trace: () -> String) = logWriter(
        logLevel = LEVEL.DEBUG, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = trace
    )

    fun verbose(throwable: Throwable? = null, sourceName: String? = null, trace: () -> String) = logWriter(
        logLevel = LEVEL.VERBOSE, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = trace
    )

    fun warn(throwable: Throwable? = null, sourceName: String? = null, trace: () -> String) = logWriter(
        logLevel = LEVEL.WARN, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = trace
    )

    fun error(throwable: Throwable? = null, sourceName: String? = null, trace: () -> String) = logWriter(
        logLevel = LEVEL.ERROR, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = trace
    )

    fun error(throwable: Throwable? = null, sourceName: String? = null) = logWriter(
        logLevel = LEVEL.ERROR, throwable = throwable, moduleName = moduleName, sourceName = sourceName, trace = { "" }
    )

    private fun logWriter(logLevel: LEVEL, throwable: Throwable? = null, moduleName: String, sourceName: String? = null, trace: () -> String) {
        if (PrintOutSettings.allowLog) {
            when (logLevel) {
                LEVEL.VERBOSE -> Log.v(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                LEVEL.DEBUG -> Log.d(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                LEVEL.INFO -> Log.i(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                LEVEL.WARN -> Log.w(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                LEVEL.ERROR -> Log.e(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
            }
        } else {
            val isLoggable = Log.isLoggable(PrintOutSettings.LOGGABLE, Log.VERBOSE)
            if (isLoggable) {
                when (logLevel) {
                    LEVEL.VERBOSE -> Log.v(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                    LEVEL.DEBUG -> Log.d(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                    LEVEL.INFO -> Log.i(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                    LEVEL.WARN -> Log.w(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                    LEVEL.ERROR -> Log.e(PrintOutSettings.SourceName, makeLog(throwable, moduleName, sourceName = sourceName, trace = trace))
                }
            }
        }
    }


    private fun makeLog(throwable: Throwable? = null, moduleName: String, sourceName: String? = null, trace: () -> String): String {
        return "[$moduleName/$sourceName] => ${trace()}" + if (throwable != null) " => ${getStackTraceString(throwable)}" else ""
    }


    private fun getStackTraceString(t: Throwable): String {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}
