package com.ad.test.learn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.database.getStringOrNull
import com.ad.test.ui.theme.TestTheme

class Main3a() : ComponentActivity() {

    // Register the result callback
    private val pickContact =
        registerForActivityResult(
            ActivityResultContracts.PickContact(),
            ::onPickContact
        )

    private val getTextContact = registerForActivityResult(
        GetText(),
        ::onGetText
    )

    private fun onGetText(text: String?) {
        Log.d("MAIN_ACTIVITY", "text: $text")
    }

    private fun onPickContact(contactUri: Uri?) {
        if (contactUri != null) {
            val contactData = contentResolver.query(contactUri, null, null, null, null)

            if (contactData != null && contactData.moveToFirst()) {
                val name = contactData.getStringOrNull(
                    contactData.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                )
                Log.d("MAIN_ACTIVITY", "name: $name")
            }
            contactData?.close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                Scaffold {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        Button({ pickContact.launch(null) }) { }
                        Button({ getTextContact.launch() }) { }
                    }
                }
            }
        }
    }
}

class GetText : ActivityResultContract<Unit, String?>() {
    override fun createIntent(
        context: Context,
        input: Unit
    ) = Intent(context, SecondActivity::class.java)

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ) = if (resultCode != Activity.RESULT_OK) null else
        intent?.getStringExtra(Intent.EXTRA_TEXT)
}

class SecondActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Button({
                    setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_TEXT, "Hello"))
                    finish()
                }) { }
            }
        }
    }
}