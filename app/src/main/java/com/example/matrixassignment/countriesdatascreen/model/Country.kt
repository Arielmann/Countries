package com.example.matrixassignment.countriesdatascreen.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.example.matrixassignment.crossapplication.adapterhelpers.ObjectIdentifier

/**
 * A model representing data of a given country
 */
data class Country (
	@SerializedName("name") val name : String,
	@SerializedName("borders") val borders : List<String>,
	@SerializedName("nativeName") val nativeName : String,
	@SerializedName("area") val area : Double): ObjectIdentifier, Parcelable {

	constructor(parcel: Parcel) : this(
		parcel.readString()!!,
		parcel.createStringArrayList()!!,
		parcel.readString()!!,
		parcel.readDouble()
	)

	override fun getUniqueProperty(): String {
		return name + nativeName //Can switch with id but its currently not available in the json
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(name)
		parcel.writeStringList(borders)
		parcel.writeString(nativeName)
		parcel.writeDouble(area)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Country> {
		override fun createFromParcel(parcel: Parcel): Country {
			return Country(parcel)
		}

		override fun newArray(size: Int): Array<Country?> {
			return arrayOfNulls(size)
		}
	}

}