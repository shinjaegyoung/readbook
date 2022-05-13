package com.example.readbook.model

data class Product(
    val pid: String? = null,
    val pName: String? = null,
    val pPrice: Int? = 0,
    val pDes: String? = null,
    val user: User? = null,
    val pChatCount: Int? = 0,
    val pViewCount: Int? = 0,
    val regDate: String? = null,
    val pImg: List<String>? = null,
    val status: String? = null,
)
