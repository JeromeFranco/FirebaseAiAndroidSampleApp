# AI Chatbot Android Sample

This is a sample Android application demonstrating how to build an AI chatbot using Firebase AI.

## Features

This project showcases the following Firebase AI capabilities:

* **Content Generation**: Generating text based on prompts.
* **Multi-modality**: Processing and understanding different types of input (e.g., text, images - if
  applicable to your demo).
* **Streamed Content**: Handling real-time streaming of responses from the AI model.
* **Multi-turn chat**: Maintaining context and engaging in extended conversations.
* **Tool use (Function Calling)**: Enabling the AI to call external functions or APIs to retrieve
  information or perform actions.

## Setup Guide

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/FirebaseAiAndroidSampleApp.git
   cd FirebaseAiAndroidSampleApp
   ```
2. **Add Firebase to your project:**
    * Go to the [Firebase console](https://console.firebase.google.com/).
    * Create a new Firebase project or select an existing one.
    * Register your Android app with the project:
        * Use `co.app.aichatbotandroid` (or your actual package name) as the Android package name.
        * Download the `google-services.json` file.
    * **Important:** Place the downloaded `google-services.json` file in the `app` directory of this
      project (`FirebaseAiAndroidSampleApp/app/google-services.json`). This file is not included in
      the repository for security reasons.
3. **Build and run the app:**
    * Open the project in Android Studio.
    * Let Gradle sync and download dependencies.
    * Run the app on an emulator or a physical device.

## Contributing

Feel free to fork this repository, make changes, and submit pull requests. If you find any issues or
have suggestions for improvements, please open an issue.
