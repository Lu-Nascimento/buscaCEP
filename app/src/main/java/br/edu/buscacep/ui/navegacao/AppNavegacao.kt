package br.edu.buscacep.ui.navegacao

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.buscacep.data.model.Endereco
import br.edu.buscacep.ui.detalhe.DetalheScreen
import br.edu.buscacep.ui.lista.ListaScreen
import br.edu.buscacep.ui.main.BuscaScreen
import br.edu.buscacep.ui.main.BuscaViewModel
import com.google.gson.Gson

/**
 * Grafo de navegação do app. O [NavHostController] (NavController) é quem executa
 * `navigate(route)` e `popBackStack()`; o [NavHost] associa cada route a uma tela.
 *
 * O [BuscaViewModel] é criado aqui (escopo da Activity), portanto é o mesmo
 * para a tela de busca e para a tela de lista.
 */
@Composable
fun AppNavegacao(
    navController: NavHostController = rememberNavController(),
    viewModel: BuscaViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Rotas.BUSCA
    ) {
        composable(Rotas.BUSCA) {
            BuscaScreen(
                viewModel = viewModel,
                onAbrirDetalhe = { endereco -> navController.navigate(Rotas.detalhe(endereco)) },
                onAbrirLista = { navController.navigate(Rotas.LISTA) }
            )
        }

        composable(Rotas.LISTA) {
            ListaScreen(
                viewModel = viewModel,
                onVoltar = { navController.popBackStack() },
                onAbrirDetalhe = { endereco -> navController.navigate(Rotas.detalhe(endereco)) }
            )
        }

        composable(
            route = Rotas.DETALHE,
            arguments = listOf(navArgument(Rotas.ARG_ENDERECO) { type = NavType.StringType })
        ) { entrada ->
            // O Navigation já devolve o argumento decodificado.
            val json = entrada.arguments?.getString(Rotas.ARG_ENDERECO).orEmpty()
            val endereco = runCatching { Gson().fromJson(json, Endereco::class.java) }.getOrNull()

            DetalheScreen(
                endereco = endereco,
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}
