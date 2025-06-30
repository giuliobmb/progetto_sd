package it.unimib.sd2025;

import java.net.*;
import java.io.*;

/**
 * Classe principale in cui parte il database.
 */
public class Main {
    /**
     * Porta di ascolto.
     */
    public static final int PORT = 3030;

    /**
     * Avvia il database e l'ascolto di nuove connessioni.
     */
    public static void startServer() throws IOException {
        var server = new ServerSocket(PORT);

        System.out.println("Database listening at localhost:" + PORT);

        try {
            while (true)
                new Handler(server.accept()).start();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            server.close();
        }
    }


    /**
     * Handler di una connessione del client.
     */
    private static class Handler extends Thread {
        private Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                Database db = Database.get();

                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received command: " + inputLine);
                    if ("END".equals(inputLine)) {
                        out.println("OKEND");
                        System.out.println("Sent command: OKEND");
                        break;
                    }

                    String[] command = inputLine.split(" ");
                    switch(command[0]){
                        case "ADDSCHEMA":
                            if(db.addSchema(command[1]) != null){
                                out.println("OK");
                                System.out.println("Sent command: OK");
                            }else{
                                out.println("ERROR");
                                System.out.println("Sent command: ERROR");
                            }

                            break;
                        case "ADDPAIR":
                            if(db.addPair(command[1], command[2], command[3]) != null){
                                out.println("OK");
                                System.out.println("Sent command: OK");
                            }else{
                                out.println("ERROR");
                                System.out.println("Sent command: ERROR");
                            }

                            break;
                        case "UPDATEPAIR":
                            if(db.updatePair(command[1], command[2], command[3]) != null){
                                out.println("OK");
                                System.out.println("Sent command: OK");
                            }
                            else{
                                out.println("ERROR");
                                System.out.println("Sent command: ERROR");
                            }

                            break;
                        case "REMOVEPAIR":
                            if(db.removePair(command[1], command[2]) != null){
                                out.println("OK");
                                System.out.println("Sent command: OK");
                            }
                            else{
                                out.println("ERROR");
                                System.out.println("Sent command: ERROR");
                            }
                            break;
                        case "GETVALUE":
                            String value = db.getValue(command[1], command[2]);
                            if(value != null){
                                out.println("VALUE " + value);
                                System.out.println("Sent command: VALUE " + value);
                            }else{
                                out.println("ERROR");
                                System.out.println("Sent command: ERROR");
                            }
                            break;
                        case "GETALL":
                            String values = db.getAll(command[1]);
                            if(values != null){
                                out.println(values);
                                System.out.println("Sent command: " + values);
                            }else{
                                out.println("ERROR");
                                System.out.println("Sent command: ERROR");
                            }
                            break;
                        default:
                            out.println("ERROR");
                    }
                }

                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * Metodo principale di avvio del database.
     *
     * @param args argomenti passati a riga di comando.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        startServer();
    }
}
