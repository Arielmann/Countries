package com.example.matrixassignment.countriesdatascreen.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixassignment.countriesdatascreen.events.MatrixAssignmentEvent
import com.example.matrixassignment.countriesdatascreen.model.AudioFileData
import com.example.matrixassignment.countriesdatascreen.network.NetworkCallback
import com.example.matrixassignment.countriesdatascreen.repository.AudioFilesRepository
import com.example.matrixassignment.countriesdatascreen.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * A viewModel built for the [SDKMainActivity]. Responsible for parsing the barcode scan results
 * and managing the [AudioFileData] obtained throughout the downloading process
 */
@HiltViewModel
class CountriesDataViewModel @Inject constructor(private val audioDataRepository: AudioFilesRepository) :
    ViewModel() {

    companion object {
        private val TAG: String? = CountriesDataViewModel::class.java.simpleName
    }

    val audioFiles: MutableLiveData<MutableMap<String, AudioFileData>> = MutableLiveData(mutableMapOf())
    val scanCanceledEvent = MutableLiveData<MatrixAssignmentEvent>()
    val qrCodeScanErrorEvent = MutableLiveData<MatrixAssignmentEvent>()
    val audioDataListDownloadErrorEvent = MutableLiveData<MatrixAssignmentEvent>()

    /**
     * Parsing the results obtained from the QR barcode scan.
     * Upon a successful parsing, the download of the data contained withing the json files will start immediately
     */
    fun handleQRScanResults(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    /**
     * Starting a download process for the audio data found in the QR barcode's response URL
     */
    private fun requestAudioDataList(qruUrl: String) {
        Log.d(TAG, "Starting audio data download")
        viewModelScope.launch(Dispatchers.IO) {
            audioDataRepository.fetchAudioDataList(qruUrl, object :
                NetworkCallback<List<AudioFileData>> {
                override fun onSuccess(result: List<AudioFileData>) {
//                    audioFiles.postValue(result)
                    downloadAudioFiles(result)
                }

                override fun onFailure(error: String) {
                    audioDataListDownloadErrorEvent.postValue(MatrixAssignmentEvent())
                }
            })
        }
    }

    /**
     * Downloads the audio files from the URLs located in the audio data list
     *
     * @param filesData Data list of the target files
     */
    private fun downloadAudioFiles(filesData: List<AudioFileData>) {
        Log.d(TAG, "downloadAudioFiles")

        filesData.forEach { audioFileData ->
            viewModelScope.launch(Dispatchers.IO) {

                audioDataRepository.downloadAudioFile(audioFileData, object :
                    NetworkCallback<ResponseBody> {
                    override fun onSuccess(result: ResponseBody) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val isFileSaved = Utils.saveAudioFile(result, audioFileData.name)
                            if (isFileSaved && !audioFiles.value!!.containsKey(audioFileData.url)) {
                                notifyNewAudioFileAvailable(audioFileData)
                            }
                        }
                    }

                    override fun onFailure(error: String) {
                        //View should not be updated about failure for this event therefore repository logs all failures
                    }
                })
            }
        }
    }

    /**
    Notifies observers that a new [AudioFileData] is available
     */
    private fun notifyNewAudioFileAvailable(audioFileData: AudioFileData) {
        Log.d(TAG, "${audioFileData.name} will be added to playlist list")
        audioFiles.value!![audioFileData.url] = audioFileData
        audioFiles.postValue(audioFiles.value!!)
    }



}