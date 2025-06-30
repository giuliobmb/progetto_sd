package it.unimib.sd2025.DatabaseClient;

import java.io.*;
import java.net.*;

/**
 * Classe di utilità per la comunicazione via socket TCP con il database.
 */
public class TCPClient {

    // Indirizzo e porta del database (aggiorna se necessario)
    private static final String HOST = "localhost";
    private static final int PORT = 3030;

    /**
     * Invia un comando al database e restituisce la risposta.
     *
     * @param comando Il comando testuale da inviare (es. "GET utente:XYZ")
     * @return La risposta del database o "ERRORE" in caso di problemi
     */
    public static String invia(String comando) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Invia il comando
            writer.write(comando);
            writer.newLine(); // importante per segnalare fine comando
            writer.flush();

            // Legge e restituisce la risposta
            return reader.readLine();

        } catch (IOException e) {
            System.err.println("Errore nella comunicazione con il database: " + e.getMessage());
            return "ERRORE";
        }
    }
}
