package com.example.smartparent.ui.design.connecteddevicescreen

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.smartparent.R
import com.example.smartparent.data.FirebaseRepositoryImp
import com.example.smartparent.data.model.ConnectedDeviceContactModel
import com.example.smartparent.data.model.FirebaseMessageModel
import com.example.smartparent.data.viewmodel.ConnectedDeviceContactViewModel
import com.example.smartparent.data.viewmodel.FireStorageViewModel
import com.example.smartparent.theme.skyBlue
import com.example.smartparent.ui.design.customizecompose.CustomButtonCompose
import com.example.smartparent.ui.design.customizecompose.CustomTextCompose
import com.example.smartparent.ui.design.getCustomButtonInstance
import com.example.smartparent.ui.design.getCustomTextInstance
import com.example.smartparent.utlity.ActivityUtils
import com.example.smartparent.utlity.NavControllerUtil

class ConnectedDeviceView(
    private val appCompatActivity: ComponentActivity,
    private val application: Application
) {

    private val textCompose: CustomTextCompose = getCustomTextInstance()
    private val buttonCompose: CustomButtonCompose = getCustomButtonInstance()
    private val repository = FirebaseRepositoryImp(application)


    @Composable
    fun connectedDeviceView(navController: NavHostController) {


        val connectedDeviceContactViewModel: ConnectedDeviceContactViewModel by lazy {
            ViewModelProvider(appCompatActivity)[ConnectedDeviceContactViewModel::class.java]
        }
        var connectedDeviceContactMutableList by remember {
            mutableStateOf<List<ConnectedDeviceContactModel>>(emptyList())
        }

        val connectedDeviceContactLiveData = connectedDeviceContactViewModel
            .getConnectedDeviceContact()

        val observerConnectedDeviceContactModel =
            Observer<List<ConnectedDeviceContactModel>> { newData ->
                connectedDeviceContactMutableList = newData
            }

        connectedDeviceContactLiveData.observe(
            appCompatActivity,
            observerConnectedDeviceContactModel
        )

        Surface() {
            Column(
                Modifier
                    .fillMaxSize()
            ) {
                LogoAndShapeAndTextIntroductionSection()
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp),

                    ) {
                    ConnectedDeviceContact(
                        connectedDeviceContactModel = connectedDeviceContactMutableList
                    )
                    ConnectedFloatingActionButtonSection(navController)
                    YourDestinationScreen(navController = navController)


                }

            }

        }
    }

    @Composable
    fun LogoAndShapeAndTextIntroductionSection() {
        Box(contentAlignment = Alignment.TopCenter) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.35f),
                painter = painterResource(id = R.drawable.shape),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                textCompose.copy(
                    fontSize = 30,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                    .CustomizeText(
                        text = stringResource(id = R.string.connectedDevice),
                        modifier = Modifier.padding(vertical = 80.dp),
                    )
            }
        }
    }


    @Composable
    fun ConnectedDeviceContact(connectedDeviceContactModel: List<ConnectedDeviceContactModel>) {

        // var item=ConnectedDeviceContact.getContactDevice()
        LazyColumn() {


            items(connectedDeviceContactModel)
            { connectedDeviceContact ->
                ListItem(connectedDeviceContact = connectedDeviceContact)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }


    @Composable
    fun ListItem(connectedDeviceContact: ConnectedDeviceContactModel) {
        //var fireStorage = FireStorageViewModel
        // val vModel = FireStorageViewModel
        var context = LocalContext.current
        val fireStorageViewModel =
            ViewModelProvider(ActivityUtils.appCompatActivity!!)[FireStorageViewModel::class.java]
        var locationButtonClicked by remember { mutableStateOf(false) }
        var cameraButtonClicked by remember { mutableStateOf(false) }
        fun resetButtonStates() {
            locationButtonClicked = false
            cameraButtonClicked = false

        }
        val state =fireStorageViewModel.dataLiveData.observeAsState().value
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.height(110.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = skyBlue),
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(24.dp))
                    Image(
                        painter = painterResource(id = R.drawable.account_profile),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    textCompose.copy(
                        fontSize = 15,
                        fontWeight = FontWeight.Bold,
                    ).CustomizeText(text = connectedDeviceContact.blindEmail)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    buttonCompose.copy(shape = 4).CustomizeButton(
                        onclick = {

                            locationButtonClicked = false
                            repository.messageSender(connectedDeviceContact.blindEmail, "0", 1, 1)

                            repository.getMessage(connectedDeviceContact.blindEmail)

                            fireStorageViewModel.dataLiveData.removeObservers(ActivityUtils.appCompatActivity!!)
                            fireStorageViewModel.dataLiveData.observe(ActivityUtils.appCompatActivity!!) { newData ->
                                Log.e("newData is what ?", newData.commandID.toString())

                                if (newData.commandID == 1) {
                                    NavControllerUtil.navController?.navigate("/imageViewer")
                                    if (!cameraButtonClicked) {

                                       // cameraButtonClicked = true
                                      //  resetButtonStates()
                                        Log.d("ButtonAction", "Camera button clicked")

                                    }


                                }


                            }


                        },
                        modifier = Modifier.width(150.dp)
                    ) {
                        textCompose.copy(fontSize = 13).CustomizeText(text = "camera")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    buttonCompose.copy(shape = 4).CustomizeButton(
                        onclick = {

                            cameraButtonClicked = false
                            repository.messageSender(connectedDeviceContact.blindEmail, "0", 2, 1)
                            repository.getMessage(connectedDeviceContact.blindEmail)
                            fireStorageViewModel.dataLiveData.removeObservers(ActivityUtils.appCompatActivity!!)
                            //  fireStorage.updateData(FirebaseMessageModel(0,"",0))
                            fireStorageViewModel.dataLiveData.observe(ActivityUtils.appCompatActivity!!) { newData ->
                                Log.e("newData is what ????", newData.commandID.toString())
                                Log.e("@@link", newData.messageValue.toString())

                                if (newData.commandID == 2) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newData.messageValue)).apply {
                                       // addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)


                                    }
                                    context.startActivity(intent)
                                    if (!locationButtonClicked) {

                                        locationButtonClicked = true
                                        resetButtonStates()
                                        Log.d("ButtonAction", "locationButtonClicked")
                                    }


                                    // fireStorage.updateData(FirebaseMessageModel(0,"",0))


                                }

                            }


                        },

                        modifier = Modifier.width(150.dp)
                    ) {
                        textCompose.copy(fontSize = 13).CustomizeText(text = "location")

                    }
                }
            }
        }
    }

    @Composable
    fun ConnectedFloatingActionButtonSection(navController: NavHostController) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 30.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    /*TODO("EmergencyFloatingActionButton")*/
                    navController.navigate("/connectedContact")
                },
                shape = FloatingActionButtonDefaults.largeShape,
                containerColor = skyBlue,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_24),
                    contentDescription = ""
                )
            }

        }
    }

    @Composable
    fun YourDestinationScreen(navController: NavController) {
        BackHandler {
            navController.navigate("/home")

        }

    }

    @Composable
    fun BackHandler() {
        BackHandler {
            var fireStorage =
                ViewModelProvider(ActivityUtils.appCompatActivity!!)[FireStorageViewModel::class.java]
            NavControllerUtil.navController?.navigate("/connected")
            fireStorage.updateData(FirebaseMessageModel(0, "", 0))

        }
    }


}