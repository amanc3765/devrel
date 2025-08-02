//package com.example.metadataretreiverapp;
//
//import static android.media.MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO;
//
import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.MetadataRetriever;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;

//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//
//public class PerfUtil {
//
//    static String TAG = "Aman";
//
//    @OptIn(markerClass = UnstableApi.class)
//    public static void startMetadataRetrievalTest(Activity context) {
//        String testPath = "/sdcard/Download/sample.mp4";
//        if (Util.maybeRequestReadStoragePermission(context, MediaItem.fromUri(testPath))) {
//            return;
//        }
//        Log.d(TAG, "startMetadataRetrievalTest");
//
//        retrieveMetadataPlatform(testPath);
//        retrieveMetadataMedia3(context, testPath);
//
//        long numIterations = 100;
//        long platformTotal = 0L;
//        long media3Total = 0L;
//        for (int i = 0; i < numIterations; i++) {
//            long platform = retrieveMetadataPlatform(testPath);
//            long media3 = retrieveMetadataMedia3(context, testPath);
//            Log.d(TAG, String.format("%3d: %7d %7d", i, platform, media3));
//            platformTotal += platform;
//            media3Total += media3;
//        }
//        Log.d(TAG, "(single) platform: " + (platformTotal / numIterations) + ", media3: " + (media3Total / numIterations));
//    }
//
//    private static long retrieveMetadataPlatform(String path) {
//        long startTimeNs = System.nanoTime();
//        try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
//            mediaMetadataRetriever.setDataSource(path);
//            mediaMetadataRetriever.extractMetadata(METADATA_KEY_HAS_VIDEO);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return (System.nanoTime() - startTimeNs) / 1000;
//    }
//
//    @OptIn(markerClass = UnstableApi.class)
//    private static long retrieveMetadataMedia3(Context context, String path) {
//        long startTimeNs = System.nanoTime();
//        try {
//            MetadataRetriever.retrieveMetadata(new ProgressiveMediaSource.Factory(new DefaultDataSource.Factory(context)), MediaItem.fromUri(path)).get();
//        } catch (ExecutionException | RuntimeException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return (System.nanoTime() - startTimeNs) / 1000;
//    }
//
//}