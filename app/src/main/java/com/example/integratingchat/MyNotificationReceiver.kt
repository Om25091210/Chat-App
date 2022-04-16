package com.example.integratingchat

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.integratingchat.model.Data_Model
import com.google.firebase.messaging.FirebaseMessagingService


class MyNotificationReceiver : BroadcastReceiver() {

    val MESSAGES: java.util.ArrayList<Data_Model> = java.util.ArrayList<Data_Model>()
    var get_the_id:String?=null


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        //getting the remote input bundle from intent
        val remoteInput = RemoteInput.getResultsFromIntent(intent)

        get_the_id=context!!.getSharedPreferences("sai_gandu", FirebaseMessagingService.MODE_PRIVATE)
                .getString("gandu_notification", "")

        val bundle= intent?.getStringExtra("NOTIFY")
        Log.e("value reply",""+bundle)

        //if there is some input
        if (remoteInput != null) {

            val msg = remoteInput.getCharSequence(ConstantResource.NOTIFICATION_REPLY)

            MESSAGES.add(Data_Model(msg, "ME"))
            Toast.makeText(context, "Message : " + msg, Toast.LENGTH_SHORT).show()

            val fire=FirebaseService()
            fire.notifun(context, msg, get_the_id)

        }
    }

}