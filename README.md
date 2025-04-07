#  Android Location & Google Maps Integration App

##  Project Overview

This Android app demonstrates the integration of **Location Services** and **Google Maps SDK**. It fetches and displays the user's current location using `FusedLocationProviderClient` and marks it on a styled Google Map. The app features a clean UI with location details and a refresh button for updating the location manually.

---

## Features

- Fetch and display current location (latitude and longitude).
- Google Map with a marker showing the user's location.
- Custom-styled Google Map (e.g., Night Mode).
- User-friendly UI with refresh button.
- Proper permission handling and error messages.



##  Setup & Run Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/AbdallahLearn/M2W1D2_location_permission.git
   
2. **Open the project in Android Studio**

3. **Add your Google Maps API key:**

Create a new file in res/values/ called google_maps_api.xml

Add the following content:

xml
Copy
Edit
<resources>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
        YOUR_API_KEY_HERE
    </string>
</resources>
Add this file to .gitignore to prevent exposing your API key.

Build and run the app on an emulator or a real device with location enabled.


##  Permissions Used
  ```bash
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />


 **Author**
Abdullah Al-Juhani
