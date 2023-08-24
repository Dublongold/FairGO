package com.funny.fairGo.views.gameView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.funny.fairGo.R
import com.funny.fairGo.models.CancellationToken
import com.funny.fairGo.navigation.SingleNavigation

class GameFragm(
    private val singleNavigation: SingleNavigation,
    private val level: Int):  Fragment(){

    private val vm: GameVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_fragm, container, false)
    }

    private fun throwLevelOutOfRange(): Nothing = throw IllegalArgumentException("Level ($level) is out of range (from 1 to 24).")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameElementImages = if(level in 1..8) {
            listOf(
                R.drawable.game_elem_90px_1,
                R.drawable.game_elem_90px_2,
                R.drawable.game_elem_90px_3,
                R.drawable.game_elem_90px_4,
                R.drawable.game_elem_90px_5,
                R.drawable.game_elem_90px_6,
                R.drawable.game_elem_90px_7,
                R.drawable.game_elem_90px_8,
                R.drawable.game_elem_90px_9,
                R.drawable.game_elem_90px_10,
                R.drawable.game_elem_90px_11,
                R.drawable.game_elem_90px_12,
            )
        }
        else {
            listOf(
                R.drawable.game_elem_65px_1,
                R.drawable.game_elem_65px_2,
                R.drawable.game_elem_65px_3,
                R.drawable.game_elem_65px_4,
                R.drawable.game_elem_65px_5,
                R.drawable.game_elem_65px_6,
                R.drawable.game_elem_65px_7,
                R.drawable.game_elem_65px_8,
                R.drawable.game_elem_65px_9,
                R.drawable.game_elem_65px_10,
                R.drawable.game_elem_65px_11,
                R.drawable.game_elem_65px_12,
            )
        }
        val timerText = view.findViewById<TextView>(R.id.timer)
        vm.seconds.observe(viewLifecycleOwner) {
            timerText.text = if(it < 60) {
                 getString(R.string.timer_text, it)
            }
            else {
                getString(R.string.timer_end_game)
            }
        }
        vm.needFound = when(level) {
            in 1..8 -> 6
            in 9..16 -> 8
            in 17..24 -> 10
            else ->  throwLevelOutOfRange()
        }
        view.findViewById<TextView>(R.id.level_title).text = getString(R.string.level_title, level)
        val found = view.findViewById<TextView>(R.id.found)
        vm.found.observe(viewLifecycleOwner) {
            found.text = getString(R.string.found, it, vm.needFound)
        }
        vm.hiddenBackgroundId = if(level in 1..8) R.drawable.game_elem_90px_h else R.drawable.game_elem_65px_h
        vm.initializeGameElementsPosition(level)
        vm.images = gameElementImages
        view.findViewById<GridLayout>(R.id.game_elements).let { gameElements ->
            if(vm.needFound != 6) {
                gameElements.columnCount = 4
            }
            for(i in 0 until (vm.needFound * 2)) {
                context?.let {context ->
                    gameElements.addView(
                        /*
                        <ImageButton
                            android:layout_width="65dp"
                            android:layout_height="65dp"
                            android:scaleType="centerCrop"
                            android:foreground="@drawable/rounded_button_fg"
                            android:background="@android:color/transparent"
                            app:srcCompat="@drawable/game_elem_65px_h"
                            android:contentDescription="@string/game_element_description"/>
                         */
                        ImageButton(context).apply {
                            id = R.id.game_element_1
                            val size = (resources.displayMetrics.density * if(level in 1..8) 90 else 65).toInt()
                            layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                                val marginEnd = (resources.displayMetrics.density * 4).toInt()
                                setMargins(marginEnd)
                            }
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setBackgroundColor(0x00000000)
                            setImageResource(vm.hiddenBackgroundId)
                            foreground = ResourcesCompat.getDrawable(resources, R.drawable.rounded_button_fg, null)
                            contentDescription = getString(R.string.game_element_description)
                            setOnClickListener {
                                if(it.isEnabled) {
                                    it.isEnabled = false
                                    vm.selectGameElement(this, i)
                                    Log.i("Game", "Clicked on button with index $i.")
                                }
                            }
                        }
                    )
                }
            }
        }
        view.findViewById<ImageButton>(R.id.button_pause).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.fragments, PauseFragm(singleNavigation, level, vm.seconds.value ?: 0, PauseFragm.OPEN_TYPE_PAUSE) {
                    vm.start()
                })
                .commit()
            vm.timerCancellationToken?.cancel()
            vm.timerCancellationToken = CancellationToken()
        }
        vm.gameOver.observe(viewLifecycleOwner) {
            if(it) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragments, PauseFragm(singleNavigation, level, vm.seconds.value ?: 0,
                        if((vm.seconds.value ?: 0) < 60) PauseFragm.OPEN_TYPE_WIN else PauseFragm.OPEN_TYPE_LOSE) {
                        vm.start()
                    })
                    .commit()
            }
        }
        view.findViewById<ImageButton>(R.id.debugButton).setOnClickListener {
            vm.changeSecond(58)
        }
    }

    override fun onStart() {
        super.onStart()
        vm.start()
    }

    override fun onDetach() {
        super.onDetach()
        vm.timerCancellationToken?.cancel()
    }
}