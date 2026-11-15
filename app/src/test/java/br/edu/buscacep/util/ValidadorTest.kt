package br.edu.buscacep.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorTest {

    @Test
    fun cepValido_aceitaComEsemHifen() {
        assertTrue(Validador.cepValido("01001-000"))
        assertTrue(Validador.cepValido("01001000"))
        assertTrue(Validador.cepValido("  01001000 "))
    }

    @Test
    fun cepValido_rejeitaFormatosInvalidos() {
        assertFalse(Validador.cepValido(""))
        assertFalse(Validador.cepValido("123"))
        assertFalse(Validador.cepValido("abcdefgh"))
        assertFalse(Validador.cepValido("01001-0000"))
    }

    @Test
    fun somenteDigitos_removeHifen() {
        assertEquals("01001000", Validador.somenteDigitos("01001-000"))
    }

    @Test
    fun ufValida_ignoraCaixa() {
        assertTrue(Validador.ufValida("sp"))
        assertFalse(Validador.ufValida("XX"))
        assertFalse(Validador.ufValida(""))
    }

    @Test
    fun textoBuscaValido_exigeTresCaracteres() {
        assertTrue(Validador.textoBuscaValido("Rua "))
        assertFalse(Validador.textoBuscaValido("ab"))
        assertFalse(Validador.textoBuscaValido("  a "))
    }
}
