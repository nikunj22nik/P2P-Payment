package com.p2p.application.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.p2p.application.databinding.ActivitySplaceBinding
import com.p2p.application.util.CommonFunction.Companion.SPLASH_DELAY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

      //  startShimmer()
        lifecycleScope.launch {
            delay(SPLASH_DELAY)
          //  stopShimmer()
            // Check login session and navigate accordingly
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    override fun attachBaseContext(newBase: Context) {
        val configuration: Configuration = newBase.resources.configuration
        configuration.fontScale = 1.0f // system font size ignore
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }



}