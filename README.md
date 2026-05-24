# ApprovalHub

Service qui reçoit des documents à valider (contrats, rapports, formulaires). Chaque document suit un circuit de validation : il est soumis par un utilisateur, relu par un reviewer, puis approuvé ou rejeté. Chaque changement de statut est tracé dans un historique consultable


## Fonctionnalités

- Un utilisateur pourra se créer un compte et pourra se définir un rôle, "submitter" ou "reviewer".
- Un utilisateur connecté et ayant le role "submitter" pourra créer des documents et les soumettres.
- Un utilisateur connecté et ayant le rôle de "reviewer" pourra accéder aux document soumis et les approuver ou les rejeter.


## Stack technique

|Couche|Technologie|
|---|---|
|Framework|Spring Boot 4.0.6|
|Persistance relationnelle|PostgreSQL + Spring Data JPA + Hibernate|
|Messaging|Apache Kafka 4.x (KRaft mode)|
|Recherche full-text|Elasticsearch 9.x + Spring Data Elasticsearch|
|Sécurité|Spring Security 7 + JWT (jjwt 0.11.5)|
|Build|Maven|
|Java|Java 21|


## Modèle de données

| Titre              | Description                                                           |
| ------------------ | --------------------------------------------------------------------- |
| User               | Créer un compte avec un nom d'utilisateur, un mot de passe et un rôle |
| Document           | Document avec un titre et une description                             |
| StatusHistory      | Historique de l'ancien et du nouvel état d'un document               |


## Démarrer l'application

### Cloner le projet

```bash
git clone https://github.com/aymlpz92/approvalhub
```

### Prérequis

- Java 21
- Maven
- PostgreSQL
- Kafka
- ElasticSearch

### Configuration

#### PostgreSQL

```properties
spring.application.name=approvalhub
spring.datasource.url=jdbc:postgresql://localhost:5432/${spring.application.name}
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=create
```

#### Kafka

```properties
spring.kafka.bootstrap-servers=localhost:9092
app.kafka.topics=document-status-events
spring.kafka.consumer.group-id=approvalhub
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
spring.kafka.consumer.properties.spring.deserializer.value.delegate.class=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*
spring.kafka.consumer.properties.spring.json.value.default.type=com.approvalhub.dto.status.DocumentStatusEvent
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

#### ElasticSearch

```properties
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.username=elastic
spring.elasticsearch.password=elastic
spring.elasticsearch.ssl.verification-mode=none
org.springframework.data.elasticsearch=DEBUG
```

### Lancement

```bash
mvn spring-boot:run
```


## Liste des endpoints

### Auth

- `POST /api/auth/register` : Enregistrement d'un utilisateur
- `POST /api/auth/login` : Connection d'un utilisateur
- `POST /api/auth/refresh` : Regénérer un token

### Documents

- `POST /api/documents` : Création d'un document
- `GET /api/documents` : Afficher tous les documents créer
- `GET /api/documents/{id}` : Recherche un document grâce à son id
- `PATCH /api/documents/{id}/submit` : Soumission d'un document
- `PATCH /api/documents/{id}/approve` : Validation d'un document
- `PATCH /api/documents/{id}/reject` : Rejet d'un document

### Recherche Elasticsearch

- `GET /api/search/history?q=` : Recherche full-text de l'historique des status d'un document
- `GET /api/search/history?docId=` : Recherche de l'historique des status d'un documents grâce à son id

## Structure du projet 

```
 com.approvalhub/
│
├── config/
│   └── SecurityConfig.java           # Configuration Spring Security + JWT + AuthenticationProvider
│
├── controller/
│   ├── AuthenticationController.java  # POST /api/auth/register, /login, /refresh
│   ├── DocumentController.java        # CRUD documents + endpoints de workflow
│   ├── ElasticSearchController.java   # GET /api/search/history
│   └── UserController.java            # Gestion des utilisateurs
│
├── domain/
│   ├── entity/
│   │   ├── Document.java              # Entité JPA — table document
│   │   ├── ErrorEntity.java           # Objet retourné par le GlobalExceptionHandler (status, message, timestamp)
│   │   ├── StatusHistory.java         # Entité JPA — table status_history
│   │   ├── StatusHistoryIndex.java    # Document Elasticsearch — index status_history
│   │   └── User.java                  # Entité JPA — table users
│   └── enums/
│       ├── Role.java                  # SUBMITTER, REVIEWER
│       └── Status.java                # DRAFT, PENDING, APPROVED, REJECTED
│
├── dto/
│   ├── document/
│   │   ├── DocumentCreateDTO.java     # Requête création document (title, description)
│   │   ├── DocumentResponseDTO.java   # Réponse document (id, title, status, owner...)
│   │   └── DocumentStatusUpdateDTO.java # Réponse changement de statut
│   ├── status/
│   │   ├── DocumentStatusEvent.java   # Event Kafka (documentId, userId, oldStatus, newStatus, comment)
│   │   └── StatusHistoryResponseDTO.java # Réponse historique (title, username, statuts, date)
│   └── user/
│       ├── AuthenticationRequest.java # Requête login (username, password)
│       ├── AuthenticationResponse.java # Réponse auth (token, expiresIn)
│       ├── RegisterRequest.java       # Requête register (username, password, confirmPassword, role)
│       └── UserResponseDTO.java       # Réponse utilisateur
│
├── exception/
│   ├── GlobalExceptionHandler.java    # @ControllerAdvice — intercepte les exceptions et retourne un ErrorEntity
│   ├── ResourceNotFoundException.java # Lancée quand une ressource est introuvable (document, user...)
│   └── UsernameAlreadyExistsException.java # Lancée lors d'un register avec un username déjà utilisé
│
├── filter/
│   └── JwtAuthFilter.java             # Filtre JWT — lecture header, validation token, peuplement SecurityContext
│
├── kafka/
│   ├── KafkaEventConsumer.java        # @KafkaListener — reçoit les events et appelle StatusHistoryService
│   └── KafkaEventProducer.java        # KafkaTemplate — publie les events sur le topic
│
├── mapper/
│   ├── DocumentMapper.java            # Document ↔ DTO
│   ├── StatusHistoryMapper.java       # StatusHistory ↔ DTO / StatusHistoryIndex
│   └── UserMapper.java                # User ↔ DTO
│
├── repository/
│   ├── DocumentRepository.java        # JpaRepository<Document, Long>
│   ├── StatusHistoryIndexRepository.java # ElasticsearchRepository<StatusHistoryIndex, String>
│   ├── StatusHistoryRepository.java   # JpaRepository<StatusHistory, Long>
│   └── UserRepository.java            # JpaRepository<User, Long>
│
├── service/
│   ├── AuthenticationService.java     # register(), authenticate(), refreshToken()
│   ├── CustomUserDetailsService.java  # UserDetailsService pour Spring Security
│   ├── DocumentService.java           # createDocument(), submit/approve/rejectDocument()
│   ├── ElasticSearchService.java      # searchHistory(), findById()
│   ├── JwtService.java                # generateToken(), extractUsername(), isTokenValid()
│   ├── StatusHistoryService.java      # updateDocumentStatus() — persiste PostgreSQL + indexe ES
│   └── UserService.java               # Gestion des utilisateurs
│
└── ApprovalhubApplication.java        # Point d'entrée Spring Boot
```
