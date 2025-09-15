# 📱 Caller Name Speaker

**Caller Name Speaker** is an Android application that announces the name or number of incoming callers and SMS senders. It helps users identify calls and messages without looking at their phones, making it especially useful when driving, exercising, or multitasking.

---

## 🚀 Features

- 📞 **Caller Name Announcement**
  - Announces the name or number of incoming callers.
  - Supports both contact names and unknown numbers.

- 💬 **SMS Content Reader**
  - Reads aloud the content of incoming SMS messages.
  - Allows hands-free reading of messages.

- 🔊 **Customizable Settings**
  - Adjust the volume and pitch of the announcer's voice.
  - Set delay times between announcements.
  - Enable or disable announcements during silent or vibrate modes.

- 🔁 **Repeat Mode**
  - Option to repeat the caller name or SMS content a specified number of times.

- 🛠️ **User-Friendly Interface**
  - Simple and intuitive design for easy navigation and setup.

---

## 💡 Technologies Used

- 🧠 Kotlin — Core language for Android development
- 📢 Android Text-to-Speech API — For announcing caller names and SMS content
- ⚙️ Android BroadcastReceiver — To detect incoming calls and SMS messages
- 🔄 Android Services — For background operations and continuous monitoring
- ☁️ Firebase Firestore — To store and manage the blacklist of blocked numbers in real-time

---

## 🛠️ How to Run

### ✅ Prerequisites

- Android Studio Giraffe or newer
- Android SDK 33+
- JDK 17+
- Text-to-Speech engine installed on the device

### 📦 Steps

```bash
# Clone the project
git clone https://github.com/Truongson-erorr/CallerNameSpeaker.git
cd CallerNameSpeaker

# Open the project in Android Studio
# Sync Gradle and build the project

# Run on an emulator or physical device.

