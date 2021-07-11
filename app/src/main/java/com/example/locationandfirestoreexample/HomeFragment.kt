package com.example.locationandfirestoreexample

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.locationandfirestoreexample.databinding.HomeFragmentBinding
import com.example.locationandfirestoreexample.vm.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var _binding: HomeFragmentBinding? = null
    private val binding: HomeFragmentBinding get() = _binding!!
    private var clicks = 0
    private var currentLocation: Location? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        onClickRequestPermission()
        binding.btnLogIn.setOnClickListener {
            viewModel.logIn(
                binding.etEmail.text.toString(),
                binding.etPass.text.toString(),
                binding.etEmail
            )
        }
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addUserFragment)
        }
        observe()
    }

    private fun observe() {
        viewModel.login.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_homeFragment_to_mainFragment)
                }
                is Resource.Error -> {
                    d("LOGINERROR,", "${it.errorMessage}")
                }
            }
        })
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    Snackbar.make(binding.root, "All pers is granted", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK") {

                        }.show()

                } else {
                    clicks++
                    if (clicks == 1) {
                        onClickRequestPermission()
                    } else if (clicks == 2) {
                        Snackbar.make(binding.root, "Go to settings", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK") {

                            }.show()
                    }
                }
            }
        }


    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Snackbar.make(binding.root, "App needs this perms", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK") {
                        requestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }.show()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
}