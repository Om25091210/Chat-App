package com.example.integratingchat


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.integratingchat.data.StaticConfig
import com.example.integratingchat.model.Data_Model
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

private const val CHANNEL_ID="my_channel"

class FirebaseService :FirebaseMessagingService(){


    val MESSAGES: ArrayList<Data_Model> = ArrayList<Data_Model>()
    var once:Boolean=true
    var data_of_roomID = 0
    var uid_of_user:String?=null
    var check_message: Boolean =true
    var notification: DatabaseReference? = null
    var check_notification: DatabaseReference? = null

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        once=true
        Log.e("zero", "zero")
        val intent=Intent(this, MainActivity::class.java)
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        uid_of_user=getSharedPreferences("UUID_OF_USER_FROM_FIREBASE", MODE_PRIVATE)
                .getString("Storing_inside_phone_storoge", StaticConfig.UID)

        // Initializing database

        notification = FirebaseDatabase.getInstance().reference.child("message").child(message.data["title"].toString())
        check_notification=FirebaseDatabase.getInstance().getReference().child("user").child(uid_of_user!!).child("Notification").child(message.data["title"].toString())
        Log.e("key check",""+message.data["key"].toString())

        check_notification!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
               if(!snapshot.child(message.data["key"].toString()).exists()){
                    Log.e("zero.1", "zero.1")
                    notification!!.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.e("zero.2", "zero.2")
                            if (once) {
                                for (key in snapshot.children) {
                                    val text = snapshot.child(key.key!!).child("text").getValue(String::class.java)
                                    val seen = snapshot.child(key.key!!).child("seen").getValue(String::class.java)
                                    val name = snapshot.child(key.key!!).child("name").getValue(String::class.java)
                                    if (seen == "false") {
                                        check_notification!!.child(key.key!!).setValue(text)
                                        Log.e("message", text + "")
                                        if (!text.equals("This message was deleted")) {
                                            MESSAGES.add(Data_Model(text, name))
                                        }
                                    }
                                }

                            }
                            once = false

                            data_of_roomID = Integer.parseInt(message.data["title"])

                            getSharedPreferences("sai_gandu", MODE_PRIVATE).edit()
                                    .putString("gandu_notification", "" + data_of_roomID).apply()

                            val notificationID = data_of_roomID
                            //Pending intent for a notification button help

                            val rec_intent = Intent(this@FirebaseService, MyNotificationReceiver::class.java)
                            rec_intent.putExtra("value", "" + data_of_roomID)

                            val helpPendingIntent = PendingIntent.getBroadcast(this@FirebaseService, ConstantResource.REQUEST_CODE_HELP, rec_intent, PendingIntent.FLAG_UPDATE_CURRENT)

                            //We need this object for getting direct input from notification
                            val bundle = Bundle()
                            bundle.putString("NOTIFICATION_ID", "" + data_of_roomID)


                            val remoteInput = RemoteInput.Builder(ConstantResource.NOTIFICATION_REPLY)
                                    .setLabel("Please enter your message")
                                    .build()

                            //For the remote input we need this action object
                            val action = NotificationCompat.Action.Builder(android.R.drawable.ic_delete, "Reply Now...", helpPendingIntent)
                                    .addRemoteInput(remoteInput)
                                    .build()

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                createNotifionChannel(notificationManager)
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                            val messagingStyle = NotificationCompat.MessagingStyle("Me")
                            messagingStyle.conversationTitle = "Dhiti"

                            for (chatMessage in MESSAGES) {
                                val notificationMessage = NotificationCompat.MessagingStyle.Message(
                                        chatMessage.text,
                                        chatMessage.timestamp,
                                        chatMessage.sender
                                )

                                messagingStyle.addMessage(notificationMessage)
                            }
                            val rand = Random()
                            val notification = NotificationCompat.Builder(this@FirebaseService, CHANNEL_ID)
                                    .setContentTitle(message.data["title"])
                                    .setContentText(message.data["message"])
                                    //.setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
                                    .setSmallIcon(R.drawable.logo)
                                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.person))
                                    .setAutoCancel(true)
                                    .setStyle(messagingStyle)
                                    .setContentIntent(getPendingIntent("" + data_of_roomID, rand.nextInt(1000)))
                                    .addAction(action)
                                    .setGroup("aryomtech_dhiti")
                                    .build()

                            notificationManager.notify(notificationID, notification)
                            MESSAGES.clear()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@FirebaseService, "Database Error!!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("DATABASE", "Database Error!!")
            }

        })

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifionChannel(notificationManager: NotificationManager){
        val channelName="ChannelName"
        val channel=NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {

            description="My channel description"
            enableLights(true)
            lightColor=Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun notifun(context: Context?, replyText: CharSequence?, get_the_id: String?) {

        val intent=Intent(context, MainActivity::class.java)
        val notificationManager=context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notification = FirebaseDatabase.getInstance().reference.child("message").child(get_the_id + "")

        Log.e("zero.1", "zero.1")
        notification!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("zero.2", "zero.2")
                if (once) {
                    for (key in snapshot.children) {
                        val text = snapshot.child(key.key!!).child("text").getValue(String::class.java)
                        val seen = snapshot.child(key.key!!).child("seen").getValue(String::class.java)
                        val name = snapshot.child(key.key!!).child("name").getValue(String::class.java)
                        if (seen == "false") {
                            Log.e("message", text + "")
                            if (!text.equals("This message was deleted")) {
                                MESSAGES.add(Data_Model(text, name))
                            }
                        }
                    }

                }
                once = false
                MESSAGES.add(Data_Model("" + replyText, "Me"))

                data_of_roomID = Integer.parseInt(get_the_id)

                val notificationID = data_of_roomID

                //Pending intent for a notification button help
                val helpPendingIntent = PendingIntent.getBroadcast(context, ConstantResource.REQUEST_CODE_HELP, Intent(context, MyNotificationReceiver::class.java)
                        .putExtra(ConstantResource.KEY_HELP, ConstantResource.REQUEST_CODE_HELP), PendingIntent.FLAG_UPDATE_CURRENT)

                //We need this object for getting direct input from notification
                val remoteInput = RemoteInput.Builder(ConstantResource.NOTIFICATION_REPLY)
                        .setLabel("Please enter your message")
                        .build()

                //For the remote input we need this action object
                val action = NotificationCompat.Action.Builder(android.R.drawable.ic_delete, "Reply Now...", helpPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotifionChannel(notificationManager)
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                val messagingStyle = NotificationCompat.MessagingStyle("Me")
                messagingStyle.conversationTitle = "Dhiti"

                for (chatMessage in MESSAGES) {
                    val notificationMessage = NotificationCompat.MessagingStyle.Message(
                            chatMessage.text,
                            chatMessage.timestamp,
                            chatMessage.sender
                    )

                    messagingStyle.addMessage(notificationMessage)
                }
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_ONE_SHOT)
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                        //.setStyle(NotificationCompat.BigTextStyle().bigText(message.data["message"]))
                        .setSmallIcon(R.drawable.logo)
                        .setAutoCancel(true)
                        .setStyle(messagingStyle)
                        .setContentIntent(helpPendingIntent)
                        .addAction(action)
                        .setContentIntent(pendingIntent)
                        .build()

                notificationManager.notify(notificationID, notification)
                MESSAGES.clear()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Database Error!!", Toast.LENGTH_SHORT).show()
            }
        })

    }
    private fun getPendingIntent(bundle: String, rc: Int): PendingIntent? {

        val notificationIntent = Intent(this, MyNotificationReceiver::class.java)
        notificationIntent.putExtra("NOTIFY", bundle)
        return PendingIntent.getActivity(this, rc, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}
