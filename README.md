# Skillvia - Skill Marketplace App

**CS2063 Group Project - Fall 2025**

Skillvia is a native Android application that connects university students who want to learn skills with those who can teach them. The app allows students to browse, request, and rate skills while providing providers with tools to manage their offerings and requests.

## Features

### Completed Features

✅ **Authentication**
- User registration with UNB email validation
- Login/logout functionality
- User profile management

✅ **Skill Management**
- Browse skills with search and category filtering
- View detailed skill information
- Create, edit, and delete skills
- Support for online, in-person, and hybrid delivery types

✅ **Request System**
- Create skill requests with custom messages
- Provider can accept/reject requests
- Request status tracking (PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, REJECTED)
- Mark requests as complete

✅ **Rating & Review System**
- Rate providers after completing a request
- View reviews on skill detail pages
- Average rating calculation and display

✅ **Location Features**
- Google Places Autocomplete for location input
- Distance calculation from user's current location
- Interactive map view with markers
- Location permission handling

✅ **User Interface**
- Material Design 3 components
- Dark mode support
- Professional card-based layout
- Responsive design for different screen sizes

## Requirements

### System Requirements
- Android SDK 24 (Android 7.0) or higher
- Internet connection for Supabase backend
- Location services enabled for location features

### Dependencies
- Jetpack Compose for UI
- Supabase for backend (authentication, database)
- Google Places API for location autocomplete
- Google Maps API for map display
- Kotlinx Serialization for data models

## Setup Instructions

### Prerequisites
1. Android Studio (latest version recommended)
2. Android SDK 24 or higher
3. Google API Key for Places and Maps services
4. Supabase project URL and anon key, Google API Key

### Configuration
Before running the app, you need to configure the following in the source code:

1. **Supabase Credentials** (`app/src/main/java/com/skillvia/app/data/supabase/SupabaseClient.kt`):
   - Replace `YOUR_SUPABASE_URL_HERE` with your Supabase project URL
   - Replace `YOUR_SUPABASE_ANON_KEY_HERE` with your Supabase anon key

2. **Google API Key** (`app/src/main/AndroidManifest.xml`):
   - Replace `YOUR_API_KEY_HERE` with your Google Places/Maps API key

**Note:** For testing, use the provided `app-debug.apk` which includes all necessary credentials.

### Sample Test Accounts

For testing purposes, you can use this account
- Email: `testuser@unb.ca` / Password: test123

Or create new accounts using the sign-up screen.

## Supported API Levels

- **Minimum SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 36 (Android 16)
- **Compile SDK:** 36

### Device Limitations
- Location features require location services to be enabled
- Map view requires Google Play Services
- Internet connection required for all backend operations

## Testing

See [TEST_PLAN.md]for detailed test scenarios.

### Quick Test Flow

1. **Authentication**
   - Sign up with a new UNB email account
   - Login with credentials
   - Verify profile information displays

2. **Browse Skills**
   - View skills list
   - Search for a skill
   - Filter by category
   - View skill details

3. **Create Request**
   - Select a skill
   - Create a request with a message
   - Verify request appears in profile

4. **Provider Actions**
   - Create a new skill
   - Accept a request
   - Mark request as complete

5. **Rating System**
   - Complete a request
   - Rate the provider
   - Verify rating appears on skill

6. **Location Features**
   - Grant location permission
   - Create skill with location
   - View distance calculation
   - Open map view

7. **Dark Mode**
   - Enable dark mode in device settings
   - Verify all screens adapt correctly

## Known Issues
- **No In-App Messaging:** Request coordination messaging is mentioned as "coming soon" in the UI but not yet implemented.
- **No Payment Integration:** Payment processing is not included in this version.
- **No Push Notifications:** Users must manually check for request updates
```

## Technologies Used

- **Kotlin** - Programming language
- **Jetpack Compose** - UI framework
- **Material Design 3** - Design system
- **Supabase** - Backend (Auth + Database)
- **Google Places API** - Location autocomplete
- **Google Maps SDK** - Map display

## Contributors

Sam Porter
Josh Dillon

---

**Note:** This app was developed as a course project for CS2063 - Introduction to Mobile Application Development at the University of New Brunswick.
