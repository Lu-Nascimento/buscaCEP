package br.edu.buscacep.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.buscacep.R
import br.edu.buscacep.data.model.Endereco
import br.edu.buscacep.util.Validador

private enum class ModoBusca { CEP, ENDERECO }

/**
 * Tela inicial (route "busca"): permite buscar por CEP (vai direto aos detalhes)
 * ou por endereço (vai para a lista de resultados e depois aos detalhes).
 * A navegação é pedida por callbacks, quem executa é o NavController.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaScreen(
    viewModel: BuscaViewModel,
    onAbrirDetalhe: (Endereco) -> Unit,
    onAbrirLista: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val teclado = LocalSoftwareKeyboardController.current

    var modo by rememberSaveable { mutableStateOf(ModoBusca.CEP) }
    var cep by rememberSaveable { mutableStateOf("") }
    var uf by rememberSaveable { mutableStateOf("") }
    var cidade by rememberSaveable { mutableStateOf("") }
    var logradouro by rememberSaveable { mutableStateOf("") }

    var erroCep by rememberSaveable { mutableStateOf<Int?>(null) }
    var erroUf by rememberSaveable { mutableStateOf<Int?>(null) }
    var erroCidade by rememberSaveable { mutableStateOf<Int?>(null) }
    var erroLogradouro by rememberSaveable { mutableStateOf<Int?>(null) }

    var menuUfAberto by remember { mutableStateOf(false) }
    val ufs = stringArrayResource(R.array.ufs)

    // Quando a busca dá certo, avisa o NavHost para trocar de tela e "consome" o estado.
    LaunchedEffect(estado) {
        when (val e = estado) {
            is UiState.SucessoCep -> {
                viewModel.consumirSucesso()
                onAbrirDetalhe(e.endereco)
            }
            is UiState.SucessoLista -> {
                viewModel.consumirSucesso()
                onAbrirLista()
            }
            else -> Unit
        }
    }

    fun limparErros() {
        erroCep = null
        erroUf = null
        erroCidade = null
        erroLogradouro = null
    }

    fun buscar() {
        focusManager.clearFocus()
        teclado?.hide()
        limparErros()

        if (modo == ModoBusca.CEP) {
            if (!Validador.cepValido(cep)) {
                erroCep = R.string.erro_cep_formato
                return
            }
            viewModel.buscarPorCep(Validador.somenteDigitos(cep))
        } else {
            val ufTexto = uf.trim().uppercase()

            var valido = true
            if (!Validador.ufValida(ufTexto)) {
                erroUf = R.string.erro_uf
                valido = false
            }
            if (!Validador.textoBuscaValido(cidade)) {
                erroCidade = R.string.erro_minimo_3
                valido = false
            }
            if (!Validador.textoBuscaValido(logradouro)) {
                erroLogradouro = R.string.erro_minimo_3
                valido = false
            }
            if (!valido) return

            viewModel.buscarPorEndereco(ufTexto, cidade, logradouro)
        }
    }

    val carregando = estado is UiState.Carregando

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.subtitulo),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                SegmentedButton(
                    selected = modo == ModoBusca.CEP,
                    onClick = { modo = ModoBusca.CEP; limparErros() },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text(stringResource(R.string.modo_cep)) }
                SegmentedButton(
                    selected = modo == ModoBusca.ENDERECO,
                    onClick = { modo = ModoBusca.ENDERECO; limparErros() },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text(stringResource(R.string.modo_endereco)) }
            }

            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (modo == ModoBusca.CEP) {
                    // ---------- Busca por CEP ----------
                    OutlinedTextField(
                        value = cep,
                        onValueChange = { novo ->
                            // apenas dígitos e hífen, no máximo 9 caracteres
                            if (novo.length <= 9 && novo.all { it.isDigit() || it == '-' }) cep = novo
                        },
                        label = { Text(stringResource(R.string.hint_cep)) },
                        isError = erroCep != null,
                        supportingText = erroCep?.let { { Text(stringResource(it)) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = { buscar() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // ---------- Busca por endereço ----------
                    ExposedDropdownMenuBox(
                        expanded = menuUfAberto,
                        onExpandedChange = { menuUfAberto = it }
                    ) {
                        OutlinedTextField(
                            value = uf,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.hint_uf)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuUfAberto)
                            },
                            isError = erroUf != null,
                            supportingText = erroUf?.let { { Text(stringResource(it)) } },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = menuUfAberto,
                            onDismissRequest = { menuUfAberto = false }
                        ) {
                            ufs.forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao) },
                                    onClick = {
                                        uf = opcao
                                        erroUf = null
                                        menuUfAberto = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = cidade,
                        onValueChange = { cidade = it },
                        label = { Text(stringResource(R.string.hint_cidade)) },
                        isError = erroCidade != null,
                        supportingText = erroCidade?.let { { Text(stringResource(it)) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = logradouro,
                        onValueChange = { logradouro = it },
                        label = { Text(stringResource(R.string.hint_logradouro)) },
                        isError = erroLogradouro != null,
                        supportingText = erroLogradouro?.let { { Text(stringResource(it)) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = { buscar() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = { buscar() },
                enabled = !carregando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.buscar))
            }

            // ---------- Estado: carregando ----------
            if (carregando) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    Text(
                        text = stringResource(R.string.buscando),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            // ---------- Estado: erro ----------
            if (estado is UiState.Erro) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Text(
                        text = stringResource((estado as UiState.Erro).mensagem),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
