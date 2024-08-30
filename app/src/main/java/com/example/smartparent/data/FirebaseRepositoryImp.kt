package com.example.smartparent.data

import android.app.Application
import android.util.Log
import com.example.smartparent.data.interfaceparent.FirebaseRepository
import com.example.smartparent.data.model.ConnectedDeviceContactModel
import com.example.smartparent.data.model.FirebaseMessageModel
import com.example.smartparent.data.viewmodel.ConnectedDeviceContactViewModel
import com.example.smartparent.data.viewmodel.FireStorageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class FirebaseRepositoryImp(private val application: Application) : FirebaseRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dataReferenceConnectedUsers: DatabaseReference =
        database.getReference("connectedUsers")
    private val dataReferenceMessages: DatabaseReference = database.getReference("messages")

    override fun getConnectedDeviceContact() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        val firstPartIdLength = userId?.length ?: 0
        val firstPartId = userId?.take(firstPartIdLength)
        dataReferenceConnectedUsers.child("connectedUser").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach { chileSnapshot ->
                        val childUserId = chileSnapshot.key
                        val connectedValue = chileSnapshot.child("connected")
                            .getValue(Int::class.java)
                        val blindId = chileSnapshot.child("blindId")
                            .value as String
                        val exist = ConnectedDeviceContactViewModel(application)
                            .getConnectedDeviceContactByBlindId(blindId)
                        val connectedContact =
                            chileSnapshot.getValue(ConnectedDeviceContactModel::class.java)

                        if (childUserId != null && childUserId.take(firstPartIdLength) == firstPartId
                            && connectedValue == 1 && exist == null && connectedContact != null
                        ) {
                            ConnectedDeviceContactViewModel(application)
                                .insertConnectedDeviceContact(connectedContact)
                            return@forEach

                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun messageSender(blindEmail:String , value :String ,isRead:Int,enable:Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val userIdFirstPart = user?.uid
        val personEmail = user?.email

        val getConnectedContact = ConnectedDeviceContactViewModel(application)
            .getConnectedDeviceContactByBlindEmail(blindEmail)

        val userIdSecondPart = getConnectedContact?.blindId
        val nodeFirebaseId = "${userIdFirstPart}${userIdSecondPart?.take(4)}"
        val nodeFirebaseSecondId = "${userIdSecondPart}${userIdFirstPart?.take(4)}"
        val currentTimeMillis = System.currentTimeMillis()
        val timeStamp = Date(currentTimeMillis)
        if (personEmail == getConnectedContact?.personEmail && getConnectedContact?.connected == 1 &&
            userIdFirstPart == getConnectedContact.personId

        ) {
            Log.e("condition true ?" ,"yes")
            val messageInfo = mapOf(
                "receiverId" to getConnectedContact.blindId,
                "senderId" to getConnectedContact.personId,
                "timeStamp" to timeStamp,
                "value" to value,
                "isRead" to isRead,
                "typeId" to "1",
                "enable" to enable
            )
            dataReferenceMessages.child("message").child(nodeFirebaseId).setValue(messageInfo)

        } else   if (personEmail == getConnectedContact?.blindEmail && getConnectedContact?.connected == 1 &&
            userIdSecondPart == getConnectedContact.blindId
        ) {
            Log.e("condition true ?" ,"yes")
            val messageInfo = mapOf(
                "receiverId" to getConnectedContact.blindId,
                "senderId" to getConnectedContact.personId,
                "timeStamp" to timeStamp,
                "value" to value,
                "isRead" to isRead,
                "typeId" to "1",
                "enable" to enable
            )
            dataReferenceMessages.child("message").child(nodeFirebaseSecondId).setValue(messageInfo)

        }

    }

    override  fun getMessage(blindEmail:String ) {
       var fireStorage=FireStorageViewModel
        var firebaseMessageModel: FirebaseMessageModel
        val user = FirebaseAuth.getInstance().currentUser
        val userIdFirstPart = user?.uid
        val personEmail = user?.email

        val getConnectedContact = ConnectedDeviceContactViewModel(application)
            .getConnectedDeviceContactByBlindEmail(blindEmail)

        val userIdSecondPart = getConnectedContact?.blindId
        val nodeFirebaseId = "${userIdFirstPart}${userIdSecondPart?.take(4)}"
        var nodeID = "${userIdSecondPart}${userIdFirstPart?.take(4)}"
        val currentTimeMillis = System.currentTimeMillis()
        val timeStamp = Date(currentTimeMillis)

        dataReferenceMessages.child("message").endAt(userIdFirstPart?.take(4)).addValueEventListener(object :ValueEventListener{
            override   fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { dataSnapshot ->
                    var key = dataSnapshot.key

                        var isRead = dataSnapshot.child("isRead").getValue(Int::class.java)

                    if (personEmail == getConnectedContact?.personEmail && getConnectedContact?.connected == 1 &&
                        userIdFirstPart == getConnectedContact.personId && key ==nodeID
                    ) {

                        var messageValue=dataSnapshot.child("value").getValue(String::class.java)
                        var commandID=dataSnapshot.child("isRead").getValue(Int::class.java)
                        var enableID=dataSnapshot.child("enable").getValue(Int::class.java)
                        fireStorage.updateData(FirebaseMessageModel(0,"",0))
                        fireStorage.updateData(FirebaseMessageModel(commandID,messageValue,enableID))
                        Log.e("fireBase",messageValue.toString())
                        Log.e("condition true ?" ,"yes")

                    }

                }
                    //dataReferenceMessages.child("message").removeEventListener(this)

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }
    /* override fun getConnectedDeviceContact() {
         val blindId = FirebaseAuth.getInstance().currentUser?.uid?.take(4)
         dataReferenceConnectedUsers.child("connectedUser").orderByKey().endAt(blindId)
             .addListenerForSingleValueEvent(object : ValueEventListener {
                 override fun onDataChange(dataSnapshot: DataSnapshot) {
                     for (childSnapshot in dataSnapshot.children) {
                         val connectedValue =
                             childSnapshot.child("connected").getValue(Int::class.java)
                         if (connectedValue == 1 ) {
                             val blindEmail =
                                 childSnapshot.child("blindEmail").getValue(String::class.java)
                             val blindID =
                                 childSnapshot.child("blindId").getValue(String::class.java)
                             val personEmail =
                                 childSnapshot.child("personEmail").getValue(String::class.java)
                             val personId =
                                 childSnapshot.child("personId").getValue(String::class.java)
                             val connected =
                                 childSnapshot.child("connected").getValue(Int::class.java)
                             val connectedDeviceContactModel = ConnectedDeviceContactModel(
                                 blindId = blindID!!,
                                 blindEmail = blindEmail!!,
                                 personId = personId!!,
                                 personEmail = personEmail!!,
                                 connected = connected!!,
                             )
                             val existContact = ConnectedDeviceContactViewModel(application)
                                 .getConnectedDeviceContactById(personId)
                             Log.i("why is null","$existContact")
                             if (existContact==null ){
                                 ConnectedDeviceContactViewModel(application)
                                     .insertConnectedDeviceContact(connectedDeviceContactModel)
                             }

                         }

                     }
                 }

                 override fun onCancelled(p0: DatabaseError) {

                 }

             })
     }*/
}