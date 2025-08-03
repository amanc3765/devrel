package com.example.metadataretrieverplatform;

import static android.media.MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.MetadataRetriever;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RetrieverTestToni {
    public static void startMetadataRetrievalTest(Activity context) {
        String testPath = "/sdcard/Download/mediadataset/dizzy.mp4";
        if (Util.maybeRequestReadStoragePermission(context, MediaItem.fromUri(testPath))) {
            return;
        }
        // Run each version once to avoid class loading etc to influence runtime.
        retrieveMetadataPlatform(testPath);
//        retrieveMetadataMedia3(context, testPath);
        // Run 50 times each and get the average.
        long platformTotal = 0;
        long media3Total = 0;
        for (int i = 0; i < 100; i++) {
            platformTotal += retrieveMetadataPlatform(testPath);
//            media3Total += retrieveMetadataMedia3(context, testPath);
        }
        Log.d("tonihei", "(single) platform: " + (platformTotal / 50) + ", media3: " + (media3Total / 50));
    }


    private static long retrieveMetadataPlatform(String path) {
        long startTimeNs = System.nanoTime();
        try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
            mediaMetadataRetriever.setDataSource(path);
            mediaMetadataRetriever.extractMetadata(METADATA_KEY_HAS_VIDEO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (System.nanoTime() - startTimeNs) / 1000;
    }

//    @OptIn(markerClass = UnstableApi.class)
//    private static long retrieveMetadataMedia3(Context context, String path) {
//        long startTimeNs = System.nanoTime();
//        try {
//            MetadataRetriever.retrieveMetadata(
//                            new ProgressiveMediaSource.Factory(new DefaultDataSource.Factory(context)),
//                            MediaItem.fromUri(path))
//                    .get();
//        } catch (ExecutionException | RuntimeException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return (System.nanoTime() - startTimeNs) / 1000;
//    }

}
