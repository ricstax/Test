# PawLink — Android Pet Health & Vet Access Prototype

PawLink is an early Android prototype for pet owners to manage dog and cat profiles, health details, veterinarian access and pet-care facilities.

## Live browser preview

GitHub Pages: https://ricstax.github.io/Test/

The browser preview mirrors the main product flow so the UI can be reviewed without installing an APK.

## Current V1 flow

- Splash screen
- Home dashboard
- Separate Dogs and Cats entry points
- Multiple pet profiles per owner
- Add pet form with:
  - pet name
  - dog/cat type
  - breed
  - date of birth
  - weight
  - owner name
  - owner email
  - owner phone
  - address
  - problems / concerns
  - medical issues / allergies / medications / previous operations
- Health overview
- Veterinarian directory with multiple profiles
- Facilities and services directory
- Owner profile section
- Emergency entry point placeholder

## Android project

Native Android code is built with Kotlin and Jetpack Compose.

Current build configuration:

- Android Gradle Plugin 9.3.0
- compileSdk / targetSdk 37
- minSdk 26
- Compose BOM 2026.08.00
- Material 3

Open the repository root in Android Studio and sync Gradle.

## Important prototype limitations

This is a first product/UI iteration. Pet data is not yet connected to a production database. Veterinarian names in the prototype are fictional placeholders and should be replaced by verified professionals before any public launch or real calling functionality is enabled.

## Next product layers

1. Final brand name, logo, typography and colors
2. Secure account registration and login
3. Persistent pet and owner database
4. Vaccination and medication records
5. Medical document uploads
6. Verified veterinarian directory and real phone calling
7. Maps, nearby clinics and emergency services
8. Appointments and reminders
9. Push notifications
10. Backend/API and admin panel
11. iOS version using the same product architecture
