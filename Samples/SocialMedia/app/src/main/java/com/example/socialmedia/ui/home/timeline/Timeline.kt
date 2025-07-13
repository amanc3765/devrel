package com.example.socialmedia.ui.home.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialmedia.R
import androidx.media3.ui.PlayerView

@Composable
fun Timeline(modifier: Modifier = Modifier) {
    val timelineMediaItem = TimelineMediaItem(
        "https://picsum.photos/200/300?grayscale", TimelineMediaType.PHOTO, 0, "", null
    )
    val media: List<TimelineMediaItem> = listOf(timelineMediaItem)
    val player: ExoPlayer = ExoPlayer.Builder(LocalContext.current).build()

    if (media.isEmpty()) {
        EmptyTimeline(modifier)
    } else {
        TimelineVerticalPager(
            modifier = modifier, mediaItems = media, player = player,
        )
    }
}

@Composable
fun EmptyTimeline(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = modifier
            .padding(contentPadding)
            .padding(64.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_timeline),
            contentDescription = null,
        )
        Text(
            text = stringResource(R.string.timeline_empty_title),
            modifier = Modifier.padding(top = 64.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.timeline_empty_message),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun TimelineVerticalPager(
    modifier: Modifier = Modifier,
    mediaItems: List<TimelineMediaItem>,
    player: Player?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        TimelinePage(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)), media = mediaItems[0], player = player
        )
    }
}

@Composable
fun TimelinePage(modifier: Modifier = Modifier, media: TimelineMediaItem, player: Player?) {
    when (media.type) {
        TimelineMediaType.VIDEO -> {
            AndroidView(
                factory = { PlayerView(it) },
                update = { playerView ->
                    playerView.player = player
                },
                modifier = modifier.fillMaxSize(),
            )
        }

        TimelineMediaType.PHOTO -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(media.uri).build(),
                contentDescription = null,
                modifier = modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EmptyTimelinePreview() {
    Timeline()
}

@Composable
@Preview(showBackground = true)
fun TimelinePagePreview() {
    val media = TimelineMediaItem(
        "https://picsum.photos/200/300?grayscale", TimelineMediaType.PHOTO, 0, "", null
    )
    TimelinePage(
        modifier = Modifier, media = media, player = null
    )
}