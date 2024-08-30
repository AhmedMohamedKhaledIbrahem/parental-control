package com.example.smartparent.ui.design.homescreen


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartparent.R
import com.example.smartparent.auth.GoogleAuthUiClient
import com.example.smartparent.utlity.BlindDataCache
import com.example.smartparent.utlity.EmailCache
import com.example.smartparent.utlity.NotificationHashCode
import com.example.smartparent.utlity.TypeIdUserLogin
import com.example.smartparent.data.model.UserDataFromGoogleAccount
import com.example.smartparent.data.model.UserProfileModel
import com.example.smartparent.data.viewmodel.ConnectedDeviceContactViewModel
import com.example.smartparent.data.viewmodel.FirebaseViewModel
import com.example.smartparent.data.viewmodel.UserProfileViewModel
import com.example.smartparent.theme.skyBlue
import com.example.smartparent.ui.design.customizecompose.CustomImageCompose
import com.example.smartparent.ui.design.customizecompose.CustomTextCompose
import com.example.smartparent.ui.design.getCustomImageInstance
import com.example.smartparent.ui.design.getCustomTextInstance
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlin.system.exitProcess


class HomeView(private val context: Context, private val appCompatActivity: ComponentActivity) {
    private val textCompose: CustomTextCompose = getCustomTextInstance()
    private val imageCompose: CustomImageCompose = getCustomImageInstance()
    private var parse: String? = null


    @Composable
    fun homeView(
        navController: NavController, activity: ComponentActivity, vm: FirebaseViewModel,
        googleAuthUiClient: GoogleAuthUiClient,
        userDataFromGoogleAccount: UserDataFromGoogleAccount?,
    ) {
        vm.showNotification(context)

        Surface {
            Column(
                Modifier
                    .fillMaxSize()
            ) {
                LogoAndShapeAndTextIntroductionSection()
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 2.dp)
                ) {

                    ImageProfileAndFullNameSection(
                        userDataFromGoogleAccount,
                        vm,
                        googleAuthUiClient,
                    )
                    Spacer(modifier = Modifier.height(120.dp))
                    UserHomeSection(navController, vm, googleAuthUiClient)
                }
                YourDestinationScreen(navController, activity)

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
                ).CustomizeText(
                    text = stringResource(id = R.string.home),
                    modifier = Modifier.padding(vertical = 80.dp),
                )

            }


        }
    }

    @Composable
    fun ImageProfileAndFullNameSection(
        userDataFromGoogleAccount: UserDataFromGoogleAccount?,
        vm: FirebaseViewModel,
        googleAuthUiClient: GoogleAuthUiClient,

        ) {


        val userProfileViewModel: UserProfileViewModel by lazy {
            ViewModelProvider(appCompatActivity)[UserProfileViewModel::class.java]
        }

        var userProfileModelMutable by remember {
            mutableStateOf(value = UserProfileModel())
        }

        val userProfileLiveData = userProfileViewModel.getUserProfileSingleData()
        val observerUserProfileModel = Observer<UserProfileModel?> { newData ->
            if (newData != null) {
                userProfileModelMutable = newData
            }
        }
        userProfileLiveData.observe(appCompatActivity, observerUserProfileModel)


        var selectedImage by remember {
            mutableStateOf(userProfileModelMutable.uri.toUri())
        }


        val pickPhoto =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    parse = getImagePathFromUri(context, uri)
                    userProfileModelMutable.uri = parse.toString()
                    selectedImage = userProfileModelMutable.uri.toUri()
                    uploadImageToFirebase(selectedImage,userProfileViewModel,userProfileModelMutable)
                }


            }




        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.height(100.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = skyBlue
                ),
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    val shape = RoundedCornerShape(
                        topStart = 50.dp,
                        topEnd = 50.dp,
                        bottomStart = 50.dp,
                        bottomEnd = 50.dp
                    )
                    Log.i("userProfileTest", "$selectedImage")
                    if (userProfileModelMutable.uri.isNotEmpty()){
                        AsyncImage(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(shape = shape)
                                .clickable { pickPhoto.launch("image/*") },
                            model = userProfileModelMutable.uri,
                            contentDescription = "Image Profile",
                        )
                    }else {
                        Image(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(shape = shape)
                                .clickable { pickPhoto.launch("image/*") },
                            painter = painterResource(id = R.drawable.account_profile),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (userProfileModelMutable.fullName.isNotEmpty()) {
                        textCompose.copy(
                            fontSize = 18,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ).CustomizeTextImage(text = userProfileModelMutable.fullName)
                    }

                }
            }
        }
    }

    @Composable
    fun UserHomeSection(
        navController: NavController,
        vm: FirebaseViewModel,
        googleAuthUiClient: GoogleAuthUiClient,
    ) {
        val logoutViewModel: UserProfileViewModel by lazy {
            ViewModelProvider(appCompatActivity)[UserProfileViewModel::class.java]
        }
        val deleteConnectedDeviceContact :ConnectedDeviceContactViewModel by lazy {
            ViewModelProvider(appCompatActivity)[ConnectedDeviceContactViewModel::class.java]
        }

        Spacer(modifier = Modifier.height(8.dp))
        imageCompose.copy(
            modifierSizeImage = 80,
            fontSizeTextImage = 15,
            fontWeightTextImage = FontWeight.Bold,
        ).CustomizeImage(
            icon = R.drawable.connectedlogo,
            text = stringResource(id = R.string.connected)
        ) {
            navController.navigate("/connected")

        }
        Spacer(modifier = Modifier.height(8.dp))
        imageCompose.copy(
            modifierSizeImage = 80,
            fontSizeTextImage = 15,
            fontWeightTextImage = FontWeight.Bold,
        ).CustomizeImage(
            icon = R.drawable.logoutlogo,
            text = stringResource(id = R.string.LogoutLogo)
        ) {
            googleAuthUiClient.signOut()
            logoutViewModel.deleteAll()
            deleteConnectedDeviceContact.deleteAllConnectedDeviceContact()
            vm.signOut()
            EmailCache.emailCache = ""
            BlindDataCache.blindEmailCache = ""
            BlindDataCache.blindIdCache = ""
            NotificationHashCode.value = 0
            TypeIdUserLogin.typeIdUserLogin = null
            navController.navigate("/login")

        }


    }


    private fun getImagePathFromUri(context: Context, uri: Uri): String? {
        var path: String? = null
        //val context = context
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            path = it.getString(columnIndex)
        }
        cursor?.close()
        return path
    }


    @Composable
    fun YourDestinationScreen(navController: NavController, activity: ComponentActivity) {
        BackHandler {
            navController.popBackStack()
            exitApp()
        }

    }

    private fun getImagePathFromUri(uri: Uri): String? {
        var path: String? = null
        val context: Context = context
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                path = cursor.getString(columnIndex)
            }

        }
        cursor?.close()
        return path
    }

    private fun uploadImageToFirebase(uri: Uri, userProfileViewModel: UserProfileViewModel ,userProfileModelMutable:UserProfileModel) {
        val photoRef = FirebaseStorage.getInstance().getReference("photos/photo2.jpg")

        // Verify the URI before attempting to putFile
        Log.d("UploadImageToFirebase", "Uploading image from URI: $uri")

        try {
            // Convert Uri to File
            val file = File(uri.path!!)

            // Proceed with putFile operation
            val uploadTask = photoRef.putFile(Uri.fromFile(file))

            uploadTask.addOnSuccessListener { uploadTaskSnapshot ->
                Log.d("UploadImageToFirebase", "Photo uploaded successfully")

                // Once the photo is uploaded, get its download URL
                photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val photoUrl = downloadUri.toString()
                    Log.d("DownloadURL", "Photo download URL: $photoUrl")

                    // Update user profile with the image URL
                    val test = userProfileModelMutable
                    test.uri = photoUrl
                    userProfileViewModel.insertUserProfile(test)
                }.addOnFailureListener { downloadException ->
                    Log.e("DownloadURL", "Error getting download URL", downloadException)
                }
            }.addOnFailureListener { uploadException ->
                Log.e("UploadImageToFirebase", "Error uploading photo", uploadException)
            }
        } catch (e: Exception) {
            Log.e("UploadImageToFirebase", "Error: ${e.message}", e)
        }
    }




    private fun exitApp() {
        exitProcess(0)
    }

}