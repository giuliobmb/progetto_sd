package it.unimib.sd2025.resource;

import it.unimib.sd2025.DatabaseClient.DatabaseClient;
import it.unimib.sd2025.DatabaseClient.DatabaseController;
import it.unimib.sd2025.model.Buono;
import it.unimib.sd2025.model.Utente;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.ArrayList;
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
    private DatabaseController db;

    public UtenteRequest() {
        try {
            this.dbClient = new DatabaseClient();
            this.jsonb = JsonbBuilder.create();
            db = DatabaseController.get();
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
            Utente test = db.getUtenteByCodiceFiscale(utente.getCodiceFiscale());
            if (test != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"Utente già registrato\"}")
                        .build();
            }

            // Registra l'utente con il contributo iniziale
            db.addUtente(utente);

            return Response.status(Response.Status.CREATED)
                    .entity(utente)  // Restituisce l'utente registrato
                    .build();

        } catch (Exception e) {
            // Gestione dell'errore
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}" + e.getMessage())
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
            Utente utente = db.getUtenteByCodiceFiscale(codiceFiscale);
            System.out.println(utente.toString());
            if (utente == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Utente non trovato\"}").build();
            }

           
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
            Utente utente = db.getUtenteByCodiceFiscale(codiceFiscale);
            if (utente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Utente non trovato\"}")
                        .build();
            }

            double contributoDisponibile = utente.getImporto();

            // Calcola contributo assegnato ai buoni e speso
            ArrayList<Buono> buoni = (ArrayList<Buono>) db.getAllBuoniUtente(codiceFiscale);
            double contributoAssegnato = 0.0;
            double contributoSpeso = 0.0;

            for (Buono buono : buoni) {
                if (buono.isConsumato()) {
                    contributoSpeso += buono.getImporto();
                } else {
                    contributoAssegnato += buono.getImporto();
                }
            }            

            Map<String, Double> statoContributo = Map.of(
                    "contributoDisponibile", CONTRIBUTO_INIZIALE - contributoSpeso,
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
    @Path("/mostratutti")
    public Response getTuttiUtenti() {

        try {
            ArrayList<Utente> tuttiUtenti = (ArrayList<Utente>) db.getAllUtenti();

            if (tuttiUtenti == null || tuttiUtenti.isEmpty()) {
                return Response.ok("[]").build();
            }

            

            return Response.ok(tuttiUtenti).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}" + e.getMessage())
                    .build();
        }
    }


}