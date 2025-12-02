# Skillvia - Test Plan

## Test Scenarios

### 1. Authentication Flow

#### Test Case 1.1: User Registration
**Steps:**
1. Launch app
2. Tap "Sign Up"
3. Enter name, UNB email, password, university
4. Submit registration

**Expected Result:**
- User is registered successfully
- Redirected to skills list screen
- Profile information is saved

#### Test Case 1.2: User Login
**Steps:**
1. Launch app
2. Enter valid UNB email and password
3. Tap "Login"

**Expected Result:**
- User is authenticated
- Redirected to skills list screen
- User session persists

#### Test Case 1.3: Login Validation
**Steps:**
1. Launch app
2. Enter non-UNB email (e.g., `test@gmail.com`)
3. Tap "Login"

**Expected Result:**
- Error message: "Must use UNB email (@unb.ca)"
- Login button remains disabled or shows error

#### Test Case 1.4: Logout
**Steps:**
1. Login to app
2. Navigate to profile or skills list
3. Tap logout icon

**Expected Result:**
- User is logged out
- Redirected to login screen
- Session is cleared

---

### 2. Skill Browsing Flow

#### Test Case 2.1: View Skills List
**Steps:**
1. Login to app
2. View skills list screen

**Expected Result:**
- Skills are displayed in cards
- Each card shows: title, provider, price, rating
- Cards have proper spacing and elevation

#### Test Case 2.2: Search Skills
**Steps:**
1. On skills list screen
2. Enter search term in search bar
3. View filtered results

**Expected Result:**
- Results filter in real-time
- Only matching skills are displayed
- Search is case-insensitive

#### Test Case 2.3: Filter by Category
**Steps:**
1. On skills list screen
2. Tap a category filter (e.g., "Academic", "Technology")
3. View filtered results

**Expected Result:**
- Only skills in selected category are shown
- Selected filter is highlighted
- "All" filter shows all skills

#### Test Case 2.4: View Skill Details
**Steps:**
1. On skills list screen
2. Tap on a skill card
3. View skill detail screen

**Expected Result:**
- Skill details are displayed correctly
- Provider information is shown
- Location and distance are displayed (if applicable)
- Description is visible
- "Request This Skill" button is present

---

### 3. Request Creation Flow

#### Test Case 3.1: Create Skill Request
**Steps:**
1. View skill detail screen
2. Tap "Request This Skill"
3. Enter message in request form
4. Select delivery preference (if applicable)
5. Tap "Send Request"

**Expected Result:**
- Request is created successfully
- Success message or confirmation is shown
- Request appears in "Skills I've Requested" section
- Status is "PENDING"

#### Test Case 3.2: View My Requests
**Steps:**
1. Navigate to profile screen
2. Scroll to "Skills I've Requested" section

**Expected Result:**
- All user's requests are displayed
- Status badges are visible (PENDING, ACCEPTED, COMPLETED, REJECTED)
- Request details are correct

---

### 4. Provider Flow

#### Test Case 4.1: Create New Skill
**Steps:**
1. Navigate to profile screen
2. Tap "Add Skill"
3. Fill in skill details (title, category, price, description)
4. Select delivery type
5. Add location (if in-person)
6. Tap "Add Skill"

**Expected Result:**
- Skill is created successfully
- Skill appears in "Skills I Offer" section
- Skill appears in public skills list

#### Test Case 4.2: Edit Skill
**Steps:**
1. Navigate to profile screen
2. Find skill in "Skills I Offer"
3. Tap edit icon
4. Modify skill details
5. Save changes

**Expected Result:**
- Skill is updated successfully
- Changes are reflected immediately
- Updated skill appears in public list

#### Test Case 4.3: Delete Skill (No Active Requests)
**Steps:**
1. Navigate to profile screen
2. Find skill with no active requests
3. Tap delete icon
4. Confirm deletion

**Expected Result:**
- Skill is deleted successfully
- Skill is removed from profile and public list
- No errors occur

#### Test Case 4.4: Delete Skill (With Active Requests)
**Steps:**
1. Navigate to profile screen
2. Find skill with PENDING/ACCEPTED requests
3. Tap delete icon

**Expected Result:**
- Error message: "Cannot delete skill with active requests"
- Skill is not deleted
- User is informed of the restriction

#### Test Case 4.5: Accept Request
**Steps:**
1. Navigate to "Manage Requests" screen
2. Find a PENDING request
3. Tap "Accept"

**Expected Result:**
- Request status changes to "ACCEPTED"
- "Mark as Complete" button appears
- Request appears in accepted section

#### Test Case 4.6: Reject Request
**Steps:**
1. Navigate to "Manage Requests" screen
2. Find a PENDING request
3. Tap "Reject"

**Expected Result:**
- Request status changes to "REJECTED"
- Request is removed from pending list
- Requester sees rejected status

#### Test Case 4.7: Mark Request as Complete
**Steps:**
1. Navigate to "Manage Requests" screen
2. Find an ACCEPTED request
3. Tap "Mark as Complete"
4. Confirm completion

**Expected Result:**
- Request status changes to "COMPLETED"
- Request moves to completed section
- Requester can now rate the provider

#### Test Case 4.8: Manage Requests Tabs
**Steps:**
1. Navigate to "Manage Requests"
2. Ensure at least one request is PENDING/ACCEPTED and one is COMPLETED (create sample data if needed)
3. Verify the "Active" tab shows pending + accepted requests
4. Switch to the "Completed" tab
5. Return to the "Active" tab

**Expected Result:**
- Tab labels show accurate counts for Active vs Completed
- Active tab lists only PENDING and ACCEPTED cards with their actions
- Completed tab lists only COMPLETED requests
- Profile "Manage Requests (#)" button count matches the number of active requests

---

### 5. Rating System

#### Test Case 5.1: Rate Provider
**Steps:**
1. Complete a request (as requester)
2. Navigate to "Skills I've Requested"
3. Find COMPLETED request
4. Tap "Rate Provider"
5. Select rating (1-5 stars)
6. Optionally add comment
7. Submit rating

**Expected Result:**
- Rating is submitted successfully
- "Already Rated" badge appears
- Rating is reflected on provider's profile
- Rating is reflected on skill detail page

#### Test Case 5.2: View Reviews
**Steps:**
1. Navigate to skill detail screen
2. Scroll to "Reviews" section

**Expected Result:**
- Reviews are displayed with ratings and comments
- Reviewer names are shown
- Review dates are displayed
- Average rating is calculated correctly

---

### 6. Location Features

#### Test Case 6.1: Location Permission Request
**Steps:**
1. Create a new skill or view skill detail
2. App requests location permission
3. Grant permission

**Expected Result:**
- Permission dialog appears
- User's location is fetched
- Distance calculation works

#### Test Case 6.2: Location Permission Denied
**Steps:**
1. Create a new skill or view skill detail
2. App requests location permission
3. Deny permission

**Expected Result:**
- App handles denial 
- Location features are disabled or show "Enable location" option
- App does not crash

#### Test Case 6.3: Google Places Autocomplete
**Steps:**
1. Create a new skill
2. Select "In-Person Only" or "Both Available"
3. Tap "Select Location" or "Change Location"
4. Search for a location in Places Autocomplete
5. Select a location

**Expected Result:**
- Places Autocomplete opens
- Location is selected and saved
- Coordinates are stored correctly
- Location name is displayed

#### Test Case 6.4: Distance Calculation
**Steps:**
1. Grant location permission
2. View skill detail with location
3. Check distance display

**Expected Result:**
- Distance is calculated correctly
- Distance is displayed in km (e.g., "5.1km away")
- Distance updates if user location changes

#### Test Case 6.5: Map View
**Steps:**
1. View skill detail with location
2. Tap location card or "Tap to view on map"
3. View map screen

**Expected Result:**
- Map opens with marker at skill location
- Marker is correctly positioned
- Location name is displayed
- Back button returns to detail screen

#### Test Case 6.6: Online-Only Skills
**Steps:**
1. Create skill with "Online Only" delivery type
2. View skill detail

**Expected Result:**
- Location field is hidden during creation
- Skill detail shows "Online" badge
- No location or map option is shown

---

### 7. User Interface

#### Test Case 7.1: Dark Mode
**Steps:**
1. Enable dark mode in device settings
2. Launch app
3. Navigate through all screens

**Expected Result:**
- All screens adapt to dark mode
- Text is readable (light text on dark background)
- Cards have appropriate dark backgrounds
- Logo displays correctly with transparent background
- No white backgrounds visible

#### Test Case 7.2: Light Mode
**Steps:**
1. Enable light mode in device settings
2. Launch app
3. Navigate through all screens

**Expected Result:**
- All screens use light theme
- Text is readable (dark text on light background)
- Cards have light grey backgrounds
- Colors are consistent

#### Test Case 7.3: Navigation
**Steps:**
1. Navigate through app using back buttons
2. Use navigation between screens

**Expected Result:**
- Back buttons work correctly
- Navigation is smooth
- No navigation loops or errors
- State is preserved where appropriate

#### Test Case 7.4: Card Display
**Steps:**
1. View any screen with cards (skills list, profile, etc.)

**Expected Result:**
- Cards have rounded corners 
- Cards have light elevation 
- Cards have appropriate background color
- Text is readable
- Icons are properly colored

---

### 8. Edge Cases

#### Test Case 8.1: Empty States
**Steps:**
1. Create new account with no skills
2. View profile screen

**Expected Result:**
- Empty state messages are displayed
- App does not crash
- User can add skills

#### Test Case 8.2: Network Error
**Steps:**
1. Disable internet connection
2. Attempt to login or load data

**Expected Result:**
- Error message is displayed
- App does not crash
- User can retry when connection is restored

#### Test Case 8.3: Invalid Data
**Steps:**
1. Attempt to create skill with empty title
2. Attempt to create request with empty message

**Expected Result:**
- Validation errors are shown
- Forms are not submitted
- User is informed of required fields

---

## Priority Test Scenarios for Instructor


