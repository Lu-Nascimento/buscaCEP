package br.edu.buscacep.ui.main

import androidx.annotation.StringRes
import br.edu.buscacep.data.model.Endereco

/** Estados da tela de busca: inicial, carregando, sucesso (CEP ou lista) e erro. */
sealed interface UiState {
    data object Inicial : UiState
    data object Carregando : UiState
    data class SucessoCep(val endereco: Endereco) : UiState
    data class SucessoLista(val enderecos: List<Endereco>) : UiState
    data class Erro(@StringRes val mensagem: Int) : UiState
}
