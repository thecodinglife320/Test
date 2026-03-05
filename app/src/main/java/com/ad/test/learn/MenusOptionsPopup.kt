package com.ad.test.learn

import android.os.Build
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.MenuProvider
import com.ad.test.R

@Preview
@Composable
fun ToolbarView() {
    AndroidView(
        { context ->
            // Wrap the context with an AppCompat theme. This is necessary because
            // androidx.appcompat.widget.Toolbar and SearchView require an AppCompat theme
            // to resolve attributes correctly during rendering. Without this,
            // the preview may fail with a "Binary XML file line #-1: null" error.
            val themedContext = ContextThemeWrapper(
                context,
                androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar
            )
            Toolbar(themedContext).apply {
                title = "Learn menus"
                addMenuProvider(object : MenuProvider {
                    override fun onCreateMenu(
                        menu: Menu,
                        menuInflater: MenuInflater
                    ) {
                        menuInflater.inflate(R.menu.example, menu)
                        val search = menu.findItem(R.id.search_icon)
                        // Use safe cast and null-check to avoid potential crashes during rendering
                        val searchView = search?.actionView as? SearchView

                        searchView?.setOnQueryTextListener(object : OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                search.collapseActionView()
                                return true
                            }

                            override fun onQueryTextChange(newText: String?) = true
                        })
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                        R.id.basic -> {
                            Toast.makeText(themedContext, menuItem.title, Toast.LENGTH_SHORT).show()
                            true
                        }

                        R.id.checkbox -> {
                            menuItem.isChecked = !menuItem.isChecked
                            true
                        }

                        R.id.with_icon -> {
                            Toast.makeText(themedContext, menuItem.title, Toast.LENGTH_SHORT).show()
                            true
                        }

                        R.id.search_icon -> {
                            true // nothing to do here
                        }

                        else -> {
                            false // none of my business
                        }
                    }
                })
            }
        }, Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun PopUpView() {
    Column(
        Modifier.size(400.dp)
    ) {
        AndroidView({ context ->
            Button(context).apply {
                text = "show menu"
                setOnClickListener {
                    PopupMenu(context, it).apply {
                        inflate(R.menu.example)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            setForceShowIcon(true)
                        }
                        setOnMenuItemClickListener {
                            true
                        }
                        show()
                    }
                }
            }
        })
    }
}