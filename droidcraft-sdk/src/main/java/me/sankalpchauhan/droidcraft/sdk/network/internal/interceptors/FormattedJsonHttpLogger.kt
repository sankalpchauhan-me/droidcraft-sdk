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

import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 * A logger for HTTP communications that formats JSON messages for enhanced readability.
 *
 * This logger extends [HttpLoggingInterceptor.Logger] to provide a specialized logging functionality
 * for HTTP requests and responses, particularly focusing on JSON formatted payloads. It is designed
 * to aid in debugging by making network interactions more understandable.
 *
 * Usage:
 * - Intended primarily for use in DEBUG mode due to potential high memory consumption when logging large bodies.
 * - Can be attached to an instance of [HttpLoggingInterceptor] to handle logging of HTTP traffic.
 *
 * Features:
 * - Logs HTTP messages with the ability to format JSON content in a readable and structured way.
 * - Supports different logging levels controlled by the [level] parameter. When set to [HttpLoggingInterceptor.Level.BODY],
 *   it includes the bodies of the requests and responses in the log.
 * - Uses Timber for actual logging, offering additional features like log tagging and level control.
 *
 * Example:
 * ```
 * val loggingInterceptor = HttpLoggingInterceptor(FormattedJsonHttpLogger(HttpLoggingInterceptor.Level.BODY))
 * val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
 * ```
 *
 * @param level The logging level from [HttpLoggingInterceptor.Level] determining the verbosity of the logs.
 */
internal class FormattedJsonHttpLogger(
    private val level: HttpLoggingInterceptor.Level
) : HttpLoggingInterceptor.Logger {

    companion object {
        private const val INDENT_SPACE = 2
    }

    /**
     * Log the message and try to log it again as a JSON formatted string.
     * Note: it can consume a lot of memory but it is only in DEBUG mode.
     *
     * @param message
     */
    @Synchronized
    override fun log(message: String) {
        Timber.v(message)

        // Try to log formatted Json only if there is a chance that [message] contains Json.
        // It can be only the case if we log the bodies of Http requests.
        if (level != HttpLoggingInterceptor.Level.BODY) return

        if (message.startsWith("{")) {
            // JSON Detected
            try {
                val o = JSONObject(message)
                logJson(o.toString(INDENT_SPACE))
            } catch (e: JSONException) {
                // Finally this is not a JSON string...
                Timber.e(e)
            }
        } else if (message.startsWith("[")) {
            // JSON Array detected
            try {
                val o = JSONArray(message)
                logJson(o.toString(INDENT_SPACE))
            } catch (e: JSONException) {
                // Finally not JSON...
                Timber.e(e)
            }
        }
        // Else not a json string to log
    }

    private fun logJson(formattedJson: String) {
        formattedJson
            .lines()
            .dropLastWhile { it.isEmpty() }
            .forEach { Timber.v(it) }
    }
}