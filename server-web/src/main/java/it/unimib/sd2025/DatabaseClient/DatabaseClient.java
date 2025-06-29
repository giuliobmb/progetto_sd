package it.unimib.sd2025.DatabaseClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

public class DatabaseClient implements DatabaseClientInterface{

    private Socket client;

    public DatabaseClient() throws UnknownHostException, IOException{
        client = new Socket("localhost", PORTA);      
    }

    @Override
    public void addSchema(String schemaName) {
        PrintWriter out;
        BufferedReader in;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                if ("OKEND".equals(inputLine)) {
                    out.println("OKEND");
                    break;
                }
                String[] response = inputLine.split(" ");
                switch(response[0]){
                    case "OK":
                        out.println("END");
                        break;
                    case "OKEND":
                        this.closeConnection();
                }

            }

            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPair(String schemaName, String key, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addPair'");
    }

    @Override
    public void removePair(String schemaName, String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removePair'");
    }

    @Override
    public void updatePair(String schemaName, String key, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePair'");
    }

    @Override
    public void getValue(String schemaName, String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValue'");
    }

    @Override
    public void getAll(String schemaName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }
    
    public void closeConnection(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
