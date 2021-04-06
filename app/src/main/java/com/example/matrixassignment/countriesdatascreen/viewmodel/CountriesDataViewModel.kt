package com.example.matrixassignment.countriesdatascreen.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixassignment.countriesdatascreen.model.CountriesSorter
import com.example.matrixassignment.crossapplication.events.MatrixAssignmentEvent
import com.example.matrixassignment.countriesdatascreen.model.Country
import com.example.matrixassignment.countriesdatascreen.model.CountrySortStrategy
import com.example.matrixassignment.countriesdatascreen.model.OrderStrategy
import com.example.matrixassignment.countriesdatascreen.network.NetworkCallback
import com.example.matrixassignment.countriesdatascreen.repository.CountriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A viewModel responsible for accessing and managing data regarding to countries
 */
@HiltViewModel
class CountriesDataViewModel @Inject constructor(private val countriesRepository: CountriesRepository) :
    ViewModel() {

    companion object {
        private val TAG: String? = CountriesDataViewModel::class.java.simpleName
    }

    val countriesBordersMap: MutableLiveData<Map<String, List<Country?>>> =
        MutableLiveData(hashMapOf())
    val countriesMap: MutableLiveData<Map<String, Country>> = MutableLiveData(hashMapOf())
    val onCountryDownloadErrorEvent = MutableLiveData<MatrixAssignmentEvent>()
    val onAscendingDisplayRequired = MutableLiveData(MatrixAssignmentEvent())
    val onDescendingDisplayRequired = MutableLiveData(MatrixAssignmentEvent())
    val onDownloadProcessStarted = MutableLiveData(MatrixAssignmentEvent())
    private val countriesSorter = CountriesSorter()

    /**
     * Starting a downloading the countries data
     */
    fun requestCountries() {
        Log.d(TAG, "Starting countries data download")

        if (countriesMap.value!!.values.isNotEmpty()) {
            //Prevents re-downloading. This choice can be changed as soon as a feature allowing the user to refresh the data will be added
            Log.d(TAG, "Data already downloaded, no need to download twice")
            return
        }

        Log.d(TAG, "posting onDownloadProcessStarted")
        onDownloadProcessStarted.postValue(MatrixAssignmentEvent())
        viewModelScope.launch(Dispatchers.IO) {
            countriesRepository.fetchCountries(object :
                NetworkCallback<List<Country>> {
                override fun onSuccess(result: List<Country>) {
                    val countriesMap = generateCountriesMap(result)
                    this@CountriesDataViewModel.countriesMap.postValue(countriesMap)
                    countriesBordersMap.postValue(generateCountriesBordersMap(countriesMap))
                }

                override fun onFailure(error: String) {
                    onCountryDownloadErrorEvent.postValue(MatrixAssignmentEvent())
                }
            })
        }
    }

    /**
     * Sorting the countries array by the predefined [CountriesSorter]
     */
    fun requestDisplayOrder(): List<Country> {
        postOrderingRequestEvents()
        return countriesMap.value?.values!!.sortedWith(countriesSorter.getComparator())
    }

    /**
     * Sorting the countries array by a given [CountrySortStrategy]
     * @param sortStrategy The strategy used for sorting
     */
    fun requestDisplayOrder(sortStrategy: CountrySortStrategy): List<Country> {
        countriesSorter.sortStrategy = sortStrategy
        return countriesMap.value?.values!!.sortedWith(countriesSorter.getComparator())
    }

    /**
     * Sorting the countries array by a given [OrderStrategy]
     * @param orderStrategy The strategy used for sorting
     */
    fun requestDisplayOrder(orderStrategy: OrderStrategy): List<Country> {
        countriesSorter.orderStrategy = orderStrategy
        postOrderingRequestEvents()
        return countriesMap.value?.values!!.sortedWith(countriesSorter.getComparator())
    }

    /**
     * Notifying observers that a data ordering request was made
     */
    private fun postOrderingRequestEvents() {
        if (countriesSorter.orderStrategy == OrderStrategy.ASCENDING) {
            onAscendingDisplayRequired.postValue(MatrixAssignmentEvent())
            return
        }
        onDescendingDisplayRequired.postValue(MatrixAssignmentEvent())
    }

    /**
     * Generating a new countries map. Each country value will be assigned to its alpha3Code key
     * @return A map of countries
     */
    private fun generateCountriesMap(countriesList: List<Country>): Map<String, Country> {
        val result = hashMapOf<String, Country>()
        countriesList.forEach {
            result[it.alpha3Code] = it
        }
        return result
    }

    /**
     * Generating a map linking between a country alpha3Code to a list of its bordering Countries
     * @param countriesMap Mapping of all countries to their alpha3code
     * @return A map of the bordering countries
     */
    private fun generateCountriesBordersMap(countriesMap: Map<String, Country>): Map<String, List<Country?>> {
        val result = hashMapOf<String, List<Country?>>()
        countriesMap.values.forEach { country ->
            val borderingCountries = mutableListOf<Country?>()
            country.borders?.forEach { borderingCountryCode ->
                countriesMap[borderingCountryCode]?.let {
                    borderingCountries.add(it)
                }
            }
            result[country.alpha3Code] = borderingCountries.toList()
        }
        return result
    }

}