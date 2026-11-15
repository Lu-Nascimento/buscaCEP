package br.edu.buscacep.ui.navegacao

import android.net.Uri
import br.edu.buscacep.data.model.Endereco
import com.google.gson.Gson

/**
 * Todas as routes (destinos) do app em um só lugar.
 *
 *  - "busca"                → tela inicial (CEP ou endereço)
 *  - "lista"                → resultados da busca por endereço
 *  - "detalhe/{endereco}"   → detalhes; o argumento {endereco} é o JSON do [Endereco]
 */
object Rotas {
    const val BUSCA = "busca"
    const val LISTA = "lista"

    const val ARG_ENDERECO = "endereco"
    const val DETALHE = "detalhe/{$ARG_ENDERECO}"

    /** Monta a route de detalhe já com o endereço (JSON codificado para caber na URL da route). */
    fun detalhe(endereco: Endereco): String =
        "detalhe/" + Uri.encode(Gson().toJson(endereco))
}
