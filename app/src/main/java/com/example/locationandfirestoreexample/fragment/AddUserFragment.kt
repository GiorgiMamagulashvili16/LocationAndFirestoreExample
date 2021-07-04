package com.example.locationandfirestoreexample.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.locationandfirestoreexample.R
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.UserModel
import com.example.locationandfirestoreexample.databinding.AddUserFragmentBinding
import com.example.locationandfirestoreexample.vm.AddUserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AddUserFragment : Fragment() {

    private var _binding: AddUserFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var userLocation: Location
    private lateinit var locationManager: LocationManager

    private val viewModel: AddUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddUserFragmentBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.btnSave.setOnClickListener {
            saveIntoFireStore()
        }
        getLocations()
        observe()
    }

    private fun observe() {
        viewModel.registerResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    d("RESPONSE", "${it.data}")
                    findNavController().navigate(R.id.action_addUserFragment_to_mainFragment)
                }
                is Resource.Error -> {
                    d("RESPONSE ", "${it.errorMessage}")
                }
            }
        })
    }

    private fun saveIntoFireStore() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        val userName = binding.etUserName.text.toString()
        val status = binding.etStatus.text.toString()
        d("USERLOCATION","${userLocation.latitude}")
        viewModel.register(
            email,
            pass,
            userLocation.latitude,
            userLocation.longitude,
            userName,
            status
        )
    }

    private fun getLocations() {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGPS || hasNetwork) {
            if (hasGPS) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f
                ) { location ->
                    userLocation = location
                }
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f
                ) { location ->
                    userLocation = location
                }
            }
        }
    }
}