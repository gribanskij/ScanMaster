package com.gribanskij.scanmaster.model.bars

data class BarDataEmail (
    val emailAddress:String = "?",
    val body:String = "?",
    val subject:String = "?",
    val type:String = "TYPE_UNKNOWN"
        )