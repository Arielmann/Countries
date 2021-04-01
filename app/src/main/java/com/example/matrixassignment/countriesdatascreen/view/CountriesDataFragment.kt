package com.example.matrixassignment.countriesdatascreen.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.matrixassignment.countriesdatascreen.network.NetworkCallback
import com.example.matrixassignment.countriesdatascreen.viewmodel.CountriesDataViewModel
import com.example.maytronicstestapp.databinding.FragmentCountriesDataBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountriesDataFragment : Fragment() {

    companion object {
        private val TAG: String? = CountriesDataFragment::class.simpleName
    }

    private lateinit var binding: FragmentCountriesDataBinding
    private val viewModel: CountriesDataViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCountriesDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.loginBtn.setOnClickListener { performLoginRequest() }
    }


    private fun performLoginRequest() {
        Log.d(TAG, "Starting new login request")
        if (binding.sernumEditText.text.isNotBlank() && binding.passwordEditText.text.isNotBlank()) {
            val email = binding.sernumEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
//            viewModel.requestLogin(email, password, networkCallback)
        }else{
            networkCallback.onFailure("Please insert sernum")
        }

    }

    private val networkCallback: NetworkCallback<String> = object : NetworkCallback<String> {
        override fun onSuccess(token: String) {
//            val action = LoginFragmentDirections.navigateToWelcome(token)
//            findNavController().navigate(action)
//            Log.d(TAG, "Token: $token")
        }

        override fun onFailure(message: String) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

}