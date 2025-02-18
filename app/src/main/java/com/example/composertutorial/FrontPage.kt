package com.example.composertutorial

import android.Manifest
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.composertutorial.ui.theme.ComposerTutorialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.composertutorial.data.User
import com.example.composertutorial.data.UserViewModel
import com.example.composertutorial.data.UserViewModelFactory
import java.io.File
import java.io.FileOutputStream
import androidx.compose.material3.AlertDialog

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun FrontPage(onNavigateToMessages: () -> Unit) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(context.applicationContext as Application))
    val user by userViewModel.userData.collectAsState(initial = null)

    var userName by remember(user) { mutableStateOf(user?.userName ?: "") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var updateImage by remember { mutableStateOf(false) }
    val imageFile = File(context.filesDir, "profile_picture.jpg")

    if (imageFile.exists()) {
        imageUri = Uri.fromFile(imageFile)
    }

    //photo picker
    val photoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {uri ->
        uri?.let {selectedUri ->
            val inputStream = context.contentResolver.openInputStream(selectedUri)
            val outputFile = File(context.filesDir, "profile_picture.jpg")

            FileOutputStream(outputFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            imageUri = Uri.fromFile(outputFile)
            updateImage = !updateImage
        }
    }

    //track dialog visibility
    var showDialog by remember { mutableStateOf(false) }

    // request permission launcher for notifications
    val requestMultiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions[Manifest.permission.FOREGROUND_SERVICE_HEALTH] == true &&
                            permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true &&
                            permissions[Manifest.permission.BODY_SENSORS] == true &&
                            permissions[Manifest.permission.HIGH_SAMPLING_RATE_SENSORS] == true

            if (isGranted) {
                val serviceIntent = Intent (context, SensorService::class.java)
                context.startService(serviceIntent)
            } else {

            }
        }
    )

    Column (modifier = Modifier.padding(64.dp)) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "WELCOME :D!" ,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        )

        //Image
        if (imageUri != null) {
            //Add profile photo using AsyncImage
            AsyncImage(
                model = imageUri,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(300.dp)
                    .border(3.dp, MaterialTheme.colorScheme.secondary)
                    .padding(top = 24.dp)
                    .padding(bottom = 24.dp)
            )
        } else {
            //otherwise the profile picture would be a logo (cat)
            Image(
                painter = painterResource(R.drawable.kissa),
                contentDescription = "Cat logo",
                modifier = Modifier
                    .size(300.dp)
                    .border(3.dp, MaterialTheme.colorScheme.secondary)
                    .padding(top = 24.dp)
                    .padding(bottom = 24.dp)
            )
        }

        //Button to choose profile picture
        Button(
            onClick = {
                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
        {
            Text("Pick your profile picture")
        }

        //Text box to input username
        OutlinedTextField(
            value = userName,
            onValueChange = { newUsername ->
                userName = newUsername
                user?.let {
                    if (it.userName != newUsername) {
                        userViewModel.updateUser(it.copy(userName = newUsername))
                    }
                } ?: run {
                    val newUser = User(userName = newUsername)
                    userViewModel.insertUser(newUser)
                }

                /*if (user != null){
                    if (user!!.userName != newUsername) {
                        userViewModel.updateUser(user!!.copy(userName = newUsername))
                    }
                }else {
                    val newUser = User(userName = newUsername)
                    userViewModel.insertUser(newUser)
                }*/

                },
            label = {Text("Username")},
            singleLine = true,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        )

        //Button to enable notifications
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Text("Enable notifications")
        }

        //if showDialog is true, this dialog shows in the screen
        if (showDialog) {
            AlertDialog (
                onDismissRequest = { showDialog = false },
                title = { Text("Are you sure you want to enable notifications?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                requestMultiplePermissionLauncher.launch(
                                    arrayOf (
                                        Manifest.permission.POST_NOTIFICATIONS,
                                        Manifest.permission.ACTIVITY_RECOGNITION,
                                        Manifest.permission.BODY_SENSORS,
                                        Manifest.permission.HIGH_SAMPLING_RATE_SENSORS
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Dismiss")
                    }
                }
            )
        }

        //Button to Messages
        Button(
            onClick = onNavigateToMessages,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Text("Messages")
        }
    }
}

@Preview
@Composable
fun PreviewFrontPge() {
    ComposerTutorialTheme {
        FrontPage(
            onNavigateToMessages = {},
        )
    }
}
