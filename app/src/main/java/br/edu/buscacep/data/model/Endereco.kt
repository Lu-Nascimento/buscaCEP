package br.edu.buscacep.data.model

import com.google.gson.annotations.SerializedName

/**
 * Representa o JSON retornado pelo ViaCEP, por exemplo:
 *
 * {
 *   "cep": "01001-000",
 *   "logradouro": "Praça da Sé",
 *   "complemento": "lado ímpar",
 *   "bairro": "Sé",
 *   "localidade": "São Paulo",
 *   "uf": "SP",
 *   "ibge": "3550308",
 *   "ddd": "11"
 * }
 *
 * Todos os campos são anuláveis porque o Gson ignora valores padrão do Kotlin
 * e porque a API pode omitir campos. Quando o CEP não existe, a API responde
 * 200 com {"erro": true}.
 *
 * Para ir de uma tela para outra, é convertido em JSON (Gson) e enviado como
 * argumento da route de navegação (veja Rotas.detalhe).
 */
data class Endereco(
    @SerializedName("cep") val cep: String? = null,
    @SerializedName("logradouro") val logradouro: String? = null,
    @SerializedName("complemento") val complemento: String? = null,
    @SerializedName("bairro") val bairro: String? = null,
    @SerializedName("localidade") val localidade: String? = null,
    @SerializedName("uf") val uf: String? = null,
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("regiao") val regiao: String? = null,
    @SerializedName("ibge") val ibge: String? = null,
    @SerializedName("ddd") val ddd: String? = null,
    @SerializedName("erro") val erro: Boolean? = null
) {

    /** Texto único com o endereço, usado para compartilhar e para abrir no mapa. */
    fun enderecoCompleto(): String =
        listOfNotNull(
            logradouro?.takeIf { it.isNotBlank() },
            bairro?.takeIf { it.isNotBlank() },
            listOfNotNull(
                localidade?.takeIf { it.isNotBlank() },
                uf?.takeIf { it.isNotBlank() }
            ).joinToString(" - ").takeIf { it.isNotBlank() },
            cep?.takeIf { it.isNotBlank() }
        ).joinToString(", ")
}
