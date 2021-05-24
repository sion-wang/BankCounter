package com.sion.bankcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel: ViewModel() {
    private val idles: ArrayList<Counter> = arrayListOf()
    private val waitings: ArrayList<Int> = arrayListOf()
    private var next = 1

    private val _counterStatus = MutableLiveData<Counter>()
    val counterStatus: LiveData<Counter> = _counterStatus

    private val _waitingStatus = MutableLiveData<Int>()
    val waitingStatus: LiveData<Int> = _waitingStatus

    private val _nextStatus = MutableLiveData<Int>()
    val nextStatus: LiveData<Int> = _nextStatus

    init {
        repeat(5) { idles.add(Counter(id = it)) }
        viewModelScope.launch(Dispatchers.Default) {
            while(true) {
                if (waitings.isNotEmpty() && idles.isNotEmpty()) {
                    doJob(idles.removeAt(0), waitings.removeAt(0))
                }
                delay(100)
            }
        }
    }

    fun next() {
        waitings.add(next++)
        _nextStatus.postValue(next)
        _waitingStatus.postValue(waitings.size)
    }

    private fun doJob(counter: Counter, number: Int) {
        _waitingStatus.postValue(waitings.size)
        counter.status = Status.Processing(number)
        _counterStatus.postValue(counter)

        viewModelScope.launch(Dispatchers.Default) {
            val duration = Random.nextLong(500, 1500)
            delay(duration)
            counter.processed.add(number)
            counter.status = Status.Idle
            idles.add(counter)
            _counterStatus.postValue(counter)
        }
    }
}