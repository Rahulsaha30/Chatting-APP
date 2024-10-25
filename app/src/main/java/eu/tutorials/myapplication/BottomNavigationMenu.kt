package eu.tutorials.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


enum class BottomNavigationItem (val icon:Int, val navDestionation:DestinationScreen ){
    STATUSLIST(R.drawable.baseline_status,DestinationScreen.StatusList),
    CHATLIST(R.drawable.baseline_chat,DestinationScreen.ChatList),
    PROFILE(R.drawable.baseline_profile,DestinationScreen.Profile)
}
/*

@Composable
fun BottomNavigationMenu(selectedItem:BottomNavigationItem, navController: NavController,modifier: Modifier){
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
        .clip(RoundedCornerShape(30.dp))
        .background(
            colorResource(id = R.color.bottombarblue)
        ), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically)
    {
        for (item in BottomNavigationItem.values()){
          Card(modifier = Modifier.size(40.dp).align(Alignment.CenterVertically),
              shape = CircleShape,
              elevation = CardDefaults.cardElevation(defaultElevation = 30.dp),

            colors = CardDefaults.cardColors(containerColor =  colorResource(id = R.color.cardbottomblue))){
                Image(
                    painter = painterResource(id = item.icon), contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .size(40.dp)
                        .padding(4.dp)
                        .weight(1f)
                        .clickable {
                            navigateTo(navController, item.navDestionation.route)
                        },
                    colorFilter =
                    if (item == selectedItem) ColorFilter.tint(color = Color(0xFFD8343F))
                    else ColorFilter.tint(Color.Gray)
                )
            }
        }
    }
}
*/

@Composable
fun BottomNavigationMenu(selectedItem:BottomNavigationItem, navController: NavController,modifier: Modifier){
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(55.dp)
        .clip(RoundedCornerShape(1.dp))
        .background(
            colorResource(id = R.color.white)
        ), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically)
    {
        for (item in BottomNavigationItem.values()){
          Card(modifier = Modifier.size(40.dp).align(Alignment.CenterVertically),
              shape = RoundedCornerShape(20.dp),
              elevation = CardDefaults.cardElevation(disabledElevation = 15.dp, pressedElevation = 35.dp),

            colors = CardDefaults.cardColors(containerColor =  colorResource(id = R.color.white))){
                Image(
                    painter = painterResource(id = item.icon), contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .size(40.dp)
                        .padding(4.dp)
                        .weight(1f)
                        .clickable {
                            navigateTo(navController, item.navDestionation.route)
                        },
                    colorFilter =
                    if (item == selectedItem) ColorFilter.tint(color = Color(0xFFD8343F))
                    else ColorFilter.tint(Color.DarkGray)
                )
            }
        }
    }
}