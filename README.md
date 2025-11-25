
# 📚 Rede Social Literária

### 🧩 Visão Geral

A **Rede Social Literária** é uma plataforma desenvolvida para conectar leitores e incentivar o hábito da leitura por meio da interação, compartilhamento e organização de experiências literárias.
O sistema permite que os usuários **criem postagens sobre livros, interajam com amigos, participem de clubes de leitura, troquem exemplares e acompanhem seu progresso de leitura** de maneira simples, intuitiva e social.

---

## 👥 Equipe de Desenvolvimento

**Nome da Equipe:** Readium

**Integrantes:**

Natan dos Santos - jnatansb   
Kendriks da Paixão - kendriks  

---

## 🚀 Objetivo do Projeto

Criar um ambiente digital que promova o **compartilhamento de leituras e descobertas literárias**, permitindo que usuários:

* Publiquem textos, imagens e marcações de livros;
* Interajam por meio de curtidas, comentários e compartilhamentos;
* Criem e participem de clubes de leitura;
* Avaliem e resenhem livros;
* Organizem listas temáticas e coleções pessoais;
* Realizem trocas de livros com outros leitores;
* Acompanhem estatísticas e gráficos sobre seu progresso literário.

---

## 💡 Principais Funcionalidades

### 📝 Postagens e Interações

* Criação de postagens com texto, imagem e livros marcados.
* Feed dinâmico com publicações do usuário e de amigos.
* Curtidas, comentários e compartilhamentos com atualização em tempo real.
* Sistema de notificações para novas interações.

### 🤝 Conexões entre Usuários

* Envio, aceitação e recusa de solicitações de amizade.
* Controle de status de amizade (“pendente”, “amigos”, “recusado”).
* Perfis personalizados com foto, biografia e preferências literárias.

### 📖 Clubes de Leitura

* Criação de clubes públicos ou privados com temas de leitura definidos.
* Envio de convites, controle de membros e sistema de chat interno.
* Atualização automática do livro em discussão e notificações aos membros.
* Listagem e filtros por tipo de clube, livro atual e participação.

### 🔖 Organização Pessoal

* Criação de listas de livros temáticas (por gênero, ocasião ou interesse).
* Ordenação das listas por arrastar e soltar (drag-and-drop).
* Favoritar livros e cadastrar novos via ISBN com busca automática em API.
* Avaliar obras com notas de 0 a 5 estrelas e escrever resenhas.

### 🔄 Trocas de Livros

* Marcar livros como disponíveis para troca, informando condição e localização.
* Buscar títulos disponíveis por filtros (gênero, cidade, condição, título).
* Enviar e acompanhar propostas de troca com status em tempo real.

### 📊 Estatísticas e Progresso de Leitura

* Painel com gráficos interativos exibindo:

  * Total de livros lidos (mensal, anual e total geral);
  * Gêneros e autores mais lidos;
  * Evolução de leituras ao longo do tempo.
* Filtros por período e visualização responsiva (desktop e mobile).

---

## 🧠 Tecnologias Utilizadas

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-34A853?style=for-the-badge&logo=android&logoColor=white)
![Clean Architecture](https://img.shields.io/badge/Clean_Architecture-FF6F00?style=for-the-badge&logo=architecture&logoColor=white)
![MVVM](https://img.shields.io/badge/MVVM-02569B?style=for-the-badge&logo=android&logoColor=white)
![ViewModel](https://img.shields.io/badge/ViewModel-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Cloud Firestore](https://img.shields.io/badge/Cloud_Firestore-FF6F00?style=for-the-badge&logo=firebase&logoColor=white)
![Firebase Auth](https://img.shields.io/badge/Firebase_Auth-FF6F00?style=for-the-badge&logo=firebase&logoColor=white)
![BaaS](https://img.shields.io/badge/BaaS-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white)

---

## ⚙️ Principais Requisitos do Sistema

* Interface responsiva e acessível.
* Persistência e consistência dos dados.
* Validação de campos obrigatórios e formatos de imagem/texto.
* Mensagens claras de erro e sucesso.
* Atualizações em tempo real em interações sociais e estatísticas.
* Requisições seguras e otimizadas.

---

## 🔀 Fluxo de Trabalho

Nós usaremos o fluxo GitHub Flow, onde teremos uma branch principal(main) e branchs de feature. A ideia é que tudo o que está na main esteja pronto para produção.

* `main` : versão para produção
* `develop` : versão em desenvolvimento
* `feature/nome-da-featue` : para novas funcionalidades
* `fix/descricao` : para corrigir bugs
* `docs/descricao` : somente para alteração de documentação 

## 📜 Nomeação de commits
Nś adotaremos o padrão *Conventional Commits*, onde usaremos os seguintes prefixos:

* **feat:** adiciona uma nova funcionalidade
* **fix:** correção de bug
* **chore:** manutenção ou configuração(dependência, etc)
* **docs:** alteração na documentação

## 📄 Licença

Este projeto é de **uso acadêmico** e destina-se a fins educacionais.
Todos os direitos reservados à equipe de desenvolvimento.