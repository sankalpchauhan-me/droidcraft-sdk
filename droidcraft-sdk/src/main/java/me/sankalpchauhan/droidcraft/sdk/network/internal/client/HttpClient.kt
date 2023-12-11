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

package me.sankalpchauhan.droidcraft.sdk.network.internal.client

import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.Timeout
import java.util.concurrent.TimeUnit

class HttpClient {
    fun getHttpClientBuilder(
        configuration: NetworkConfiguration,
        interceptors: List<Interceptor>
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .apply {
                interceptors.forEach {
                    addInterceptor(it)
                }
            }
        configuration.connectTimeoutInSecs?.let {
            builder.readTimeout(it, TimeUnit.SECONDS)
        }
        configuration.readTimeoutInSecs?.let {
            builder.readTimeout(it, TimeUnit.SECONDS)
        }
        configuration.writeTimeoutInSecs?.let {
            builder.readTimeout(it, TimeUnit.SECONDS)
        }
        builder.interceptors().removeAll(configuration.networkInterceptors)
        configuration.networkInterceptors.forEach {
            builder.addInterceptor(it)
        }

        return builder.build()
    }
}