# Progetto Sistemi Distribuiti 2024-2025 - API REST

API REST – Carta Cultura Giovani

Il sistema espone una serie di endpoint REST per la gestione del contributo e dei buoni associati a ciascun utente.

**Registrazione utente**
Metodo: POST
Endpoint: /api/users
Descrizione: Registra un nuovo utente e assegna un contributo iniziale di 500 euro.
Richiesta:
{
"nome": "Mario",
"cognome": "Rossi",
"email": "mario.rossi@example.com",
"codiceFiscale": "RSSMRA00A01H501U"
}
Risposta:
201 Created se l’utente è stato registrato correttamente.
409 Conflict se l’utente esiste già.


**Recupero stato contributo**
Metodo: GET
Endpoint: /api/users/{cf}/status
Descrizione: Restituisce la suddivisione del contributo per l’utente specificato dal codice fiscale.
Risposta:
{
"available": 300,
"assigned": 100,
"spent": 100
}

**Elenco buoni utente**
Metodo: GET
Endpoint: /api/users/{cf}/vouchers
Descrizione: Restituisce la lista completa e cronologica dei buoni generati dall’utente.
Risposta:
[
{
"id": 1,
"importo": 100,
"tipologia": "cinema",
"dataCreazione": "2025-06-20",
"dataConsumo": null,
"stato": "non consumato"
}
]

**Creazione di un nuovo buono**
Metodo: POST
Endpoint: /api/vouchers
Descrizione: Crea un nuovo buono associato a un utente, prelevando parte del contributo disponibile.
Richiesta:
{
"cf": "RSSMRA00A01H501U",
"importo": 100,
"tipologia": "libri"
}
Risposta:
{
"id": 3,
"importo": 100,
"tipologia": "libri",
"dataCreazione": "2025-06-25",
"dataConsumo": null,
"stato": "non consumato"
}

Errori:
400 Bad Request se l’importo supera il contributo disponibile.
409 Conflict in caso di concorrenza su un contributo.
Dettaglio di un buono specifico
Metodo: GET
Endpoint: /api/vouchers/{id}
Descrizione: Restituisce le informazioni complete su un buono identificato dal suo id.
Modifica tipologia buono
Metodo: PUT
Endpoint: /api/vouchers/{id}
Descrizione: Permette la modifica della tipologia di un buono non ancora consumato.
Richiesta:
{
"tipologia": "teatro"
}
Risposta:
200 OK se la modifica è avvenuta.
400 Bad Request se il buono è già stato consumato.
Eliminazione buono
Metodo: DELETE
Endpoint: /api/vouchers/{id}
Descrizione: Cancella un buono se non è ancora stato consumato.
Risposta:
200 OK se eliminato.
400 Bad Request se il buono è già stato consumato.
Consumo di un buono
Metodo: POST
Endpoint: /api/vouchers/{id}/consume
Descrizione: Segna il buono come consumato e aggiorna la data di consumo.
Risposta:
{
"id": 3,
"dataConsumo": "2025-06-30",
"stato": "consumato"
}

**Stato globale del sistema**
Metodo: GET
Endpoint: /api/system/status
Descrizione: Fornisce statistiche sull’intero sistema: numero utenti, contributi totali, buoni generati e consumati.
Risposta:
{
"utentiRegistrati": 5,
"contributi": {
"disponibile": 1200,
"assegnato": 600,
"speso": 700
},
"buoni": {
"generati": 15,
"consumati": 9,
"nonConsumati": 6
}
}
Note:

Le date sono espresse in formato YYYY-MM-DD.
Lo stato di un buono è determinato dalla presenza o meno della dataConsumo.
Gli utenti sono identificati univocamente dal loro codice fiscale.
Ogni buono è identificato da un campo id generato automaticamente dal server.
Tutti i dati vengono scambiati in formato JSON (Content-Type: application/json).
