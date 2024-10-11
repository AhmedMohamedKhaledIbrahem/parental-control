package com.example.smartparent.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartparent.data.model.FirebaseMessageModel

class FireStorageViewModel  : ViewModel(){
    private val _dataLiveData = MutableLiveData<FirebaseMessageModel>()


    val dataLiveData: LiveData<FirebaseMessageModel>
        get() = _dataLiveData

    // Function to update the LiveData object with new data
    fun updateData(newData: FirebaseMessageModel) {
        _dataLiveData.value = newData
    }

}