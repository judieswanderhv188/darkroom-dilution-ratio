package com.darkroomdilution.ratio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.darkroomdilution.ratio.core.di.ServiceLocator
import com.darkroomdilution.ratio.core.theme.AppTheme
import com.darkroomdilution.ratio.presentation.app.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}
