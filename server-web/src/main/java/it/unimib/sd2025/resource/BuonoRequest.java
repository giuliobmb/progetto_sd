package it.unimib.sd2025.resource;

import it.unimib.sd2025.DatabaseClient.DatabaseController;
import it.unimib.sd2025.DatabaseClient.DatabaseClient;
import it.unimib.sd2025.model.Buono;
import it.unimib.sd2025.model.StatoBuono;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Classe REST per la gestione dei buoni della Carta Cultura Giovani.
 * Gestisce tutte le operazioni CRUD sui buoni e il controllo del contributo.
 */
@Path("/buoni")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BuonoRequest {

    private DatabaseClient dbClient;
    private DatabaseController dbController;
    private Jsonb jsonb;

    public BuonoRequest() {
        try {
            this.dbClient = new DatabaseClient();
            this.jsonb = JsonbBuilder.create();
        } catch (IOException e) {
            System.err.println("Errore nell'inizializzazione del client database: " + e.getMessage());
        }
    }

    /**
     * Genera un nuovo buono per un utente.
     *
     * @param codiceFiscale Codice fiscale dell'utente
     * @param buono Oggetto buono da creare
     * @return Response con il buono creato o errore
     */
    @POST
    @Path("/{codiceFiscale}")
    public Response generaBuono(@PathParam("codiceFiscale") String codiceFiscale, Buono buono) {
        try {
            // Verifica se l'utente esiste
            String contributoStr = dbClient.getValue("utenti", codiceFiscale);
            if (contributoStr == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Utente non trovato\"}")
                        .build();
            }

            double contributoDisponibile = Double.parseDouble(contributoStr);

            // Verifica se il contributo è sufficiente
            if (buono.getImporto() > contributoDisponibile) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Contributo insufficiente\"}")
                        .build();
            }

            // Genera ID univoco per il buono
            String buonoId = UUID.randomUUID().toString();
            buono.setId(buonoId);
            buono.setCodiceFiscale(codiceFiscale);
            buono.setDataCreazione(LocalDateTime.now().toString());
            buono.setStato(StatoBuono.NON_CONSUMATO);

            // AGGIORNARE CON IL NUOVO SCHEMA
            dbController.addBuono(buono);


            // Aggiorna il contributo dell'utente
            double nuovoContributo = contributoDisponibile - buono.getImporto();
            dbClient.updatePair("utenti", codiceFiscale, String.valueOf(nuovoContributo));

            return Response.status(Response.Status.CREATED)
                    .entity(buono)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Restituisce tutti i buoni di un utente.
     *
     * @param codiceFiscale Codice fiscale dell'utente
     * @return Response con la lista dei buoni
     */
    @GET
    @Path("/{codiceFiscale}")
    public Response getBuoniUtente(@PathParam("codiceFiscale") String codiceFiscale) {
        try {
            // Verifica se l'utente esiste
            String contributoStr = dbClient.getValue("utenti", codiceFiscale);
            if (contributoStr == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Utente non trovato\"}")
                        .build();
            }

            // Ottieni tutti i buoni
            List<Buono> buoniUtente = new ArrayList<>();
            buoniUtente = dbController.getAllBuoniUtente(codiceFiscale);

            if (buoniUtente != null) {
                buoniUtente.sort((b1, b2) -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // adatta il pattern al tuo formato
                    LocalDateTime data1 = LocalDateTime.parse(b1.getDataCreazione(), formatter);
                    LocalDateTime data2 = LocalDateTime.parse(b2.getDataCreazione(), formatter);
                    return data2.compareTo(data1); // ordina dal più recente al più vecchio
                });
            }

            // Ordina per data di creazione (cronologico)

            return Response.ok(buoniUtente).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"} " + e.getMessage())
                    .build();
        }
    }

    /**
     * Restituisce i dettagli di un singolo buono.
     *
     * @param buonoId ID del buono
     * @return Response con i dettagli del buono
     */
    @GET
    @Path("/dettagli/{buonoId}")
    public Response getDettagliBuono(@PathParam("buonoId") String buonoId) {
        try {
            Buono buono = dbController.getBuonoById(buonoId);
            if (buono == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Buono non trovato\"}")
                        .build();
            }
            return Response.ok(buono).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Modifica un buono non ancora consumato.
     *
     * @param buonoId ID del buono da modificare
     * @param buonoModificato Buono con le modifiche
     * @return Response con il buono modificato
     */
    @PUT
    @Path("/{buonoId}")
    public Response modificaBuono(@PathParam("buonoId") String buonoId, Buono buonoModificato) {
        try {
            Buono buonoEsistente = dbController.getBuonoById(buonoId);
            if (buonoEsistente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Buono non trovato\"}")
                        .build();
            }

            // Verifica se il buono è già stato consumato
            if (buonoEsistente.isConsumato()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Non è possibile modificare un buono già consumato\"}")
                        .build();
            }

            // Aggiorna solo i campi modificabili (tipologia di bene)
            buonoEsistente.setTipologia(buonoModificato.getTipologia());
            // Se l'importo è stato modificato, verifica il contributo disponibile
            if (buonoModificato.getImporto() > 0) {
                String contributoStr = dbClient.getValue("utenti", buonoEsistente.getCodiceFiscale());
                if (contributoStr == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"error\":\"Utente non trovato\"}")
                            .build();
                }

                double contributoDisponibile = Double.parseDouble(contributoStr);
                if (buonoModificato.getImporto() > contributoDisponibile) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\":\"Contributo insufficiente\"}")
                            .build();
                }
                buonoEsistente.setImporto(buonoModificato.getImporto());
            }


            // Salva le modifiche
            dbController.updateBuono(buonoEsistente);

            return Response.ok(buonoEsistente).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Consuma un buono.
     *
     * @param buonoId ID del buono da consumare
     * @return Response di conferma
     */
    @PUT
    @Path("/consuma/{buonoId}")
    public Response consumaBuono(@PathParam("buonoId") String buonoId) {
        try {
            Buono buono = dbController.getBuonoById(buonoId);
            if (buono == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Buono non trovato\"}")
                        .build();
            }

            // Verifica se il buono è già stato consumato
            if (buono.isConsumato()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Buono già consumato\"}")
                        .build();
            }

            // Segna il buono come consumato
            buono.setStato(StatoBuono.CONSUMATO);
            buono.setDataConsumo(LocalDateTime.now().toString());

            // Salva le modifiche
            dbController.updateBuono(buono);

            return Response.ok("{\"message\":\"Buono consumato con successo\"}").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Cancella un buono non ancora consumato.
     *
     * @param buonoId ID del buono da cancellare
     * @return Response di conferma
     */
    @DELETE
    @Path("/{buonoId}")
    public Response cancellaBuono(@PathParam("buonoId") String buonoId) {
        try {
            Buono buono = dbController.getBuonoById(buonoId);
            if (buono == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Buono non trovato\"}")
                        .build();
            }

            // Verifica se il buono è già stato consumato
            if (buono.isConsumato()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Non è possibile cancellare un buono già consumato\"}")
                        .build();
            }

            // Riaccredita il contributo all'utente
            String contributoStr = dbClient.getValue("utenti", buono.getCodiceFiscale());
            double contributoAttuale = Double.parseDouble(contributoStr);
            double nuovoContributo = contributoAttuale + buono.getImporto();
            dbClient.updatePair("utenti", buono.getCodiceFiscale(), String.valueOf(nuovoContributo));

            dbController.removeBuono(buono);

            return Response.ok("{\"message\":\"Buono cancellato con successo\"}").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }

    /**
     * Restituisce le statistiche globali dei buoni.
     *
     * @return Response con le statistiche
     */
    @GET
    @Path("/statistiche")
    public Response getStatisticheBuoni() {
        try {
            List<Buono> tuttiBuoni = dbController.getAllBuoni(); // Ottieni tutti i buoni

            int buoniGenerati = 0;
            int buoniConsumati = 0;
            int buoniNonConsumati = 0;

            if (tuttiBuoni != null) {
                buoniGenerati = tuttiBuoni.size();

                for (Buono buono : tuttiBuoni) {
                    if (buono.isConsumato()) {
                        buoniConsumati++;
                    } else {
                        buoniNonConsumati++;
                    }
                }
            }

            Map<String, Integer> statistiche = Map.of(
                    "buoniGenerati", buoniGenerati,
                    "buoniConsumati", buoniConsumati,
                    "buoniNonConsumati", buoniNonConsumati
            );

            return Response.ok(statistiche).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Errore interno del server\"}")
                    .build();
        }
    }
}