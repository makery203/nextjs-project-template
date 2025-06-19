package com.eventmanagement.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.eventmanagement.app.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Delay for 2 seconds then check authentication state
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthState()
        }, SPLASH_DELAY)
    }
    
    private fun checkAuthState() {
        // Check if user is signed in
        if (auth.currentUser != null) {
            // User is signed in, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not signed in, go to AuthActivity
            startActivity(Intent(this, AuthActivity::class.java))
        }
        // Finish splash activity
        finish()
    }
    
    companion object {
        private const val SPLASH_DELAY = 2000L // 2 seconds
    }
}
