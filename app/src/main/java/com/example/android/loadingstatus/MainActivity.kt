package com.example.android.loadingstatus

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private val NOTIFICATION_ID = 0

    var title = ""
    var description = ""


    lateinit var notificationManager: NotificationManager
    var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {

            if (!isChecked) {
                Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT)
                        .show()
            } else {
                download()
                custom_button.updateButtonState(ButtonState.Loading, title)
                Handler().postDelayed({
                    startNotification()

                }, 3000)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val query = DownloadManager.Query()
            query.setFilterById(id!!)

            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    custom_button.updateButtonState(ButtonState.Loading, title)
                    custom_button.updateStatus("Success")
                }
                if (DownloadManager.STATUS_FAILED == status) {
                    custom_button.updateButtonState(ButtonState.Loading, title)
                    custom_button.updateStatus("Fail")
                }
            }
        }
    }
    private fun download() {

        val request =
                DownloadManager.Request(Uri.parse(channelId))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radioButton ->
                    if (checked) {
                        isChecked = true
                        title = getString(R.string.glideLibraryDownLoad)
                        description = getString(R.string.notification_descriptionGlide)
                        channelId = GLIDE_URL
                    }
                R.id.radioButton2 ->
                    if (checked) {
                        isChecked = true
                        title = getString(R.string.notification_title)
                        description = getString(R.string.notification_description)
                        channelId = LOAD_APP_URL
                    }
                R.id.radioButton3 ->
                    if (checked) {
                        isChecked = true
                        title = getString(R.string.retrofitLibrary)
                        description = getString(R.string.notification_descriptionRetrofit)
                        channelId = RETROFIT_URL
                    }

            }
        }

    }

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
        val snoozeIntent = Intent(applicationContext, DetailActivity::class.java)
        val snoozePendingIntent: PendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(snoozeIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(
                applicationContext,

                applicationContext.getString(R.string.notification_channel_id)
        )
                .setSmallIcon(R.drawable.ic_baseline_cloud_download_48)
                .setContentTitle(title)
                .setContentText(messageBody)
                .addAction(
                        R.drawable.ic_baseline_cloud_download_48,
                        "Check the status",
                        snoozePendingIntent
                )
                .setAutoCancel(true)

                .setPriority(NotificationCompat.PRIORITY_HIGH)

        notify(NOTIFICATION_ID, builder.build())

    }

    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }


    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            )
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"

            val notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    fun startNotification() {
        notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(
                description,
                applicationContext
        )

        createChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name)
        )
    }

    companion object {
        private const val GLIDE_URL =
                "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
                "https://github.com/square/retrofit/archive/master.zip"

        var channelId = ""
    }
}