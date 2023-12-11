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

package me.sankalpchauhan.droidcraft.sdk.network.internal.di

import dagger.Component
import dagger.Lazy
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.retrofit.RetrofitFactory
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

@Component(modules = [NetworkModule::class])
internal interface NetworkComponent {
//    fun retrofit(): Retrofit
    fun retrofitFactory(): RetrofitFactory

    fun networkConfiguration(): NetworkConfiguration

    fun converterFactory(): List<@JvmSuppressWildcards Converter.Factory>
    fun okHttpClient(): Lazy<OkHttpClient>
    @Component.Builder
    interface Builder {
        fun networkModule(networkModule: NetworkModule): Builder
        fun build(): NetworkComponent
    }
}