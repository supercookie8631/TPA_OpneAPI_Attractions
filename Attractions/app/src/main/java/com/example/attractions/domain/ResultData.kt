package com.example.attractions.domain

import com.example.attractions.api.ApiException

data class ResultData<T>(
    val total: Int,
    val data: T
) {
    fun apiData(): T {
        if (total == 0) {
            throw ApiException("查無資料")
        } else {
            return data
        }
    }
}
