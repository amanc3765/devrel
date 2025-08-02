package com.example.metadataretreiverapp

import androidx.annotation.OptIn
import androidx.media3.common.Format
import androidx.media3.common.Timeline
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.TrackGroupArray

@OptIn(UnstableApi::class)
object RetrieverUtil {
    
    fun printTimeline(timeline: Timeline) {
        Log.d(TAG, "Timeline window count: ${timeline.windowCount}")
        Log.d(TAG, "Timeline period count: ${timeline.periodCount}")

        val window = Timeline.Window()
        val period = Timeline.Period()

        for (i in 0 until timeline.windowCount) {
            timeline.getWindow(i, window)
            Log.d(TAG, "Window $i:")
            Log.d(TAG, "  DurationUs: ${window.durationUs}")
            Log.d(TAG, "  DefaultPositionUs: ${window.defaultPositionUs}")
            Log.d(TAG, "  IsSeekable: ${window.isSeekable}")
            Log.d(TAG, "  IsDynamic: ${window.isDynamic}")
            Log.d(TAG, "  LiveOffsetUs: ${window.windowStartTimeMs}")
        }

        for (i in 0 until timeline.periodCount) {
            timeline.getPeriod(i, period)
            Log.d(TAG, "Period $i:")
            Log.d(TAG, "  DurationUs: ${period.durationUs}")
            Log.d(TAG, "  PositionInWindowUs: ${period.positionInWindowUs}")
            Log.d(TAG, "  WindowIndex: ${period.windowIndex}")
            Log.d(TAG, "  AdGroupCount: ${period.adGroupCount}")
        }
    }

    fun printTrackGroups(trackGroups: TrackGroupArray) {
        Log.d(TAG, "TrackGroupArray:")
        Log.d(TAG, "  Total Track Groups: ${trackGroups.length}")
        Log.d(TAG, "  --------------------------------------")

        for (i in 0 until trackGroups.length) {
            val group = trackGroups.get(i)
            val groupType = getTrackTypeString(group.getFormat(0).sampleMimeType)
            Log.d(TAG, "  Track Group $i: ($groupType Tracks)")
            Log.d(TAG, "  --------------------------------------")
            Log.d(TAG, "    Number of Tracks in this Group: ${group.length}")

            for (j in 0 until group.length) {
                val format = group.getFormat(j)
                Log.d(TAG, "\n    Track $i.$j:")
                Log.d(TAG, "      Format:")
                format.id?.let { Log.d(TAG, "        ID: $it") }
                format.containerMimeType?.let { Log.d(TAG, "        Container Mime Type: $it") }
                format.sampleMimeType?.let { Log.d(TAG, "        Sample Mime Type: $it") }
                format.codecs?.let { Log.d(TAG, "        Codecs: $it") }
                if (format.bitrate != Format.NO_VALUE) {
                    Log.d(TAG, "        Average Bitrate: ${format.bitrate} bps")
                }
                format.language?.let { Log.d(TAG, "        Language: $it") }

                // Video Specific
                if (format.width != Format.NO_VALUE && format.height != Format.NO_VALUE) {
                    Log.d(TAG, "        Video Properties:")
                    Log.d(TAG, "          Dimensions: ${format.width}x${format.height}")
                    if (format.frameRate != Format.NO_VALUE.toFloat()) {
                        Log.d(TAG, "          Frame Rate: ${format.frameRate} fps")
                    }
                    format.colorInfo?.let { color ->
                        Log.d(TAG, "          Color Info:")
                        Log.d(TAG, "            Color Space: ${color.colorSpace}")
                        Log.d(TAG, "            Color Range: ${color.colorRange}")
                        Log.d(TAG, "            Color Transfer: ${color.colorTransfer}")
                        // You can add more colorInfo details if needed
                    }
                }

                // Audio Specific
                if (format.channelCount != Format.NO_VALUE && format.sampleRate != Format.NO_VALUE) {
                    Log.d(TAG, "        Audio Properties:")
                    Log.d(TAG, "          Channel Count: ${format.channelCount}")
                    Log.d(TAG, "          Sample Rate: ${format.sampleRate} Hz")
                }

                // Text Specific
                if (format.roleFlags != 0) {
                    Log.d(TAG, "        Role Flags: ${format.roleFlags}")
                }
                if (format.selectionFlags != 0) {
                    Log.d(TAG, "        Selection Flags: ${format.selectionFlags}")
                }


                if (format.maxInputSize != Format.NO_VALUE) {
                    Log.d(TAG, "        Max Input Size: ${format.maxInputSize}")
                }
                // Add more format fields as needed
                Log.d(TAG, "  --------------------------------------")
            }
        }
    }

    private fun getTrackTypeString(sampleMimeType: String?): String {
        return when {
            sampleMimeType == null -> "Unknown"
            sampleMimeType.startsWith("video/") -> "Video"
            sampleMimeType.startsWith("audio/") -> "Audio"
            sampleMimeType.startsWith("text/") || sampleMimeType.startsWith("application/") -> "Text/Subtitle" // common for text
            else -> "Other"
        }
    }
}