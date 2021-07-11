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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationandfirestoreexample.PostAdapter
import com.example.locationandfirestoreexample.R
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.databinding.MainFragmentBinding
import com.example.locationandfirestoreexample.vm.MainViewModel
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private val viewModel: MainViewModel by viewModels()

    private val postAdapter: PostAdapter by lazy { PostAdapter() }

    private val auth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        getLocations()

        initRec()
        binding.fabAddUser.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addPostFragment)
        }
        lifecycleScope.launch {
            delay(3000)

        }
        observe()
    }


    private fun observe() {
        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    postAdapter.differ.submitList(it.data)
                }
                is Resource.Error -> {
                    d("POSTPOST", "${it.errorMessage}")
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
                currentLocation = locationResult.lastLocation
                viewModel.getPosts(locationResult.lastLocation)
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

    private fun initRec() {
        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
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