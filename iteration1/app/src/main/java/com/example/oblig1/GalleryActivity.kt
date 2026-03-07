package com.example.oblig1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import coil.compose.rememberAsyncImagePainter

class GalleryActivity : ComponentActivity() {
    
    private var refreshTrigger = mutableStateOf(0)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GalleryScreen(refreshTrigger.value)
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GalleryScreen(externalTrigger: Int) {
        val app = application as QuizApplication
        
        var isReversed by remember { mutableStateOf(false) }
        
        var updateTrigger by remember { mutableStateOf(0) }
        
        // sort based on choice 
        val sortedEntries = remember(isReversed, updateTrigger, externalTrigger) {
            if (isReversed) {
                app.photoEntries.sortedByDescending { it.name }
            } else {
                app.photoEntries.sortedBy { it.name }
            }
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
                    // sort button
                    Button(onClick = {
                        isReversed = !isReversed
                    }) {
                        Text(if (isReversed) stringResource(R.string.sort_a_z) else stringResource(R.string.sort_z_a))
                    }
                    
                    // add entry button
                    Button(onClick = {
                        startActivity(Intent(this@GalleryActivity, AddEntryActivity::class.java))
                    }) {
                        Text(stringResource(R.string.add_entry))
                    }
                }
                
                // show msg if no imagfes
                if (sortedEntries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.no_entries))
                    }
                } else {
                    // image grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sortedEntries) { entry ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    // rm image when clicked
                                    app.photoEntries.remove(entry)
                                    updateTrigger++
                                }
                            ) {
                                // show image
                                if (entry.imageResId != 0) {
                                    Image(
                                        painter = painterResource(id = entry.imageResId),
                                        contentDescription = entry.name,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .fillMaxWidth()
                                    )
                                } else if (entry.imageUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(Uri.parse(entry.imageUri)),
                                        contentDescription = entry.name,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .fillMaxWidth()
                                    )
                                }
                                // name under image
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
    
    // Kalles når vi kommer tilbake fra AddEntryActivity
    override fun onResume() {
        super.onResume()
        // Oppdater triggeren for å tvinge rekomposisjon
        refreshTrigger.value++
    }
}
