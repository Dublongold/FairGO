package com.funny.fairGo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.funny.fairGo.extentions.flags
import com.funny.fairGo.extentions.setOnBackPressedListener
import com.funny.fairGo.navigation.SingleNavigation
import com.funny.fairGo.network.KtorRequest
import kotlinx.coroutines.launch

class MainWindow : AppCompatActivity() {
    private val singleNavigation = SingleNavigation(::getSupportFragmentManager)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_window)

        val flag = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        window.flags = flag to flag

        lifecycleScope.launch {
            if(checkUrl()) return@launch
            val result = KtorRequest().makeRequestAndGetObject()
            Log.i("Network result", "${result?.link} (${result?.allow} ).")
            if(result?.link != null && result.allow) {
                if(!saveUrl(result.link)) {
                    Log.e("Save url", "Cannot save url.")
                }
                (application as GlobalApplication).url = result.link
                startActivity(WebWindow.getIntent(this@MainWindow))
            }
            else {
                singleNavigation.destination = SingleNavigation.MENU_DESTINATION
                singleNavigation.navigate(allowingStateLoss = true)
            }
        }
        setOnBackPressedListener(singleNavigation)
        singleNavigation.navigate(false)
    }

    private fun getPrivateSharedPreferences(name: String): SharedPreferences {
        return getSharedPreferences(name, MODE_PRIVATE)
    }

    private fun checkUrl(): Boolean {
        val t = getPrivateSharedPreferences("data").getString("place", null)
        if(t != null) {
            (application as GlobalApplication).url = t
            startActivity(WebWindow.getIntent(this))
        }
        return t != null
    }

    private fun saveUrl(url: String): Boolean {
        return try {
            getPrivateSharedPreferences("data").edit().putString("place", url).apply()
            true
        }
        catch (_: Exception) {
            false
        }
    }
}