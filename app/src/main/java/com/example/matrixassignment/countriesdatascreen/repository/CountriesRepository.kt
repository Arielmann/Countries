package com.example.matrixassignment.countriesdatascreen.repository

import android.accounts.NetworkErrorException
import android.util.Log
import com.example.matrixassignment.crossapplication.utils.Utils.isNetworkAvailable
import com.example.matrixassignment.crossapplication.app.MatrixAssignmentApp
import com.example.matrixassignment.countriesdatascreen.model.Country
import com.example.matrixassignment.countriesdatascreen.network.CountriesDataNetworkService
import com.example.matrixassignment.countriesdatascreen.network.NetworkCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


class CountriesRepository @Inject constructor(private val countriesDataNetworkService: CountriesDataNetworkService) {

    companion object {
        private val TAG: String? = CountriesRepository::class.simpleName
    }

    /**
     * Starts an operation if network is available for the client. Otherwise throws a [NetworkErrorException]
     *
     * @param operation The desired operation for execution
     *
     * @throws NetworkErrorException if network is not available
     */
    private suspend fun <T> startOperationIfNetworkAvailable(operation: suspend () -> T) {
        val context = MatrixAssignmentApp.context
        if (isNetworkAvailable(context)) {
            Log.d(TAG, "Network available, starting network operation")
            operation.invoke()
        } else {
            throw NetworkErrorException("No network connection")
        }
    }

    /**
     * Downloading the data regarding the entire list of files
     *
     * @param networkCallback a callback for updating the operation's status
     */
    suspend fun fetchCountries(networkCallback: NetworkCallback<List<Country>>) {

        try {
            startOperationIfNetworkAvailable {
                Log.d(TAG, "Executing countries data fetch request")
                val call = countriesDataNetworkService.fetchCountriesRawData()

                call.enqueue(object : Callback<List<Country>> {
                    override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                        Log.d(TAG, "Countries data fetch response: $response")
                        if (response.body() != null && response.isSuccessful) {
                            if (response.body() != null) {
                                Log.d(TAG, "Countries data download successful")
                                networkCallback.onSuccess(response.body()!!)
                                return
                            }
                        }
                        Log.d(TAG, "Countries data download fetched a bad response ")
                        networkCallback.onFailure("Countries fetch request failed")
                    }

                    override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                        Log.e(TAG, "Countries data fetch error: " + t.message)
                        t.printStackTrace()
                        networkCallback.onFailure("Countries fetch request failed")
                    }
                })

            }
        } catch (e: Exception) {
            val errorMsg = "fetchCountries: An error has occurred but was caught not caught within the retrofit designated failure mechanism"
            Log.e(TAG, errorMsg)
            e.printStackTrace()
            networkCallback.onFailure(errorMsg)
        }

    }

}
