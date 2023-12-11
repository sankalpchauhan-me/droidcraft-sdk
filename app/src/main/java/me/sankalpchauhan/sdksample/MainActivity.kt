package me.sankalpchauhan.sdksample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.sankalpchauhan.droidcraft.sdk.network.internal.client.TokenProvider


class MainActivity : AppCompatActivity(), TokenProvider {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun getToken(): String? {
        return null
    }

    override fun refreshToken(callback: (String?, Int) -> Unit) {
        callback(null, 200)
    }
}