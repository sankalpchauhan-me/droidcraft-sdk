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

package me.sankalpchauhan.droidcraft.sdk.network.internal.retrofit

import dagger.Lazy
import me.sankalpchauhan.droidcraft.sdk.util.ensureTrailingSlash
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Inject


internal class RetrofitFactory @Inject constructor() {
    private val retrofitInstanceMap: MutableMap<String, Retrofit> = HashMap()
    private fun create(
        okHttpClient: Lazy<OkHttpClient>,
        baseUrl: String,
        converterFactories: List<Converter.Factory>
    ): Retrofit {
        val retrofitInstance = Retrofit.Builder()
            .baseUrl(baseUrl.ensureTrailingSlash())
            .callFactory { request -> okHttpClient.get().newCall(request) }
            .apply {
                converterFactories.forEach { addConverterFactory(it) }
            }
            .build()
        return retrofitInstance;
    }

    fun getRetrofitInstance(
        okHttpClient: Lazy<OkHttpClient>,
        baseUrl: String,
        converterFactories: List<Converter.Factory>
    ): Retrofit {
        return if (retrofitInstanceMap.containsKey(baseUrl) && retrofitInstanceMap[baseUrl] != null) {
            retrofitInstanceMap[baseUrl]!!
        } else {
            val newRetrofitInstance = create(okHttpClient, baseUrl, converterFactories)
            retrofitInstanceMap[baseUrl] = newRetrofitInstance
            newRetrofitInstance
        }
    }
}