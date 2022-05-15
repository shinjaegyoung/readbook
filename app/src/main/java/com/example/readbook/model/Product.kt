package com.example.readbook.model


data class Product(
    var pid: String? = "",
    var pName: String? = "",
    var pPrice: String = "",
    var user: String? = "",
    var pDes: String? = "",
    var pViewCount: Int? = 0,
    var regDate: Any = Any(),
    var status: String? = "",
    val productImg : HashMap<String, ProductImg> = HashMap()){
    class ProductImg(var pImg: String? = "")
}
