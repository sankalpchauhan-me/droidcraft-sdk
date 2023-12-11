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

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class RetryInterceptor @Inject constructor(
    val networkConfiguration: NetworkConfiguration
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val maxRetries: Int? = networkConfiguration.retryConfiguration?.maxRetries
        val initialDelayMillis: Long? = networkConfiguration.retryConfiguration?.initialDelayMillis
        if(maxRetries== null || initialDelayMillis == null) {
            return chain.proceed(chain.request())
        }
        var lastException: IOException? = null
        var currentDelay = initialDelayMillis

        for (attempt in 0 until maxRetries) {
            try {
                val response = chain.proceed(chain.request())
                if (response.isSuccessful) {
                    return response
                }
                response.close()
            } catch (e: IOException) {
                lastException = e
                if (attempt < maxRetries - 1) {
                    runBlocking {
                        delay(currentDelay)
                        currentDelay *= 2
                    }
                }
            }
        }
        throw lastException ?: IOException("Failed to execute request after $maxRetries attempts")
    }
}