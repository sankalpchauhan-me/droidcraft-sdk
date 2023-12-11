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

package me.sankalpchauhan.droidcraft.sdk.network.api

import me.sankalpchauhan.droidcraft.BuildConfig
import me.sankalpchauhan.droidcraft.sdk.network.internal.client.TokenProvider
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.di.DaggerNetworkComponent
import me.sankalpchauhan.droidcraft.sdk.network.internal.di.NetworkComponent
import me.sankalpchauhan.droidcraft.sdk.network.internal.di.NetworkModule
import timber.log.Timber

object NetworkModuleApi {
    private lateinit var networkComponent: NetworkComponent
    fun initialize(configuration: NetworkConfiguration, tokenProvider: TokenProvider) {
        if (Timber.treeCount==0 &&  BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        val networkModule = NetworkModule(configuration, tokenProvider)
        networkComponent = DaggerNetworkComponent.builder()
            .networkModule(networkModule)
            .build()
    }

    fun <T> provideService(service: Class<T>, baseUrl:String? = null): T {
        if(!service.isInterface){
            throw IllegalArgumentException("API declarations must be interfaces.")
        }
        if (!::networkComponent.isInitialized) {
            throw IllegalStateException("NetworkModule must be initialized first")
        }
        return networkComponent.retrofitFactory().getRetrofitInstance(
            networkComponent.okHttpClient(),
            baseUrl ?: networkComponent.networkConfiguration().homeServerUrl,
            networkComponent.converterFactory()
        ).create(service)
    }
}
