package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val electionDivision = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        viewModel = ViewModelProvider(this, VoterInfoViewModelFactory(requireActivity().application, electionId, electionDivision)).get(VoterInfoViewModel::class.java)

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.saveElectionBtn.setOnClickListener {
            viewModel.onFollowButtonClick(viewModel.voterInfo.value!!.election)
        }

        viewModel.checkFollowByElection().observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.saveElectionBtn.text = "UnFollow Election"
            }
            else {
                binding.saveElectionBtn.text = "Follow Election"
            }
        })

        return binding.root
    }

}