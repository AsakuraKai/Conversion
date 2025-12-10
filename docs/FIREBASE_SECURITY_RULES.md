# Firebase Security Rules

## Firestore Security Rules

Deploy these rules to your Firebase Console → Firestore Database → Rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // User preferences - only accessible by the user who owns them
    match /users/{userId}/data/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### Rule Explanation:
- Users can only read/write their own data
- Requires authentication (no anonymous access to Firestore)
- Uses user's Firebase UID as document ID for security

## Firebase Storage Security Rules

Deploy these rules to your Firebase Console → Storage → Rules:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // User files - only accessible by the user who owns them
    match /users/{userId}/files/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### Rule Explanation:
- Users can only access their own files
- Requires authentication
- Files stored under `/users/{uid}/files/` path structure

## Deployment Instructions

### Via Firebase Console:
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select project: `daten-sequence`
3. Navigate to:
   - **Firestore Database** → Rules tab → Paste Firestore rules → Publish
   - **Storage** → Rules tab → Paste Storage rules → Publish

### Via Firebase CLI:
1. Install Firebase CLI: `npm install -g firebase-tools`
2. Login: `firebase login`
3. Initialize: `firebase init` (select Firestore and Storage)
4. Edit generated `firestore.rules` and `storage.rules` files
5. Deploy: `firebase deploy --only firestore:rules,storage:rules`

## Testing Rules

### Firestore Rules Test:
```javascript
// Simulate authenticated user
const testUserId = "test-user-123";

// Should PASS: User reading their own data
match /users/test-user-123/data/preferences {
  allow read: if request.auth.uid == "test-user-123";
}

// Should FAIL: User reading another user's data
match /users/other-user/data/preferences {
  allow read: if request.auth.uid == "test-user-123";
}
```

### Storage Rules Test:
Similar pattern - test via Firebase Console Emulator or Unit Tests.

## Security Best Practices

1. **Never** expose API keys in client code (handled by Firebase SDK)
2. **Always** authenticate users before Firestore/Storage access
3. **Use** server-side timestamp (`FieldValue.serverTimestamp()`) for conflict resolution
4. **Validate** data types and required fields in rules (optional enhancement)
5. **Monitor** Firebase Console for unusual access patterns

## Data Privacy Compliance

- User data is stored in Firestore US region by default
- Files stored in Firebase Storage inherit same region
- For GDPR compliance: Implement user data deletion endpoint
- For data portability: Implement export functionality
