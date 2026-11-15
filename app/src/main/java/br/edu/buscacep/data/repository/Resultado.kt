package br.edu.buscacep.data.repository

import androidx.annotation.StringRes

/** Resultado de uma operação remota: sucesso com dados ou falha com mensagem para o usuário. */
sealed interface Resultado<out T> {
    data class Sucesso<T>(val dados: T) : Resultado<T>
    data class Falha(@StringRes val mensagem: Int) : Resultado<Nothing>
}
