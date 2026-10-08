package ac.id.staialyasini.siakad

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import ac.id.staialyasini.siakad.core.ui.theme.SiakadAlYasiniTheme
import ac.id.staialyasini.siakad.presentation.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity menggunakan FragmentActivity agar mendukung penuh AndroidX BiometricPrompt
 * (Sidik Jari / Face Unlock) serta Dagger Hilt (@AndroidEntryPoint).
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SiakadAlYasiniTheme {
                AppNavHost()
            }
        }
    }
}
