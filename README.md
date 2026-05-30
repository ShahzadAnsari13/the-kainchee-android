# ✂️ THE KAINCHEE USER

A modern Android salon & parlour booking application that enables users to discover nearby parlours, explore services, select stylists, choose appointment slots, and book grooming services seamlessly.

Built with modern Android development practices using **Kotlin, MVVM, Clean Architecture, Hilt, Retrofit, Coroutines, Room Database, Google Maps, and Navigation Component**.

---

## 📱 Features

### 🔐 Authentication
- OTP Based Login
- JWT Authentication
- Secure Session Management
- Persistent User Login

### 📍 Location Management
- Current Location Detection
- Google Places Search
- Save Multiple Addresses
- Home / Work / Other Address Types
- Default Address Selection
- Location Switching

### 🏪 Parlour Discovery
- Nearby Parlours
- Trending Parlours
- Category Based Filtering
- Distance Based Search
- Verified Parlour Listings
- Real-Time Availability

### ✂️ Service Booking
- Browse Services By Category
- Service Details & Pricing
- Dynamic Cart Management
- Multi-Service Selection
- Total Price Calculation
- Total Duration Calculation

### 👨‍🔧 Stylist Selection
- View Available Stylists
- Experience Information
- Stylist Based Booking

### 📅 Appointment Scheduling
- Date Selection
- Available Slot Selection
- Dynamic Slot Availability
- Appointment Summary

### 🗺️ Navigation & Maps
- Google Maps Integration
- Parlour Location View
- Turn-By-Turn Directions
- Distance Calculation

### 📋 Booking Management
- Booking Preview
- Appointment Confirmation
- Upcoming Bookings
- Booking Tracking

### 🎨 User Experience
- Empty State Handling
- Error State Handling
- Loading States
- Responsive UI
- Smooth Navigation Flow

---

# 🏗️ Architecture

The application follows **MVVM + Clean Architecture** principles with clear separation of concerns.

```text
Presentation Layer
        ↓
Domain Layer
        ↓
Data Layer
```

---

## 📂 Project Structure

```text
com.thekainchee.user
│
├── data
│   ├── local
│   ├── location
│   ├── mapper
│   ├── remote
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── di
│   ├── DatabaseModule
│   ├── NetworkModule
│   └── RepositoryModule
│
├── presentation
│   ├── auth
│   ├── base
│   ├── booking
│   ├── common
│   ├── dashboard
│   ├── location
│   │   ├── adapter
│   │   ├── fragment
│   │   ├── model
│   │   ├── state
│   │   └── viewmodel
│   │
│   ├── parlour
│   ├── service
│   └── splash
│
├── utils
│
└── MainActivity
```

---

# 📚 Layer Explanation

## Presentation Layer

Responsible for UI rendering and user interaction.

### Contains

- Activities
- Fragments
- ViewModels
- UI States
- Adapters
- Navigation Handling

### Features

```text
presentation
├── auth
├── booking
├── dashboard
├── location
├── parlour
├── service
├── splash
├── common
└── base
```

---

## Domain Layer

Contains pure business logic.

### Contains

- Domain Models
- Repository Contracts
- Use Cases

```text
domain
├── model
├── repository
└── usecase
```

---

## Data Layer

Handles all data sources.

### Contains

- Retrofit APIs
- DTO Models
- Room Database
- Repository Implementations
- Data Mappers
- Location Services

```text
data
├── local
├── location
├── mapper
├── remote
└── repository
```

---

## Dependency Injection

Hilt is used for dependency management.

```text
di
├── DatabaseModule
├── NetworkModule
└── RepositoryModule
```

### Responsibilities

- Retrofit Initialization
- OkHttp Configuration
- Room Database Provision
- Repository Injection
- Singleton Management

---

# 🚀 Tech Stack

## Language

- Kotlin

## Architecture

- MVVM
- Clean Architecture

## Dependency Injection

- Hilt

## Networking

- Retrofit
- OkHttp

## Async Programming

- Kotlin Coroutines
- Flow

## Local Storage

- Room Database
- DataStore

## Maps & Location

- Google Maps SDK
- Google Places API
- Fused Location Provider

## UI

- XML
- Material Design 3
- Navigation Component

---

# 🔄 Complete User Flow

```text
Splash
   ↓
Authentication
   ↓
Location Selection
   ↓
Home Screen
   ↓
Nearby / Trending Parlours
   ↓
Parlour Details
   ↓
Service Selection
   ↓
Booking Preview
   ↓
Stylist Selection
   ↓
Date Selection
   ↓
Time Slot Selection
   ↓
Payment
   ↓
Booking Confirmation
   ↓
Upcoming Bookings
```

---

# 📍 Location Flow

```text
Search Location
        ↓
Select Location
        ↓
Save Address
        ↓
Set Default Address
        ↓
Discover Nearby Parlours
```

---

# ✂️ Service Booking Flow

```text
Select Parlour
        ↓
Explore Services
        ↓
Add Services
        ↓
View Booking Preview
        ↓
Calculate Price & Duration
        ↓
Proceed To Appointment
```

---

# 📅 Appointment Flow

```text
Select Stylist
        ↓
Choose Date
        ↓
Choose Time Slot
        ↓
Review Appointment
        ↓
Proceed To Payment
```

---

# 🛡️ Clean Architecture Benefits

- Scalable Codebase
- Testable Business Logic
- Easy Maintenance
- Separation Of Concerns
- Feature Based Development
- Better Team Collaboration
- Independent Layer Testing

---

# 🎯 Key Highlights

- Multi-Service Booking System
- Stylist Based Appointment Scheduling
- Google Maps Integration
- Dynamic Slot Availability
- Address Management System
- Feature Based Modular UI Structure
- MVVM + Clean Architecture
- Hilt Dependency Injection
- Production Ready Project Structure

---

# 🔮 Future Enhancements

- Razorpay Payment Gateway
- Booking Cancellation
- Booking Rescheduling
- Push Notifications
- Wallet System
- Loyalty Rewards
- Review & Rating System
- Offers & Coupons
- Dark Mode Support
- Real-Time Booking Updates

---

# 🧑‍💻 Developed With

- Kotlin
- MVVM
- Clean Architecture
- Hilt
- Retrofit
- Coroutines
- Room
- Google Maps SDK
- Navigation Component
- Material Design

---

## ⭐ Project Goal

THE KAINCHEE USER aims to provide a seamless salon booking experience by connecting users with nearby parlours, simplifying appointment scheduling, and delivering a modern mobile booking experience.

---
```
Made with ❤️ using Kotlin & Modern Android Development Practices
```
