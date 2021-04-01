package com.example.matrixassignment.countriesdatascreen.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matrixassignment.countriesdatascreen.events.MatrixAssignmentEvent
import com.example.matrixassignment.countriesdatascreen.viewmodel.CountriesDataViewModel
import com.example.maytronicstestapp.R
import com.example.maytronicstestapp.databinding.ActivitySdkMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity of the sdk. Responsible for starting the barcode scan process
 * and displaying its results
 */
@AndroidEntryPoint
class SDKMainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = SDKMainActivity::class.java.simpleName
    }

    private lateinit var audioDataAdapter: AudioDataAdapter
    private lateinit var binding: ActivitySdkMainBinding
    private val countriesDataViewmodel: CountriesDataViewModel by viewModels()

    private val onRestartClickListener = View.OnClickListener { startQRScan() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySdkMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAudioFilesList()
        setupDataObservation()
        binding.restartProcessIV.setOnClickListener(onRestartClickListener)
        startQRScan()
    }

    /**
    Initializing the QR scan process by opening the device's camera.
     */
    private fun startQRScan() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        countriesDataViewmodel.handleQRScanResults(requestCode, resultCode, data)
        binding.progressBar.show()
    }

    /**
     * Starts audio files data observation via the fragment's viewModel.
     * When the observer is activated, it's data will be displayed in the audio files list
     */
    private fun setupDataObservation() {
        countriesDataViewmodel.audioFiles.observe(this, { audioFiles ->
            audioFiles?.let {
                Log.d(TAG, "Audio file data received from viewModel")
                audioDataAdapter.submitList(audioFiles.values.toList())
                hideProgressionUI()
            } ?: Log.w(TAG, "No audio data received")
        })

        observeFailureScenario(
            countriesDataViewmodel.qrCodeScanErrorEvent,
            getString(R.string.error_qr_scan_failed)
        )
        observeFailureScenario(
            countriesDataViewmodel.audioDataListDownloadErrorEvent,
            getString(R.string.error_audio_data_fetch_failed)
        )
        observeFailureScenario(
            countriesDataViewmodel.scanCanceledEvent,
            getString(R.string.event_message_scan_cancelled)
        )
    }

    /**
     * Displaying the title and progressbar and hiding any error messages\views
     */
    private fun showProgressionUI() {
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
        message: String = getString(R.string.error_unknown_failure)
    ) {
        event.observe(this, { result ->
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
        audioDataAdapter = AudioDataAdapter(this)
        audioDataAdapter.setHasStableIds(true)

        audioDataAdapter.onItemClickListener = { audioData ->
        }

        binding.audioDataRV.apply {
            adapter = audioDataAdapter
            layoutManager = LinearLayoutManager(this@SDKMainActivity)
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