package com.example.matrixassignment.countriesdatascreen.network

import com.example.matrixassignment.countriesdatascreen.model.AudioFileData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface AudioFilesNetworkService {

    @GET
    fun fetchAudioData(@Url qrURL: String): Call<List<AudioFileData>>

    @GET
    @Streaming
    suspend fun downloadFile(@Url fileUrl:String): Response<ResponseBody>?

}
