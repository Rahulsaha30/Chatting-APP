package eu.tutorials.myapplication

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.red)) // Replace with your background color
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter) // Align to the top center
                .padding(top = 80.dp) // Add padding if needed
        ) {
            Image(
                painter = painterResource(id = R.drawable.buzzz_png),
                contentDescription = "Buzzz"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight() // Adjust height as needed
                .padding(top = 200.dp)
                .background(colorResource(id = R.color.white))
                .border(
                    width = 0.dp,
                    color = colorResource(id = R.color.red), shape = RoundedCornerShape(26.dp)
                )
                .clip(RoundedCornerShape(26.dp))

        ) {

            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passwordState = remember { mutableStateOf(TextFieldValue()) }

            val focus = LocalFocusManager.current


            Text(
                text = "Welcome back", fontSize = 22.sp,
                color = colorResource(id = R.color.red),
                style = TextStyle(
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier
                    .padding(top = 30.dp, start = 30.dp)

            )

            Text(
                text = "Continue to sign in!", fontSize = 22.sp, fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(id = R.color.red),
                modifier = Modifier.padding(start = 30.dp)
            )

            //Email
            Text(text = "Email", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = colorResource(id = R.color.mygrey),
                modifier = Modifier.padding(start = 30.dp, top = 20.dp))


            OutlinedTextField(value = emailState.value, onValueChange = { emailState.value = it},
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp, top = 2.dp)
                    .background(colorResource(id = R.color.textfieldgrey))
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        width = 0.dp, color = colorResource(id = R.color.textfieldgrey),
                        shape = RoundedCornerShape(0.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorResource(id = R.color.textfieldgrey),
                    focusedBorderColor = colorResource(id = R.color.textfieldgrey),
                    unfocusedTextColor = Color.Black, // Set unfocused text color to black
                    focusedTextColor = Color.Black, // Set focused text color to black
                    disabledTextColor = Color.Gray
                ))

            //PASSWORD
            Text(text = "PASSWORD", fontSize = 18.sp, fontWeight = FontWeight.Medium,
                color = colorResource(
                    id = R.color.mygrey
                ),
                modifier = Modifier.padding(start = 30.dp, top = 10.dp))

            OutlinedTextField(value = passwordState.value, onValueChange = {passwordState.value = it},
                visualTransformation = PasswordVisualTransformation()
                , modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp, top = 2.dp)
                    .background(colorResource(id = R.color.textfieldgrey))
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        width = 0.dp, color = colorResource(id = R.color.textfieldgrey),
                        shape = RoundedCornerShape(0.dp)
                    ),  colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorResource(id = R.color.textfieldgrey),
                    focusedBorderColor = colorResource(id = R.color.textfieldgrey),
                    unfocusedTextColor = Color.Black, // Set unfocused text color to black
                    focusedTextColor = Color.Black, // Set focused text color to black
                    disabledTextColor = Color.Gray
                ))


            Text(text = "Forgot Password?", fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 35.dp, top = 10.dp)
                    .clickable { navController.navigate(DestinationScreen.ForgotPassword.route) }
                ,color = colorResource(id = R.color.red))

            Button(onClick = {
                vm.onLogin(
                emailState.value.text,
                passwordState.value.text
            ) },shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red)),
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp, top = 20.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally)) {
                Text(text = "Sign in",color = colorResource(id = R.color.white),
                    fontWeight = FontWeight.Medium, fontSize = 18.sp)
            }


            Text(text = "Don't have an Account?", fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.grey),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 15.dp)
                    .clickable { navigateTo(navController, DestinationScreen.Signup.route) })

            Text(text = "Sign Up", fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.red),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 1.dp)
                    .clickable { navigateTo(navController, DestinationScreen.Signup.route) })
        }

        val isLoading = vm.inProgress.value
        if (isLoading)
            CommonProgressSpinner()
    }
}