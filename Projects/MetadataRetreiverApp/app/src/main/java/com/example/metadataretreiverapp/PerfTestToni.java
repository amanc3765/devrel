package com.example.metadataretreiverapp;

import static android.media.MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@OptIn(markerClass = UnstableApi.class)
public class PerfTestToni {
//
//    public static void startMetadataRetrievalTest(Activity context) {
//        String testPath = "/sdcard/Download/sample2.mp4";
//        if (Util.maybeRequestReadStoragePermission(context, MediaItem.fromUri(testPath))) {
//            return;
//        }
//        // Run each version once to avoid class loading etc to influence runtime.
//        retrieveMetadataPlatform(testPath);
//        retrieveMetadataMedia3(context, testPath);
//        // Run 50 times each and get the average.
//        long platformTotal = 0;
//        long media3Total = 0;
//        for (int i = 0; i < 50; i++) {
//            long platform = retrieveMetadataPlatform(testPath);
//            long media3 = retrieveMetadataMedia3(context, testPath);
//            platformTotal += platform;
//            media3Total += media3;
//            Log.d("tonihei", String.format("%3d: %7d %7d", i, platform, media3));
//        }
//        Log.d("tonihei", "(single) platform: " + (platformTotal / 50) + ", media3: " + (media3Total / 50));
//        // Run in bulk 50 times and get average time.
//        long platformBulkAverage = retrieveMetadataPlatformBulk(testPath, 50) / 50;
//        long media3BulkAverage = retrieveMetadataMedia3Bulk(context, testPath, 50) / 50;
//        Log.d("tonihei", "(bulk) platform: " + platformBulkAverage + ", media3: " + media3BulkAverage);
//    }
//
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
//
//    private static long retrieveMetadataPlatformBulk(String path, int count) {
//        ExecutorService executors = Executors.newFixedThreadPool(5);
//        long startTimeNs = System.nanoTime();
//        List<Future<?>> futures = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            futures.add(executors.submit(() -> {
//                try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
//                    mediaMetadataRetriever.setDataSource(path);
//                    mediaMetadataRetriever.extractMetadata(METADATA_KEY_HAS_VIDEO);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }));
//        }
//        for (int i = 0; i < count; i++) {
//            try {
//                futures.get(i).get();
//            } catch (ExecutionException | RuntimeException | InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        long totalTime = (System.nanoTime() - startTimeNs) / 1000;
//        executors.shutdown();
//        return totalTime;
//    }
//
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
//
//    @OptIn(markerClass = UnstableApi.class)
//    private static long retrieveMetadataMedia3Bulk(Context context, String path, int count) {
//        long startTimeNs = System.nanoTime();
//        List<Future<?>> futures = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            futures.add(MetadataRetriever.retrieveMetadata(new ProgressiveMediaSource.Factory(new DefaultDataSource.Factory(context)), MediaItem.fromUri(path)));
//        }
//        for (int i = 0; i < count; i++) {
//            try {
//                futures.get(i).get();
//            } catch (ExecutionException | RuntimeException | InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        long totalTime = (System.nanoTime() - startTimeNs) / 1000;
//        return totalTime;
//    }

}
