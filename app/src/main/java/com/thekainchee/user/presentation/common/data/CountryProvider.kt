package com.thekainchee.user.presentation.common.data

import com.thekainchee.user.presentation.common.model.Country

object CountryProvider {
    fun getCountries() : List<Country>{
        return listOf(Country("India", "+91", "🇮🇳"),
            Country("United States", "+1", "🇺🇸"),
            Country("United Kingdom", "+44", "🇬🇧"),
            Country("Canada", "+1", "🇨🇦"),
            Country("Australia", "+61", "🇦🇺"),
            Country("Germany", "+49", "🇩🇪"),
            Country("France", "+33", "🇫🇷"),
            Country("UAE", "+971", "🇦🇪"),
            Country("Saudi Arabia", "+966", "🇸🇦"),
            Country("Singapore", "+65", "🇸🇬"))
    }
}