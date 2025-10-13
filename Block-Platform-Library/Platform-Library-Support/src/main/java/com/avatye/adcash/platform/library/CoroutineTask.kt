package com.avatye.adcash.platform.library

import kotlinx.coroutines.*

abstract class CoroutineTask<Params, Result> {

    protected open fun onPreExecute() {}

    protected abstract fun doInBackground(vararg params: Params): Result

    protected open fun onPostExecute(result: Result) {}

    private lateinit var job: Job
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.Main) }

    fun execute(vararg params: Params) {
        job = coroutineScope.launch {
            onPreExecute()
            val result = async(Dispatchers.IO) {
                doInBackground(*params)
            }
            onPostExecute(result.await())
        }
    }

    fun cancel() {
        if (::job.isInitialized && job.isActive) {
            job.cancel()
        }
    }

    fun cancelAll() {
        if (coroutineScope.isActive) {
            coroutineScope.cancel()
        }
    }
}