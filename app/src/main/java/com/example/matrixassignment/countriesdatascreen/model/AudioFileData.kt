package com.example.matrixassignment.countriesdatascreen.model

import com.google.gson.annotations.SerializedName
import com.example.matrixassignment.countriesdatascreen.adapterhelpers.ObjectIdentifier

/**
 * A model representing data about an audio file within a remote server
 */
data class AudioFileData (
	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("url") val url : String
)  : ObjectIdentifier {

	override fun getUniqueProperty(): String {
		return id.toString()
	}

}