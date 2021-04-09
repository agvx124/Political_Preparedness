package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(private val database: ElectionDatabase) {
    private lateinit var mElectionList: List<Election>
    private lateinit var mVoterInfoResponse: VoterInfoResponse

    private val api = CivicsApi.retrofitService

    suspend fun getUpcomingElection() : List<Election> {
        withContext(Dispatchers.IO) {
            val electionResponse = api.getElections()
            mElectionList = electionResponse.elections
        }

        return mElectionList
    }

    suspend fun getVoterInfo(address: String, id: Int) : VoterInfoResponse {
        withContext(Dispatchers.IO){
            val voterInfoResponse = api.getVoterInfo(address, id)
            mVoterInfoResponse = voterInfoResponse
        }

        return mVoterInfoResponse
    }

    suspend fun getSavedElections() : List<Election> {
        withContext(Dispatchers.IO) {
            val electionList = database.electionDao.getAllElection()
            mElectionList = electionList
        }

        return mElectionList
    }

    suspend fun saveElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.insertElection(election)
        }
    }

    fun checkElection(id: Int) : Boolean {
        val election = database.electionDao.getSingleElection(id)
        election?.let {
            return !election!!.name.isNullOrEmpty()
        }
        return false
    }

    suspend fun deleteElection(electionId: Int) {
        withContext(Dispatchers.IO) {
            database.electionDao.deleteElection(electionId)
        }
    }

    suspend fun clearElection() {
        withContext(Dispatchers.IO) {
            database.electionDao.clearElection()
        }
    }

}