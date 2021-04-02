package com.example.matrixassignment.countriesdatascreen.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.example.matrixassignment.crossapplication.adapterhelpers.ObjectIdentifier
import kotlinx.parcelize.Parcelize

/**
 * A model representing data of a given country
 */
@Parcelize
data class Country (
	@SerializedName("name") val name : String,
	@SerializedName("borders") val borders : List<String>?,
	@SerializedName("nativeName") val nativeName : String,
	@SerializedName("alpha3Code") val alpha3Code : String,
	@SerializedName("area") val area : Double): ObjectIdentifier, Parcelable {

	constructor(parcel: Parcel) : this(
		parcel.readString()!!,
		parcel.createStringArrayList(),
		parcel.readString()!!,
		parcel.readString()!!,
		parcel.readDouble()
	)

	override fun getUniqueProperty(): String {
		return name + nativeName //Can switch with id but its currently not available in the json
	}

}