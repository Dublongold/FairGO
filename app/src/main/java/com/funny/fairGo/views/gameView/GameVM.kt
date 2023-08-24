package com.funny.fairGo.views.gameView

import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funny.fairGo.levelConfiguration.LevelConfiguration
import com.funny.fairGo.models.CancellationToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Level

class GameVM: ViewModel() {
    private val _seconds: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }
    private val _gameOver: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }
    private val _found: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }
    private val firstSelected: MutableLiveData<Pair<ImageButton?, Int>> by lazy {
        MutableLiveData(null to -1)
    }

    private val secondSelected: MutableLiveData<Pair<ImageButton?, Int>> by lazy {
        MutableLiveData(null to -1)
    }

    private lateinit var levelPositions: List<Int>
    lateinit var images: List<Int>
    var timerCancellationToken: CancellationToken? = CancellationToken()

    var needFound = -1
    var hiddenBackgroundId = -1

    val seconds: LiveData<Int>
        get() = _seconds

    val gameOver: LiveData<Boolean>
        get() = _gameOver

    val found: LiveData<Int>
        get() = _found

    fun start() {
        viewModelScope.launch {
            val currentCancellationToken = timerCancellationToken!!
            while(!currentCancellationToken.isCancelled) {
                delay(1000)
                if(!currentCancellationToken.isCancelled) {
                    _seconds.value?.let { s ->
                        _seconds.value =  s + 1
                        if (s+1 == 60) {
                            _gameOver.value = true
                            currentCancellationToken.cancel()
                        }
                    }
                }
            }
        }
    }

    fun changeSecond(value: Int) {
        _seconds.value = value
    }

    private fun increaseFound() {
        _found.value?.run {
            _found.value = this + 1
        }
        if(_found.value == needFound) {
            _gameOver.value = true
            timerCancellationToken?.cancel()
            timerCancellationToken = null
        }
    }

    fun initializeGameElementsPosition(level: Int) {
        levelPositions = LevelConfiguration.LEVELS[level - 1]
    }

    fun selectGameElement(view: ImageButton, id: Int) {
        view.setImageResource(images[levelPositions[id]])
        if(firstSelected.value?.first == null) {
            firstSelected.value = view to id
        }
        else {
            secondSelected.value = view to id
            val firstId = levelPositions[firstSelected.value?.second ?: 0]
            val secondId = levelPositions[secondSelected.value?.second ?: 1]
            if(firstId == secondId) {
                firstSelected.value = null to -1
                secondSelected.value = null to -1
                increaseFound()
            }
            else {
                firstSelected.value?.first?.let { firstView ->
                    secondSelected.value?.first?.let { secondView ->
                        firstSelected.value = null to -1
                        secondSelected.value = null to -1
                        viewModelScope.launch {
                            delay(1000)
                            firstView.isEnabled = true
                            secondView.isEnabled = true
                            if (hiddenBackgroundId != -1) {
                                firstView.setImageResource(hiddenBackgroundId)
                                secondView.setImageResource(hiddenBackgroundId)
                            }
                        }
                    }
                }
            }
        }
    }
}