package br.edu.buscacep.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.buscacep.data.model.Endereco
import br.edu.buscacep.data.repository.EnderecoRepository
import br.edu.buscacep.data.repository.Resultado
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Guarda o estado da busca. Sobrevive à rotação da tela e executa as chamadas
 * de rede em uma coroutine (sem travar a interface).
 */
class BuscaViewModel(
    private val repositorio: EnderecoRepository = EnderecoRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState>(UiState.Inicial)
    val estado: StateFlow<UiState> = _estado.asStateFlow()

    /** Lista da última busca por endereço; a tela de lista (route "lista") lê daqui. */
    private val _resultados = MutableStateFlow<List<Endereco>>(emptyList())
    val resultados: StateFlow<List<Endereco>> = _resultados.asStateFlow()

    private var buscaAtual: Job? = null

    fun buscarPorCep(cepSomenteDigitos: String) {
        iniciar {
            when (val r = repositorio.buscarPorCep(cepSomenteDigitos)) {
                is Resultado.Sucesso -> UiState.SucessoCep(r.dados)
                is Resultado.Falha -> UiState.Erro(r.mensagem)
            }
        }
    }

    fun buscarPorEndereco(uf: String, cidade: String, logradouro: String) {
        iniciar {
            when (val r = repositorio.buscarPorEndereco(uf, cidade.trim(), logradouro.trim())) {
                is Resultado.Sucesso -> {
                    _resultados.value = r.dados
                    UiState.SucessoLista(r.dados)
                }
                is Resultado.Falha -> UiState.Erro(r.mensagem)
            }
        }
    }

    /** Chamado depois que a navegação para a próxima tela foi feita. */
    fun consumirSucesso() {
        val atual = _estado.value
        if (atual is UiState.SucessoCep || atual is UiState.SucessoLista) {
            _estado.value = UiState.Inicial
        }
    }

    private fun iniciar(busca: suspend () -> UiState) {
        if (_estado.value is UiState.Carregando) return // evita requisições duplicadas
        _estado.value = UiState.Carregando
        buscaAtual?.cancel()
        buscaAtual = viewModelScope.launch {
            _estado.value = busca()
        }
    }
}
