package com.example.oblig1

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

class AddEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AddEntryScreen()
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddEntryScreen() {
        val app = application as QuizApplication
        
        var nameText by remember { mutableStateOf("") }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedImageUri = uri
        }
        
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.add_new_entry)) })
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
								// name output
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text(stringResource(R.string.enter_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = stringResource(R.string.selected_image),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(stringResource(R.string.no_image_selected), color = Color.Gray)
                    }
                }
                
                // Pick image buttonh
                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.select_image_from_gallery))
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // save btn
                Button(
                    onClick = {
                        val newEntry = PhotoEntry(
                            name = nameText,
                            imageUri = selectedImageUri.toString()
                        )
                        app.photoEntries.add(newEntry)
                        finish()
                    },
                    enabled = nameText.isNotBlank() && selectedImageUri != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save_entry))
                }
            }
        }
    }
}
