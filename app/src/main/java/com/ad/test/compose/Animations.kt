package com.ad.test.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ad.test.R

@Preview
@Composable
fun ExpandableCard() {
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.size(300.dp)) {
        Button(onClick = { expanded = !expanded }) {
            Text("Toggle")
        }

        AnimatedVisibility(visible = expanded) {
            Text(
                "This content animates in and out!",
                modifier = Modifier.padding(16.dp),
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF7C4DFF)
@Composable
fun FadeSlideContent() {
    var visible by remember { mutableStateOf(true) }
    Column {
        Button({ visible = !visible }) { }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Fades and slides!", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun ColorAnimationExample() {
    var isCorrect by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isCorrect) Color.Green else Color.Red,
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(backgroundColor)
            .clickable { isCorrect = !isCorrect }
    )
}

@Preview
@Composable
fun AlphaAnimationExample() {
    var enabled by remember { mutableStateOf(true) }

    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.3f,
        animationSpec = tween(durationMillis = 1000)
    )

    val fontSize by animateIntAsState(
        30,
        tween(1000)
    )

    Text(
        text = "Fading text",
        color = Color.Green,
        fontSize = fontSize.sp,
        modifier = Modifier.alpha(alpha)
    )
}

@Preview
@Composable
fun SizeAnimationExample() {
    var expanded by remember { mutableStateOf(false) }

    val size by animateDpAsState(
        targetValue = if (expanded) 200.dp else 100.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .size(size)
            .background(Color.Blue)
            .clickable { expanded = !expanded }
    )
}

@Preview
@Composable
fun ExpandableText() {
    var expanded by remember { mutableStateOf(false) }
    val text by remember { derivedStateOf { if (expanded) "This is additional content that appears when expanded. The column will smoothly animate its size to fit the new content." else "Title" } }

    Column(
        modifier = Modifier
            .size(300.dp)
            .animateContentSize()  // Animate size changes
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.animateContentSize()
        )
    }
}

@Preview
@Composable
fun AnimatedLikeButton() {
    var isLiked by remember { mutableStateOf(false) }

    // Animate color from gray to red
    val iconColor by animateColorAsState(
        targetValue = if (isLiked) Color.Red else Color.Gray,
        animationSpec = tween(durationMillis = 1000)
    )

    // Animate size with spring for bounce
    val iconSize by animateDpAsState(
        targetValue = if (isLiked) 32.dp else 24.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    IconButton(
        onClick = { isLiked = !isLiked }
    ) {
        Icon(
            painterResource(if (isLiked) R.drawable.favorite_fill_24px else R.drawable.favorite_24px),
            contentDescription = if (isLiked) "Unlike" else "Like",
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview
@Composable
fun AnimatedLikeButtonAdvanced() {
    var isLiked by remember { mutableStateOf(false) }
    var animatingSize by remember { mutableStateOf(false) }

    val iconColor by animateColorAsState(
        targetValue = if (isLiked) Color.Red else Color.Gray,
        tween(600)
    )

    val iconSize by animateDpAsState(
        targetValue = if (animatingSize) 32.dp else 24.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            if (animatingSize) {
                animatingSize = false  // Shrink back to normal
            }
        }
    )

    IconButton(
        onClick = {
            isLiked = !isLiked
            if (isLiked) {
                animatingSize = true  // Trigger bounce
            }
        }
    ) {
        Crossfade(isLiked) {
            Icon(
                painterResource(if (it) R.drawable.favorite_fill_24px else R.drawable.favorite_24px),
                contentDescription = if (it) "Unlike" else "Like",
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Preview
@Composable
fun ContentSwitcher(showFirst: Boolean = true) {
    Crossfade(targetState = showFirst) { isFirst ->
        if (isFirst) {
            Text("First Content")
        } else {
            Text("Second Content")
        }
    }
}