package com.example.matrixassignment.countriesdatascreen.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.matrixassignment.crossapplication.adapterhelpers.DefaultDiffUtilCallback
import com.example.matrixassignment.countriesdatascreen.model.Country
import com.example.matrixassignment.crossapplication.app.MatrixAssignmentApp
import com.example.matrixassignment.databinding.VhCountryBinding

/**
 * Adapter for managing the display of the [Country] data
 */
class CountriesAdapter : ListAdapter<Country, CountriesAdapter.CountryViewHolder>(DefaultDiffUtilCallback<Country>()) {

    companion object {
        @Suppress("unused")
        val TAG: String = CountriesAdapter::class.simpleName!!
    }

    var onItemClickListener: ((country: Country) -> Unit)? = null

    /**
     * A view holder for managing the screen display of a single [Country]
     */
    inner class CountryViewHolder(private val binding: VhCountryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) {
            binding.countryViewHolderNameTV.text = country.name
            binding.countryViewHolderNativeNameTV.text = country.nativeName
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(country)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = VhCountryBinding.inflate(LayoutInflater.from(MatrixAssignmentApp.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


