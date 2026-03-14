package com.example.oblig1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.oblig1.viewmodel.GalleryViewModel

class GalleryActivity : ComponentActivity() {

    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GalleryScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GalleryScreen() {
        val allEntries by viewModel.entries.observeAsState(emptyList())
        var isReversed by rememberSaveable { mutableStateOf(false) }
        var showAddDialog by rememberSaveable { mutableStateOf(false) }
        val sortedEntries = if (isReversed) allEntries.sortedByDescending { it.name }
                            else allEntries.sortedBy { it.name }

        if (showAddDialog) {
            AddEntryDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, uri ->
                    viewModel.insert(PhotoEntry(name = name, imageUri = uri.toString()))
                    showAddDialog = false
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.gallery_title)) })
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { isReversed = !isReversed },
                        modifier = Modifier.testTag("sort_button")
                    ) {
                        Text(if (isReversed) stringResource(R.string.sort_a_z) else stringResource(R.string.sort_z_a))
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_entry_button")
                    ) {
                        Text(stringResource(R.string.add_entry))
                    }
                }

                if (sortedEntries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.no_entries))
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("gallery_grid")
                    ) {
                        items(sortedEntries, key = { it.id }) { entry ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .testTag("gallery_item_${entry.name}")
                                    .clickable { viewModel.delete(entry) }
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(Uri.parse(entry.imageUri)),
                                    contentDescription = entry.name,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .fillMaxWidth()
                                )
                                Text(
                                    text = entry.name,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AddEntryDialog(onDismiss: () -> Unit, onSave: (String, Uri) -> Unit) {
        var nameText by remember { mutableStateOf("") }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        val context = LocalContext.current

        val imagePickerLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) { }
                selectedImageUri = uri
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.add_new_entry)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text(stringResource(R.string.enter_name)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_field")
                    )
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("select_image_button")
                    ) {
                        Text(stringResource(R.string.select_image_from_gallery))
                    }
                    if (selectedImageUri != null) {
                        Text(stringResource(R.string.selected_image))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onSave(nameText, selectedImageUri!!) },
                    enabled = nameText.isNotBlank() && selectedImageUri != null,
                    modifier = Modifier.testTag("save_button")
                ) {
                    Text(stringResource(R.string.save_entry))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}
