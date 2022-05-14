package com.example.readbook.model

import android.net.Uri

data class Product(
    var pid: String? = null,
    var pName: String? = null,
    var pPrice: Int? = 0,
    var pDes: String? = null,
    var user: String? = null,
    var pViewCount: Int? = 0,
    var regDate: Any = Any(),
    var pImg: MutableList<Uri>? = null,
    var status: String? = null,
)
