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
