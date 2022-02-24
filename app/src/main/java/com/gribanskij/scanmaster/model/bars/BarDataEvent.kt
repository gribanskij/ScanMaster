package com.gribanskij.scanmaster.model.bars

data class BarDataEvent(
    val description:String? = "?",
    val endTime: BarDataDateTime = BarDataDateTime(),
    val startTime: BarDataDateTime = BarDataDateTime(),
    val location:String = "?",
    val organizer:String = "?",
    val status:String = "?",
    val summary:String = "?"
)
