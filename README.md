# 📱 The Kainchee User

The Kainchee User is a modern salon & parlour booking Android application built using **Kotlin**, **MVVM**, **Clean Architecture**, **Hilt**, **Coroutines**, and **Retrofit**.

Users can discover nearby parlours, browse services, select stylists, schedule appointments, manage addresses, and complete bookings seamlessly.

---

## ✨ Features

- 🔐 JWT Authentication
- 📍 Multiple Address Management
- 🗺️ Current Location Detection
- 🔎 Search Locations
- 💈 Nearby Parlour Discovery
- 🏪 Parlour Details & Services
- 👨‍🔧 Stylist Selection
- 📅 Appointment Scheduling
- ⏰ Time Slot Booking
- 🛒 Booking Preview
- 🧾 Booking History
- 🧭 Google Maps Navigation
- 🔔 Real-time Updates
- 📱 Modern Material UI

---

## 🏗️ Architecture

```text
Presentation (UI)
      │
      ▼
ViewModel
      │
      ▼
UseCases
      │
      ▼
Repository
      │
      ▼
Remote API / Local Storage
```

- MVVM Architecture
- Clean Architecture
- Repository Pattern
- Dependency Injection (Hilt)
- Kotlin Coroutines & Flow

---

## 🛠️ Tech Stack

| Technology | Usage |
|------------|--------|
| Kotlin | Main Language |
| XML | UI Design |
| MVVM | Architecture |
| Hilt | Dependency Injection |
| Retrofit | Networking |
| Coroutines | Async Operations |
| StateFlow | UI State Management |
| Navigation Component | Navigation |
| Glide | Image Loading |
| Google Maps | Location & Navigation |

---

# 📸 Application Screenshots

## 📍 Location Management

<p align="center">
  <img src="screenshots/location_search.png" width="250"/>
  <img src="screenshots/saved_addresses.png" width="250"/>
  <img src="screenshots/map_selection.png" width="250"/>
</p>

---

## 🏠 Home Experience

<p align="center">
  <img src="screenshots/home_screen.png" width="250"/>
  <img src="screenshots/nearby_parlours.png" width="250"/>
</p>

---

## 💈 Parlour Discovery

<p align="center">
  <img src="screenshots/parlour_details.png" width="250"/>
  <img src="screenshots/parlour_closed.png" width="250"/>
</p>

---

## ✨ Service Selection

<p align="center">
  <img src="screenshots/hair_spa_services.png" width="250"/>
  <img src="screenshots/facial_services.png" width="250"/>
  <img src="screenshots/head_massage_services.png" width="250"/>
</p>

---

## 🛒 Booking Preview

<p align="center">
  <img src="screenshots/booking_preview.png" width="250"/>
</p>

---

## 🗺️ Navigation Support

<p align="center">
  <img src="screenshots/parlour_location.png" width="250"/>
  <img src="screenshots/google_maps_navigation.png" width="250"/>
</p>

---

## 📅 Appointment Booking

<p align="center">
  <img src="screenshots/stylist_selection.png" width="250"/>
  <img src="screenshots/time_slot_selection.png" width="250"/>
</p>

---

## 📂 Project Structure

```text
com.thekainchee.user
│
├── data
│   ├── remote
│   ├── repository
│   └── dto
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
│   └── profile
│
├── di
│
└── utils
```

---

## 🚀 Future Enhancements

- Razorpay Integration
- Push Notifications
- Live Booking Tracking
- Chat Support
- Loyalty Rewards
- Favourite Parlours

---

## 👨‍💻 Developer

**Shahzad Ansari**

Android Developer | Kotlin | MVVM | Clean Architecture

Built with ❤️ using Kotlin.
