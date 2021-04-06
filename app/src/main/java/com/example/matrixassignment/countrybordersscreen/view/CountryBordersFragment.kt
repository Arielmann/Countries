package com.example.matrixassignment.countrybordersscreen.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matrixassignment.R
import com.example.matrixassignment.countriesdatascreen.model.Country
import com.example.matrixassignment.countriesdatascreen.view.CountriesAdapter
import com.example.matrixassignment.countrybordersscreen.view.CountryBordersFragmentArgs.fromBundle
import com.example.matrixassignment.databinding.FragmentCountryBordersBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment responsible to display data regarding the bordering countries of a given country
 */
@AndroidEntryPoint
class CountryBordersFragment : Fragment() {

    private lateinit var borderingCountries: Array<Country>

    companion object {
        private val TAG: String? = CountryBordersFragment::class.simpleName
    }

    private lateinit var binding: FragmentCountryBordersBinding
    private lateinit var countriesAdapter: CountriesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCountryBordersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        borderingCountries = fromBundle(requireArguments()).stringKeyBorderingCountries
        setupToolbar()
        setupBorderingCountriesList()
    }

    private fun setupToolbar() {
        binding.countriesToolBar.setNavigationIcon(R.drawable.ic_arrow_back_24)
        binding.countriesToolBar.setNavigationOnClickListener {
            Log.d(TAG, "Navigating to countries list")
            findNavController().popBackStack()
        }
    }

    /**
     * Sets and displays the bordering countries list
     */
    private fun setupBorderingCountriesList() {
        countriesAdapter = CountriesAdapter()

        binding.borderingCountriesRV.apply {
            adapter = countriesAdapter
            layoutManager = LinearLayoutManager(this@CountryBordersFragment.requireContext())
        }

        if (borderingCountries.isNotEmpty()) {
            binding.borderingCountriesNoBordersTV.visibility = View.INVISIBLE
            countriesAdapter.submitList(borderingCountries.toList())
        }
    }

}