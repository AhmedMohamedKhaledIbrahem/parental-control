package com.example.smartparent.utlity

import android.app.Application
import com.example.smartparent.data.viewmodel.UserProfileViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UserProfileModule {
    @Provides
    fun userProfileViewModel(application: Application): UserProfileViewModel {
        return UserProfileViewModel(application)
    }

}