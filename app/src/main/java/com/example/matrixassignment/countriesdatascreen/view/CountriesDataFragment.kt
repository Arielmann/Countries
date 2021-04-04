package com.example.matrixassignment.countriesdatascreen.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matrixassignment.countriesdatascreen.model.CountrySortStrategy
import com.example.matrixassignment.countriesdatascreen.model.OrderStrategy
import com.example.matrixassignment.crossapplication.events.MatrixAssignmentEvent
import com.example.matrixassignment.countriesdatascreen.viewmodel.CountriesDataViewModel
import com.example.maytronicstestapp.R
import com.example.maytronicstestapp.databinding.FragmentCountriesDataBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountriesDataFragment : Fragment() {

    companion object {
        val TAG: String = CountriesDataFragment::class.java.simpleName
        private const val UPWARD_ARROW_MENU_ITEM_POSITION: Int = 1
        private const val DOWNWARD_ARROW_MENU_ITEM_POSITION: Int = 2
    }

    private lateinit var countriesAdapter: CountriesAdapter
    private lateinit var binding: FragmentCountriesDataBinding
    private val viewModel: CountriesDataViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCountriesDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataObservation()
        setupCountriesList()
        requestCountries()
        setupToolbar()
        binding.countriesListRestartProcessIV.setOnClickListener { requestCountries() }
    }

    private fun requestCountries() {
        showDownloadInProgressUI()
        viewModel.requestCountries()
    }

    private fun setupToolbar() {
        binding.letterGeneratorToolBar.inflateMenu(R.menu.countries_menu)

        binding.letterGeneratorToolBar.setOnMenuItemClickListener { item ->
            when (item?.itemId) {

                R.id.menuActionAscendingOrder -> {
                    countriesAdapter.submitList(viewModel.requestSorting(OrderStrategy.ASCENDING)) {
                        binding.countriesListRV.layoutManager?.scrollToPosition(0)
                        setUpwardArrowImage(R.drawable.ic_baseline_arrow_upward_selected_24)
                        setDownwardArrowImage(R.drawable.ic_baseline_arrow_downward_24)
                    }
                    true
                }

                R.id.menuActionDescendingOrder -> {
                    countriesAdapter.submitList(viewModel.requestSorting(OrderStrategy.DESCENDING)) {
                        binding.countriesListRV.layoutManager?.scrollToPosition(0)
                        setUpwardArrowImage(R.drawable.ic_baseline_arrow_upward_24)
                        setDownwardArrowImage(R.drawable.ic_baseline_arrow_downward_selected_24)
                    }
                    true
                }

                R.id.menuActionSortByNativeName -> {
                    countriesAdapter.submitList(viewModel.requestSorting(CountrySortStrategy.NATIVE_NAME)) {
                        binding.countriesListRV.layoutManager?.scrollToPosition(0)
                    }
                    true
                }

                R.id.menuActionSortByArea -> {
                    countriesAdapter.submitList(viewModel.requestSorting(CountrySortStrategy.AREA)) {
                        binding.countriesListRV.layoutManager?.scrollToPosition(0)
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun setUpwardArrowImage(@DrawableRes icon: Int) {
        if (binding.letterGeneratorToolBar.menu.getItem(UPWARD_ARROW_MENU_ITEM_POSITION) != null) { //If menu is inflated
            binding.letterGeneratorToolBar.menu.getItem(UPWARD_ARROW_MENU_ITEM_POSITION).setIcon(icon)
        }
    }

    private fun setDownwardArrowImage(@DrawableRes icon: Int) {
        if (binding.letterGeneratorToolBar.menu.getItem(DOWNWARD_ARROW_MENU_ITEM_POSITION) != null) { //If menu is inflated
            binding.letterGeneratorToolBar.menu.getItem(DOWNWARD_ARROW_MENU_ITEM_POSITION).setIcon(icon)
        }
    }

    /**
     * Starts countries data observation via the fragment's viewModel.
     * When the observer is activated, it's data will be displayed in the countries list
     */
    private fun setupDataObservation() {
        viewModel.countriesMap.observe(viewLifecycleOwner, { countries ->
            countries?.let {
                Log.d(TAG, "New countries received from viewModel")
                countriesAdapter.submitList(viewModel.requestSorting())
                hideProgressionUI()
            } ?: Log.w(TAG, "No countries data received")
        })

        observeFailureScenario(
            viewModel.countryDownloadErrorEvent,
            getString(R.string.error_audio_data_fetch_failed)
        )

    }

    /**
     * Displaying the title and progressbar and hiding any error messages\views
     */
    private fun showDownloadInProgressUI() {
        Handler(Looper.getMainLooper()).postDelayed({ binding.countriesListProgressBar.show() }, 1)
        binding.countriesListRestartProcessIV.visibility = View.GONE
        binding.countriesListTitleTV.visibility = View.VISIBLE
        binding.countriesListErrorTV.visibility = View.INVISIBLE
    }

    /**
     * Hiding the progressbar and hiding any error messages\views
     */
    private fun hideProgressionUI() {
        binding.countriesListProgressBar.hide()
        binding.countriesListTitleTV.visibility = View.VISIBLE
        binding.countriesListErrorTV.visibility = View.INVISIBLE
    }


    /**
     * Defining the UI display after an unsuccessful scenario (not necessarily an error)
     *
     * @param event The occurring event
     * @param message Message to be displayed to the user
     */
    private fun observeFailureScenario(event: MutableLiveData<MatrixAssignmentEvent>, message: String = getString(R.string.error_unknown_failure)) {
        event.observe(viewLifecycleOwner, { result ->
            result?.let {
                if (result.message.isNotBlank()) {
                    onDataDisplayRequestFailed(result.message)
                } else {
                    onDataDisplayRequestFailed(message)
                }
            } ?: onDataDisplayRequestFailed(getString(R.string.error_null_event))
        })
    }

    /**
     * Sets the countries list
     */
    private fun setupCountriesList() {
        countriesAdapter = CountriesAdapter()
        countriesAdapter.onItemClickListener = { country ->
            Log.d(TAG, "Navigating to country ${country.name}")
            viewModel.countriesBordersMap.value!![country.alpha3Code]?.let {
                val action = CountriesDataFragmentDirections.navigateToCountryBorders(it.toTypedArray())
                findNavController().navigate(action)
            } ?: Toast.makeText(requireContext(), getString(R.string.error_cannot_access_borders), LENGTH_LONG).show()
        }

        binding.countriesListRV.apply {
            adapter = countriesAdapter
            layoutManager = LinearLayoutManager(this@CountriesDataFragment.requireContext())
        }
    }

    /**
     * Displayed whenever the data request operation ends with an error
     * @param reason Error's reason
     */
    private fun onDataDisplayRequestFailed(reason: String) {
        Log.d(TAG, reason)
        binding.countriesListProgressBar.hide()
        binding.countriesListErrorTV.text = reason
        binding.countriesListErrorTV.visibility = View.VISIBLE
        binding.countriesListTitleTV.visibility = View.INVISIBLE
        binding.countriesListRestartProcessIV.visibility = View.VISIBLE
        binding.countriesListRestartProcessIV.setImageResource(R.drawable.ic_baseline_refresh_gray_48)
    }

}