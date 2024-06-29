package eu.tutorials.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.tutorials.myapplication.ui.theme.MyApplicationTheme


sealed class DestinationScreen(val route :String){
    object Signup:DestinationScreen("signup")
    object Login:DestinationScreen("login")
    object Profile:DestinationScreen("profile")
    object ChatList:DestinationScreen("chatList")
    object SingleChat:DestinationScreen("singleChat/{chatId}"){
        fun createRoute(id:String)="singleChat/$id"
    }
    object StatusList:DestinationScreen("statusList")
    object SingleStatus:DestinationScreen("singleStatus/{statusId}"){
        fun createRoute(id:String)="singleStatus/$id"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatAppNavigation()
                }
            }
        }
    }
}

@Composable
fun ChatAppNavigation(){
    val navController= rememberNavController()

    NavHost(navController = navController, startDestination = DestinationScreen.Profile.route ){
        composable(DestinationScreen.Signup.route){
            SignupScreen()
        }
        composable(DestinationScreen.Login.route){
            LoginScreen()
        }
        composable(DestinationScreen.Profile.route){
            ProfileScreen(navController=navController)
        }
        composable(DestinationScreen.StatusList.route){
            StatusListScreen(navController=navController)
        }
        composable(DestinationScreen.SingleStatus.route){
            SingleStatusScreen(statusId = "123")
        }
        composable(DestinationScreen.ChatList.route){
            ChatListScreen(navController=navController)
        }
        composable(DestinationScreen.SingleChat.route){
            SingleChatScreen(chatId = "123")
        }
    }
}
