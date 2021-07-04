package com.example.locationandfirestoreexample.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
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
import com.example.locationandfirestoreexample.*
import com.example.locationandfirestoreexample.databinding.MainFragmentBinding
import com.example.locationandfirestoreexample.vm.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 0
    }

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!


    private lateinit var locationManager: LocationManager
    private lateinit var currentLocation: Location
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
        requestPermission()
        lifecycleScope.launch {
            viewModel.getUsers()
        }
        initRec()
        observe()
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.fabAddUser.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addUserFragment)
        }

    }

    private fun observe() {
        viewModel.users.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    val nearUserList = mutableListOf<UserModel>()
                    for (i in it.data!!.indices) {
                        val userLoc = Location("userLoc")
                        userLoc.latitude = it.data[i].lat as Double
                        userLoc.longitude = it.data[i].long as Double

                        if (auth.currentUser?.uid != it.data[i].uid && currentLocation.distanceTo(
                                userLoc
                            ) < 50000.0
                        ) {
                            val user = UserModel(
                                it.data[i].uid,
                                it.data[i].userName,
                                it.data[i].status,
                                it.data[i].lat,
                                it.data[i].long,
                            )
                            nearUserList.add(user)
                        }
                    }
                    postAdapter.differ.submitList(nearUserList)
                    d("LOCLOC", "${nearUserList}")

                }
                is Resource.Error -> {
                    d("LOCLOC", "${it.errorMessage}")
                }
            }
        })
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
                    currentLocation = location
                }
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f
                ) { location ->
                    currentLocation = location
                }
            }
        }
    }

    private fun initRec() {
        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun requestPermission() {
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}