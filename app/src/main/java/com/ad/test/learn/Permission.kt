package com.ad.test.learn

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ad.test.ui.theme.TestTheme

class PermissionAc : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ::onRequestCameraPermissionResult
        )

    private val requestAllPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ::onRequestAllPermissionResult
        )

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                Scaffold { paddingValues ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Button(::checkPermissions) {
                            Text("Request multiple permissions")
                        }
                    }
                }
            }
        }
    }

    fun checkPermissions() {
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        when {
            permissionsToRequest.isEmpty() -> {
                Toast.makeText(this, "All permissions are already granted.", Toast.LENGTH_SHORT)
                    .show()
            }

            permissionsToRequest.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            } -> {
                permissionsToRequest.forEach {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, it)) {
                        AlertDialog.Builder(this)
                            .setTitle("Permission required")
                            .setMessage("This app needs permission $it to access this feature.")
                            .setPositiveButton("Grant") { _, _ ->
                                requestPermissionLauncher.launch(it)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
            }

            else -> {
                requestAllPermissionsLauncher.launch(permissionsToRequest)
            }
        }
    }

    fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) ==
                    PackageManager.PERMISSION_GRANTED -> {
                Log.d(
                    this::class.simpleName,
                    "Camera permission is already granted. Feel free to take photos!"
                )
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // The permission was denied some time before. Show the rationale!
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("This app needs permission to access this feature.")
                    .setPositiveButton("Grant") { _, _ ->
                        requestPermissionLauncher
                            .launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            else -> {
                // The permission is not granted. Ask for it
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

    }

    fun onRequestCameraPermissionResult(granted: Boolean) {
        if (granted)
            Log.d(
                this::class.simpleName,
                "Camera permission is already granted. Feel free to take photos!"
            ) else if (!ActivityCompat
                .shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
        )
        // "Don't ask again"
            AlertDialog.Builder(this)
                .setTitle("Permission required")
                .setMessage(
                    "This app needs permission to access this feature. " +
                            "Please grant it in Settings."
                )
                .setPositiveButton("Grant") { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null),
                    )
                    try {
                        startActivity(intent)
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(this, "Cannot open settings", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show() else {
            // The permission was denied without checking "Don't ask again".
            // Do nothing, wait for better times…
        }
    }

    fun onRequestAllPermissionResult(granted: Map<String, Boolean>) {
        if (granted.values.all { it }) {
            Log.d(this::class.simpleName, "All permissions granted")
            Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
        } else {
            Log.d(this::class.simpleName, "Some permissions were denied")

            val somePermissionsPermanentlyDenied = granted.entries.any { (permission, isGranted) ->
                !isGranted && !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            }

            if (somePermissionsPermanentlyDenied) {
                // Show a dialog to open app settings
                AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("Some permissions were permanently denied. Please grant them in app settings to use this feature.")
                    .setPositiveButton("Settings") { _, _ ->
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(this, "Cannot open settings", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(this, "Some permissions were denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}