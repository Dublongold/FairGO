package com.funny.fairGo.views.loadingView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funny.fairGo.R
import com.funny.fairGo.navigation.SingleNavigation

class LoadingFragm(private val singleNavigation: SingleNavigation): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.loading_fragm, container, false)
    }

    fun navigateToMenu() {
        singleNavigation.destination = SingleNavigation.MENU_DESTINATION
        singleNavigation.navigate()
    }
}