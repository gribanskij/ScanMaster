package com.gribanskij.scanmaster.model.bars

data class BarDataContactInfo(
    val addresses:List<BarDataAddress> = listOf(BarDataAddress()),
    val emails:List<BarDataEmail> = listOf(BarDataEmail()),
    val name:BarDataPersonName = BarDataPersonName(),
    val organization: String = "?",
    val phones:List<BarDataPhone> = listOf(BarDataPhone()),
    val title:String = "?",
    val urls:List<BarDataUrl> = listOf(BarDataUrl())
)
