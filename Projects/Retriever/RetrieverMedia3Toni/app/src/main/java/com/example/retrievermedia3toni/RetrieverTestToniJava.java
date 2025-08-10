package com.example.retrievermedia3toni;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.MetadataRetriever;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class RetrieverTestToniJava {

    private final String testPath;

    RetrieverTestToniJava(String testPath) {
        this.testPath = testPath;
    }
    
    public Long startMetadataRetrievalTest(Activity context) {
        long media3Total = 0;
        for (int i = 0; i < 50; i++) {
            media3Total += retrieveMetadataMedia3(context, this.testPath);
        }
        long meanTimeUs = media3Total / 50;
        Log.d("Media3TestToni", String.format(Locale.ROOT, "[Media3TestToni][Serial] Mean Retriever Time: %30s %7d", this.testPath, meanTimeUs));
        return meanTimeUs;
    }

    @OptIn(markerClass = UnstableApi.class)
    private static long retrieveMetadataMedia3(Context context, String path) {
        long startTimeNs = System.nanoTime();
        try {
            MetadataRetriever.retrieveMetadata(new ProgressiveMediaSource.Factory(new DefaultDataSource.Factory(context)), MediaItem.fromUri(path)).get();
        } catch (ExecutionException | RuntimeException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return (System.nanoTime() - startTimeNs) / 1000;
    }

}
