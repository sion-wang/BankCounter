package com.sion.bankcounter

sealed class Status {
    object Idle: Status()
    class Processing(val number: Int): Status()
}
