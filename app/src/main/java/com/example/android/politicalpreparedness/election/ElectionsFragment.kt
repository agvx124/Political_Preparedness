package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this, ElectionsViewModelFactory(requireActivity().application)).get(ElectionsViewModel::class.java)

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val upcomingAdapter = ElectionListAdapter(ElectionListener {
            viewModel.onElectionClick(it)
        })
        binding.upcomingRecyclerView.adapter = upcomingAdapter

        val savedAdapter = ElectionListAdapter(ElectionListener {
            viewModel.onElectionClick(it)
        })
        binding.savedRecyclerView.adapter = savedAdapter


        viewModel.upcomingElection.observe(viewLifecycleOwner, Observer {
            upcomingAdapter.submitList(it)
        })

        viewModel.savedElection.observe(viewLifecycleOwner, Observer {
            savedAdapter.submitList(it)
        })

        viewModel.navigateToElection.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                viewModel.onElectionClicked()
            }
        })


        return binding.root
    }

    // Refresh adapters when fragment loads
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshElections()
    }

}