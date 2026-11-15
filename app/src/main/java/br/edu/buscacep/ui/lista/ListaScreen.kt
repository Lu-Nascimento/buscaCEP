package br.edu.buscacep.ui.lista

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.buscacep.R
import br.edu.buscacep.data.model.Endereco
import br.edu.buscacep.ui.main.BuscaViewModel

/** Tela da route "lista": exibe os endereços da busca por logradouro. Ao tocar em um item, abre os detalhes. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: BuscaViewModel,
    onVoltar: () -> Unit,
    onAbrirDetalhe: (Endereco) -> Unit
) {
    val enderecos by viewModel.resultados.collectAsStateWithLifecycle()

    if (enderecos.isEmpty()) {
        // não há dados para mostrar (ex.: processo recriado): volta para a busca
        LaunchedEffect(Unit) { onVoltar() }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.titulo_resultados))
                        Text(
                            text = pluralStringResource(
                                R.plurals.resultados_encontrados, enderecos.size, enderecos.size
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.voltar)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(enderecos) { _, endereco ->
                ItemEndereco(endereco = endereco, aoClicar = { onAbrirDetalhe(endereco) })
            }
        }
    }
}

@Composable
private fun ItemEndereco(endereco: Endereco, aoClicar: () -> Unit) {
    val logradouro = endereco.logradouro?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.sem_logradouro)

    val bairroCidade = listOfNotNull(
        endereco.bairro?.takeIf { it.isNotBlank() },
        listOfNotNull(
            endereco.localidade?.takeIf { it.isNotBlank() },
            endereco.uf?.takeIf { it.isNotBlank() }
        ).joinToString(" - ").takeIf { it.isNotBlank() }
    ).joinToString(" • ")

    Card(
        onClick = aoClicar,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = logradouro, style = MaterialTheme.typography.titleMedium)
            Text(
                text = bairroCidade,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = stringResource(R.string.cep_formato, endereco.cep.orEmpty()),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
