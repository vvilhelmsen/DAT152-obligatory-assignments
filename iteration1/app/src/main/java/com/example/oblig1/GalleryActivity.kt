package com.example.oblig1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

        val sortedEntries = remember(allEntries, isReversed) {
            if (isReversed) allEntries.sortedByDescending { it.name }
            else allEntries.sortedBy { it.name }
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
                        onClick = { startActivity(Intent(this@GalleryActivity, AddEntryActivity::class.java)) },
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
}

