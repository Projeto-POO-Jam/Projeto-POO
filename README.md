# PinguimJam: Plataforma de Game Jams

O PinguimJam é uma plataforma web completa desenvolvida para a comunidade de desenvolvimento de jogos. Ela permite que utilizadores organizem, participem e descubram "game jams" — competições onde os participantes criam um jogo do zero dentro de um prazo definido. A plataforma oferece um ecossistema robusto para a submissão de jogos, votação, feedback através de comentários e interação social entre os desenvolvedores.

## ✨ Funcionalidades Principais

-   **👤 Gestão de Utilizadores e Autenticação:**
    -   Sistema seguro de registo e login (utilizando e-mail ou nome de utilizador).
    -   Perfis de utilizador personalizáveis com foto, banner e links para redes sociais (GitHub, LinkedIn, Instagram e Facebook).
    -   Perfil listando todos os jogos criados, jams criadas, jams participadas e jogos avaliados.
    -   Funcionalidade para alteração de senha.

-   **:space_invader: Criação e Gestão de Jams:**
    -   Ferramenta para criar novas jams com título, descrição, datas de início e fim.
    -   Personalização visual completa da página da jam com `HTML` e `CSS`, incluindo cores, imagens de capa, wallpaper e banner.
    -   Gestão de jams criadas, com opções para editar e apagar.

-   **🎮 Submissão e Visualização de Jogos:**
    -   Inscrição fácil em jams abertas.
    -   Upload de jogos para as jams em que o utilizador está inscrito.
    -   Páginas dedicadas para cada jogo com descrição, conteúdo `HTML` e `CSS` personalizado, e link para jogar.
    -   Galeria para explorar todos os jogos submetidos na plataforma.

-   **❤️ Interação e Comunidade:**
    -   Sistema de votação ("likes") para os jogos.
    -   Secção de comentários para feedback.
    -   Ranking de jogos em cada jam com base nos votos da comunidade.

-   **🔔 Notificações em Tempo Real:**
    -   Notificações instantâneas sobre o início e fim de jams, novos comentários e outras atividades relevantes.

## 🛠️ Tecnologias Utilizadas

O projeto foi construído com uma stack de tecnologias modernas e robustas, tanto no backend como no frontend.

### Backend

-   **Java 21:** Versão LTS do Java, oferecendo uma base sólida e moderna.
-   **Spring Boot:** Framework Java, que facilita e agiliza o desenvolvimento de aplicações web e microserviços.
-   **Spring Security:** Para funcionalidades de autenticação e controle de acesso.
-   **Spring Data JPA & Hibernate:** Para a camada de persistência de dados e interação com a base de dados.
-   **MySQL / MariaDB:** Sistemas de gestão de base de dados relacionais.
-   **RabbitMQ:** Message broker para agendamento de atividades e processamento assíncrono.
-   **Maven:** Ferramenta de gestão de dependências e automação de build.
-   **Swagger / OpenAPI:** Para documentação interativa da API REST.

### Frontend

-   **HTML5 & CSS3:** Para a estrutura e estilização das páginas.
-   **JavaScript (ES6+):** Para a lógica e interatividade do lado do cliente.
-   **jQuery:** Biblioteca para simplificar a manipulação do DOM e requisições AJAX.
-   **Thymeleaf:** Motor de templates para renderização de páginas dinâmicas no servidor.

### Ferramentas Adicionais

-   **Docker:** Para containerização do RabbitMQ.
-   **Git & GitHub:** Para controle de versões e colaboração.
-   **Trello:** Para gestão de projetos colaborativa e visual, baseada no sistema Kanban.
-   **Figma:** Ferramenta de design, focada em design de interface do usuário (UI) e experiência do usuário (UX).
-   **Postman:** Plataforma colaborativa para testar e documentar APIs.
-   **IntelliJ:** IDE para o desenvolvimento em Java.
-   **Discord:** Plataforma de comunicação entre os colaboradores do projeto.
-   **Unity:** Motor gráfico para desenvolvimento de jogos 3D e 2D para multiplataforma como web.
  
## 🚀 Como Executar o Projeto

Para executar o projeto no seu ambiente local, siga estes passos:

1.  **Clonar o Repositório:**
    ```bash
    git clone [https://github.com/seu-utilizador/Projeto-POO.git](https://github.com/seu-utilizador/Projeto-POO.git)
    cd Projeto-POO/jam
    ```

2.  **Configurar a Base de Dados:**
    -   Certifique-se de que tem uma instância do MySQL ou MariaDB na porta `3306`.
    -   Crie uma base de dados (ex: `projetopoo`).
    -   Crie o arquivo `/application-local.properties` com as suas credenciais de acesso à base de dados conforme o arquivo `/application-local.properties.example`.

3.  **Configurar o RabbitMQ:**
    -   Certifique-se que a porta `15672` está liberada para uso.
    -   Altere o arquivo `/application-local.properties` para configurar as credenciais do RabbitMQ.
    -   Crie o arquivo `/.env` com as suas credenciais de acesso do RabbitMQ conforme o arquivo `/.env.example`.
    
5.  **Executar o RabbitMQ (via Docker):**
    -   Certifique-se de que tem o Docker e o Docker Compose instalados.
    -   A partir da raiz do diretório `jam`, execute:
        ```bash
        docker-compose build --no-cache
        docker-compose up -d
        ```
    -   Isto construirá uma imagem para o container RabbitMQ e a utiliza para criar um container RabbitMQ.

6.  **Executar a Aplicação:**
    -   Pode executar a aplicação através da sua IDE (ex: IntelliJ, Eclipse) localizando e correndo a classe `JamApplication.java`.
    -   Alternativamente, pode usar o Maven na linha de comandos:
        ```bash
        mvn spring-boot:run
        ```

5.  **Acessar a Aplicação:**
    -   Certifique-se que a porta `8080` está liberada para uso.
    -   Abra o seu navegador e navegue para `http://localhost:8080`.
    -   A documentação da API estará disponível em `http://localhost:8080/swagger-ui.html`.

## 📂 Estrutura do Projeto

O projeto segue a estrutura padrão de uma aplicação Spring Boot:
```
/
|── .gitignore
|── pom.xml
|── docker-compose.yml
|── README.md
└── src
    |── main
    |   |── java/com/projetopoo/jam/
    |   |   |── config/              # Pastas de configuração do Spring (Security, Beans, etc.)
    |   |   |── controller/          # Controladores da API REST que gerem os endpoints HTTP
    |   |   |── dto/                 # Data Transfer Objects (DTOs) para a comunicação
    |   |   |── exception/           # Gestores de exceções personalizadas e globais
    |   |   |── model/               # Entidades de domínio (JPA) que mapeiam para a base de dados
    |   |   |── repository/          # Interfaces do Spring Data JPA para o acesso a dados
    |   |   |── service/             # Lógica de negócio da aplicação
    |   |   |── util/                # Classes utilitárias (ex: manipulação de arquivos)
    |   |   └── JamApplication.java  # Ponto de entrada da aplicação Spring Boot
    |   |
    |   └── resources/
    |       |── static/              # Recursos estáticos (CSS, JavaScript, Imagens)
    |       |   |── css/
    |       |   |── js/
    |       |   |── img/
    |       |   └── upload/
    |       |── templates/           # Templates HTML do Thymeleaf
    |       |   |── auth/
    |       |   |── fragments/
    |       |   └── pages/
    |       └── application.properties # Arquivo de configuração principal da aplicação
    |
    └── test/
        └── java/com/projetopoo/jam/  # Testes unitários e de integração
```

