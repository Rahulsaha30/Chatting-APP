package eu.tutorials.myapplication

import android.window.SplashScreen
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eu.tutorials.myapplication.ui.theme.Purple40
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(navController: NavController,vm:CAViewModel) {
 var startAnimation by remember { mutableStateOf(false) }
    val alphanim= animateFloatAsState(targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000))

    LaunchedEffect(key1 = true){
        startAnimation=true
        delay(2000)
        navController.popBackStack()
        if(vm.signedIn.value==false){
        navigateTo(navController,DestinationScreen.Login.route)
        }else navigateTo(navController,DestinationScreen.Profile.route)
        }
    SplashScreen(alpha=alphanim.value)
}


@Composable
fun SplashScreen(alpha:Float){
            Box(modifier = Modifier
                .background(if (isSystemInDarkTheme()) Color.Black else Purple40)
                .fillMaxSize(), contentAlignment = Alignment.Center){
                Icon(imageVector = Icons.Default.Favorite, contentDescription =null,
                    tint = Color.White, modifier = Modifier.size(120.dp).alpha(alpha=alpha))

            }
}