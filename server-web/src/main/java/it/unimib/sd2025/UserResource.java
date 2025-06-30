package it.unimib.sd2025;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2025.model.Utente;
import jakarta.json.JsonException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


@Path("/user")
public class UserResource {

    // POST /user
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(Utente user) {
        // Logica per creare un utente
        return Response.status(Status.CREATED).entity(user).build();
    }

    // GET /user/contributo/{cf}
    @GET
    @Path("/contributo/{cf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContributo(@PathParam("cf") String codiceFiscale) {
        // Logica per ottenere contributo
        return Response.ok().build();
    }

    // GET /user/buono/{cf}
    @GET
    @Path("/buono/{cf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuoniByUser(@PathParam("cf") String codiceFiscale) {
        // Logica per ottenere buoni per utente
        return Response.ok().build();
    }
}
