# THE KAINCHEE - Android App

This is the Android application for THE KAINCHEE, a salon/parlour booking system.

## Tech Stack

- Kotlin
- MVVM Architecture
- Clean Architecture
- Hilt (Dependency Injection)
- Retrofit + OkHttp
- Coroutines
- DataStore
- Google Maps & Location Services

## Features

- JWT-based authentication with token handling using OkHttp interceptor
- Logging interceptor for debugging API requests and responses
- OTP verification and secure token storage using DataStore
- Backend API integration using Retrofit
- Current location fetching using Fused Location Provider (latitude & longitude)
- Address resolution using Geocoder
- Place search using Google Places API
- Google Maps integration inside the app

## Booking & Location Logic

- Fetches user’s current location if no default address is available
- Stores user location in local database for future use
- If a default address already exists, it is prioritized over live location
- Updates local storage with latest location when required

## Architecture

- Follows Clean Architecture:
  - data/
  - domain/
  - presentation/
- MVVM pattern with proper separation of concerns
- Dependency Injection using Hilt

## Navigation

- Manual fragment navigation for authentication flow
- Navigation Component used for location-based flows
- Defined navigation graph inside `res/navigation`

## Networking

- Retrofit for API calls
- OkHttp interceptor for automatic token injection
- Logging interceptor for monitoring API responses

## Backend

This Android app is integrated with a custom backend system developed by me using Node.js, which handles authentication, booking logic, payments, and analytics.

Backend Repository: https://github.com/ShahzadAnsari13/the-kainchee-backend

## Status

The app is currently under development and is fully integrated with backend APIs.
