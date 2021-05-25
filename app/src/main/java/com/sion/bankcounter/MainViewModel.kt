package com.sion.bankcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sion.bankcounter.intent.MainIntent
import com.sion.bankcounter.model.Counter
import com.sion.bankcounter.model.Status
import com.sion.bankcounter.state.CounterState
import com.sion.bankcounter.state.MainState
import com.sion.bankcounter.state.NextState
import com.sion.bankcounter.state.WaitingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel: ViewModel() {
    private val idles: ArrayList<Counter> = arrayListOf()
    private val waitings: ArrayList<Int> = arrayListOf()
    private var next = 1

    val mainIntent = Channel<MainIntent>(Channel.UNLIMITED)

    private val _counterState = MutableLiveData<CounterState>(CounterState.InitState)
    val counterState: LiveData<CounterState> = _counterState
    private val _waitingState = MutableLiveData<WaitingState>(WaitingState.InitState)
    val waitingState: LiveData<WaitingState> = _waitingState
    private val _nextState = MutableLiveData<NextState>(NextState.InitState)
    val nextState: LiveData<NextState> = _nextState



    init {
        repeat(5) { idles.add(Counter(id = it)) }
        handleIntent()
        activateJob()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            mainIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.NextClicked -> next()
                }
            }
        }
    }

    private fun activateJob() {
        viewModelScope.launch(Dispatchers.Default) {
            while(true) {
                if (waitings.isNotEmpty() && idles.isNotEmpty()) {
                    doJob(idles.removeAt(0), waitings.removeAt(0))
                }
                delay(100)
            }
        }
    }

    private fun next() {
        waitings.add(next++)
        _nextState.value = NextState.IncreaseState(next)
        _waitingState.value = WaitingState.ModifiedState(waitings.size)
    }

    private fun doJob(counter: Counter, number: Int) {
        println("@@doJob: $counter, $number")
        _waitingState.postValue(WaitingState.ModifiedState(waitings.size))
        counter.status = Status.Processing(number)
        _counterState.postValue(CounterState.ModifiedState(counter))

        viewModelScope.launch(Dispatchers.Default) {
            val duration = Random.nextLong(500, 1500)
            delay(duration)
            counter.processed.add(number)
            counter.status = Status.Idle
            idles.add(counter)
            _counterState.postValue(CounterState.ModifiedState(counter))
        }
    }
}