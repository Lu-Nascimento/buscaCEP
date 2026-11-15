package br.edu.buscacep.data.repository

import br.edu.buscacep.R
import br.edu.buscacep.data.model.Endereco
import br.edu.buscacep.data.remote.RetrofitClient
import br.edu.buscacep.data.remote.ViaCepApi
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Faz as chamadas à API e converte qualquer falha (rede, HTTP, JSON inválido)
 * em um [Resultado.Falha] com mensagem amigável. Assim o app nunca fecha por erro de rede.
 */
class EnderecoRepository(
    private val api: ViaCepApi = RetrofitClient.viaCepApi
) {

    suspend fun buscarPorCep(cep: String): Resultado<Endereco> = executar {
        val endereco = api.buscarPorCep(cep)
        if (endereco.erro == true) {
            Resultado.Falha(R.string.erro_cep_nao_encontrado)
        } else {
            Resultado.Sucesso(endereco)
        }
    }

    suspend fun buscarPorEndereco(
        uf: String,
        cidade: String,
        logradouro: String
    ): Resultado<List<Endereco>> = executar {
        val lista = api.buscarPorEndereco(uf, cidade, logradouro)
        if (lista.isEmpty()) {
            Resultado.Falha(R.string.erro_nenhum_resultado)
        } else {
            Resultado.Sucesso(lista)
        }
    }

    private suspend fun <T> executar(bloco: suspend () -> Resultado<T>): Resultado<T> =
        try {
            bloco()
        } catch (e: CancellationException) {
            throw e // cancelamento de coroutine não é erro
        } catch (e: HttpException) {
            Resultado.Falha(
                when (e.code()) {
                    400 -> R.string.erro_requisicao_invalida
                    in 500..599 -> R.string.erro_servico_indisponivel
                    else -> R.string.erro_resposta_inesperada
                }
            )
        } catch (e: JsonParseException) {
            Resultado.Falha(R.string.erro_resposta_invalida)
        } catch (e: SocketTimeoutException) {
            Resultado.Falha(R.string.erro_tempo_esgotado)
        } catch (e: UnknownHostException) {
            Resultado.Falha(R.string.erro_sem_conexao)
        } catch (e: IOException) {
            Resultado.Falha(R.string.erro_sem_conexao)
        } catch (e: Exception) {
            Resultado.Falha(R.string.erro_inesperado)
        }
}
