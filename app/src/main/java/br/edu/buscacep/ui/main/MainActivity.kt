package br.edu.buscacep.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.edu.buscacep.ui.navegacao.AppNavegacao
import br.edu.buscacep.ui.theme.BuscaCepTheme

/**
 * Única Activity do app. Apenas hospeda o tema e o grafo de navegação
 * (NavHost); as telas de busca, lista e detalhe são destinos do NavHost.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BuscaCepTheme {
                AppNavegacao()
            }
        }
    }
}
