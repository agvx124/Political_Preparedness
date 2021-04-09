package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application): AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)
    private val electionRepository = ElectionRepository(database)

    //TODO: Create live data val for upcoming elections
    private val _upcomingElection = MutableLiveData<List<Election>>()
    val upcomingElection : LiveData<List<Election>>
        get() = _upcomingElection

    private val _savedElection = MutableLiveData<List<Election>>()
    val savedElection : LiveData<List<Election>>
        get() = _savedElection

    private val _navigateToElection = MutableLiveData<Election>()
    val navigateToElection: LiveData<Election>
        get() = _navigateToElection


    private fun getUpComingElection() {
        viewModelScope.launch {
            _upcomingElection.value = electionRepository.getUpcomingElection()
        }
    }

    private fun getSavedElections(){
        viewModelScope.launch {
            _savedElection.value = electionRepository.getSavedElections()
        }
    }

    fun refreshElections() {
        getUpComingElection()
        getSavedElections()
    }

    fun onElectionClick(election: Election) {
        _navigateToElection.value = election
    }

    fun onElectionClicked() {
        _navigateToElection.value = null
    }


    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}