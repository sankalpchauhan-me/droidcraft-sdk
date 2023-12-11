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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import me.sankalpchauhan.droidcraft.sdk.network.internal.client.HttpClient
import me.sankalpchauhan.droidcraft.sdk.network.internal.client.TokenProvider
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.LoggingConfigurationLevel
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.interceptors.ApiInterceptor
import me.sankalpchauhan.droidcraft.sdk.network.internal.interceptors.AuthTokenInterceptor
import me.sankalpchauhan.droidcraft.sdk.network.internal.interceptors.CurlLoggingInterceptor
import me.sankalpchauhan.droidcraft.sdk.network.internal.interceptors.FormattedJsonHttpLogger
import me.sankalpchauhan.droidcraft.sdk.network.internal.interceptors.TimeOutInterceptor
import me.sankalpchauhan.droidcraft.sdk.network.internal.retrofit.RetrofitFactory
import me.sankalpchauhan.droidcraft.sdk.network.internal.retrofit.UnitConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
internal class NetworkModule(private val networkConfiguration: NetworkConfiguration, private val tokenProvider: TokenProvider) {

    @Provides
    fun provideTokenProvider(): TokenProvider = tokenProvider

    @Provides
    fun provideNetworkConfiguration(): NetworkConfiguration {
        return networkConfiguration
    }

    @Module
    companion object {

        @Provides
        fun provideAuthTokenInterceptor(
            token:TokenProvider,
            networkConfiguration: NetworkConfiguration
        ): AuthTokenInterceptor {
            return AuthTokenInterceptor(token, networkConfiguration)
        }

        @Provides
        @JvmStatic
        fun provideConverterFactories(gson: Gson): List<@JvmSuppressWildcards Converter.Factory> {
            return listOf(
                UnitConverterFactory,
                GsonConverterFactory.create(gson)
            )
        }

        @Provides
        @JvmStatic
        fun provideRetrofitFactory(): RetrofitFactory {
            return RetrofitFactory()
        }

        @Provides
        @JvmStatic
        fun providesCurlLoggingInterceptor(): CurlLoggingInterceptor {
            return CurlLoggingInterceptor()
        }

        @Provides
        @JvmStatic
        fun providesTimeOutInterceptor(): TimeOutInterceptor {
            return TimeOutInterceptor()
        }

        @Provides
        @JvmStatic
        fun provideHttpLoggingInterceptor(networkConfiguration: NetworkConfiguration): HttpLoggingInterceptor {
            val httpLoggingLevel = when(networkConfiguration.loggingConfiguration.loggingConfigurationLevel){
                LoggingConfigurationLevel.NONE -> HttpLoggingInterceptor.Level.NONE
                LoggingConfigurationLevel.BASIC -> HttpLoggingInterceptor.Level.BASIC
                LoggingConfigurationLevel.BODY -> HttpLoggingInterceptor.Level.BODY
                LoggingConfigurationLevel.HEADERS ->  HttpLoggingInterceptor.Level.HEADERS
            }
            val logger = FormattedJsonHttpLogger(httpLoggingLevel)
            val interceptor = HttpLoggingInterceptor(logger)
            if(!networkConfiguration.loggingConfiguration.debugPrivateData){
                interceptor.redactHeader("Authorization")
                interceptor.redactHeader("Cookie")
            }
            interceptor.level = httpLoggingLevel
            return interceptor
        }

        @Provides
        @JvmStatic
        fun provideOkHttpClient(
            configuration: NetworkConfiguration,
            httpLoggingInterceptor: HttpLoggingInterceptor,
            curlLoggingInterceptor: CurlLoggingInterceptor,
            timeOutInterceptor: TimeOutInterceptor,
            apiInterceptor: ApiInterceptor,
            authTokenInterceptor: AuthTokenInterceptor
        ): OkHttpClient {
            val interceptors = mutableListOf(
                authTokenInterceptor,
                apiInterceptor,
                httpLoggingInterceptor,
                timeOutInterceptor,
            )
            if (configuration.loggingConfiguration.debugPrivateData) {
                interceptors.add(curlLoggingInterceptor)
            }
            return HttpClient().getHttpClientBuilder(configuration, interceptors)
        }

        @Provides
        @JvmStatic
        fun provideRetrofitClient(
            configuration: NetworkConfiguration,
            okHttpClient: dagger.Lazy<OkHttpClient>,
            retrofitFactory: RetrofitFactory,
            converterFactories: List<@JvmSuppressWildcards Converter.Factory>
        ): Retrofit {
            return retrofitFactory.getRetrofitInstance(
                okHttpClient,
                configuration.homeServerUrl,
                converterFactories
            )
        }

        @Provides
        @JvmStatic
        fun provideGson(networkConfiguration: NetworkConfiguration): Gson {
            val gsonConfig = networkConfiguration.gsonConfiguration

            val gsonBuilder = GsonBuilder()
                .setDateFormat(gsonConfig.dateFormat)

            if (gsonConfig.prettyPrinting) {
                gsonBuilder.setPrettyPrinting()
            }

            gsonConfig.typeAdapters.forEach { (type, adapter) ->
                gsonBuilder.registerTypeAdapter(type, adapter)
            }

            return gsonBuilder.create()
        }
    }
}