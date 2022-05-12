package com.example.readbook.model

class ProductList (val products : HashMap<String, Product> = HashMap()
){
    class Product(
        val pid: String? = null,
        val pName: String? = null,
        val pPrice: Int? = 0,
        val pDes: String? = null,
        val seller: User? = null,
        val pChat: Int? = 0,
        val pCount: Int? = 0,
        val regDate: String? = null,
        val pImg: String? = null)
}