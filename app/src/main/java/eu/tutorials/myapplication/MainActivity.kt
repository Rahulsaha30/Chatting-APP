package eu.tutorials.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.tutorials.myapplication.ui.theme.MyApplicationTheme
import androidx.hilt.navigation.compose.hiltViewModel




sealed class DestinationScreen(val route :String){
    object Signup:DestinationScreen("signup")
    object Login:DestinationScreen("login")
    object Profile:DestinationScreen("profile")
    object ChatList:DestinationScreen("chatList")
    object SingleChat:DestinationScreen("singleChat/{chatId}"){
        fun createRoute(id:String)="singleChat/$id"
    }
    object StatusList:DestinationScreen("statusList")
    object SingleStatus:DestinationScreen("singleStatus/{userId}"){
        fun createRoute(userId:String?)="singleStatus/$userId"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    val vm=hiltViewModel<CAViewModel>()

    NotificationMessage(vm = vm)
    
    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route ){
        composable(DestinationScreen.Signup.route){
            SignupScreen(navController,vm)
        }
        composable(DestinationScreen.Login.route){
            LoginScreen(navController,vm)
        }
        composable(DestinationScreen.Profile.route){
            ProfileScreen(navController=navController,vm)
        }
        composable(DestinationScreen.StatusList.route){
            StatusListScreen(navController=navController,vm)
        }
        composable(DestinationScreen.SingleStatus.route){
           val userId=it.arguments?.getString("userId")
            userId?.let {
               SingleStatusScreen(navController = navController, vm = vm, userId =userId )
            }
        }
        composable(DestinationScreen.ChatList.route){
            ChatListScreen(navController=navController,vm)
        }
        composable(DestinationScreen.SingleChat.route){
            val chatId=it.arguments?.getString("chatId")
            chatId?.let {
                    SingleChatScreen(navController=navController,vm,chatId = it)
            }
        }
    }
}
