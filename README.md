````markdown
# ✂️ THE KAINCHEE USER

A modern salon & parlour booking Android application built using **Kotlin, MVVM, Clean Architecture, Hilt, Retrofit, Coroutines, Navigation Component, and XML UI**.

THE KAINCHEE enables users to discover nearby parlours, explore services, select stylists, book appointments, make secure payments, and manage bookings effortlessly.

---

# 🎥 Demo Video

https://youtube.com/shorts/mVcTSJBwLGE?si=Kun5t6e6-0twuU7c

---

# 📸 Application Preview

## Authentication

| Login | OTP Verification |
|--------|--------|
| <img src="screenshots/login.png" width="250"/> | <img src="screenshots/otp_verification.png" width="250"/> |

---

## Location Management

| Location Search | Saved Addresses |
|--------|--------|
| <img src="screenshots/location_search.png" width="250"/> | <img src="screenshots/saved_addresses.png" width="250"/> |

| Map Selection |
|--------|
| <img src="screenshots/map_selection.png" width="250"/> |

---

## Home Experience

| Home Screen | Nearby Parlours |
|--------|--------|
| <img src="screenshots/home_screen.png" width="250"/> | <img src="screenshots/nearby_parlours.png" width="250"/> |

---

## Parlour Discovery

| Parlour Details | Services |
|--------|--------|
| <img src="screenshots/parlour_details.png" width="250"/> | <img src="screenshots/service_selection.png" width="250"/> |

---

## Booking Flow

| Booking Preview | Stylist Selection |
|--------|--------|
| <img src="screenshots/booking_preview.png" width="250"/> | <img src="screenshots/stylist_selection.png" width="250"/> |

| Slot Selection | Google Maps Directions |
|--------|--------|
| <img src="screenshots/slot_selection.png" width="250"/> | <img src="screenshots/navigation.png" width="250"/> |

---

# 🚀 Features

## 🔐 Authentication
- OTP Based Login
- JWT Authentication
- Secure User Sessions
- Auto Login Support

## 📍 Location Management
- Current Location Detection
- Google Places Search
- Multiple Saved Addresses
- Home / Work / Other Address Types
- Default Address Selection
- Location-Based Recommendations

## 🏠 Home Screen
- Nearby Parlours
- Trending Parlours
- Trending Services
- Category Based Filtering
- Dynamic Content Based on User Location

## 💈 Parlour Discovery
- Detailed Parlour Profiles
- Verified Parlour Badge
- Open / Closed Status
- Working Hours
- Distance Calculation
- Service Categories
- Directions Support

## ✨ Service Booking
- Browse Services by Category
- View Price & Duration
- Service Descriptions
- Add / Remove Services
- Dynamic Booking Cart

## 👨‍💼 Stylist Selection
- Select Preferred Stylist
- Experience Details
- Availability Based Booking
- Dynamic Slot Assignment

## 📅 Appointment Scheduling
- Date Selection
- Available Slot Selection
- Real-Time Slot Availability
- Multi-Service Duration Calculation
- Conflict-Free Scheduling

## 🛒 Booking Preview
- Selected Services Summary
- Total Duration
- Total Price
- Remove Services
- Appointment Review

## 💳 Payments
- Razorpay Integration
- Secure Payment Processing
- Payment Verification
- Booking Confirmation

## 📖 Booking Management
- Upcoming Bookings
- Booking Details
- Booking History
- Booking Status Tracking
- Cancel Booking
- Reschedule Booking

## 🗺️ Maps & Navigation
- Google Maps Integration
- Open Directions
- Distance Tracking
- Turn-by-Turn Navigation

---

# 🏗️ Architecture

The project follows **Clean Architecture + MVVM** principles.

```text
Presentation Layer
│
├── Activities
├── Fragments
├── ViewModels
├── Adapters
├── UI State Management
└── Navigation
│
Domain Layer
│
├── UseCases
├── Repository Contracts
└── Domain Models
│
Data Layer
│
├── Repository Implementations
├── Remote APIs
├── DTOs
├── Mappers
└── Data Sources
│
Dependency Injection
│
├── AppModule
├── NetworkModule
└── RepositoryModule
```

---

# 📂 Project Structure

```text
com.thekainchee.user
│
├── data
│   ├── remote
│   │   ├── api
│   │   ├── dto
│   │   └── interceptor
│   │
│   ├── mapper
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── presentation
│   ├── auth
│   ├── home
│   ├── location
│   ├── parlour
│   ├── booking
│   ├── profile
│   ├── common
│   └── adapter
│
├── di
│
├── utils
│
└── MainApplication.kt
```

---

# 🛠️ Tech Stack

### Language
- Kotlin

### UI
- XML
- Material Components

### Architecture
- MVVM
- Clean Architecture

### Dependency Injection
- Hilt

### Networking
- Retrofit
- OkHttp

### Async Programming
- Coroutines
- Flow
- StateFlow
- SharedFlow

### Navigation
- Navigation Component
- Safe Args

### Maps & Location
- Google Maps SDK
- Google Places API
- Fused Location Provider

### Payments
- Razorpay Payment Gateway

### Backend
- Node.js
- Express.js
- MongoDB

---

# 🌐 Backend Repository

https://github.com/ShahzadAnsari13/the-kainchee-backend

---

# 🔥 Highlights

- Production-Ready Android Architecture
- Startup-Level Booking Platform
- Dynamic Appointment Scheduling
- Multi-Service Booking Flow
- Stylist-Based Slot Management
- Location-Aware Parlour Discovery
- Google Maps Integration
- Razorpay Payment Integration
- Clean, Scalable, Maintainable Codebase
- Interview-Ready Project Structure

---

# 🚀 Future Enhancements

- Push Notifications
- Coupon & Discount System
- Reviews & Ratings
- Favourite Parlours
- Loyalty Rewards
- Wallet Integration
- Referral Program

---

# 👨‍💻 Developer

**Shahzad Ansari**

Android Developer | Kotlin | MVVM | Clean Architecture | Hilt | Retrofit | Coroutines

### Connect

- LinkedIn: https://www.linkedin.com/in/shahzad-ansari-306345363
- LeetCode: https://leetcode.com/ShahzadAnsari13
- GitHub: https://github.com/ShahzadAnsari13

---

⭐ If you found this project interesting, consider giving it a star.
````
