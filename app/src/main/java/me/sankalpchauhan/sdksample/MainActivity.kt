package me.sankalpchauhan.sdksample

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import me.sankalpchauhan.droidcraft.sdk.network.api.NetworkModuleApi
import me.sankalpchauhan.droidcraft.sdk.network.internal.client.TokenProvider
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.CacheConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.HeaderMapConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.LoggingConfiguration
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.LoggingConfigurationLevel
import me.sankalpchauhan.droidcraft.sdk.network.internal.config.NetworkConfiguration
import me.sankalpchauhan.service.CatService
import me.sankalpchauhan.service.EntryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.Timer
import java.util.TimerTask
import java.util.regex.Pattern


class MainActivity : AppCompatActivity(), TokenProvider {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pattern = Pattern.compile(".*")
        setContentView(R.layout.activity_main)
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        val headers = HashMap<String, String>()
        headers["clientId"] = "TestClient"
        headers["apiKey"] = "$123"
        val versionCode =  getVersionCode()
        versionCode?.let {
            headers["appVersion"] = it
        }
        val cacheConfiguration =
            try {
                CacheConfiguration(
                    cacheSize = 2 * 1024 * 1024,
                    cacheDirectory = this.cacheDir
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        NetworkModuleApi.initialize(
            configuration = NetworkConfiguration(
                "https://api.publicapis.org/",
                loggingConfiguration = LoggingConfiguration(
                    debugPrivateData = true,
                    loggingConfigurationLevel = LoggingConfigurationLevel.HEADERS
                ),
                cookieManager = cookieManager,
                cacheConfiguration = cacheConfiguration,
                headerMapConfiguration =  HeaderMapConfiguration(
                    headers = headers,
                    patternRegex = pattern.toString()
                )
            ),
            tokenProvider = this,
        )

        val t = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                val service = NetworkModuleApi.createService(EntryService::class.java)
                service.getService().enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    Log.e("RESPONSE", (response.body()).toString())
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                    Log.e("RESPONSE", t.printStackTrace().toString())
                    }

                })
                val service2 =
                    NetworkModuleApi.createService(CatService::class.java, "https://catfact.ninja/")
                service2.getFact().enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    Log.e("RESPONSE", (response.body()).toString())
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                    Log.e("RESPONSE", t.printStackTrace().toString())
                    }
                })

                val service3 = NetworkModuleApi.createService(EntryService::class.java)
                service3.getService().enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    Log.e("RESPONSE", (response.body()).toString())
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("RESPONSE", t.printStackTrace().toString())
                    }

                })
            }

        }
        t.schedule(timerTask, 5000, 10000)
    }

    @Suppress("DEPRECATION")
    private fun getVersionCode(): String? {
        return try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                pInfo.longVersionCode.toString()
            } else{
                pInfo.versionCode.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    override fun getToken(): String? {
        return null
    }

    override fun refreshToken(callback: (String?, Int) -> Unit) {
        callback(null, 200)
    }
}