package com.sion.bankcounter.state

import com.sion.bankcounter.model.Counter

sealed class WaitingState {
    object InitState: WaitingState()
    data class ModifiedState(val count: Int): WaitingState()
}