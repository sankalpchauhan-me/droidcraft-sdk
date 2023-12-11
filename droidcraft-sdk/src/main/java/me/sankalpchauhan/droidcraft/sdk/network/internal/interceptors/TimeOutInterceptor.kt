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

import me.sankalpchauhan.droidcraft.sdk.network.internal.di.NetworkScope
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * An OkHttp interceptor that allows setting custom timeout values (connect, read, write) per HTTP request.
 *
 * This interceptor provides the functionality to configure different timeout settings for individual
 * requests using special headers. It is particularly useful for applications that require dynamic
 * control over network timeouts, based on the specific needs of each request.
 *
 * Usage:
 * Add this interceptor to your OkHttp client. Then, specify timeouts by adding headers to your requests:
 * - CONNECT_TIMEOUT: Sets the connection timeout.
 * - READ_TIMEOUT: Sets the timeout for reading data.
 * - WRITE_TIMEOUT: Sets the timeout for writing data.
 * The timeout values should be specified in milliseconds.
 *
 * Example:
 * ```
 * val client = OkHttpClient.Builder()
 *     .addInterceptor(TimeOutInterceptor())
 *     .build()
 *
 * val request = Request.Builder()
 *     .url("https://example.com")
 *     .header("CONNECT_TIMEOUT", "30000") // 30 seconds
 *     .header("READ_TIMEOUT", "60000")    // 60 seconds
 *     .build()
 * ```
 *
 * Note: The interceptor removes these headers before proceeding with the request to ensure they are not sent to the server.
 *
 * Inspired by: [Retrofit issue](https://github.com/square/retrofit/issues/2561)
 */
@NetworkScope
internal class TimeOutInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val connectTimeout = request.header(CONNECT_TIMEOUT)?.let { Integer.valueOf(it) } ?: chain.connectTimeoutMillis()
        val readTimeout = request.header(READ_TIMEOUT)?.let { Integer.valueOf(it) } ?: chain.readTimeoutMillis()
        val writeTimeout = request.header(WRITE_TIMEOUT)?.let { Integer.valueOf(it) } ?: chain.writeTimeoutMillis()

        val newRequestBuilder = request.newBuilder()
            .removeHeader(CONNECT_TIMEOUT)
            .removeHeader(READ_TIMEOUT)
            .removeHeader(WRITE_TIMEOUT)

        request = newRequestBuilder.build()

        return chain
            .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            .proceed(request)
    }

    companion object {
        // Custom header name
        const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
        const val READ_TIMEOUT = "READ_TIMEOUT"
        const val WRITE_TIMEOUT = "WRITE_TIMEOUT"

        // 1 minute
        const val DEFAULT_LONG_TIMEOUT: Long = 60_000
    }
}