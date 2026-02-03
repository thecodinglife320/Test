package com.ad.test

import android.content.Context
import android.content.res.Resources
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun A(modifier: Modifier = Modifier) {
    val resources = LocalResources.current
    Text(
        resources.getScore(2, 10)
    )
}

fun Resources.getScore(pointsScored: Int, totalPoints: Int): String {
    return getString(
        R.string.students_score,
        getQuantityString(R.plurals.points, pointsScored, pointsScored),
        totalPoints
    )
}