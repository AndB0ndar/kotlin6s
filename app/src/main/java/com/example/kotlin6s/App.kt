package com.example.kotlin6s

import android.R
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(EXAMPLE_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(
            "dark_mode",
            false
        )
        if (darkTheme) {
            setTheme(R.style.Theme_Black)
        } else {
            setTheme(R.style.Theme)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val EXAMPLE_PREFERENCES = "example_preferences"
    }
}