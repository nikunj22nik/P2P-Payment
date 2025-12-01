package com.p2p.application.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class MultipartUtil {

    companion object {
        fun uriToMultipart(
            context: Context,
            uri: Uri,
            partName: String = "file"
        ): MultipartBody.Part? {
            val contentResolver = context.contentResolver

            val fileName = getFileName(contentResolver, uri) ?: return null
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileBytes = inputStream.use { it.readBytes() }

            val requestFile = RequestBody.create(mimeType.toMediaTypeOrNull(), fileBytes)

            return MultipartBody.Part.createFormData(partName, fileName, requestFile)
        }

        private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
            var name: String? = null
            val returnCursor = contentResolver.query(uri, null, null, null, null)
            returnCursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        name = it.getString(nameIndex)
                    }
                }
            }
            return name
        }

        fun stringToRequestBody(value: String, mediaType: String = "text/plain"): RequestBody {
            return RequestBody.create(mediaType.toMediaTypeOrNull(), value)
        }

        fun ensureStartsWithSlash(path: String?): String {
            if(path == null) return ""
            return if (path.startsWith("/")) path else "/$path"
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getMinutesUntilStart(startTimeStr: String): Long {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a")
            val now = LocalTime.now()
            val startTime = LocalTime.parse(startTimeStr, formatter)

            return if (now.isBefore(startTime)) {
                ChronoUnit.MINUTES.between(now, startTime)
            } else {
                0L // start time has passed
            }
        }

        fun isFileLargerThan2048KB(context: Context, uri: Uri): Boolean {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (it.moveToFirst()) {
                    val size = it.getLong(sizeIndex)
                    return size > 2048 * 1024 // size in bytes
                }
            }
            return false
        }

        fun isFileLargerThan10MB(context: Context, uri: Uri): Boolean {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1 && it.moveToFirst()) {
                    val sizeInBytes = it.getLong(sizeIndex)
                    return sizeInBytes > 10240 * 1024 // 10 MB in bytes
                }
            }
            return false // Could not determine file size
        }

        fun isFileLargerThan5MB(context: Context, uri: Uri): Boolean {
            val cursor = context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        val sizeInBytes = it.getLong(sizeIndex)
                        val fiveMBInBytes = 5L * 1024 * 1024 // 5 MB
                        return sizeInBytes > fiveMBInBytes
                    }
                }
            }
            return false // File size couldn't be determined or not larger than 5 MB
        }

    }
}