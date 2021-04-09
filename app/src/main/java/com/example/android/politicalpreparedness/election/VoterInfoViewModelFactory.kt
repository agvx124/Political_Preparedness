package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election

//TODO: Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(private val application: Application, private val electionId: Int, private val electionDivision: Division): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoterInfoViewModel(application, electionId, electionDivision) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }

}