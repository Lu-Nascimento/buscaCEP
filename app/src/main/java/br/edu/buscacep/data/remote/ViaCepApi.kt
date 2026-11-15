package br.edu.buscacep.data.remote

import br.edu.buscacep.data.model.Endereco
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Definição dos endpoints da API ViaCEP (https://viacep.com.br).
 */
interface ViaCepApi {

    /** GET https://viacep.com.br/ws/{cep}/json/  → um único endereço. */
    @GET("ws/{cep}/json/")
    suspend fun buscarPorCep(@Path("cep") cep: String): Endereco

    /** GET https://viacep.com.br/ws/{uf}/{cidade}/{logradouro}/json/  → lista de endereços. */
    @GET("ws/{uf}/{cidade}/{logradouro}/json/")
    suspend fun buscarPorEndereco(
        @Path("uf") uf: String,
        @Path("cidade") cidade: String,
        @Path("logradouro") logradouro: String
    ): List<Endereco>
}
