package me.sankalpchauhan.sdksample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.sankalpchauhan.droidcraft.sdk.network.RetrofitFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RetrofitFactory()
    }
}