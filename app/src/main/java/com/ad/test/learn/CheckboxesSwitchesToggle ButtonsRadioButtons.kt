package com.ad.test.learn

import android.graphics.Color
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.ToggleButton
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.ad.test.R

@Preview
@Composable
fun Checkboxes() {

    val userChoice = remember { mutableStateListOf(false, false) }

    Column {
        Text("Choose your extras:")
        AndroidView({ context ->
            CheckBox(context).apply {
                text = context.getString(R.string.fries)
                setOnCheckedChangeListener { _, bool ->
                    userChoice[0] = bool
                }
            }
        }, update = {
            it.setTextColor(if (userChoice[0]) Color.RED else Color.YELLOW)
        })
        AndroidView(
            { context ->
                CheckBox(context).apply {
                    text = context.getString(R.string.salad)
                    setOnCheckedChangeListener { _, bool ->
                        userChoice[1] = bool
                    }
                }
            }, update = {
                it.setTextColor(if (userChoice[1]) Color.RED else Color.YELLOW)
            }
        )
    }
}

@Preview
@Composable
fun Switch() {
    AndroidView({ context ->
        Switch(context).apply {
            text = context.getString(R.string.dark_mode)
        }
    })
}

@Preview
@Composable
fun ToggleButton() {
    AndroidView({ context ->
        ToggleButton(context).apply {
            text = context.getString(R.string.toggle_is_off)
            textOff = "Toggle is off"
            textOn = "Toggle is on"
        }
    })
}

@Preview
@Composable
fun RadioButton() {

    var userChoice by remember { mutableStateOf("") }

    Column {
        AndroidView({ context ->
            RadioGroup(context).apply {
                setOnCheckedChangeListener { _, i ->
                    when (i) {
                        R.id.choiceA -> userChoice = context.getString(R.string.choice_a)
                        R.id.choiceB -> userChoice = context.getString(R.string.choice_b)
                        R.id.choiceC -> userChoice = context.getString(R.string.choice_c)
                        R.id.choiceD -> userChoice = context.getString(R.string.choice_d)
                    }
                }
                addView(
                    RadioButton(context).apply {
                        text = context.getString(R.string.choice_a)
                        id = R.id.choiceA
                    }
                )
                addView(RadioButton(context).apply {
                    text = context.getString(R.string.choice_b)
                    id = R.id.choiceB
                })
                addView(RadioButton(context).apply {
                    text = context.getString(R.string.choice_c)
                    id = R.id.choiceC
                })
                addView(RadioButton(context).apply {
                    text = context.getString(R.string.choice_d)
                    id = R.id.choiceD
                })
            }
        })
        Text(userChoice)
    }
}