package eu.tutorials.myapplication

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, vm: CAViewModel) {
    CheckSignedIn(vm = vm, navController = navController)

    val context = LocalContext.current as? Activity
    BackHandler {
        if (!vm.signedIn.value) {
            context?.finish()
        } else {
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(if (isSystemInDarkTheme())Color.Black else Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passwordState = remember { mutableStateOf(TextFieldValue()) }

            val focus = LocalFocusManager.current

            Image(
                painter = painterResource(id = R.drawable.agree),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )

            Text(
                text = "Login",
                modifier = Modifier.padding(8.dp),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan,
                fontFamily = FontFamily.SansSerif
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Email") })

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                gradientbutton(text = "LOGIN",
                    textColor = Color.White,
                    gradient = Brush.horizontalGradient(colors = listOf(Color.Cyan,Color.LightGray))){
                    focus.clearFocus(force = true)
                    vm.onLogin(
                        emailState.value.text,
                        passwordState.value.text
                    )
                }
            }

            Text(
                text = "Forgot Password?",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { navController.navigate(DestinationScreen.ForgotPassword.route) }
            )



            Text(text = "New here? Go to signup ->",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { navigateTo(navController, DestinationScreen.Signup.route) }
            )
        }

        val isLoading = vm.inProgress.value
        if (isLoading)
            CommonProgressSpinner()
    }
}