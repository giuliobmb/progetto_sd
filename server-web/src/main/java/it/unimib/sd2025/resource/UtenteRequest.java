package it.unimib.sd2025.resource;

import it.unimib.sd2025.DatabaseClient.DatabaseClient;
import it.unimib.sd2025.model.Buono;
import it.unimib.sd2025.model.Utente;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe REST per la gestione degli utenti della Carta Cultura Giovani.
 * Gestisce registrazione, consultazione stato e statistiche globali.
 */
@Path("/utenti")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UtenteRequest {

    private DatabaseClient dbClient;
    private Jsonb jsonb;
    private static final double CONTRIBUTO_INIZIALE = 500.0;

    public UtenteRequest() {
        try {
            this.dbClient = new DatabaseClient();
            this.jsonb = JsonbBuilder.create();
        } catch (IOException e) {
            System.err.println("Errore nell'inizializzazione del client database: " + e.getMessage());
        }
    }

    /**
     * Registra un nuovo utente nel sistema.
     *
     * @param utente Oggetto utente da registrare
     * @return Response con l'utente registrato o errore
     */
    @POST
    @Path("/registrazione")
    public Response registraUtente(Utente utente) {
        try {
            // Verifica se l'utente esiste già
            String contributoEsistente = dbClient.getValue("utenti", utente.getCodiceFiscale());
            if (contributoEsistente != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"Utente già registrato\"}")
                        .build();
            }

            // Registra l'utente con il contributo iniziale
            dbClient.addPair("utenti", utente.getCodiceFiscale(), String.valueOf(CONTRIBUTO_INIZIALE));

            // Salva i dettagli dell'utente in uno schema separato
            String utenteJson = jsonb.toJson(utente);
            dbClient.addSchema("dettagli_utenti");
            dbClient.addPair("dettagli_utenti", utente.getCodiceFiscale(), utenteJson);

            return Response.status(Response.Status.CREATED)
                    .entity(utente)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Restituisce i dettagli di un utente.
     *
     * @param codiceFiscale Codice fiscale dell'utente
     * @return Response con i dettagli dell'utente
     */
    @GET
    @Path("/{codiceFiscale}")
    public Response getUtente(@PathParam("codiceFiscale") String codiceFiscale) {
        try {
            String utenteJson = dbClient.getValue("dettagli_utenti", codiceFiscale);
            if (utenteJson == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Utente non trovato\"}")
                        .build();
            }

            Utente utente = jsonb.fromJson(utenteJson, Utente.class);
            return Response.ok(utente).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Restituisce lo stato del contributo di un utente.
     * Include contributo disponibile, assegnato e speso.
     *
     * @param codiceFiscale Codice fiscale dell'utente
     * @return Response con lo stato del contributo
     */
    @GET
    @Path("/{codiceFiscale}/stato")
    public Response getStatoContributo(@PathParam("codiceFiscale") String codiceFiscale) {
        try {
            // Verifica se l'utente esiste
            String contributoStr = dbClient.getValue("utenti", codiceFiscale);
            if (contributoStr == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Utente non trovato\"}")
                        .build();
            }

            double contributoDisponibile = Double.parseDouble(contributoStr);

            // Calcola contributo assegnato ai buoni e speso
            HashMap<String, String> tuttiBuoni = dbClient.getAll("buoni");
            double contributoAssegnato = 0.0;
            double contributoSpeso = 0.0;

            if (tuttiBuoni != null) {
                for (String buonoJson : tuttiBuoni.values()) {
                    Buono buono = jsonb.fromJson(buonoJson, Buono.class);
                    if (codiceFiscale.equals(buono.getCodiceFiscale())) {
                        if (buono.isConsumato()) {
                            contributoSpeso += buono.getImporto();
                        } else {
                            contributoAssegnato += buono.getImporto();
                        }
                    }
                }
            }

            Map<String, Double> statoContributo = Map.of(
                    "contributoDisponibile", contributoDisponibile,
                    "contributoAssegnato", contributoAssegnato,
                    "contributoSpeso", contributoSpeso,
                    "contributoTotale", CONTRIBUTO_INIZIALE
            );

            return Response.ok(statoContributo).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Restituisce le statistiche globali del sistema.
     * Include numero utenti registrati e stato complessivo dei contributi.
     *
     * @return Response con le statistiche globali
     */
    @GET
    @Path("/statistiche/globali")
    public Response getStatisticheGlobali() {
        try {
            // Conta gli utenti registrati
            HashMap<String, String> tuttiUtenti = dbClient.getAll("utenti");
            int numUtentiRegistrati = (tuttiUtenti != null) ? tuttiUtenti.size() : 0;

            // Calcola le statistiche sui contributi
            double contributoTotaleDisponibile = 0.0;
            double contributoTotaleAssegnato = 0.0;
            double contributoTotaleSpeso = 0.0;

            if (tuttiUtenti != null) {
                for (Map.Entry<String, String> entry : tuttiUtenti.entrySet()) {
                    String codiceFiscale = entry.getKey();
                    double contributoDisponibile = Double.parseDouble(entry.getValue());
                    contributoTotaleDisponibile += contributoDisponibile;

                    // Calcola contributo assegnato e speso per questo utente
                    HashMap<String, String> tuttiBuoni = dbClient.getAll("buoni");
                    if (tuttiBuoni != null) {
                        for (String buonoJson : tuttiBuoni.values()) {
                            Buono buono = jsonb.fromJson(buonoJson, Buono.class);
                            if (codiceFiscale.equals(buono.getCodiceFiscale())) {
                                if (buono.isConsumato()) {
                                    contributoTotaleSpeso += buono.getImporto();
                                } else {
                                    contributoTotaleAssegnato += buono.getImporto();
                                }
                            }
                        }
                    }
                }
            }

            // Conta i buoni
            HashMap<String, String> tuttiBuoni = dbClient.getAll("buoni");
            int buoniGenerati = 0;
            int buoniConsumati = 0;
            int buoniNonConsumati = 0;

            if (tuttiBuoni != null) {
                buoniGenerati = tuttiBuoni.size();
                for (String buonoJson : tuttiBuoni.values()) {
                    Buono buono = jsonb.fromJson(buonoJson, Buono.class);
                    if (buono.isConsumato()) {
                        buoniConsumati++;
                    } else {
                        buoniNonConsumati++;
                    }
                }
            }

            Map<String, Object> statisticheGlobali = Map.of(
                    "numUtentiRegistrati", numUtentiRegistrati,
                    "contributoTotaleDisponibile", contributoTotaleDisponibile,
                    "contributoTotaleAssegnato", contributoTotaleAssegnato,
                    "contributoTotaleSpeso", contributoTotaleSpeso,
                    "buoniGenerati", buoniGenerati,
                    "buoniConsumati", buoniConsumati,
                    "buoniNonConsumati", buoniNonConsumati
            );

            return Response.ok(statisticheGlobali).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Restituisce la lista di tutti gli utenti registrati.
     *
     * @return Response con la lista degli utenti
     */
    @GET
    public Response getTuttiUtenti() {
        try {
            HashMap<String, String> tuttiUtenti = dbClient.getAll("dettagli_utenti");

            if (tuttiUtenti == null || tuttiUtenti.isEmpty()) {
                return Response.ok("[]").build();
            }

            java.util.List<Utente> listaUtenti = new java.util.ArrayList<>();
            for (String utenteJson : tuttiUtenti.values()) {
                Utente utente = jsonb.fromJson(utenteJson, Utente.class);
                listaUtenti.add(utente);
            }

            return Response.ok(listaUtenti).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Verifica se un utente esiste nel sistema.
     *
     * @param codiceFiscale Codice fiscale dell'utente
     * @return Response di conferma esistenza
     */
    @GET
    @Path("/{codiceFiscale}/exists")
    public Response verificaEsistenzaUtente(@PathParam("codiceFiscale") String codiceFiscale) {
        try {
            String contributoStr = dbClient.getValue("utenti", codiceFiscale);
            boolean esiste = (contributoStr != null);

            Map<String, Boolean> risposta = Map.of("esiste", esiste);
            return Response.ok(risposta).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }
}