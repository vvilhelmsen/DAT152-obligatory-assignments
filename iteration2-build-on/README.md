# DAT152 Oblig 2 — Quiz App

A photo-name quiz app. Browse a gallery, add entries from your phone, and test yourself with a multiple-choice quiz.

## What's new since oblig 1

- **Room database** — entries persist across restarts. Built-in images use `android.resource://` URIs so everything goes through the same Coil pipeline.
- **ViewModels** — `QuizViewModel` keeps score and current question alive on rotation. `GalleryViewModel` handles inserts/deletes via coroutines.
- **ContentProvider** — exposes entries to other apps under authority `com.example.oblig1.provider`.
- **Espresso tests** — three test classes covering navigation, quiz scoring, and gallery add/delete.

---

## ContentProvider — adb testing

```bash
adb shell content query --uri content://com.example.oblig1.provider/entries
```

Expected output after first launch:

```
Row: 0 name=Fugl, URI=android.resource://com.example.oblig1/2131230720
Row: 1 name=Hund, URI=android.resource://com.example.oblig1/2131230721
Row: 2 name=Katt, URI=android.resource://com.example.oblig1/2131230722
```

The provider is read-only. Columns: `name`, `URI`.

---

## Tests

### MainMenuTest — `clickGalleryButtonOpensGalleryActivity`

Launches `MainActivity`, clicks the "Galleri" button, and verifies that an intent targeting `GalleryActivity` is fired.

**Result:  passed**

---

### QuizActivityTest — `scoreUpdatesCorrectlyAfterRightAndWrongAnswer`

Seeds the database with 3 entries and launches `QuizActivity` directly. Reads the correct answer from the ViewModel, submits it, and checks the score shows `1 / 1`. Then advances to the next question, picks a wrong answer, and checks the score shows `1 / 2`.

**Result:  passed**

---

### GalleryActivityTest — `addEntryIncreasesCount`

Seeds 3 entries and launches `GalleryActivity`. Stubs `ACTION_GET_CONTENT` to return the built-in cat drawable URI (no real picker opens). Clicks add, fills in a name, triggers the stubbed picker, saves, and checks the grid now has 4 items.

**Result: passed**

### GalleryActivityTest — `deleteEntryDecreasesCount`

Seeds 3 entries, clicks the "Katt" grid item to delete it, and checks the grid is down to 2 items.

**Result: passed**

---

## Rotation fixes

Two bugs were found after the initial implementation:

**Quiz question changed on rotation** — the guard flag `questionGenerated` was in `remember {}`, so it reset on rotation and triggered a new question. Fixed by checking `viewModel.currentEntry == null` instead, which is ViewModel-owned state and survives rotation.

**Gallery sort direction reset on rotation** — `isReversed` was in `remember {}`. Changed to `rememberSaveable {}` so it's saved in the instance state bundle.

