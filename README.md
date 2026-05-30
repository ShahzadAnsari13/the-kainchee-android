# ✂️ THE KAINCHEE USER

### Modern Salon & Parlour Booking Application

THE KAINCHEE USER is a production-ready Android application that enables users to discover nearby parlours, explore services, select stylists, choose appointment slots, and book grooming services seamlessly.

Built using modern Android development practices with **Kotlin, MVVM, Clean Architecture, Hilt, Retrofit, Coroutines, Room Database, Google Maps SDK, Places API, and Navigation Component**.

---

## 🎥 Demo Video

Watch the complete application walkthrough:

👉 **[Watch Demo Video](YOUR_YOUTUBE_OR_DRIVE_LINK_HERE)**

---

## 📱 Screenshots

### Authentication
- OTP Login
- Secure Authentication Flow

### Location Management
- Search Location
- Current Location Detection
- Save Address
- Home / Work / Other Address Types

### Home Screen
- Nearby Parlours
- Trending Parlours
- Trending Services
- Category Filtering

### Parlour Details
- Parlour Information
- Open / Closed Status
- Working Hours
- Google Maps Integration
- Direction Support

### Service Booking
- Service Categories
- Dynamic Service Selection
- Price Calculation
- Duration Calculation

### Appointment Scheduling
- Stylist Selection
- Date Selection
- Time Slot Selection
- Appointment Summary

### Booking Preview
- Selected Services
- Total Price
- Total Duration
- Dynamic Booking Cart

---

# 🚀 Features

## 🔐 Authentication

- OTP Based Login
- JWT Authentication
- Secure Session Handling
- Persistent Login Sessions

---

## 📍 Location Management

- Current Location Detection
- Google Places Search
- Multiple Saved Addresses
- Home / Work / Other Labels
- Default Address Selection
- Dynamic Location Switching

---

## 🏪 Parlour Discovery

- Nearby Parlours
- Trending Parlours
- Category Based Filtering
- Distance Based Search
- Verified Parlour Listings
- Real-Time Availability

---

## ✂️ Service Management

- Browse Services By Category
- Dynamic Service Selection
- Add / Remove Services
- Real-Time Price Calculation
- Duration Calculation

---

## 👨‍🔧 Stylist Selection

- Multiple Stylists
- Experience Information
- Stylist Based Scheduling

---

## 📅 Appointment Scheduling

- Date Selection
- Time Slot Selection
- Slot Availability Handling
- Appointment Summary

---

## 🗺️ Navigation Support

- Google Maps Integration
- Parlour Location
- Turn By Turn Navigation
- Distance Calculation

---

## 📋 Booking System

- Booking Preview
- Dynamic Cart
- Appointment Confirmation
- Upcoming Bookings
- Booking Tracking

---

## 🎨 User Experience

- Empty States
- Error States
- Loading States
- Smooth Navigation
- Responsive Design

---

# 🏗️ Architecture

The application follows **MVVM + Clean Architecture** principles with complete separation of concerns.

```text
Presentation Layer
        ↓
Domain Layer
        ↓
Data Layer
```

---

# 📂 Project Structure

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

# 📚 Layer Breakdown

## Presentation Layer

Responsible for:

- Activities
- Fragments
- ViewModels
- UI States
- Navigation
- Adapters

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

Responsible for business logic.

Contains:

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

Responsible for data management.

Contains:

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

Implemented using Hilt.

```text
di
├── DatabaseModule
├── NetworkModule
└── RepositoryModule
```

Provides:

- Retrofit
- OkHttp
- Repositories
- Room Database
- Singleton Dependencies

---

# 🛠️ Tech Stack

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

## Asynchronous Programming

- Kotlin Coroutines
- Kotlin Flow

## Local Storage

- Room Database
- DataStore

## Maps & Location

- Google Maps SDK
- Google Places API
- Fused Location Provider

## UI

- XML
- Material Design
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
Nearby Parlours
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

# ✂️ Booking Flow

```text
Select Parlour
        ↓
Explore Services
        ↓
Add Services
        ↓
Booking Preview
        ↓
Select Stylist
        ↓
Choose Date
        ↓
Choose Time Slot
        ↓
Proceed To Payment
```

---

# 🎯 Highlights

- Feature Based Architecture
- MVVM + Clean Architecture
- Hilt Dependency Injection
- Google Maps Integration
- Google Places Search
- Dynamic Slot Booking System
- Stylist Based Scheduling
- Multi-Service Booking
- Location Based Parlour Discovery
- Production Ready Folder Structure

---

# 🔮 Upcoming Features

- Razorpay Payment Integration
- Booking Cancellation
- Booking Rescheduling
- Push Notifications
- Wallet System
- Loyalty Rewards
- Reviews & Ratings
- Coupons & Offers
- Dark Theme
- Real-Time Booking Updates

---

# ⚙️ Setup

### Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/TheKaincheeUser.git
```

### Open Project

```bash
Android Studio Hedgehog+
```

### Add API Keys

```properties
MAPS_API_KEY=YOUR_KEY
PLACES_API_KEY=YOUR_KEY
```

### Build

```bash
Sync Gradle
Run Application
```

---

# 🧑‍💻 Developed With

- Kotlin
- MVVM
- Clean Architecture
- Hilt
- Retrofit
- Coroutines
- Flow
- Room Database
- DataStore
- Google Maps SDK
- Google Places API
- Material Design

---

## ⭐ Project Goal

THE KAINCHEE USER aims to simplify salon appointment booking through location-based discovery, intelligent scheduling, seamless service selection, and a modern Android user experience.

---

### Made with ❤️ using Kotlin and Modern Android Development Practices
