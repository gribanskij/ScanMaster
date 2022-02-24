package com.gribanskij.scanmaster.model.bars

data class BarDataDateTime(
    val day:Int? = null,
    val hours:Int? = null,
    val minutes:Int? = null,
    val month:Int? = null,
    val seconds:Int? = null,
    val year:Int? = null,
    val isUtc:Boolean? = null
)
