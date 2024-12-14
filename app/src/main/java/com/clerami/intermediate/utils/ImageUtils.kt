package com.clerami.intermediate.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import java.io.File
import java.io.FileOutputStream


object ImageUtils {


    fun compressImage(context: Context, uri: Uri, originalFile: File): File? {
        try {
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))


            val newBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true)

            val compressedFile = File(context.cacheDir, "compressed_${originalFile.name}")
            val outputStream = FileOutputStream(compressedFile)

            newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            outputStream.flush()
            outputStream.close()

            return compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

