package com.example.swiftwave.data.remote

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

fun callNotifAPI(
    jsonObject: JSONObject
){
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()
    val url = "https://fcm.googleapis.com/fcm/send"
    val requestBody = jsonObject.toString().toRequestBody(JSON)
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .header("Authorization", "Bearer Can't Share Server Key :)")
        .build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {}
        override fun onResponse(call: Call, response:  Response) {}
    })
}
