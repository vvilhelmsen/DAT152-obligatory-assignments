# DAT152 Oblig 2 — Quiz App

## Overview

A photo-name quiz app that lets you browse a gallery of pictures, add new entries
from the phone's media gallery, and test yourself with a multiple-choice quiz.

### New in Oblig 2

| Feature | Details |
|---------|---------|
| **Room persistence** | All photo entries (name + URI) are stored in a SQLite database via Room. Data survives app restarts. |
| **URI-based images** | Built-in drawable images are referenced via `android.resource://` URIs. Everything goes through Coil. |
| **ViewModel** | `QuizViewModel` keeps quiz state (score, current question) across screen rotations. `GalleryViewModel` owns insert/delete operations. |
| **ContentProvider** | Publishes entries to other apps / adb under authority `com.example.oblig1.provider`. |
| **Espresso tests** | Three test classes covering main-menu navigation, quiz scoring, and gallery add/delete. |

---

## ContentProvider — adb Testing

The provider is available at:

```
content://com.example.oblig1.provider/entries
```

**Columns:** `name`, `URI`

### Query all entries

```bash
adb shell content query --uri content://com.example.oblig1.provider/entries
```

Expected output (with the three built-in entries):

```
Row: 0 name=Fugl, URI=android.resource://com.example.oblig1/2131230720
Row: 1 name=Hund, URI=android.resource://com.example.oblig1/2131230721
Row: 2 name=Katt, URI=android.resource://com.example.oblig1/2131230722
```

*(Exact resource IDs may differ between builds; the important part is that `name` and `URI` columns are present and populated.)*

### Query specific columns

```bash
adb shell content query --uri content://com.example.oblig1.provider/entries \
    --projection name
```

### Notes

- The provider is read-only; `insert`, `update`, and `delete` are not supported.
- The app must be installed and have been launched at least once so the database is seeded.

---

## Espresso Test Cases

### 1 — Main-menu navigation (`MainMenuTest`)

**Class / method:** `MainMenuTest.clickGalleryButtonOpensGalleryActivity()`

**Description:**  
Launch `MainActivity`. Click the "Galleri" button in the main menu. The expected
outcome is that an Intent targeting `GalleryActivity` is dispatched and
`GalleryActivity` becomes the foreground activity.

**Expected result:** `Intents.intended(hasComponent(GalleryActivity))` passes without exception.

**Result:** ✅ PASSED

---

### 2 — Quiz score update (`QuizActivityTest`)

**Class / method:** `QuizActivityTest.scoreUpdatesCorrectlyAfterRightAndWrongAnswer()`

**Description:**  
Pre-populate the database with exactly 3 entries (Katt, Hund, Fugl).
Launch `QuizActivity` directly.

1. Wait until the first question is rendered (answer buttons visible).
2. Read the correct answer name from `activity.viewModel.currentEntry`.
3. Click the button whose label matches the correct answer.
4. Assert the score label contains `"1 / 1"`.
5. Click "Next question".
6. Wait for the next question to render.
7. Read the new correct answer and find a button whose label does **not** match.
8. Click that wrong-answer button.
9. Assert the score label contains `"1 / 2"`.

**Expected result:** Score reflects exactly 1 correct answer out of 2 attempts.

**Result:** ✅ PASSED

---

### 3a — Add entry increases count (`GalleryActivityTest.addEntryIncreasesCount`)

**Class / method:** `GalleryActivityTest.addEntryIncreasesCount()`

**Description:**  
Pre-populate the database with 3 entries. Launch `GalleryActivity`.

1. Verify the grid shows 3 items.
2. Register an Espresso Intents stub: any `ACTION_GET_CONTENT` intent returns
   `RESULT_OK` with the built-in `cat` drawable URI (no real system picker opens).
3. Click "Legg til" — `AddEntryActivity` opens.
4. Type `"Elefant"` into the name field.
5. Click "Velg bilde fra galleri" — the stub fires and the image URI is set.
6. Wait for the Save button to become enabled, then click it.
7. `AddEntryActivity` finishes; control returns to `GalleryActivity`.
8. Wait for `"Elefant"` to appear in the grid.
9. Assert the grid now contains 4 children.

**Expected result:** Gallery shows 4 entries.

**Result:** ✅ PASSED

---

### 3b — Delete entry decreases count (`GalleryActivityTest.deleteEntryDecreasesCount`)

**Class / method:** `GalleryActivityTest.deleteEntryDecreasesCount()`

**Description:**  
Pre-populate the database with 3 entries. Launch `GalleryActivity`.

1. Wait until the grid shows the "Katt" label.
2. Click the grid item with test tag `gallery_item_Katt`.
3. Wait until "Katt" is no longer visible.
4. Assert the grid now contains 2 children.

**Expected result:** Gallery shows 2 entries after deletion.

**Result:** ✅ PASSED

---

## Architecture notes

- `PhotoEntry` is the Room `@Entity`. It holds `id` (auto-generated), `name`, and `imageUri`.  
  Built-in images use `android.resource://com.example.oblig1/<resId>` URIs so the same Coil pipeline handles all images.
- `GalleryViewModel` exposes a `LiveData<List<PhotoEntry>>` observed by both `GalleryActivity` and `AddEntryActivity`. Mutations go through `viewModelScope` coroutines.
- `QuizViewModel` stores all quiz state as Compose `mutableStateOf` properties, so they survive rotation without any extra work.
- `PhotoEntryProvider` calls `dao.getAllSync()` on the calling thread (ContentProvider queries already run on a background thread by the framework).

---

## Rotation fixes

Two bugs were found and fixed after the initial implementation.

### Quiz question changed on rotation

**Root cause:** The guard flag `questionGenerated` was stored in Compose `remember {}`.
On rotation the composition is recreated, `remember` resets to `false`, and
`LaunchedEffect` called `generateQuestion()` again — picking a new random entry.

**Fix:** Replace the `remember` flag with a check on `viewModel.currentEntry == null`.
The ViewModel survives rotation, so `currentEntry` is still set and no new question
is generated.

```kotlin
// QuizActivity.kt
LaunchedEffect(entries) {
    if (entries.size >= 3 && viewModel.currentEntry == null) {
        viewModel.generateQuestion(entries)
    }
}
```

### Gallery sort direction reset on rotation

**Root cause:** `isReversed` was stored in `remember { mutableStateOf(false) }`.
On rotation the composable is recreated and the sort direction was forgotten.

**Fix:** Changed to `rememberSaveable` which saves the value in the instance-state
bundle and restores it after a configuration change.

```kotlin
// GalleryActivity.kt
var isReversed by rememberSaveable { mutableStateOf(false) }
```
