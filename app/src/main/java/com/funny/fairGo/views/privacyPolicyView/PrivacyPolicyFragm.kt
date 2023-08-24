package com.funny.fairGo.views.privacyPolicyView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funny.fairGo.R
import com.funny.fairGo.extentions.setBackButtonClickListener
import com.funny.fairGo.navigation.SingleNavigation

class PrivacyPolicyFragm(private val singleNavigation: SingleNavigation): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.privacy_policy_fragm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener(singleNavigation)
    }
}