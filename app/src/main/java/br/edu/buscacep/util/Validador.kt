package br.edu.buscacep.util

/** Regras de validação das entradas do usuário (sem dependência do Android, fácil de testar). */
object Validador {

    private val regexCep = Regex("^\\d{5}-?\\d{3}$")

    private val ufsValidas = setOf(
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
        "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    )

    /** CEP válido: 8 dígitos, com ou sem hífen (ex.: 01001-000 ou 01001000). */
    fun cepValido(texto: String): Boolean = regexCep.matches(texto.trim())

    fun somenteDigitos(texto: String): String = texto.filter { it.isDigit() }

    fun ufValida(texto: String): Boolean = texto.trim().uppercase() in ufsValidas

    /** A API ViaCEP exige pelo menos 3 caracteres para cidade e logradouro. */
    fun textoBuscaValido(texto: String): Boolean = texto.trim().length >= 3
}
