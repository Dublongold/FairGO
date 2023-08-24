package com.funny.fairGo.views.selectLevelView

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.funny.fairGo.R
import com.funny.fairGo.extentions.getStateOfLevels
import com.funny.fairGo.extentions.setBackButtonClickListener
import com.funny.fairGo.navigation.SingleNavigation

class SelectLevelFragm(private val singleNavigation: SingleNavigation): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_level_fragm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackButtonClickListener(singleNavigation)
        val textIds = listOf(R.string.level_1, R.string.level_2, R.string.level_3, R.string.level_4,
            R.string.level_5, R.string.level_6, R.string.level_7, R.string.level_8,
            R.string.level_9, R.string.level_10, R.string.level_11, R.string.level_12,
            R.string.level_13, R.string.level_14, R.string.level_15, R.string.level_16,
            R.string.level_17, R.string.level_18, R.string.level_19, R.string.level_20,
            R.string.level_21, R.string.level_22, R.string.level_23, R.string.level_24)
        val stateOfLevels = getStateOfLevels()
        view.findViewById<GridLayout>(R.id.level_container).let {gridLayout ->
            for(i in 0..23) {
                /*
                <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button_level1"
                android:layout_width="74dp"
                android:layout_height="74dp"
                android:textSize="28sp"
                android:text="@string/level_1"
                android:textColor="@color/levelText"
                android:background="@drawable/select_level_elem_bg"/>
                 */

                context?.let {context ->
                    gridLayout.addView(
                        AppCompatButton(context).apply {
                            this.id = R.id.button_level_1
                            val size = (resources.displayMetrics.density * 74).toInt()
                            layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                                    val layoutMargin = (resources.displayMetrics.density * 8).toInt()
                                    setMargins(0, if(i > 3) layoutMargin else 0, if((i+1) % 4 != 0) layoutMargin else 0, 0)
                                }
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                            setTextColor(resources.getColor(R.color.levelText, null))
                            setBackgroundResource(if(stateOfLevels[i]) R.drawable.select_level_elem_bg else R.drawable.locked)
                            if(stateOfLevels[i]) {
                                text = getString(textIds[i])
                                setOnClickListener {
                                    singleNavigation.level = i + 1
                                    singleNavigation.destination = SingleNavigation.GAME_DESTINATION
                                    singleNavigation.navigate()
                                }
                            }
                        }

                    )
                } ?: Log.w("Select level fragment", "CONTEXT IS NULL!!!")
            }
        }
    }
}