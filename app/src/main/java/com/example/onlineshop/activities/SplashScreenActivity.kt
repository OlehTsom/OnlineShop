package com.example.onlineshop.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import com.example.onlineshop.R
import com.example.onlineshop.databinding.ActivitySplechScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplechScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplechScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lv.playAnimation()

        binding.lv.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

                alphaAnimatorLottieView().start()

                binding.lv.visibility = View.VISIBLE
                binding.lv.animate().interpolator = DecelerateInterpolator()

                slideUpAnimationAppName().start()

            }

            override fun onAnimationEnd(p0: Animator) {
                navigateToNextLayout()
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
    }

    private fun alphaAnimatorLottieView() : ObjectAnimator{
        val alphaAnimator = ObjectAnimator.ofFloat(binding.lv, "alpha", 0f, 1f)
        alphaAnimator.duration = 1000
        alphaAnimator.interpolator = DecelerateInterpolator()
        return alphaAnimator
    }

    private fun slideUpAnimationAppName() : ObjectAnimator{
        val slideUpAnimation = ObjectAnimator.ofFloat(binding.tvAppName, "translationY", 0f, -100f)
        slideUpAnimation.duration = 800
        return slideUpAnimation
    }

    private fun navigateToNextLayout(){
        val intent = Intent(this@SplashScreenActivity, LoginRegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.from_righ, R.anim.to_left)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                finish()
            }
        })
    }
}