package com.ad.test.learn

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

@Preview(showSystemUi = false, showBackground = true, backgroundColor = 0xFFAED581)
@Composable
fun Spinner() {
    Column(Modifier.fillMaxSize()) {
        AndroidView(
            { context ->
                Spinner(context).apply {
                    adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        listOf("USA", "Germany", "Belgium", "France")
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
            },
            Modifier.fillMaxWidth(),
        )
        AndroidView(
            { context ->
                AutoCompleteTextView(context).apply {
                    setAdapter(
                        ArrayAdapter(
                            context,
                            android.R.layout.simple_dropdown_item_1line,
                            listOf("USA", "Germany", "Belgium", "France")
                        )
                    )
                }
            }, Modifier.fillMaxWidth()
        )
    }
}