package com.sion.bankcounter.state

sealed class NextState {
    object InitState: NextState()
    data class IncreaseState(val next: Int): NextState()
}
