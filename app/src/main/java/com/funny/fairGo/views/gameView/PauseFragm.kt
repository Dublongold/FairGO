package com.funny.fairGo.views.gameView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.funny.fairGo.R
import com.funny.fairGo.extentions.getStateOfLevels
import com.funny.fairGo.extentions.setStateOfLevels
import com.funny.fairGo.navigation.SingleNavigation

class PauseFragm(
    private val singleNavigation: SingleNavigation,
    private val level: Int,
    private val seconds: Int,
    private val openType: Int,
    private val callbackOnPause: (() -> Unit)? = null
): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pause_fragm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(openType == OPEN_TYPE_WIN) {
            val stateOfLevels = getStateOfLevels()
            if(stateOfLevels.count {it} == level && stateOfLevels.size != level) {
                stateOfLevels[level] = true
                Log.i("State of levels", "State of levels: $stateOfLevels")
            }
            setStateOfLevels(stateOfLevels)
        }
        if(openType != OPEN_TYPE_LOSE) {
            /*
            <ImageButton
                android:id="@+id/button_pause_move"
                android:layout_width="60dp"
                android:layout_height="60dp"
                android:layout_marginEnd="16dp"
                android:background="@android:color/transparent"
                android:contentDescription="@string/button_back_description"
                android:foreground="@drawable/rounded_button_fg"
                android:scaleType="centerCrop"
                app:srcCompat="@drawable/button_back" />
             */
            val buttonMove = ImageButton(context).apply {
                id = R.id.button_pause_move
                val size = (resources.displayMetrics.density * 60).toInt()
                layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                    setMargins(0, 0, (resources.displayMetrics.density * 16).toInt(), 0)
                }
                setBackgroundColor(0x00000000)
                contentDescription = getString(if(openType == OPEN_TYPE_PAUSE) R.string.leave_pause_description else R.string.go_next_description)
                foreground = ResourcesCompat.getDrawable(resources, R.drawable.rounded_button_fg, null)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(if(openType == OPEN_TYPE_PAUSE) R.drawable.button_back else R.drawable.play)
                setOnClickListener {
                    when (openType) {
                        OPEN_TYPE_PAUSE -> {
                            Log.i("Pause fragment", "Pause).")
                            parentFragmentManager.beginTransaction()
                                .remove(this@PauseFragm)
                                .commit()
                            callbackOnPause?.invoke()
                        }
                        OPEN_TYPE_WIN -> {
                            Log.i("Pause fragment", "Win.")
                            singleNavigation.level = level + 1
                            singleNavigation.destination = SingleNavigation.GAME_DESTINATION
                            singleNavigation.navigate()
                        }
                        else -> {
                            Log.i("Pause fragment", "Else...")
                        }
                    }
                }
            }
            view.findViewById<LinearLayout>(R.id.pause_buttons).addView(buttonMove, 0)
        }
        view.findViewById<TextView>(R.id.level_pause).text = getString(R.string.level_title, level)
        view.findViewById<TextView>(R.id.pause_timer).text = if(seconds < 60) getString(R.string.timer_text, seconds) else getString(R.string.timer_end_game)
        view.findViewById<ImageButton>(R.id.button_pause_home).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
            singleNavigation.destination = SingleNavigation.SELECT_LEVEL_DESTINATION
            singleNavigation.navigate()
        }
        view.findViewById<ImageButton>(R.id.button_pause_replay).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
            singleNavigation.level = level
            singleNavigation.destination = SingleNavigation.GAME_DESTINATION
            singleNavigation.navigate()
        }
        view.findViewById<TextView>(R.id.pause_text).text = getString(when(openType) {
            OPEN_TYPE_WIN -> R.string.you_win
            OPEN_TYPE_LOSE -> R.string.you_lose
            else -> R.string.game_paused
        })
    }

    companion object {
        const val OPEN_TYPE_PAUSE = 0
        const val OPEN_TYPE_WIN = 1
        const val OPEN_TYPE_LOSE = 2
    }
}