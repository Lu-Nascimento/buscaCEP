# BuscaCEP – Consulta de endereços

**Disciplina:** Desenvolvimento para Dispositivos Móveis – Trabalho NP2
**Integrantes:** Ana Luísa Nascimento e Antonia Karoline Lima

## Problema e público-alvo
Preencher formulários, enviar encomendas ou localizar um endereço que exige o CEP (ou a rua) corretos. O **BuscaCEP** permite descobrir o endereço completo a partir do CEP, ou descobrir o CEP a partir do nome da rua. Público-alvo: qualquer pessoa que precise consultar endereços brasileiros (estudantes, pequenos comércios, entregadores).

## Funcionalidades
- **Busca por CEP** → vai direto para a tela de detalhes.
- **Busca por endereço** (UF + cidade + rua) → lista de resultados → toque em um item → detalhes.
- Validação das entradas (CEP com 8 dígitos, UF válida, mínimo de 3 letras) com mensagem no próprio campo.
- Estados de **carregamento** (indicador + botão desabilitado), **sucesso** (dados exibidos) e **erro** (cartão com mensagem: sem internet, tempo esgotado, CEP inexistente, serviço fora do ar, JSON inválido).
- Na tela de detalhes: copiar CEP, compartilhar endereço e abrir no app de mapas.

## API utilizada
[ViaCEP](https://viacep.com.br) – pública, gratuita, sem chave/token.

| Uso | Endpoint |
|---|---|
| Busca por CEP | `GET https://viacep.com.br/ws/{cep}/json/` |
| Busca por endereço | `GET https://viacep.com.br/ws/{uf}/{cidade}/{logradouro}/json/` |

Exemplo de JSON (`/ws/01001000/json/`):
```json
{ "cep": "01001-000", "logradouro": "Praça da Sé", "complemento": "lado ímpar",
  "bairro": "Sé", "localidade": "São Paulo", "uf": "SP", "ibge": "3550308", "ddd": "11" }
```
- CEP inexistente: a API responde HTTP 200 com `{"erro": true}` → tratado como erro "CEP não encontrado".
- Busca por endereço retorna um **array** de objetos como o acima (`List<Endereco>`).
- Campos usados no app: cep, logradouro, complemento, bairro, localidade, uf, estado, regiao, ibge, ddd.

## Tecnologias
Kotlin · Android Studio · **Jetpack Compose** · Material 3 · **Navigation Compose (NavHost + NavController + routes)** · ViewModel + StateFlow · Coroutines · **Retrofit 2.11** + **Gson** (JSON) · OkHttp (timeouts) · JUnit (testes do validador).
`minSdk 26` (Android 8.0) · `targetSdk/compileSdk 34`.

## Organização do projeto
```
app/src/main/java/br/edu/buscacep/
├── data/
│   ├── model/Endereco.kt            # modelo do JSON
│   ├── remote/ViaCepApi.kt          # interface Retrofit (endpoints)
│   ├── remote/RetrofitClient.kt     # configuração do Retrofit/OkHttp/Gson
│   └── repository/                  # EnderecoRepository + Resultado (sucesso/falha, trata exceções)
├── ui/
│   ├── navegacao/
│   │   ├── Rotas.kt                 # routes: "busca", "lista", "detalhe/{endereco}"
│   │   └── AppNavegacao.kt          # NavHost + NavController (grafo de navegação)
│   ├── main/                        # MainActivity (única), BuscaScreen, BuscaViewModel, UiState
│   ├── lista/ListaScreen.kt         # lista de resultados (LazyColumn)
│   ├── detalhe/DetalheScreen.kt     # detalhes do endereço
│   └── theme/Tema.kt                # tema Material 3 (mesmas cores do app original)
└── util/Validador.kt                # validação de CEP, UF e textos
```

## Navegação (routes, NavHost e NavController)
O app tem **uma única Activity** (`MainActivity`) que hospeda o `AppNavegacao`. Nele, o `rememberNavController()` cria o **NavController** e o **NavHost** liga cada **route** a uma tela:

| Route | Tela | Observação |
|---|---|---|
| `busca` | `BuscaScreen` | destino inicial (`startDestination`) |
| `lista` | `ListaScreen` | lê os resultados do `BuscaViewModel` |
| `detalhe/{endereco}` | `DetalheScreen` | `{endereco}` é o `Endereco` em JSON (Gson), codificado com `Uri.encode` por `Rotas.detalhe(...)` |

- Ir para outra tela: `navController.navigate(Rotas.LISTA)` / `navController.navigate(Rotas.detalhe(endereco))`.
- Voltar: `navController.popBackStack()` (seta da barra superior e botão voltar do sistema).
- As telas não conhecem o NavController: recebem callbacks (`onAbrirDetalhe`, `onAbrirLista`, `onVoltar`).

**Fluxo:** `BuscaScreen` valida → `BuscaViewModel` chama `EnderecoRepository` → Retrofit executa a requisição (coroutine) → Gson converte JSON em `Endereco` → `UiState` atualiza a tela → ao ter sucesso, o `NavController` navega para a route `detalhe/{endereco}` (busca por CEP) ou `lista` (busca por endereço).

## Para executar (Android Studio)
1. Instale o **Android Studio** (versão recente, com JDK 17+ embutido).
2. `File > Open` e selecione a pasta **BuscaCEP** (a que contém `settings.gradle.kts`).
3. Aguarde o **Gradle Sync** (precisa de internet na 1ª vez; aceite instalar o *Android SDK Platform 34* se solicitado).
4. Crie um emulador: `Tools > Device Manager > Create Device` → ex.: *Pixel 7* → imagem **API 34** (ou 30+) → Finish.
5. Escolha o emulador na barra superior e clique em **Run ▶** (Shift+F10).
6. Teste: digite `01001-000` e toque em **Buscar**. Em "Por endereço": `SP`, `São Paulo`, `Paulista`.
7. Para ver o erro de rede: ative o modo avião no emulador e busque de novo.
> O emulador precisa de internet (usa a do computador). Em celular físico: ative *Depuração USB* e conecte o cabo.

## Limitações conhecidas
- Depende da internet e da disponibilidade do ViaCEP.
- Busca por endereço retorna no máximo 50 resultados (limite da API).
- Sem histórico/favoritos (fora do escopo do T2).
- Sem versão em modo escuro.

## Uso de IA
### Uso de Inteligência Artificial
Durante a realização deste projeto, utilizamos ferramentas de Inteligência Artificial como apoio em algumas etapas do desenvolvimento. A IA nos ajudou principalmente na organização inicial do projeto, na definição da estrutura de pastas e na compreensão de como implementar alguns dos requisitos solicitados. Também foi utilizada como suporte durante a programação, auxiliando na criação de alguns trechos de código e na compreensão de determinadas funcionalidades. Além disso, recorremos à ferramenta quando surgiram erros durante o desenvolvimento, utilizando as sugestões apresentadas para entender os problemas e encontrar possíveis soluções. Também foi uma fonte de consulta para esclarecer dúvidas sobre conceitos e recursos utilizados no projeto. É importante destacar que a IA foi utilizada apenas como ferramenta de apoio. Todo o código sugerido foi analisado, adaptado e revisado pelas integrantes do grupo. Realizamos nossos próprios ajustes e testes para verificar o funcionamento das funcionalidades, sendo todo o processo de desenvolvimento acompanhado e validado por nós.



