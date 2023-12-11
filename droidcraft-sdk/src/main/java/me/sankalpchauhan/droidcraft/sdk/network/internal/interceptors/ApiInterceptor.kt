/*
 * Copyright (c) 2023. Sankalp Singh Chauhan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.sankalpchauhan.droidcraft.sdk.network.internal.interceptors

import me.sankalpchauhan.droidcraft.sdk.network.internal.ApiPath
import me.sankalpchauhan.droidcraft.sdk.network.internal.extensions.tryOrNull
import me.sankalpchauhan.droidcraft.sdk.network.internal.listeners.ApiInterceptorListener
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject



/**
 * An OkHttp interceptor for monitoring and handling API responses for specified paths and methods.
 *
 * This interceptor allows for the interception of HTTP responses based on defined API paths and methods.
 * It notifies registered listeners with the response data, enabling custom handling or processing of API responses.
 * This is particularly useful for applications that require specific actions or logging based on the API interactions.
 *
 * Usage:
 * - Add this interceptor to the OkHttp client setup.
 * - Register listeners for specific API paths and methods using [addListener].
 * - Optionally, remove listeners using [removeListener].
 *
 * Example:
 * ```
 * val client = OkHttpClient.Builder()
 *     .addInterceptor(ApiInterceptor())
 *     .build()
 *
 * val interceptor = ApiInterceptor()
 * interceptor.addListener(ApiPath("/path", "GET"), object : ApiInterceptorListener {
 *     override fun onApiResponse(apiPath: ApiPath, response: String) {
 *         // Handle the API response
 *     }
 * })
 * ```
 *
 * Features:
 * - Thread-safe listener registration and removal for specific API paths.
 * - Notifies registered listeners with the response data for intercepted API paths.
 * - Can be used for detailed response logging, analytics, or custom processing based on the API response.
 *
 * Note: This class is intended for internal use within the SDK and should be used with caution to avoid memory leaks or excessive processing.
 */
internal class ApiInterceptor @Inject constructor() : Interceptor {

    init {
        Timber.d("ApiInterceptor.init")
    }

    private val apiResponseListenersMap = mutableMapOf<ApiPath, MutableList<ApiInterceptorListener>>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath.replaceFirst("/", "")
        val method = request.method

        val response = chain.proceed(request)

        synchronized(apiResponseListenersMap) {
            findApiPath(path, method)?.let { apiPath ->
                response.peekBody(Long.MAX_VALUE).string().let { networkResponse ->
                    apiResponseListenersMap[apiPath]?.forEach { listener ->
                        tryOrNull("Error in the implementation") {
                            listener.onApiResponse(apiPath, networkResponse)
                        }
                    }
                }
            }
        }

        return response
    }

    private fun findApiPath(path: String, method: String): ApiPath? {
        return apiResponseListenersMap
            .keys
            .find { apiPath ->
                apiPath.method === method && isTheSamePath(apiPath.path, path)
            }
    }

    private fun isTheSamePath(pattern: String, path: String): Boolean {
        val patternSegments = pattern.split("/")
        val pathSegments = path.split("/")

        if (patternSegments.size != pathSegments.size) return false

        return patternSegments.indices.all { i ->
            patternSegments[i] == pathSegments[i] || patternSegments[i].startsWith("{")
        }
    }

    /**
     * Adds listener to send intercepted api responses through.
     */
    fun addListener(path: ApiPath, listener: ApiInterceptorListener) {
        synchronized(apiResponseListenersMap) {
            apiResponseListenersMap.getOrPut(path) { mutableListOf() }
                .add(listener)
        }
    }

    /**
     * Remove listener to send intercepted api responses through.
     */
    fun removeListener(path: ApiPath, listener: ApiInterceptorListener) {
        synchronized(apiResponseListenersMap) {
            apiResponseListenersMap[path]?.remove(listener)
            if (apiResponseListenersMap[path]?.isEmpty() == true) {
                apiResponseListenersMap.remove(path)
            }
        }
    }
}