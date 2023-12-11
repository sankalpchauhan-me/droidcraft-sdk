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

import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.extensions.addHeadersIfAbsent
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URL
import java.util.regex.Pattern
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val networkConfiguration: NetworkConfiguration
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val regex: String = networkConfiguration.headerMapConfiguration.patternRegex?:URL(networkConfiguration.homeServerUrl).host
        val pattern = Pattern.compile(regex)
        if (pattern.matcher(request.url.host).matches()){
            if(networkConfiguration.headerMapConfiguration.headers.isNotEmpty()){
                request.addHeadersIfAbsent(networkConfiguration.headerMapConfiguration.headers)
            }
        }
        return chain.proceed(builder.build())
    }
}


