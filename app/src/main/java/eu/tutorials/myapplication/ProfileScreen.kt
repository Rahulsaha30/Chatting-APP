package eu.tutorials.myapplication



import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun ProfileScreen(navController: NavController, vm: CAViewModel) {

    val userData = vm.userData.value
    var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
    var number by rememberSaveable { mutableStateOf(userData?.number ?: "") }


    val scrollState = rememberScrollState()
    val focus = LocalFocusManager.current


    Column {

        ProfileContent(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(top = 3.dp),
            vm = vm,
            name = name,
            number = number,
            onNameChange = { name = it },
            onNumberChange = { number = it },
            onSave = {
                focus.clearFocus(true)
                vm.updateProfileData(name, number)
            },
            onBack = {
                focus.clearFocus(true)
                navigateTo(navController, DestinationScreen.ChatList.route)
            },
            onLogout = {
                vm.onLogout()
                navigateTo(navController, DestinationScreen.Login.route)
            }
        )

        BottomNavigationMenu(
            modifier = Modifier.padding(bottom = 40.dp),
            selectedItem = BottomNavigationItem.PROFILE,
            navController = navController
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier,
    vm: CAViewModel,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val imageUrl = vm.userData.value?.imageUrl

    Column(modifier = modifier,horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = colorResource(
                id = R.color.Customdarkblue
            ), modifier = Modifier.clickable { onBack.invoke() })
            Text(text = "Save",fontWeight = FontWeight.Bold,fontSize = 20.sp,color = colorResource(
                id = R.color.Customdarkblue
            ) , modifier = Modifier.clickable { onSave.invoke() })
        }

        CommonDivider()

       ProfileImage(imageUrl = imageUrl, vm = vm)

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name", fontWeight = FontWeight.Bold,color = colorResource(
                id = R.color.Customdarkblue
            ), modifier = Modifier.width(100.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.colors(
                 focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Number", fontWeight = FontWeight.Bold,color = colorResource(
                id = R.color.Customdarkblue
            ), modifier = Modifier.width(100.dp))
            TextField(
                value = number,
                onValueChange = onNumberChange,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }


        CommonDivider()

        Box(
            modifier = Modifier
                .background(
                    color = colorResource(
                        id = R.color.Customdarkblue
                    )
                )
                .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                .clickable { onLogout.invoke() }
                .padding(8.dp)) {
            Text(text = "LOG OUT",fontWeight = FontWeight.Bold,color = Color.White, fontSize = 20.sp)
        }

    }
}

@Composable
fun ProfileImage(imageUrl: String?, vm: CAViewModel) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            vm.uploadProfileImage(uri)
        }
    }

    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                launcher.launch("image/*")
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(shape = CircleShape, modifier = Modifier
                .padding(8.dp)
                .size(100.dp)) {
                CommonImage(data = imageUrl)
            }
            Text(
                text = "Change profile picture", fontWeight = FontWeight.Bold,
                color = colorResource(
                    id = R.color.Customdarkblue
                ),
            )
        }

        val isLoading = vm.inProgress.value
        if (isLoading)
            CommonProgressSpinner()
    }
}

