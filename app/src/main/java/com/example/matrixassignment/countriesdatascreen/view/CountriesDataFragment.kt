package com.example.matrixassignment.countriesdatascreen.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matrixassignment.crossapplication.events.MatrixAssignmentEvent
import com.example.matrixassignment.countriesdatascreen.viewmodel.CountriesDataViewModel
import com.example.maytronicstestapp.R
import com.example.maytronicstestapp.databinding.FragmentCountriesDataBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountriesDataFragment : Fragment() {

    companion object {
        val TAG: String = CountriesDataFragment::class.java.simpleName
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
        setupAudioFilesList()
        viewModel.requestAudioDataList()
        showDownloadInProgressUI()
    }

    /**
     * Starts audio files data observation via the fragment's viewModel.
     * When the observer is activated, it's data will be displayed in the audio files list
     */
    private fun setupDataObservation() {
        viewModel.countriesList.observe(viewLifecycleOwner, { audioFiles ->
            audioFiles?.let {
                Log.d(TAG, "Audio file data received from viewModel")
                countriesAdapter.submitList(audioFiles)
                hideProgressionUI()
            } ?: Log.w(TAG, "No audio data received")
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
        binding.progressBar.show()
        binding.restartProcessIV.visibility = View.INVISIBLE
        binding.errorTV.visibility = View.INVISIBLE
        binding.titleTV.visibility = View.VISIBLE
    }

    /**
     * Hiding the progressbar and hiding any error messages\views
     */
    private fun hideProgressionUI() {
        binding.progressBar.hide()
        binding.errorTV.visibility = View.INVISIBLE
        binding.titleTV.visibility = View.VISIBLE
    }


    /**
     * Defining the UI display after an unsuccessful scenario (not necessarily an error)
     *
     * @param event The occurring event
     * @param message Message to be displayed to the user
     */
    private fun observeFailureScenario(
        event: MutableLiveData<MatrixAssignmentEvent>,
        message: String = getString(R.string.error_unknown_failure)) {
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
     * Sets the audio data list
     */
    private fun setupAudioFilesList() {
        countriesAdapter = CountriesAdapter()

        countriesAdapter.onItemClickListener = { country ->
            Log.d(TAG, "Navigating to country ${country.name}")
            val action = CountriesDataFragmentDirections.navigateToCountryBorders(country)
            findNavController().navigate(action)
        }

        binding.audioDataRV.apply {
            adapter = countriesAdapter
            layoutManager = LinearLayoutManager(this@CountriesDataFragment.requireContext())
        }
    }

    /**
     * Displayed whenever the image request operation ends with an error
     */
    private fun onDataDisplayRequestFailed(reason: String) {
        Log.d(TAG, reason)
        binding.progressBar.hide()
        binding.errorTV.text = reason
        binding.errorTV.visibility = View.VISIBLE
        binding.titleTV.visibility = View.INVISIBLE
        binding.restartProcessIV.visibility = View.VISIBLE
        binding.restartProcessIV.setImageResource(R.drawable.ic_baseline_refresh_gray_48)
    }

}