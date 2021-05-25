package com.sion.bankcounter.state

import com.sion.bankcounter.model.Counter

sealed class MainState {
    object InitState: MainState()
    data class NextState(val next: Int): MainState()
    data class WaitingState(val count: Int): MainState()
    data class CounterState(val counter: Counter): MainState()
}
