package com.example.smartparent.data.interfaceparent

interface FirebaseRepository {


    fun getConnectedDeviceContact()
    fun messageSender(blindEmail:String,value :String,isRead:Int,enable:Int)
    fun getMessage(blindEmail:String )
}