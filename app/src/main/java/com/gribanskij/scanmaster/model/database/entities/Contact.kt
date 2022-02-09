package com.gribanskij.scanmaster.model.database.entities


data class Contact (
    val title:String = "?",
    val organization:String? = null,
    val adressid:Int? = null,
    val emailid:Int? = null,
    val nameid:Int? = null,
    val phoneid:Int? = null,
    val urlid:Int? = null,
    val list:List<Any?>? = mutableListOf(1,null,null,"fgshsdh")
    )