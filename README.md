# OrdinaMii

OrdinaMii è un backend REST sviluppato con Spring Boot per la gestione digitale di un ristorante. Il sistema permette di gestire menu, tavoli, ordini, prenotazioni, richieste di assistenza e area personale dell’utente autenticato.

Il progetto integra autenticazione e autorizzazione tramite Keycloak/JWT, persistenza su PostgreSQL, DTO, mapper, validazione degli input, paginazione, caching con Caffeine, gestione centralizzata delle eccezioni e regole business orientate al mantenimento dello storico.

---

## Tecnologie utilizzate

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- Keycloak
- PostgreSQL
- Docker Compose
- Maven
- Lombok
- Bean Validation
- Caffeine Cache

---

## Funzionalità principali

Il backend gestisce:

- piatti del menu;
- tavoli del ristorante;
- ordini d’asporto e ordini al tavolo;
- prenotazioni;
- richieste di assistenza al tavolo;
- utenti autenticati tramite Keycloak;
- area personale dell’utente autenticato;
- autorizzazioni differenziate in base al ruolo;
- cancellazione logica per preservare lo storico.

Gli ordini possono essere di due tipi:

```text
TAKE_AWAY
ON_THE_TABLE
```

Un ordine d’asporto è associato all’utente. Un ordine al tavolo è associato sia all’utente sia al tavolo.

---

## Ruoli applicativi

I ruoli previsti sono:

```text
CLIENTE
ADMIN
CAMERIERE
CUOCO
```

| Ruolo | Permessi principali |
|---|---|
| CLIENTE | Consultare menu e tavoli, creare ordini personali, creare prenotazioni personali, creare richieste di assistenza, consultare la propria area personale |
| CAMERIERE | Gestire ordini, prenotazioni e richieste di assistenza |
| CUOCO | Consultare ordini e aggiornarne lo stato operativo |
| ADMIN | Gestire menu, tavoli, utenti, ordini e prenotazioni |

---

## Struttura del progetto

```text
src/main/java/com/example/ordinaMii
├── Config
├── Controller
├── DTO
│   ├── Request
│   └── Response
├── Entity
│   └── Enum
├── Exceptions
├── Mapper
├── Repository
├── Security
└── Services
```

La cartella `Files` contiene documentazione di supporto:

```text
Files/
├── OrdinaMiiSQL.sql
└── RestAPI.txt
```

---

## Configurazione del database

Il progetto usa PostgreSQL. Il file `docker-compose.yml` avvia il database sulla porta locale `5433`.

Creare nella root del progetto un file `.env` con le variabili usate da Docker Compose:

```env
POSTGRES_DB=OrdinaMii
DB_USERNAME=postgres
DB_PASSWORD=your_password_here
```

Il file `.env` contiene dati locali e non deve essere caricato su GitHub. Per documentare le variabili richieste, è consigliato mantenere anche un file `.env.example` con valori di esempio:

```env
POSTGRES_DB=OrdinaMii
DB_USERNAME=postgres
DB_PASSWORD=your_password_here
```

---

## Configurazione Spring Boot

Nel progetto attuale Spring Boot legge le credenziali del database da un file locale:

```text
src/main/resources/passwords.yaml
```

Esempio:

```yaml
db:
  username: postgres
  password: your_password_here
```

Il file `passwords.yaml` deve rimanere escluso da Git tramite `.gitignore`, perché contiene credenziali locali.

Nel file `application.yaml` la connessione al database è configurata così:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/OrdinaMii
    username: ${db.username}
    password: ${db.password}
```

---

## Avvio del database

Dalla root del progetto:

```bash
docker compose up -d
```

Per fermare i container:

```bash
docker compose down
```

Per eliminare anche i volumi:

```bash
docker compose down -v
```

---

## Configurazione Keycloak

Il backend è configurato come OAuth2 Resource Server e valida i JWT emessi da Keycloak.

Nel file `application.yaml` è presente:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8081/realms/OrdinaMii
```

Keycloak deve quindi essere disponibile su:

```text
http://localhost:8081
```

Configurazione richiesta:

```text
Realm: OrdinaMii
Client: OrdinaMii-Backend
Roles: CLIENTE, ADMIN, CAMERIERE, CUOCO
```

Il backend legge i ruoli dal token JWT e li converte in ruoli Spring Security.

---

## Avvio dell’applicazione

Dopo aver avviato PostgreSQL e Keycloak, eseguire:

```bash
./mvnw spring-boot:run
```

Su Windows:

```bash
mvnw.cmd spring-boot:run
```

L’applicazione è disponibile su:

```text
http://localhost:8080
```

---

## Sicurezza

L’applicazione è stateless e usa JWT Bearer Token.

Ogni richiesta protetta deve includere l’header:

```text
Authorization: Bearer <token>
```

Gli endpoint pubblici principali sono:

```text
GET /dishes
GET /dishes/{id}
GET /tables
GET /tables/{id}
```

Gli altri endpoint richiedono autenticazione e ruolo adeguato.

---

## Endpoint principali

### Piatti

```text
GET /dishes
GET /dishes/{id}
POST /dishes
PUT /dishes/{id}
DELETE /dishes/{id}
```

`DELETE /dishes/{id}` non elimina fisicamente il piatto, ma lo disattiva impostando:

```text
available = false
```

Questo permette di mantenere coerente lo storico degli ordini.

---

### Tavoli

```text
GET /tables
GET /tables/{id}
POST /tables
PUT /tables/{id}
DELETE /tables/{id}
```

`DELETE /tables/{id}` non elimina fisicamente il tavolo, ma lo disattiva impostando:

```text
active = false
```

Un tavolo disattivato non può essere usato per nuovi ordini, nuove prenotazioni o richieste di assistenza.

Inoltre, un tavolo non può essere disattivato se esistono prenotazioni future attive associate a quel tavolo.

---

### Ordini

```text
GET /orders
GET /orders/{id}
POST /orders
PUT /orders/{id}
PUT /orders/{id}/status
PUT /orders/{id}/payment-status
DELETE /orders/{id}
```

Regole principali:

- un ordine `TAKE_AWAY` non deve avere `tableId`;
- un ordine `ON_THE_TABLE` deve avere `tableId`;
- un ordine non può essere associato a un tavolo disattivato;
- un ordine servito non può più essere modificato;
- un ordine cancellato non può più essere modificato;
- un pagamento già effettuato non può essere modificato;
- un ordine pagato non può essere cancellato.

`DELETE /orders/{id}` esegue una cancellazione logica:

```text
status = CANCELLED
paymentStatus = CANCELLED
```

L’ordine e le sue righe restano nel database.

---

### Prenotazioni

```text
GET /reservations
GET /reservations/{id}
GET /reservations/table/{tableId}
POST /reservations
PUT /reservations/{id}
PUT /reservations/{id}/status
DELETE /reservations/{id}
```

Regole principali:

- una prenotazione non può superare i posti disponibili del tavolo;
- una prenotazione non può essere creata su un tavolo disattivato;
- una prenotazione cancellata non blocca più lo slot del tavolo;
- una prenotazione completata o cancellata non può più essere modificata;
- una prenotazione futura non può essere marcata come `COMPLETED`.

`DELETE /reservations/{id}` esegue una cancellazione logica:

```text
status = CANCELLED
```

La prenotazione resta nello storico e non blocca più il tavolo.

---

### Richieste di assistenza

```text
GET /assistance-requests
GET /assistance-requests/{id}
POST /assistance-requests
PUT /assistance-requests/{id}/status
```

Una richiesta di assistenza può essere creata solo su un tavolo attivo.

Stati previsti:

```text
PENDING
RESOLVED
CANCELLED
```

Una richiesta risolta o cancellata non può più cambiare stato.

---

### Area personale

```text
GET /me
PATCH /me
GET /me/orders
POST /me/orders
GET /me/reservations
POST /me/reservations
```

Gli endpoint `/me` usano l’utente autenticato presente nel JWT.

Il cliente non passa mai `userId` nel body quando crea ordini o prenotazioni personali. Questo evita che un cliente possa creare risorse a nome di un altro utente.

---

### Utenti

```text
GET /users
GET /users/{id}
GET /users/{id}/orders
GET /users/{id}/reservations
```

Gli endpoint sugli utenti sono riservati ai ruoli autorizzati.

---

## Stati degli ordini

Lo stato operativo dell’ordine è rappresentato da `OrderStatus`:

```text
PENDING
PREPARING
SERVED
CANCELLED
```

Il pagamento è rappresentato separatamente da `PaymentStatus`:

```text
NOT_PAID
PENDING
PAY_AT_COUNTER
PAID
CANCELLED
```

Questa separazione evita stati incoerenti tra preparazione dell’ordine e pagamento.

Ciclo operativo dell’ordine:

```text
PENDING   → PREPARING oppure CANCELLED
PREPARING → SERVED oppure CANCELLED
SERVED    → stato finale
CANCELLED → stato finale
```

Ciclo del pagamento:

```text
NOT_PAID       → PENDING, PAY_AT_COUNTER oppure PAID
PENDING        → PAID
PAY_AT_COUNTER → PAID
PAID           → stato finale
CANCELLED      → stato finale
```

---

## Stati delle prenotazioni

Le prenotazioni usano:

```text
CONFIRMED
CANCELLED
COMPLETED
```

Il ciclo logico è:

```text
CONFIRMED → CANCELLED
CONFIRMED → COMPLETED
```

Gli stati `CANCELLED` e `COMPLETED` sono finali.

---

## Cache

Il progetto usa Spring Cache con Caffeine.

La configurazione si trova in:

```text
src/main/java/com/example/ordinaMii/Config/CacheConfig.java
```

Sono previste cache per:

```text
dishById
dishSearch
tableById
tableSearch
userById
userSearch
orderById
orderSearch
myOrderSearch
userOrderSearch
reservationById
reservationSearch
reservationTableSearch
myReservationSearch
assistanceRequestById
assistanceRequestSearch
```

Le cache per singolo id hanno dimensione maggiore perché salvano singole risorse. Le cache di ricerca hanno dimensione più contenuta perché ogni combinazione di filtri, pagina e ordinamento genera una chiave diversa.

Gli aggiornamenti invalidano le cache interessate tramite:

```text
@Cacheable
@CacheEvict
@CachePut
@Caching
```

---

## Gestione errori

Il progetto usa eccezioni custom:

```text
ResourceNotFoundException
BadRequestException
ConflictException
```

La gestione è centralizzata in `GlobalExceptionHandler`.

Sono gestiti errori comuni come:

- risorsa non trovata;
- richiesta non valida;
- conflitto con regole business;
- validazione DTO fallita;
- body JSON non leggibile;
- parametri di tipo errato;
- violazione di vincoli database;
- errori generici imprevisti.

Le risposte di errore usano una struttura uniforme tramite `ErrorResponse`.

---

## Validazione

I DTO di request usano Bean Validation.

Esempi:

```text
@NotNull
@NotBlank
@NotEmpty
@Min
@DecimalMin
@FutureOrPresent
@Valid
```

La validazione blocca input non validi prima dell’esecuzione della logica dei service.

---

## Documentazione aggiuntiva

La cartella `Files` contiene:

```text
OrdinaMiiSQL.sql
RestAPI.txt
```

`OrdinaMiiSQL.sql` documenta lo schema logico del database.  
`RestAPI.txt` documenta gli endpoint REST disponibili, i ruoli e i principali parametri.

---

## Note sullo storico

Il progetto evita la cancellazione fisica dei dati rilevanti per lo storico.

Sono gestiti tramite cancellazione logica:

```text
piatti        → available = false
tavoli        → active = false
ordini        → status = CANCELLED
prenotazioni  → status = CANCELLED
```

Questa scelta consente di mantenere tracciabilità su ordini, prenotazioni, tavoli e menu.

---

## File da non caricare su GitHub

Non devono essere caricati:

```text
.env
src/main/resources/passwords.yaml
logs/
target/
.idea/
*.iml
```

Il file `.env.example`, invece, può essere caricato perché contiene solo valori di esempio.

---

## Avvio rapido

1. Creare `.env` partendo da `.env.example`.

2. Avviare PostgreSQL:

```bash
docker compose up -d
```

3. Avviare Keycloak su `http://localhost:8081` con realm `OrdinaMii`.

4. Creare `src/main/resources/passwords.yaml` con le credenziali locali del database.

5. Avviare Spring Boot:

```bash
./mvnw spring-boot:run
```

6. Usare un token JWT di Keycloak per chiamare gli endpoint protetti.

---

## Stato del progetto

Il progetto implementa un backend REST completo per la gestione di un ristorante, con particolare attenzione a:

- separazione dei layer;
- sicurezza tramite JWT;
- gestione dei ruoli;
- DTO e mapper;
- paginazione;
- cache;
- validazione input;
- gestione centralizzata degli errori;
- regole business coerenti;
- mantenimento dello storico tramite cancellazione logica.
