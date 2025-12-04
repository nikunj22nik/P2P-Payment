package com.p2p.application.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

    private var shimmerAnim: Animation? = null

//    private val shimmerViewIds = listOf(
//        R.id.shimmerAvatar1, R.id.shimmerLine1_1, R.id.shimmerLine1_2, R.id.shimmerLine1_3,
//        R.id.shimmerAvatar2, R.id.shimmerLine2_1, R.id.shimmerLine2_2, R.id.shimmerLine2_3,
//        R.id.shimmerAvatar3, R.id.shimmerLine3_1, R.id.shimmerLine3_2, R.id.shimmerLine3_3,
//        R.id.shimmerAvatar4, R.id.shimmerLine4_1, R.id.shimmerLine4_2, R.id.shimmerLine4_3,
//        R.id.shimmerAvatar5, R.id.shimmerLine5_1, R.id.shimmerLine5_2, R.id.shimmerLine5_3,
//        R.id.shimmerAvatar6, R.id.shimmerLine6_1, R.id.shimmerLine6_2, R.id.shimmerLine6_3,
//        R.id.shimmerAvatar7, R.id.shimmerLine7_1, R.id.shimmerLine7_2, R.id.shimmerLine7_3,
//        R.id.shimmerAvatar8, R.id.shimmerLine8_1, R.id.shimmerLine8_2, R.id.shimmerLine8_3
//    )

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

//    private fun startShimmer() {
//        shimmerAnim = AnimationUtils.loadAnimation(this, R.anim.shimmer_translate)
//
//        // Apply to each view
//        getShimmerViews().forEachIndexed { index, view ->
//            view.apply {
//                visibility = View.VISIBLE
//                animation = shimmerAnim.apply { this?.startOffset = index * 60L }
//            }
//        }
//    }

//    private fun stopShimmer() {
//        getShimmerViews().forEach { view ->
//            view.clearAnimation()
//        }
//        shimmerAnim = null
//    }

//    private fun getShimmerViews(): List<View> =
//        shimmerViewIds.mapNotNull { binding.root.findViewById<View>(it) }





}