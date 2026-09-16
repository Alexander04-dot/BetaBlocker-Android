# Beta Blocker - Android

An Android replica of Isla2D's Beta Blocker, a real-time screen content censoring application.

## Features

- **Real-Time Content Censoring**: Monitors screen and applies censoring effects instantly
- **Multiple Censor Styles**: 
  - Pixelate
  - Blur
  - Solid Box
- **Customizable Settings**:
  - Adjust sensitivity levels
  - Choose censor colors
  - Select censor style
- **Background Service**: Runs silently without interrupting user experience
- **Accessibility Integration**: Uses Android's AccessibilityService for comprehensive screen monitoring

## Architecture

### Components

1. **MainActivity** - Main UI for app controls and status
2. **SettingsActivity** - Configuration interface for censor preferences
3. **CensoringAccessibilityService** - Accessibility service for monitoring screen events
4. **CensorOverlayService** - Manages overlay views for censoring visual elements
5. **PreferencesManager** - DataStore-based preference management
6. **CensorEffectRenderer** - Applies visual censoring effects
7. **ContentDetector** - Detects censorable content on screen

### Project Structure

```
BetaBlocker-Android/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/isla2d/betablocker/
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── SettingsActivity.kt
│   │   │   ├── service/
│   │   │   │   ├── CensoringAccessibilityService.kt
│   │   │   │   └── CensorOverlayService.kt
│   │   │   ├── data/
│   │   │   │   └── PreferencesManager.kt
│   │   │   └── utils/
│   │   │       ├── CensorEffects.kt
│   │   │       └── ContentDetector.kt
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── xml/
│   │   │       ├── accessibility_config.xml
│   │   │       ├── data_extraction_rules.xml
│   │   │       └── backup_rules.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
├── .github/
│   └── workflows/
│       └── build.yml
└── README.md
```

## Requirements

- Android SDK 24 (Android 7.0) or higher
- Android Studio Arctic Fox or later
- Kotlin 1.9.0+
- Gradle 8.1.0+

## Dependencies

- AndroidX Core
- AndroidX AppCompat
- Jetpack Compose
- Jetpack Lifecycle
- DataStore Preferences
- AccessibilityService
- WorkManager

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Alexander04-dot/BetaBlocker-Android.git
   ```

2. Open the project in Android Studio

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run on emulator or device:
   ```bash
   ./gradlew installDebug
   ```

## Permissions Required

- `SYSTEM_ALERT_WINDOW` - Display overlays
- `FOREGROUND_SERVICE` - Run background service
- `BIND_ACCESSIBILITY_SERVICE` - Accessibility service
- `INTERNET` - Future cloud features

## Usage

1. Launch the app
2. Go to **Settings > Accessibility** and enable the Beta Blocker service
3. Return to the app to configure censor preferences
4. The service will automatically censor content based on your settings

## Development Roadmap

- [ ] Implement censor effect rendering (pixelate, blur)
- [ ] ML-based content detection
- [ ] Screen recording with censorship applied
- [ ] Custom content filtering rules
- [ ] Performance optimization
- [ ] App whitelisting/blacklisting
- [ ] User gesture controls for quick toggle
- [ ] Cloud sync for settings

## License

MIT License

## Author

Alexander04-dot

## Disclaimer

This is an educational project inspired by Isla2D's Beta Blocker. Use responsibly and in compliance with your device's terms of service.
