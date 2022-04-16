package com.example.integratingchat

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


const val TOPIC = "/topics/myTopic2"
class topic {
    val TAG="MainActivity"

    fun noti(name:String,phone:String){

        //please add this line

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        val title=name
        val message=phone
        val key=""

        if(message!=null){
            if(title!=null){
                if(title.isNotEmpty() && message.isNotEmpty()){
                    PushNotification(
                            NotificationData(title,key,message),
                            TOPIC
                    ).also {
                        sendNotification(it)
                    }
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {

        try{
            val response=RetrofirInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d(TAG,"Response:${Gson().toJson(response)}")
            }else{
                Log.e(TAG,response.errorBody().toString())
            }

        }catch (e: Exception){
            Log.e(TAG,e.toString())
        }

    }

}