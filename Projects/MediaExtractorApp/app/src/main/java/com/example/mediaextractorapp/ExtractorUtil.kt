package com.example.mediaextractorapp

import android.media.MediaFormat
import android.os.Build
import android.util.Log

object ExtractorUtil {

    fun printMediaFormat(
        mediaFormat: MediaFormat
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaFormat.keys.sorted().forEach { key ->
                val value: Any? = try {
                    // Attempt to get as String first
                    mediaFormat.getString(key)
                } catch (e: ClassCastException) {
                    // If not a String, try as Integer
                    try {
                        mediaFormat.getInteger(key)
                    } catch (e: ClassCastException) {
                        // If not an Integer, try as Long
                        try {
                            mediaFormat.getLong(key)
                        } catch (e: ClassCastException) {
                            // If not a Long, try as Float
                            try {
                                mediaFormat.getFloat(key)
                            } catch (e: ClassCastException) {
                                // If not a Float, try as Number
                                try {
                                    mediaFormat.getNumber(key)
                                } catch (e: ClassCastException) {
                                    // If not a Number, try as ByteBuffer
                                    mediaFormat.getByteBuffer(key)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle other potential exceptions or log them
                    Log.w(TAG, "Could not retrieve value for key: $key", e)
                    "NOT FOUND"
                }
                Log.i(TAG, "  ${key}: $value")
            }
        }
    }
}