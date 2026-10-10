package com.karan.schemesaathi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.ui.navigation.SchemeSaathiNavGraph
import com.karan.schemesaathi.ui.theme.SchemeSaathiTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchemeSaathiTheme {
                SchemeSaathiNavGraph(userPreferences = userPreferences)
            }
        }
    }
}
