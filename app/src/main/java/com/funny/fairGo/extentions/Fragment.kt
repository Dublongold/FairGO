package com.funny.fairGo.extentions

import android.app.Activity
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.funny.fairGo.R
import com.funny.fairGo.navigation.SingleNavigation

fun Fragment.setBackButtonClickListener(singleNavigation: SingleNavigation, destination: String = SingleNavigation.MENU_DESTINATION) {
    view?.run {
        findViewById<ImageButton>(R.id.button_pause).setOnClickListener {
            singleNavigation.destination = destination
            singleNavigation.navigate()
        }
    }
}

var Fragment.musicState: Boolean
    get() = activity?.getSharedPreferences("data", Activity.MODE_PRIVATE)
        ?.getBoolean("music", true) ?: true
    set(value) {
        activity?.getSharedPreferences("data", Activity.MODE_PRIVATE)
            ?.edit()?.putBoolean("music", value)?.apply()
    }

fun Fragment.getStateOfLevels(): MutableList<Boolean> = activity?.getSharedPreferences("data", Activity.MODE_PRIVATE)
        ?.getString("stateOfLevels", null)?.split(",")?.map { it.toBoolean() }?.toMutableList() ?: MutableList(24) {it == 0}

fun Fragment.setStateOfLevels(value: List<Boolean>) {
    activity?.getSharedPreferences("data", Activity.MODE_PRIVATE)?.edit()
        ?.putString("stateOfLevels", value.joinToString(","))?.apply()
}
