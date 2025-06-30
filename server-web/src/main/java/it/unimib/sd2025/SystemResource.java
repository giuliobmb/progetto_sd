package it.unimib.sd2025;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
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


@Path("/system")
public class SystemResource {

    // GET /system/status
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        // Logica per ottenere lo stato del sistema
        SystemStatus status = new SystemStatus();
        status.setStatus("OK");
        status.setMessage("System is running smoothly.");
        status.setTimestamp(new Date().toString());

        // Usa JsonbBuilder per convertire l'oggetto in JSON
        Jsonb jsonb = JsonbBuilder.create();
        String jsonResponse = jsonb.toJson(status);

        return jsonb;
    }
}
