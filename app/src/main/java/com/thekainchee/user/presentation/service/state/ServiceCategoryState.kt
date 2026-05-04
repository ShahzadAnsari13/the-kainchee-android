package com.thekainchee.user.presentation.service.state

import com.thekainchee.user.presentation.service.model.ServiceCategory

sealed class ServiceCategoryState{
    object Idle : ServiceCategoryState()
    object Loading : ServiceCategoryState()
    data class Success(val data: List<ServiceCategory>) : ServiceCategoryState()
    object Empty : ServiceCategoryState()
    data class Error(val message : String) : ServiceCategoryState()
}