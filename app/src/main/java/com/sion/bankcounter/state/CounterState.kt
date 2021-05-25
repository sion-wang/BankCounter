package com.sion.bankcounter.state

import com.sion.bankcounter.model.Counter

sealed class CounterState {
    object InitState: CounterState()
    data class ModifiedState(val counter: Counter): CounterState()
}
