package com.example.locationandfirestoreexample

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.locationandfirestoreexample.databinding.AddPostFragmentBinding
import com.example.locationandfirestoreexample.vm.AddPostViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddPostFragment : Fragment() {
    private var _binding:AddPostFragmentBinding?  = null
    private val binding get() = _binding!!

    private val viewModel:AddPostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddPostFragmentBinding.inflate(layoutInflater,container,false)
        init()
        return binding.root
    }
    private fun init(){

        val postText = binding.etUserName.text

        binding.btnAddPost.setOnClickListener {
           viewModel.addPost(binding.etUserName.text.toString())
            lifecycleScope.launch {
                delay(5000)
                findNavController().navigate(R.id.action_addPostFragment_to_mainFragment)
            }
        }
        observe()
    }
    private fun observe(){
        viewModel.addPost.observe(viewLifecycleOwner,{
            when(it){
                is Resource.Success -> {

                }
                is Resource.Error ->{
                    d("ADDPOSTERROR","${it.errorMessage}")
                }
            }
        })
    }
}