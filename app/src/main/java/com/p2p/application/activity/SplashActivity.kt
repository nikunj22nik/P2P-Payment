package com.p2p.application.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.p2p.application.R
import com.p2p.application.databinding.ActivitySplaceBinding
import com.p2p.application.util.CommonFunction.Companion.SPLASH_DELAY
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplaceBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_splace)
        lifecycleScope.launch {
            delay(SPLASH_DELAY)

            // Check login session and navigate accordingly
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

    }
}