package it.unimib.sd2025.resource;

import it.unimib.sd2025.model.Buono;
import it.unimib.sd2025.model.StatoBuono;
import it.unimib.sd2025.model.Utente;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import it.unimib.sd2025.DatabaseClient.TCPClient;

import jakarta.json.bind.JsonbBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Path("/utenti/{cf}/buoni")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BuonoResource {

    private static final Map<String, Object> lockUtente = new ConcurrentHashMap<>();

    private String toJson(Object obj) {
        return JsonbBuilder.create().toJson(obj);
    }

    private <T> T fromJson(String json, Class<T> cls) {
        return JsonbBuilder.create().fromJson(json, cls);
    }

    // 📌 1. Elenca tutti i buoni di un utente
    @GET
    public Response listaBuoni(@PathParam("cf") String cf) {
        Utente utente = getUtente(cf);
        if (utente == null) return Response.status(404).build();
        return Response.ok(utente.buoni).build();
    }

    // 📌 2. Crea un nuovo buono
    @POST
    public Response creaBuono(@PathParam("cf") String cf, Buono nuovo) {
        synchronized (lockUtente.computeIfAbsent(cf, k -> new Object())) {
            Utente utente = getUtente(cf);
            if (utente == null) return Response.status(404).build();

            double totaleUsato = utente.buoni.stream().mapToDouble(b -> b.importo).sum();
            double disponibile = 500 - totaleUsato;

            if (nuovo.importo <= 0 || nuovo.importo > disponibile) {
                return Response.status(400).entity("{\"errore\": \"Importo non valido\"}").build();
            }

            nuovo.id = UUID.randomUUID().toString();
            nuovo.dataCreazione = LocalDateTime.now();
            nuovo.stato = StatoBuono.NON_CONSUMATO;

            utente.buoni.add(nuovo);
            salvaUtente(utente);

            return Response.status(201).entity(nuovo).build();
        }
    }

    // 📌 3. Visualizza un buono specifico
    @GET
    @Path("/{id}")
    public Response getBuono(@PathParam("cf") String cf, @PathParam("id") String id) {
        Utente utente = getUtente(cf);
        if (utente == null) return Response.status(404).build();

        return utente.buoni.stream()
                .filter(b -> b.id.equals(id))
                .findFirst()
                .map(buono -> Response.ok(buono).build())
                .orElse(Response.status(404).build());
    }

    // 📌 4. Modifica la tipologia di un buono (se non consumato)
    @PUT
    @Path("/{id}")
    public Response modificaBuono(@PathParam("cf") String cf, @PathParam("id") String id, Buono modificato) {
        Utente utente = getUtente(cf);
        if (utente == null) return Response.status(404).build();

        for (Buono b : utente.buoni) {
            if (b.id.equals(id)) {
                if (b.stato == StatoBuono.CONSUMATO)
                    return Response.status(400).entity("{\"errore\": \"Buono già consumato\"}").build();

                b.tipologia = modificato.tipologia;
                salvaUtente(utente);
                return Response.ok(b).build();
            }
        }
        return Response.status(404).build();
    }

    // 📌 5. Elimina un buono (solo se non consumato)
    @DELETE
    @Path("/{id}")
    public Response eliminaBuono(@PathParam("cf") String cf, @PathParam("id") String id) {
        Utente utente = getUtente(cf);
        if (utente == null) return Response.status(404).build();

        Iterator<Buono> iterator = utente.buoni.iterator();
        while (iterator.hasNext()) {
            Buono b = iterator.next();
            if (b.id.equals(id)) {
                if (b.stato == StatoBuono.CONSUMATO)
                    return Response.status(400).entity("{\"errore\": \"Buono già consumato\"}").build();

                iterator.remove();
                salvaUtente(utente);
                return Response.noContent().build();
            }
        }

        return Response.status(404).build();
    }

    // 📌 6. Consuma un buono
    @POST
    @Path("/{id}/consuma")
    public Response consumaBuono(@PathParam("cf") String cf, @PathParam("id") String id) {
        Utente utente = getUtente(cf);
        if (utente == null) return Response.status(404).build();

        for (Buono b : utente.buoni) {
            if (b.id.equals(id)) {
                if (b.stato == StatoBuono.CONSUMATO)
                    return Response.status(400).entity("{\"errore\": \"Già consumato\"}").build();

                b.stato = StatoBuono.CONSUMATO;
                b.dataConsumo = LocalDateTime.now();
                salvaUtente(utente);
                return Response.ok(b).build();
            }
        }

        return Response.status(404).build();
    }

    // --- METODI DI SUPPORTO ---

    private Utente getUtente(String cf) {
        String risposta = TCPClient.invia("GET utente:" + cf);
        if (risposta == null || risposta.equals("NULLO")) return null;
        return fromJson(risposta, Utente.class);
    }

    private void salvaUtente(Utente utente) {
        String key = "utente:" + utente.codiceFiscale;
        String json = toJson(utente);
        TCPClient.invia("SET " + key + " " + json);
    }
}
