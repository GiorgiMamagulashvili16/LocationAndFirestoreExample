package com.example.locationandfirestoreexample.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
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
import com.example.locationandfirestoreexample.databinding.AddUserFragmentBinding
import com.example.locationandfirestoreexample.vm.AddUserViewModel
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class AddUserFragment : Fragment() {

    private var _binding: AddUserFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var userLocation: Location? = null

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

        getLocations()
        observe()
    }

    private fun observe() {
        viewModel.registerResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    d("RESPONSE", "${it.data}")
                    findNavController().navigate(R.id.action_addUserFragment_to_homeFragment)
                }
                is Resource.Error -> {
                    d("RESPONSE ", "${it.errorMessage}")
                }
            }
        })
    }

    private fun getLocations() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(1)
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                binding.btnSave.setOnClickListener {
                    saveIntoFireStore(locationResult.lastLocation)
                }

            }
        }
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
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun saveIntoFireStore(location: Location) {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        val userName = binding.etUserName.text.toString()
        d("USERLOCATION", "${userLocation?.latitude}")
        viewModel.register(
            email,
            pass,
            location.latitude,
            location.longitude,
            userName,

        )
    }

    override fun onPause() {
        super.onPause()
        val removeLoc = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        removeLoc.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                d("DVANEE", "successs")

            } else {
                d("DVANEE", "failure")
            }
        }
    }
}