package com.example.m2w1d2_location_permission

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.m2w1d2_location_permission.ui.theme.M2W1D2_location_permissionTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            M2W1D2_location_permissionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FullscreenImage(imageRes = R.drawable.ic_launcher_foreground)
                        BottomCard()
                        GetCurrentLocation()
                    }
                }
            }
        }
    }
}

@Composable
fun FullscreenImage(imageRes: Int) {


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image filling the screen
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BottomCard() {
    // Get screen height
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val offsetY = with(LocalDensity.current) { (screenHeight / 2.8f).toPx().toInt() } // Convert Dp to pixels

    Box(modifier = Modifier.fillMaxSize()) {
        // Card positioned half from the bottom
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(x = 0, y = offsetY) } // Offset using calculated pixel value
                .fillMaxSize(), // Adjust card width as needed
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        )  {
            // Card content
            Text(
                text = "This is a half-screen rounded card!",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                color = Color.Black
            )
        }
    }
}

@Composable
fun GetCurrentLocation() {
    val context = LocalContext.current
    var coordinates by remember { mutableStateOf("Fetching coordinates...") }
    var errorMessage by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchCoordinates(context, fusedLocationClient) { result, error ->
                coordinates = result ?: "Coordinates not available"
                errorMessage = error ?: ""
                loading = false
            }
        } else {
            coordinates = "Permission denied"
            errorMessage = "Location permission was denied."
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        when (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionChecker.PERMISSION_GRANTED -> {
                fetchCoordinates(context, fusedLocationClient) { result, error ->
                    coordinates = result ?: "Coordinates not available"
                    errorMessage = error ?: ""
                    loading = false
                }
            }
            else -> {
                loading = true
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LocationCard(
        coordinates = coordinates,
        error = errorMessage,
        isLoading = loading,
        onRefresh = {
            loading = true
            fetchCoordinates(context, fusedLocationClient) { result, error ->
                coordinates = result ?: "Coordinates not available"
                errorMessage = error ?: ""
                loading = false
            }
        }
    )
}



@Composable
fun LocationCard(
    coordinates: String,
    error: String,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        isLoading -> "Loading..."
                        error.isNotEmpty() -> error
                        else -> "📍 Current Location:\n$coordinates"
                    },
                    color = Color.Black,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onRefresh,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text(text = "Refresh Location", color = Color.White)
                }
            }
        }
    }
}



@SuppressLint("MissingPermission")
private fun fetchCoordinates(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onResult: (String?, String?) -> Unit
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            location?.let {
                val coords = "Latitude: ${it.latitude}, Longitude: ${it.longitude}"
                onResult(coords, null)
            } ?: onResult(null, "Location not found")
        }
        .addOnFailureListener {
            onResult(null, "Error: ${it.message}")
        }
}
