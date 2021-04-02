package com.example.matrixassignment.countriesdatascreen.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixassignment.crossapplication.events.MatrixAssignmentEvent
import com.example.matrixassignment.countriesdatascreen.model.Country
import com.example.matrixassignment.countriesdatascreen.network.NetworkCallback
import com.example.matrixassignment.countriesdatascreen.repository.CountriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A viewModel Responsible for parsing the barcode scan results
 * and managing the [Country] obtained throughout the downloading process
 */
@HiltViewModel
class CountriesDataViewModel @Inject constructor(private val countriesRepository: CountriesRepository) :
    ViewModel() {

    companion object {
        private val TAG: String? = CountriesDataViewModel::class.java.simpleName
    }

    val countriesList: MutableLiveData<List<Country>> = MutableLiveData(mutableListOf())
    val countryDownloadErrorEvent = MutableLiveData<MatrixAssignmentEvent>()

    /**
     * Starting a download process for the audio data found in the QR barcode's response URL
     */
    fun requestAudioDataList() {
        Log.d(TAG, "Starting audio data download")
        viewModelScope.launch(Dispatchers.IO) {
            countriesRepository.fetchCountries(object :
                NetworkCallback<List<Country>> {
                override fun onSuccess(result: List<Country>) {
                    countriesList.postValue(result)
                }

                override fun onFailure(error: String) {
                    countryDownloadErrorEvent.postValue(MatrixAssignmentEvent())
                }
            })
        }
    }


}