package com.example.attractions.Repository

import com.example.attractions.api.RetrofitClient

class AttractionsRepository {
    suspend fun getAttractionsListData(page: Int) =
        RetrofitClient.apiService.getAttractionsList(page).apiData()
}