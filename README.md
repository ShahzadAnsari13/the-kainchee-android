# ✂️ THE KAINCHEE USER

### Modern Salon & Parlour Booking Application

THE KAINCHEE USER is a production-ready Android application that enables users to discover nearby parlours, browse services, select professional stylists, choose appointment slots, and book grooming services seamlessly.

Built using modern Android development practices with Kotlin, MVVM, Clean Architecture, Hilt, Retrofit, Coroutines, Google Maps, and Location Services.

---

## 🎥 Demo Video

Watch the complete application walkthrough:

👉 https://youtube.com/shorts/mVcTSJBwLGE?si=Kun5t6e6-OtwuU7c

---

## 📱 Application Screenshots

### Login & Authentication

![Login Screen](screenshots/login_screen.jpeg)

![OTP Verification](screenshots/otp_verification.jpeg)

---

### Home & Address Management

![Home Screen](screenshots/home_screen.jpeg)

![Saved Addresses](screenshots/saved_addresses.jpeg)

![Location Picker](screenshots/location_picker.jpeg)

---

### Parlour Discovery

![Parlour Details](screenshots/parlour_details.jpeg)

---

### Service Selection

![Service Selection](screenshots/service_selection.jpeg)

---

### Booking Preview

![Booking Preview](screenshots/booking_preview.jpeg)

---

### Appointment Scheduling

![Stylist Selection](screenshots/stylist_selection.jpeg)

![Slot Selection](screenshots/slot_selection.jpeg)

---

## 🚀 Features

### 🔐 Authentication

- OTP Based Login
- JWT Authentication
- Secure Session Management
- Persistent User Login

### 📍 Location Services

- Current Location Detection
- Google Maps Integration
- Address Selection
- Saved Address Management
- Reverse Geocoding

### 🏪 Parlour Discovery

- Nearby Parlours
- Parlour Details
- Distance Calculation
- Business Information
- Opening & Closing Status

### ✂️ Service Booking

- Category-wise Services
- Multiple Service Selection
- Dynamic Price Calculation
- Total Duration Calculation
- Booking Preview

### 👨‍💼 Stylist Management

- Available Stylists
- Experience Information
- Stylist Selection

### 📅 Appointment Scheduling

- Date Selection
- Dynamic Slot Loading
- Real-Time Availability
- Booking Confirmation

### 💳 Payment Ready Architecture

- Booking Flow Prepared
- Payment Integration Layer Ready
- Order Summary Support

---

## 🏗 Architecture

This project follows **MVVM + Clean Architecture**.

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
│   ├── booking
│   ├── dashboard
│   ├── location
│   ├── parlour
│   ├── service
│   ├── splash
│   ├── common
│   ├── base
│   └── viewmodel
│
└── utils
