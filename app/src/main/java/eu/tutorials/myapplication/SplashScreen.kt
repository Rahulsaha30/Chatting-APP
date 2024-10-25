package eu.tutorials.myapplication

import android.window.SplashScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eu.tutorials.myapplication.ui.theme.Purple40
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(navController: NavController, vm: CAViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500) // Fade-in duration
        )
        delay(1000L) // Delay for 2.5 seconds (including fade-in)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen(alpha = alpha.value)
    } else {
        LaunchedEffect(Unit) {
            navController.popBackStack()
            if (vm.signedIn.value == false) {
                navigateTo(navController, DestinationScreen.Login.route)
            } else {
                navigateTo(navController, DestinationScreen.ChatList.route)
            }
        }
    }
}

@Composable
fun SplashScreen(alpha: Float) {
    Box(
        modifier = Modifier
            .background(Color(0xFFD8343F))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.buzzz_png),
            contentDescription = "Buzz PNG",
            modifier = Modifier
                .size(1000.dp)
                .alpha(alpha) // Apply animated alpha
        )
    }
}
