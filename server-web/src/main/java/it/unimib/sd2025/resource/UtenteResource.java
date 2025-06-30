package it.unimib.sd2025.resource;

import it.unimib.sd2025.DatabaseClient.TCPClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.sd2025.model.Utente;
import it.unimib.sd2025.model.Buono;
import it.unimib.sd2025.model.StatoBuono;

import jakarta.json.bind.JsonbBuilder;

import java.util.HashMap;
import java.util.Map;

@Path("/utenti")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UtenteResource {

    private String toJson(Object obj) {
        return JsonbBuilder.create().toJson(obj);
    }

    private <T> T fromJson(String json, Class<T> cls) {
        return JsonbBuilder.create().fromJson(json, cls);
    }

    // 📌 1. REGISTRAZIONE NUOVO UTENTE
    @POST
    public Response registraUtente(Utente utente) {
        if (utente == null || utente.codiceFiscale == null || utente.codiceFiscale.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"errore\": \"Codice fiscale obbligatorio\"}").build();
        }

        String chiave = "utente:" + utente.codiceFiscale;
        String json = toJson(utente);

        String risposta = TCPClient.invia("SET " + chiave + " " + json);

        if (risposta.equals("OK")) {
            return Response.status(Response.Status.CREATED).build();
        } else {
            return Response.serverError().entity("{\"errore\": \"Errore salvataggio\"}").build();
        }
    }

    // 📌 2. STATO DEL CONTRIBUTO DI UN UTENTE
    @GET
    @Path("/{cf}")
    public Response statoContributo(@PathParam("cf") String cf) {
        Utente utente = getUtente(cf);
        if (utente == null) return Response.status(Response.Status.NOT_FOUND).build();

        double disponibile = 500;
        double buoniNonConsumati = 0;
        double buoniConsumati = 0;

        for (Buono b : utente.buoni) {
            if (b.stato == StatoBuono.NON_CONSUMATO)
                buoniNonConsumati += b.importo;
            else if (b.stato == StatoBuono.CONSUMATO)
                buoniConsumati += b.importo;
        }

        disponibile -= (buoniNonConsumati + buoniConsumati);

        Map<String, Object> stato = new HashMap<>();
        stato.put("disponibile", disponibile);
        stato.put("nonConsumati", buoniNonConsumati);
        stato.put("consumati", buoniConsumati);

        return Response.ok(stato).build();
    }

    // --- METODI DI SUPPORTO ---
    private Utente getUtente(String cf) {
        String risposta = TCPClient.invia("GET utente:" + cf);
        if (risposta == null || risposta.equals("NULLO")) return null;
        return fromJson(risposta, Utente.class);
    }
}
