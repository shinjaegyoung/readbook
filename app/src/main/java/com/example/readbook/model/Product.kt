package com.example.readbook.model


data class Product(
    var pid: String? = null,
    var pName: String? = null,
    var pPrice: String? = null,
    var pDes: String? = null,
    var user: String? = null,
    var pViewCount: Int? = 0,
    var regDate: Any = Any(),
    var status: String? = null,
    val productImg : HashMap<String, ProductImg> = HashMap()){
    class ProductImg(var pImg: String? = null)
}
