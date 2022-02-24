package com.gribanskij.scanmaster.model.bars

data class BarDataAddress(
    val type:String = "TYPE_UNKNOWN",
    val addressLines:List<String> = listOf("?")
)
