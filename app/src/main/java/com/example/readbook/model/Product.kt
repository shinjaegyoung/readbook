package com.example.readbook.model

import java.time.LocalDate.now
import java.util.*

data class Product(
    val pid: String? = null,
    val pName: String? = null,
    val pPrice: Int? = 0,
    val pDes: String? = null,
    val pUser: User? = null,
    val pChat: Int? = 0,
    val pCount: Int? = 0,
    val regDate: String? = null,
    val pImg: String? = null,
)
