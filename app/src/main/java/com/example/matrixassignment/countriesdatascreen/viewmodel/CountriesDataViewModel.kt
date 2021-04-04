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
    val countryDownloadErrorEvent = MutableLiveData<MatrixAssignmentEvent>()
    private val countriesSorter = CountriesSorter()

    /**
     * Starting a downloading the countries data
     */
    fun requestCountries() {
        Log.d(TAG, "Starting countries data download")

 /*       if (countriesMap.value!!.values.isNotEmpty()) {
            Log.d(TAG, "No need to re-download data as it is only needs to be downloaded once")
            return
        }*/

        viewModelScope.launch(Dispatchers.IO) {
            countriesRepository.fetchCountries(object :
                NetworkCallback<List<Country>> {
                override fun onSuccess(result: List<Country>) {
                    countriesMap.postValue(generateCountriesMap(result))
                    countriesBordersMap.postValue(generateCountriesBordersMap(result))
                }

                override fun onFailure(error: String) {
                    countryDownloadErrorEvent.postValue(MatrixAssignmentEvent())
                }
            })
        }
    }

    /**
     * Sorting the countries array by the predefined [CountriesSorter]
     */
    fun requestSorting(): List<Country> {
        return countriesMap.value?.values!!.sortedWith(countriesSorter.getComparator())
    }

    /**
     * Sorting the countries array by a given [CountrySortStrategy]
     * @param sortStrategy The strategy used for sorting
     */
    fun requestSorting(sortStrategy: CountrySortStrategy): List<Country> {
        countriesSorter.sortStrategy = sortStrategy
        return countriesMap.value?.values!!.sortedWith(countriesSorter.getComparator())
    }

    /**
     * Sorting the countries array by a given [OrderStrategy]
     * @param orderStrategy The strategy used for sorting
     */
    fun requestSorting(orderStrategy: OrderStrategy): List<Country> {
        countriesSorter.orderStrategy = orderStrategy
        return countriesMap.value?.values!!.sortedWith(countriesSorter.getComparator())
    }

    fun getCountriesDataAscendingOrder(): List<Country> {
        return countriesMap.value?.values!!.sortedBy { it.name }
    }

    /**
     * Generating a new countries map. Each country value will be assigned to its alpha3Code key
     * * @return A map of countries
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
     * @return A map of the bordering countries
     */
    private fun generateCountriesBordersMap(countriesList: List<Country>): Map<String, List<Country?>> {
        val result = hashMapOf<String, List<Country?>>()
        countriesList.forEach { country ->
            val borderingCountries = mutableListOf<Country?>()
            country.borders?.forEach { borderingCountryCode ->
                countriesMap.value?.get(borderingCountryCode)?.let {
                    borderingCountries.add(it)
                }
            }
            result[country.alpha3Code] = borderingCountries.toList()
        }
        return result
    }

}