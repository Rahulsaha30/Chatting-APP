package eu.tutorials.myapplication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter


fun navigateTo(navController: NavController, route:String){
    navController.navigate(route){
        popUpTo(route)
        launchSingleTop=true
    }
}

@Composable
fun CommonProgressSpinner(){
    Row (modifier = Modifier
        .fillMaxSize()
        .alpha(0.5f)
        .background(Color.LightGray)
        .clickable(enabled = false) {},
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically)
    {
        CircularProgressIndicator()
    }
}

@Composable
fun NotificationMessage(vm:CAViewModel){
    val notifState=vm.popupNotification.value
    val notifMessage=notifState?.getContentIfNotHandled()
    if(!notifMessage.isNullOrEmpty()){
        Toast.makeText(LocalContext.current,notifMessage,Toast.LENGTH_LONG).show()
    }
}
@Composable
fun CheckSignedIn(vm:CAViewModel,navController: NavController) {
    val alreadySignedIn = remember {
        mutableStateOf(false)
    }
    val SignedIn = vm.signedIn.value
    if (SignedIn && !alreadySignedIn.value) {
        alreadySignedIn.value = true
        navController.navigate(DestinationScreen.Profile.route) {
            popUpTo(0)
        }
    }else if(!SignedIn && alreadySignedIn.value){
        alreadySignedIn.value = false
        navController.navigate(DestinationScreen.Login.route) {
            popUpTo(0)
        }
    }
}

@Composable
fun CommonDivider(){
    HorizontalDivider(
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        thickness = 1.dp,
        color = Color.LightGray
    )
}

@Composable
fun CommonImage(data:String?,
    modifier: Modifier=Modifier.wrapContentSize(),
        contentScale:ContentScale=ContentScale.Crop){
    val painter= rememberAsyncImagePainter(model = data)
        Image(painter = painter, contentDescription = null,
            modifier = modifier,contentScale=contentScale)
    if(painter.state is AsyncImagePainter.State.Loading){
        CommonProgressSpinner()
    }
    }

@Composable
fun CommonRow(imageUrl:String?,name:String?,onItemClick:()->Unit){
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(75.dp)
        .clickable { onItemClick.invoke() }, verticalAlignment = Alignment.CenterVertically){
        CommonImage(data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Red))
        Text(text = name?:"---", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun TitleText(txt:String){
    Text(text = txt, fontWeight = FontWeight.Bold, fontSize = 35.sp,color = colorResource(
        id = R.color.Customdarkblue
    ), modifier = Modifier.padding(8.dp))
}

@Composable
fun gradientbutton(text:String, textColor:Color, gradient: Brush, onClick:()->Unit){
    Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(50.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        onClick = { onClick()}){
        Box(modifier = Modifier.background(gradient,
            shape = RoundedCornerShape(50.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment =Alignment.Center){
            Text(text = text, color = textColor,fontSize = 18.sp,
                fontWeight = FontWeight.Bold)
        }
            
    }
}