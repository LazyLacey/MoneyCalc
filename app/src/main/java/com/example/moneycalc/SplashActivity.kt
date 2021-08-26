package com.example.moneycalc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var sPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        var sPrefSettings = PreferenceManager.getDefaultSharedPreferences(this)

        if (sPref.getBoolean("isFirstStart", true) || sPrefSettings.getString("daily_money", "0")!!.toInt() == 0) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            sPref.edit().putBoolean("isFirstStart", false).apply()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        finishAffinity()

    }
}