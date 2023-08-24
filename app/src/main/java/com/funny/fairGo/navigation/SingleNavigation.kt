package com.funny.fairGo.navigation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.funny.fairGo.R
import com.funny.fairGo.views.gameView.GameFragm
import com.funny.fairGo.views.loadingView.LoadingFragm
import com.funny.fairGo.views.menuView.MenuFragm
import com.funny.fairGo.views.privacyPolicyView.PrivacyPolicyFragm
import com.funny.fairGo.views.selectLevelView.SelectLevelFragm

class SingleNavigation(private val getFragmentManager: () -> FragmentManager) {
    var destination: String = DEFAULT_DESTINATION
    var level = -1

    fun navigate(replace: Boolean = true, allowingStateLoss: Boolean = false) {
        Log.i("Navigation", "Navigate to $destination.")
        getFragmentManager().beginTransaction().apply {
            if(replace) {
                replace(R.id.fragments, getFragmentByDestination())
            }
            else {
                add(R.id.fragments, getFragmentByDestination())
            }
        }.apply {
            if(allowingStateLoss) commitAllowingStateLoss() else commit()
        }
    }

    private fun getFragmentByDestination(): Fragment = when(destination) {
        LOADING_DESTINATION -> LoadingFragm(this)
        MENU_DESTINATION -> MenuFragm(this)
        PRIVACY_POLICY_DESTINATION -> PrivacyPolicyFragm(this)
        SELECT_LEVEL_DESTINATION -> SelectLevelFragm(this)
        GAME_DESTINATION -> GameFragm(this, level)
        else -> throw IllegalArgumentException()
    }

    companion object {
        const val LOADING_DESTINATION = "loading"
        const val MENU_DESTINATION = "menu"
        const val PRIVACY_POLICY_DESTINATION = "privacy policy"
        const val SELECT_LEVEL_DESTINATION = "select level"
        const val GAME_DESTINATION = "game"
        const val DEFAULT_DESTINATION = LOADING_DESTINATION
    }
}