package br.edu.buscacep.ui.detalhe

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.buscacep.R
import br.edu.buscacep.data.model.Endereco
import kotlinx.coroutines.launch

/**
 * Tela da route "detalhe/{endereco}": mostra todos os dados de um endereço e
 * oferece ações: copiar CEP, compartilhar e abrir no mapa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(
    endereco: Endereco?,
    onVoltar: () -> Unit
) {
    if (endereco == null) {
        // não há dados para mostrar
        LaunchedEffect(Unit) { onVoltar() }
        return
    }

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val msgCopiado = stringResource(R.string.msg_cep_copiado)
    val msgSemMapa = stringResource(R.string.erro_sem_app_mapa)
    val tituloCompartilhar = stringResource(R.string.compartilhar)

    val estado = listOfNotNull(endereco.estado, endereco.uf)
        .filter { it.isNotBlank() }
        .joinToString(" - ")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_detalhes)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // CEP em destaque
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                SelectionContainer {
                    Text(
                        text = endereco.cep.orEmpty(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Campo(R.string.rotulo_logradouro, endereco.logradouro)
                    Campo(R.string.rotulo_complemento, endereco.complemento)
                    Campo(R.string.rotulo_bairro, endereco.bairro)
                    Campo(R.string.rotulo_cidade, endereco.localidade)
                    Campo(R.string.rotulo_estado, estado)
                    Campo(R.string.rotulo_regiao, endereco.regiao)
                    Campo(R.string.rotulo_ddd, endereco.ddd)
                    Campo(R.string.rotulo_ibge, endereco.ibge)
                }
            }

            // Abrir no mapa
            Button(
                onClick = {
                    val uri = Uri.parse("geo:0,0?q=" + Uri.encode(endereco.enderecoCompleto()))
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    } catch (ex: ActivityNotFoundException) {
                        scope.launch {
                            snackbarHostState.showSnackbar(msgSemMapa, duration = SnackbarDuration.Long)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) { Text(stringResource(R.string.abrir_mapa)) }

            // Copiar CEP
            OutlinedButton(
                onClick = {
                    clipboard.setText(AnnotatedString(endereco.cep.orEmpty()))
                    scope.launch { snackbarHostState.showSnackbar(msgCopiado) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) { Text(stringResource(R.string.copiar_cep)) }

            // Compartilhar
            OutlinedButton(
                onClick = {
                    val envio = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, endereco.enderecoCompleto())
                    }
                    context.startActivity(Intent.createChooser(envio, tituloCompartilhar))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) { Text(stringResource(R.string.compartilhar)) }
        }
    }
}

/** Mostra um campo (rótulo + valor); se o valor não existir, mostra "—". */
@Composable
private fun Campo(@StringRes rotulo: Int, valor: String?) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(rotulo),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        SelectionContainer {
            Text(
                text = valor?.takeIf { it.isNotBlank() } ?: stringResource(R.string.nao_informado),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
