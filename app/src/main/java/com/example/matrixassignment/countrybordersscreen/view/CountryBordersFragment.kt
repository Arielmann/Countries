package com.example.matrixassignment.countrybordersscreen.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.maytronicstestapp.databinding.FragmentCountryBordersBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CountryBordersFragment : Fragment() {

    companion object {
        private val TAG: String? = CountryBordersFragment::class.simpleName
    }

    private lateinit var binding: FragmentCountryBordersBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCountryBordersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}