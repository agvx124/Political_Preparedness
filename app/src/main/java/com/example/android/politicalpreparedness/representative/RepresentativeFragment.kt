package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.*
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        private val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        viewModel = ViewModelProvider(this).get(RepresentativeViewModel::class.java)
        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val representativeAdapter = RepresentativeListAdapter()
        binding.recyclerRepresentative.adapter = representativeAdapter

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()

            viewModel.getAddress(
                    Address(
                        binding.addressLine1.text.toString(),
                        binding.addressLine2.text.toString(),
                        binding.city.text.toString(),
                        binding.state.selectedItem.toString(),
                        binding.zip.text.toString()
                    ))

            viewModel.getRepresentatives()
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            checkLocationPermissions()
        }

        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            representativeAdapter.submitList(it)
        })

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //: Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        try {
            val mLocRequest = LocationRequest.create();
            mLocRequest.interval = 20000
            mLocRequest.smallestDisplacement = 5F
            mLocRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

            fusedLocationClient.requestLocationUpdates(mLocRequest,
                    locationCallback,
                    null)
        } catch (e: SecurityException) {
            Log.e("TAG", e.message!!)
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                val address = geoCodeLocation(location)
                viewModel.getAddress(address)
                viewModel.getRepresentatives()
            }
        }
    }
}