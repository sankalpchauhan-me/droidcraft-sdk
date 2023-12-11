package me.sankalpchauhan.sdksample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import me.sankalpchauhan.droidcraft.sdk.network.api.NetworkModuleApi
import me.sankalpchauhan.droidcraft.sdk.network.internal.client.TokenProvider
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.LoggingConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.LoggingConfigurationLevel
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import me.sankalpchauhan.service.CatService
import me.sankalpchauhan.service.EntryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity(), TokenProvider {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetworkModuleApi.initialize(
            configuration = NetworkConfiguration(
                "https://api.publicapis.org/",
                loggingConfiguration = LoggingConfiguration(
                    debugPrivateData = true,
                    loggingConfigurationLevel = LoggingConfigurationLevel.HEADERS
                ),
            ),
            tokenProvider = this
        )

        val t = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                val service = NetworkModuleApi.provideService(EntryService::class.java)
                service.getService().enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    Log.e("RESPONSE", (response.body()).toString())
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                    Log.e("RESPONSE", t.printStackTrace().toString())
                    }

                })
                val service2 =
                    NetworkModuleApi.provideService(CatService::class.java, "https://catfact.ninja/")
                service2.getFact().enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    Log.e("RESPONSE", (response.body()).toString())
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                    Log.e("RESPONSE", t.printStackTrace().toString())
                    }
                })

                val service3 = NetworkModuleApi.provideService(EntryService::class.java)
                service3.getService().enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    Log.e("RESPONSE", (response.body()).toString())
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                    Log.e("RESPONSE", t.printStackTrace().toString())
                    }

                })
            }

        }
        t.schedule(timerTask, 5000, 10000)
    }


    override fun getToken(): String? {
        return null
    }

    override fun refreshToken(callback: (String?, Int) -> Unit) {
        callback(null, 200)
    }
}