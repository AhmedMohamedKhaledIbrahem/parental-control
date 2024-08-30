package com.example.smartparent.data

import androidx.lifecycle.LiveData
import com.example.smartparent.data.interfaceparent.ParentDao
import com.example.smartparent.data.model.ConnectedDeviceContactModel
import com.example.smartparent.data.model.UserProfileModel

class ParentRepository(private val parentDao: ParentDao) {

    fun insertUserProfile(userProfileModel: UserProfileModel) {
        parentDao.insertUserProfile(userProfileModel)
    }

    fun getUserProfile(): LiveData<List<UserProfileModel>> {
        return parentDao.getUserProfile()
    }

    fun getUserProfileSingleData(): LiveData<UserProfileModel> {
        return parentDao.getUserProfileSingleData()
    }

    fun deleteAll() {
        parentDao.deleteAll()
    }





    fun insertConnectedDeviceContact(connectedDeviceContactModel: ConnectedDeviceContactModel) {
        parentDao.insertConnectedDeviceContact(connectedDeviceContactModel)
    }

    fun getConnectedDeviceContact(): LiveData<List<ConnectedDeviceContactModel>> {
        return parentDao.getConnectedDeviceContact()
    }


    fun getConnectedDeviceContactByBlindId(blindId:String):ConnectedDeviceContactModel?{
        return parentDao.getConnectedDeviceContactByBlindId(blindId)
    }
      fun getConnectedDeviceContactByBlindEmail(blindEmail:String):ConnectedDeviceContactModel?{
        return parentDao.getConnectedDeviceContactByBlindEmail(blindEmail)
    }

    fun deleteAllConnectedDeviceContact() {
        parentDao.deleteAllConnectedDeviceContact()
    }



}