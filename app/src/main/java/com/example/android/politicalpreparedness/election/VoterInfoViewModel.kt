package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(application: Application, private val electionId: Int, private val electionDivision: Division) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)
    private val electionRepository = ElectionRepository(database)

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _isElectionSaved :MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getVoterInfo()
        }
    }

    fun checkFollowByElection(): LiveData<Boolean> {
        viewModelScope.launch(Dispatchers.IO) {
            val isElectionOnDb = electionRepository.checkFollow(electionId)
            _isElectionSaved.postValue(isElectionOnDb)
        }
        return _isElectionSaved
    }

    private suspend fun getVoterInfo() {
        withContext(Dispatchers.IO) {
            _voterInfo.postValue(CivicsApi.retrofitService.getVoterInfo(electionDivision.state.plus(" ").plus(electionDivision.country), electionId))
        }
    }

    fun onFollowButtonClick(election: Election){
        viewModelScope.launch {
            if (_isElectionSaved.value!!) {
                electionRepository.deleteElection(electionId)
            }
            else {
                electionRepository.saveElection(election)
            }
            _isElectionSaved.value = !_isElectionSaved.value!!
        }

    }

}