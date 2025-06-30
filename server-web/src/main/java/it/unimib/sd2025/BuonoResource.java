package it.unimib.sd2025;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2025.model.Buono;
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


@Path("/buono")
public class BuonoResource {

    // POST /buono
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBuono(Buono buono) {
        // Logica per creare un buono
        return Response.status(Status.CREATED).entity(buono).build();
    }

    // PUT /buono/{id}
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBuono(@PathParam("id") String id, Buono buono) {
        // Logica per aggiornare un buono
        return Response.ok().build();
    }

    // GET /buono/{id}
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuonoById(@PathParam("id") String id) {
        // Logica per ottenere un buono tramite ID
        return Response.ok().build();
    }

    // POST /buono/consume/{id}
    @POST
    @Path("/consume/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consumeBuono(@PathParam("id") String id, Buono buono) {
        // Logica per consumare un buono e aggiornarne lo stato
        return Response.ok().build();
    }
}
