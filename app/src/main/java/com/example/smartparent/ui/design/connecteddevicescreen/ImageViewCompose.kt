package com.example.smartparent.ui.design.connecteddevicescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.example.smartparent.data.model.FirebaseMessageModel
import com.example.smartparent.data.viewmodel.FireStorageViewModel
import com.example.smartparent.utlity.ActivityUtils
import com.example.smartparent.utlity.NavControllerUtil


class ImageViewCompose {


    @Composable
    fun ImageViewer() {
       // val vModel = FireStorageViewModel

        val fireStorageViewModel =
            ViewModelProvider(ActivityUtils.appCompatActivity!!)[FireStorageViewModel::class.java]
        var firebaseMessageModel = FirebaseMessageModel(0, "", 0)
        // Use Coil to load the image from the URL
      /*  fireStorageViewModel.dataLiveData.observe(ActivityUtils.appCompatActivity!!) { newData ->

            firebaseMessageModel = newData


        }*/
        val state =fireStorageViewModel.dataLiveData.observeAsState().value

        if (/*firebaseMessageModel.commandID*/state?.commandID == 1) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Display the image using the Image composable


                AsyncImage(

                    model =state?.messageValue ,//firebaseMessageModel.messageValue,
                    contentDescription = null,
                )


            }
        }
        //

        BackHandler()
    }


    @Composable
    fun BackHandler() {
        androidx.activity.compose.BackHandler {
            var fireStorage = ViewModelProvider(ActivityUtils.appCompatActivity!!)[FireStorageViewModel::class.java]
            NavControllerUtil.navController?.navigate("/connected") {
                popUpTo("/connected") { inclusive = true }  // Clears the back stack to avoid navigation issues
            }
            fireStorage.updateData(FirebaseMessageModel(0, "", 0))

        }
    }
}