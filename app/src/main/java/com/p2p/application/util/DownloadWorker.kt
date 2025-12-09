package com.p2p.application.util

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.Random
import java.util.concurrent.TimeUnit

class DownloadWorker {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadPdfWithNotification(context: Context, url: String, fileName: String) {
        val channelId = "pdf_download_channel"
        val notificationId = Random().nextInt(10000)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PDF Downloads",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val progressBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.stat_sys_download)
            .setContentTitle("Downloading...")
            .setContentText(fileName)
            .setProgress(100, 0, true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManager.notify(notificationId, progressBuilder.build())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .build()

                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val body = response.body ?: throw IOException("Empty response")

                val total = body.contentLength()
                var downloaded = 0L

                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IOException("Failed to create file")

                resolver.openOutputStream(uri).use { outputStream ->
                    val inputStream = body.byteStream()
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream?.write(buffer, 0, read)
                        downloaded += read
                        val progress = if (total > 0) ((downloaded * 100) / total).toInt() else 0
                        Log.d("PDFDownload", "Progress: $progress%")
                        progressBuilder.setProgress(100, progress, false)
                        notificationManager.notify(notificationId, progressBuilder.build())
                    }
                    outputStream?.flush()
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                // ✅ STEP 1: Cancel the ongoing download notification
                notificationManager.cancel(notificationId)

                // ✅ STEP 2: Build a NEW notification for completion
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val pendingIntent = PendingIntent.getActivity(
                    context, 0, viewIntent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    else PendingIntent.FLAG_UPDATE_CURRENT
                )

                val completeBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.stat_sys_download_done)
                    .setContentTitle("Download Complete")
                    .setContentText("Tap to open $fileName")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                // ✅ STEP 3: Issue a new notification
                notificationManager.notify(notificationId + 1, completeBuilder.build())

                Log.d("PDFDownload", "✅ Download complete, notification shown")
            } catch (e: Exception) {
                Log.e("PDFDownload", "❌ Error: ${e.localizedMessage}")
                e.printStackTrace()

                notificationManager.cancel(notificationId) // cancel old

                val failedBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.stat_notify_error)
                    .setContentTitle("Download Failed")
                    .setContentText(e.localizedMessage ?: "Unknown error")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                notificationManager.notify(notificationId + 2, failedBuilder.build())
            }
        }
    }

}




