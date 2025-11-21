# Getting Started - Auto Rename File Service
## Quick Setup Guide for Developers

**Last Updated:** November 21, 2025  
**Project Status:** Phase 2 - CHUNK 2 Complete (100%)

---

## 👥 For New Developers Joining the Project

### Prerequisites

Before you begin, make sure you have:

```
✅ Android Studio Hedgehog (2023.1.1) or later
✅ JDK 17 or later
✅ Git installed and configured
✅ GitHub account
✅ Minimum 8GB RAM (16GB recommended)
✅ At least 5GB free disk space
```

---

## 🚀 Initial Setup (First Time Only)

### Step 1: Get Access to the Repository

**Option A: If you're a collaborator (Recommended)**
- Kai will add you as a collaborator on GitHub
- Accept the invitation email from GitHub
- You'll have push access to the repo

**Option B: If using fork workflow**
- Fork the repository to your GitHub account
- You'll work on your fork and create PRs

---

### Step 2: Clone the Repository

Open your terminal/command prompt and run:

```bash
# Navigate to where you want the project
cd C:\Users\YourName\Desktop\Projects
# or on Mac/Linux: cd ~/Projects

# Clone the repository
git clone https://github.com/AsakuraKai/Conversion.git

# Navigate into the project
cd Conversion
```

**Expected output:**
```
Cloning into 'Conversion'...
remote: Enumerating objects: 150, done.
remote: Counting objects: 100% (150/150), done.
remote: Compressing objects: 100% (100/100), done.
Receiving objects: 100% (150/150), 50.00 KiB | 2.50 MiB/s, done.
```

---

### Step 3: Switch to the Development Branch

All active development is happening on the `kai` branch:

```bash
# Check available branches
git branch -a

# Switch to kai branch
git checkout kai

# Verify you're on the correct branch
git branch
# Should show: * kai
```

---

### Step 4: Create Your Personal Development Branch

**For Kai:**
```bash
git checkout -b kai-dev
git push -u origin kai-dev
```

**For Sokchea:**
```bash
git checkout -b sokchea-dev
git push -u origin sokchea-dev
```

**For other developers:**
```bash
git checkout -b <yourname>-dev
git push -u origin <yourname>-dev
```

---

### Step 5: Open Project in Android Studio

1. **Launch Android Studio**

2. **Open the Project:**
   - Click "Open"
   - Navigate to the `Conversion` folder
   - Click "OK"

3. **Wait for Gradle Sync:**
   - Android Studio will automatically sync Gradle
   - This may take 5-10 minutes on first run
   - Dependencies will be downloaded

4. **Trust the Project:**
   - If prompted, click "Trust Project"

**Expected Gradle sync output:**
```
BUILD SUCCESSFUL in 2m 30s
```

---

### Step 6: Verify Setup

Run these commands to ensure everything works:

```bash
# Build the project
./gradlew build
# On Windows: gradlew.bat build

# Run tests
./gradlew test
# On Windows: gradlew.bat test

# Clean build (if issues occur)
./gradlew clean build
```

**Expected output:**
```
BUILD SUCCESSFUL in 45s
```

---

### Step 7: Read Your Task Guide

**For Kai (Backend Developer):**
```bash
# Open and read
KAI_TASKS.md
```

**For Sokchea (Frontend Developer):**
```bash
# Open and read
SOKCHEA_TASKS.md
```

**Everyone should also read:**
```bash
WORK_DIVISION.md  # Collaboration workflow
README.md         # Project overview
```

---

## 📂 Project Structure Overview

After cloning, you'll see this structure:

```
Conversion/
├── .git/                           # Git repository data
├── .gradle/                        # Gradle build cache
├── .idea/                          # Android Studio settings
├── app/                            # Main application module
│   ├── build.gradle.kts           # App-level build config
│   └── src/
│       ├── main/
│       │   ├── java/com/example/conversion/
│       │   │   ├── data/          # Data layer (Kai's work)
│       │   │   ├── di/            # Dependency Injection (Kai's work)
│       │   │   ├── domain/        # Domain layer (Kai's work)
│       │   │   ├── presentation/  # Presentation layer (Sokchea's work)
│       │   │   ├── ui/            # UI components (Sokchea's work)
│       │   │   └── MainActivity.kt
│       │   ├── res/               # Resources (layouts, strings, etc.)
│       │   └── AndroidManifest.xml
│       └── test/                  # Unit tests
│           └── java/
├── gradle/                         # Gradle wrapper
├── build.gradle.kts               # Project-level build config
├── settings.gradle.kts            # Project settings
├── gradle.properties              # Gradle properties
├── local.properties               # Local SDK path (you create this)
│
├── .gitignore                     # Git ignore rules
├── README.md                      # Project documentation
├── GETTING_STARTED.md            # This file
├── WORK_DIVISION.md              # Team workflow
├── KAI_TASKS.md                  # Backend developer guide
├── SOKCHEA_TASKS.md              # Frontend developer guide
├── CHUNK_1_COMPLETION.md         # Architecture foundation docs
└── CHUNK_2_COMPLETION.md         # Permissions system docs
```

---

## 🔧 Configure Local Environment

### Create `local.properties` (If Not Auto-Created)

Android Studio usually creates this automatically, but if not:

**On Windows:**
```properties
# local.properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

**On Mac:**
```properties
# local.properties
sdk.dir=/Users/YourName/Library/Android/sdk
```

**On Linux:**
```properties
# local.properties
sdk.dir=/home/YourName/Android/Sdk
```

**Note:** This file is in `.gitignore` and should NOT be committed!

---

## 🎯 Your First Task

### For Kai (Backend Developer):

1. **Read your guide:**
   ```bash
   code KAI_TASKS.md  # or open in Android Studio
   ```

2. **Current status:**
   - ✅ CHUNK 1: Complete (Architecture foundation)
   - ✅ CHUNK 2: Complete (Permissions system)
   - 🔜 CHUNK 3: File Selection (YOUR NEXT TASK)

3. **Start CHUNK 3:**
   ```bash
   # Create feature branch
   git checkout kai-dev
   git checkout -b feature/chunk-3-file-selection-backend
   
   # Start working on domain models
   # See KAI_TASKS.md for detailed instructions
   ```

---

### For Sokchea (Frontend Developer):

1. **Read your guide:**
   ```bash
   code SOKCHEA_TASKS.md  # or open in Android Studio
   ```

2. **Current status:**
   - ✅ CHUNK 1: Complete (Settings UI)
   - ✅ CHUNK 2: Complete (Permissions UI)
   - ⏳ CHUNK 3: Waiting for Kai's domain models

3. **Preparation:**
   ```bash
   # Create your dev branch
   git checkout kai
   git checkout -b sokchea-dev
   
   # Review existing code
   # Study CHUNK 1 & 2 implementations
   # Wait for Kai's "[READY]" notification for CHUNK 3
   ```

---

## 🔄 Daily Development Workflow

### Morning Routine (Both Developers)

```bash
# 1. Switch to your dev branch
git checkout <yourname>-dev

# 2. Pull latest changes from kai branch
git pull origin kai --rebase

# 3. Check for updates
git status

# 4. Read team updates (check Discord/Slack/GitHub)
```

---

### Starting New Work

```bash
# 1. Create a feature branch from your dev branch
git checkout <yourname>-dev
git checkout -b feature/chunk-X-component-name

# Example for Kai:
# git checkout -b feature/chunk-3-file-selection-backend

# Example for Sokchea:
# git checkout -b feature/chunk-3-file-selection-ui

# 2. Make your changes
# 3. Test frequently
# 4. Commit often with clear messages
```

---

### Committing Your Work

```bash
# 1. Check what changed
git status

# 2. Add your changes
git add .
# or add specific files:
# git add app/src/main/java/com/example/conversion/domain/model/FileItem.kt

# 3. Commit with a clear message
git commit -m "[CHUNK X] Implement Feature - Component"

# Examples:
# git commit -m "[CHUNK 3] Add FileItem domain model"
# git commit -m "[CHUNK 3] Implement FileSelectionScreen UI"

# 4. Push to your feature branch
git push origin feature/chunk-X-component-name
```

---

### Creating a Pull Request

1. **Push your feature branch to GitHub:**
   ```bash
   git push origin feature/chunk-X-component-name
   ```

2. **Go to GitHub:**
   - Navigate to: `https://github.com/AsakuraKai/Conversion`
   - Click "Compare & pull request" (appears after push)

3. **Fill in PR details:**
   ```
   Title: [CHUNK X] Feature Name - Backend/UI Implementation
   
   Base branch: kai
   Compare branch: feature/chunk-X-component-name
   
   Description:
   ## What's Implemented
   - ✅ [List your changes]
   
   ## For Review
   - [Points to focus on]
   
   ## Testing
   - [What you tested]
   
   @OtherDeveloper - [Tag if they need to know]
   ```

4. **Request review** from the other developer

5. **Wait for approval and merge**

---

### After Your PR is Merged

```bash
# 1. Switch back to your dev branch
git checkout <yourname>-dev

# 2. Pull the merged changes
git pull origin kai --rebase

# 3. Delete your feature branch (cleanup)
git branch -d feature/chunk-X-component-name
git push origin --delete feature/chunk-X-component-name

# 4. Start next feature
git checkout -b feature/chunk-Y-next-feature
```

---

## 🧪 Testing Your Changes

### Run Unit Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests PermissionsManagerImplTest

# Run tests with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

---

### Build and Run the App

**Option 1: Android Studio**
1. Click the "Run" button (green play icon)
2. Select an emulator or connected device
3. Wait for app to build and launch

**Option 2: Command Line**
```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and install
./gradlew installDebug
adb shell am start -n com.example.conversion/.MainActivity
```

---

### Check for Code Style Issues

```bash
# Check code style
./gradlew ktlintCheck

# Auto-format code
./gradlew ktlintFormat
```

---

## 🐛 Troubleshooting Common Issues

### Issue 1: Gradle Sync Failed

**Error:** "Gradle sync failed: ..."

**Solution:**
```bash
# Clear Gradle cache
./gradlew clean

# Invalidate caches in Android Studio
# File → Invalidate Caches → Invalidate and Restart

# Delete .gradle folder and rebuild
rm -rf .gradle/
./gradlew build
```

---

### Issue 2: SDK Not Found

**Error:** "SDK location not found"

**Solution:**
1. Create `local.properties` file (see above)
2. Or set in Android Studio:
   - File → Project Structure → SDK Location
   - Set Android SDK location

---

### Issue 3: Merge Conflicts

**Error:** "CONFLICT (content): Merge conflict in ..."

**Solution:**
```bash
# 1. Open the conflicting file
# 2. Look for conflict markers:
#    <<<<<<< HEAD
#    your changes
#    =======
#    their changes
#    >>>>>>> branch-name

# 3. Manually resolve conflicts
# 4. Remove conflict markers
# 5. Add and continue
git add <resolved-file>
git rebase --continue
```

---

### Issue 4: Can't Push to Repository

**Error:** "Permission denied" or "403 Forbidden"

**Solution:**
1. Check if you're added as a collaborator
2. Verify GitHub authentication:
   ```bash
   git remote -v
   # Should show: https://github.com/AsakuraKai/Conversion.git
   ```
3. Use personal access token if needed
4. Contact Kai to add you as collaborator

---

### Issue 5: Build Errors After Pulling

**Error:** "Unresolved reference: ..."

**Solution:**
```bash
# 1. Clean and rebuild
./gradlew clean build

# 2. Sync Gradle files in Android Studio
# File → Sync Project with Gradle Files

# 3. Rebuild project
# Build → Rebuild Project

# 4. If still failing, check if you need to pull again
git pull origin kai --rebase
```

---

## 📚 Essential Resources

### Documentation to Read First:
1. **GETTING_STARTED.md** ← You are here!
2. **Your task guide** (`KAI_TASKS.md` or `SOKCHEA_TASKS.md`)
3. **WORK_DIVISION.md** - Collaboration workflow
4. **README.md** - Project overview and features

### Code References:
- **CHUNK_1_COMPLETION.md** - Architecture foundation patterns
- **CHUNK_2_COMPLETION.md** - Permissions system implementation

### Android Documentation:
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

---

## 💬 Communication Channels

### Daily Standup (Async):
Post in your team chat every morning:
```
Morning! 👋
Yesterday: [What you completed]
Today: [What you're working on]
Blockers: [Any issues or waiting for something]
```

### Getting Help:
- **Technical questions:** Ask in team Discord/Slack
- **Merge conflicts:** Coordinate with other developer
- **Bugs:** Create GitHub issue
- **Architecture questions:** Check CHUNK completion docs

---

## ✅ Setup Verification Checklist

Before starting development, ensure:

```
□ Repository cloned successfully
□ On correct branch (kai)
□ Personal dev branch created
□ Android Studio opens project without errors
□ Gradle sync completed successfully
□ ./gradlew build passes
□ ./gradlew test passes (11 tests should pass)
□ Can run app on emulator/device
□ Read your task guide (KAI_TASKS.md or SOKCHEA_TASKS.md)
□ Read WORK_DIVISION.md
□ Understand Git workflow
□ Know how to create feature branches
□ Know how to create Pull Requests
□ Added as GitHub collaborator (or fork configured)
□ Team communication channel joined
```

---

## 🎉 You're Ready to Start!

### Next Steps:

**For Kai:**
1. Read `KAI_TASKS.md` in detail
2. Review completed CHUNKs (1 & 2)
3. Start working on CHUNK 3: File Selection backend
4. Create feature branch: `feature/chunk-3-file-selection-backend`

**For Sokchea:**
1. Read `SOKCHEA_TASKS.md` in detail
2. Review existing UI implementation (Settings, Permissions)
3. Study the MVI pattern used in the project
4. Wait for Kai's "[READY]" notification to start CHUNK 3 UI

---

## 🆘 Need Help?

- **Setup issues:** Check Troubleshooting section above
- **Git workflow:** See WORK_DIVISION.md
- **Architecture questions:** See CHUNK_1_COMPLETION.md
- **Implementation examples:** See CHUNK_2_COMPLETION.md
- **Task-specific help:** See your task guide (KAI_TASKS.md or SOKCHEA_TASKS.md)

---

**Welcome to the Auto Rename File Service project!** 🚀

Let's build something awesome together! 💪

---

**Last Updated:** November 21, 2025  
**Maintained by:** Kai  
**For questions:** Contact Kai or open a GitHub issue
