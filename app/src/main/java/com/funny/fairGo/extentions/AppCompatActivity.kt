package com.funny.fairGo.extentions

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.funny.fairGo.navigation.SingleNavigation

fun AppCompatActivity.setOnBackPressedListener(singleNavigation: SingleNavigation) {
    onBackPressedDispatcher.addCallback(
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when(singleNavigation.destination) {
                    SingleNavigation.MENU_DESTINATION -> finish()
                    SingleNavigation.PRIVACY_POLICY_DESTINATION, SingleNavigation.SELECT_LEVEL_DESTINATION -> {
                        singleNavigation.destination = SingleNavigation.MENU_DESTINATION
                        singleNavigation.navigate()
                    }
                    SingleNavigation.GAME_DESTINATION -> {
                        singleNavigation.destination = SingleNavigation.SELECT_LEVEL_DESTINATION
                        singleNavigation.navigate()
                    }
                }
            }
        }
    )
}