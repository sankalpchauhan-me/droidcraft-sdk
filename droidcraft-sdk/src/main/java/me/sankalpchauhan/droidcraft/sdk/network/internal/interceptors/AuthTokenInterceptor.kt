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

import me.sankalpchauhan.droidcraft.sdk.network.internal.client.TokenProvider
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.extensions.addHeaderIfAbsent
import me.sankalpchauhan.droidcraft.sdk.util.HttpStatusCode
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthTokenInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val networkConfiguration: NetworkConfiguration
) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response = chain.proceed(requestWithToken(request))

        if (response.code == HttpStatusCode.UNAUTHORIZED.code) {
            synchronized(this) {
                response.close()
                var newToken: String? = null
                val latch = CountDownLatch(1)
                tokenProvider.refreshToken { token, int ->
                    newToken = token
                    latch.countDown()
                }
                //TODO: Check if this is the right way to do this
                val tokenRefreshed = latch.await(networkConfiguration.refreshTokenTimeoutInSecs, TimeUnit.SECONDS)
                if (tokenRefreshed && newToken != null) {
                    request = requestWithToken(request, newToken)
                    response = chain.proceed(request)
                } else {
                    return response
                }
            }
        }

        return response
    }

    private fun requestWithToken(request: Request, token: String? = tokenProvider.getToken()): Request {
        return if (!token.isNullOrEmpty()) {
            request.addHeaderIfAbsent("Authorization", "Bearer $token")
        } else {
            request
        }
    }
}