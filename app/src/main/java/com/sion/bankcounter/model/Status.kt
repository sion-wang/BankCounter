package com.sion.bankcounter.model

sealed class Status {
    object Idle: Status()
    class Processing(val number: Int): Status()
}
