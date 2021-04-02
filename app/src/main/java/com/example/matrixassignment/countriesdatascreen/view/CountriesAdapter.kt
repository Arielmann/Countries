package com.example.matrixassignment.countriesdatascreen.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.matrixassignment.crossapplication.adapterhelpers.DefaultDiffUtilCallback
import com.example.matrixassignment.countriesdatascreen.model.Country
import com.example.matrixassignment.crossapplication.app.MatrixAssignmentApp
import com.example.maytronicstestapp.databinding.VhCountryBinding

/**
 * Adapter for managing the display of the [Country] data
 */
class CountriesAdapter : ListAdapter<Country, CountriesAdapter.AudioDataViewHolder>(DefaultDiffUtilCallback<Country>()) {

    companion object {
        val TAG: String = CountriesAdapter::class.simpleName!!
    }

    lateinit var onItemClickListener: (country: Country) -> Unit

    /**
     * A view holder for managing the screen display of a single [Country]
     */
    inner class AudioDataViewHolder(private val binding: VhCountryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) {
            binding.countryViewHolderNameTV.text = country.name
            binding.countryViewHolderNativeNameTV.text = country.nativeName
            binding.root.setOnClickListener {
                onItemClickListener.invoke(country)
            }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioDataViewHolder {
        val binding = VhCountryBinding.inflate(LayoutInflater.from(MatrixAssignmentApp.context), parent, false)
        return AudioDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioDataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


