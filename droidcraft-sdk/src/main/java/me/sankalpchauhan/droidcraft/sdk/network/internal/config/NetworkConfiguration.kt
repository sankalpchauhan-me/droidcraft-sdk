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

package me.sankalpchauhan.droidcraft.sdk.network.internal.config

import okhttp3.Interceptor
import java.io.File
import java.lang.reflect.Type
import java.net.CookieManager

data class NetworkConfiguration(
    val homeServerUrl: String,
    val networkInterceptors: List<Interceptor> = emptyList(),
    val interceptors: List<Interceptor> = emptyList(),
    val readTimeoutInSecs: Long? = null,
    val writeTimeoutInSecs: Long? = null,
    val connectTimeoutInSecs: Long? = null,
    val refreshTokenTimeoutInSecs: Long = 30,
    val retryConfiguration: RetryConfiguration? = null,
    val gsonConfiguration: GsonConfiguration = GsonConfiguration(),
    val loggingConfiguration: LoggingConfiguration = LoggingConfiguration(),
    val headerMapConfiguration: HeaderMapConfiguration = HeaderMapConfiguration(),
    val cacheConfiguration: CacheConfiguration? = null,
    val cookieManager: CookieManager? = null,
)

data class HeaderMapConfiguration(
    val headers: Map<String, String> = emptyMap(),
    val patternRegex: String? = null
)

data class RetryConfiguration(
    val maxRetries: Int,
    val initialDelayMillis: Long
)

data class CacheConfiguration(
    val cacheSize: Long,
    val cacheDirectory: File
)

data class LoggingConfiguration(
    val debugPrivateData: Boolean = false,
    val loggingConfigurationLevel: LoggingConfigurationLevel = LoggingConfigurationLevel.BASIC,
)

data class GsonConfiguration(
    val dateFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ",
    val typeAdapters: List<Pair<Type, Any>> = listOf(),
    val prettyPrinting: Boolean = false
)

enum class LoggingConfigurationLevel {
    NONE,
    BASIC,
    BODY,
    HEADERS
}