package com.thekainchee.user.presentation.parlour.state

import com.thekainchee.user.presentation.parlour.model.ParlourDetailedUI
import com.thekainchee.user.presentation.parlour.model.ServiceCategory

sealed class ServiceCategoryState{
    object Idle : ServiceCategoryState()
    object Loading : ServiceCategoryState()
    data class Success(val data: List<ServiceCategory>) : ServiceCategoryState()
    object Empty : ServiceCategoryState()
    data class Error(val message : String) : ServiceCategoryState()
}
