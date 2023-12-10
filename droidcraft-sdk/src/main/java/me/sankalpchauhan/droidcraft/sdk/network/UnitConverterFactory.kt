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

package me.sankalpchauhan.droidcraft.sdk.network

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * A custom converter factory for handling `Unit` responses in Retrofit.
 *
 * This factory provides a converter for Retrofit responses where the expected return type is `Unit`.
 * It's specifically designed for API calls that do not require processing of the response body.
 * For such calls, it efficiently closes the response body without further handling, which is
 * particularly useful for requests where only the status code is relevant (e.g., certain POST requests).
 *
 * Usage:
 * Add this converter factory to your Retrofit builder to handle responses where the expected type is `Unit`.
 * It works alongside other converters like `MoshiConverterFactory` to handle different types of responses.
 *
 * Example:
 * ```
 * Retrofit.Builder()
 *     .addConverterFactory(UnitConverterFactory)
 *     .addConverterFactory(...)
 *     .build()
 * ```
 *
 * Note: Ensure that this is only used for API calls where the response body is not needed. Incorrect usage
 * for calls that return important data in the body can lead to data loss or bugs.
 */
internal object UnitConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type, annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == Unit::class.java) UnitConverter else null
    }

    private object UnitConverter : Converter<ResponseBody, Unit> {
        override fun convert(value: ResponseBody) {
            value.close()
        }
    }
}