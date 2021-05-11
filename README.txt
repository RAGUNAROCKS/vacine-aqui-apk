Vacine Aqui 0.1

- Construção estrutural do aplicativo.
    - O mapa é gerado a partir de um fragmento.
    - Foi adicionado um botão que redireciona o mapa para o centro da cidade.
- Foi adicionado três marcadores, indicado os postos de vacina.
    - UBS Ramiro De Azevedo
    - Unidade de Saúde da Familia Parque Pituaçu / Posto de Vacinação
    - UBS Manoel Vitorino
- Adição da chave do API no aplicativo.

Vacine Aqui 0.2

- Adição da geolocalização do usuário no mapa.
- O mapa ao iniciar, só exibirá o marcador do usuário.
- A cor do marcador foi modificada para laranja.
- Ao clicar no botão, será exibido os marcadores dos postos de vacina.

Vacine Aqui 0.3

- Criação de um vetor de marcadores para indicar os postos de Saúde
- Inclusão do minimap(icone) do VacineAqui
- Modificações irrisorias no botão de rota.

Vacine Aqui 0.4

- Adicionado a função de calculo de distancia entre os pontos
- O Botão fab foi renomeado para geraRota
- Adicionado a função de buscar o posto mais próximo no botão geraRota
- Mudança na cor da localização do usuário, de laranja para ciano.
- Realocação do botão geraRota para a tornar visivel o botão do google maps.

Vacine Aqui 0.5

- Desenvolvimento do botão de menu (fabOpcoes), que gera outros outros botões, onde sera adicionados novas funções, além do geraRota.
- O nome dos botões foram modificados para a inclusão do indicador fab (fabOpcoes, fabGeraRota, fabSituação).
- Aplicação de um banco de dados para relacionar dados de cada marcador, substituindo o vetor de marcadores.
- Adotado o modelo CRUD para a adminstração do aplicativo no banco de dados.
- Inserido a verificação de disponibilidade do Posto de Saúde ao gerar a rota.
- Atualização da lista do postos de vacina, baseado na lista do Vacinometro (https://filometro.saude.salvador.ba.gov.br/)

Vacine Aqui 0.6

- Criação do botão fabUsuario, que exibe a janela de login(login_dialog).
- Foi desenvolvido a aplicação de controle de fila (Filômetro), que é conectado a janela de login.
- Para acessar o Filômetro, coloque o id do posto (de 0 à 28), e provisóriamente a senha está padronizada para '1234'.
- No filometro, poderá modificar o numero de pacientes, enfermeiros e se o local está aberto e fechado.
    - Foi criado um bloqueio nos botões para acionar eles, só quando o posto estiver aberto.
    - Foi criado um bloqueio no botão Salvar(btnSalvar), para que não gere numero de clientes negativos e enfermeiros menores ou iguais a zero.
- O botão geraRota, agora considera a quantidade de pessoas na fila, relacionado a quantidadede pacientes para a quantidade de enfermeiros do posto.
- Icones dos botões modificados, para facil entendimento das ações.

Vacine Aqui 0.7

- Criação da pasta DatabaseNode que possui os codigos de conexão com o Servidor Node.JS.
- A pasta database foi renomeada para DatabaseLocal.
- O Banco de dados Local serve como busca rápida, enquanto o banco de dados do Node, serve para transmitir e atualizar as informações dos postos.
- Criado o campo Senha no banco de dados.