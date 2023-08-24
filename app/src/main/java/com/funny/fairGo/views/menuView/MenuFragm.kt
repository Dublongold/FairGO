package com.funny.fairGo.views.menuView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.funny.fairGo.R
import com.funny.fairGo.extentions.musicState
import com.funny.fairGo.navigation.SingleNavigation

class MenuFragm(private val singleNavigation: SingleNavigation): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.menu_fragm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.button_privacy_policy).setOnClickListener {
            singleNavigation.destination = SingleNavigation.PRIVACY_POLICY_DESTINATION
            singleNavigation.navigate()
        }

        view.findViewById<ImageButton>(R.id.button_play).setOnClickListener {
            singleNavigation.destination = SingleNavigation.SELECT_LEVEL_DESTINATION
            singleNavigation.navigate()
        }

        if(!musicState) {
            view.findViewById<ImageButton>(R.id.button_music).setImageResource(R.drawable.music_off)
        }

        view.findViewById<ImageButton>(R.id.button_music).setOnClickListener {
            val musicStateVal = !musicState
            musicState = musicStateVal
            if(musicStateVal) {
                view.findViewById<ImageButton>(R.id.button_music).setImageResource(R.drawable.music_on)
            }
            else {
                view.findViewById<ImageButton>(R.id.button_music).setImageResource(R.drawable.music_off)
            }
        }
    }
}