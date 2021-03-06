package com.sion.bankcounter.model

data class Counter(
    val id: Int = -1,
    var status: Status = Status.Idle,
    var processed: ArrayList<Int> = arrayListOf()
)
