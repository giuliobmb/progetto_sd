package it.unimib.sd2025.DatabaseUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

public class DatabaseUtility implements DatabaseInterface{

    private Socket client;

    public DatabaseUtility() throws UnknownHostException, IOException{
        client = new Socket("localhost", PORTA);      
    }

    @Override
    public void addSchema() {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void addPair() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addPair'");
    }

    @Override
    public void removePair() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removePair'");
    }

    @Override
    public void updatePair() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePair'");
    }

    @Override
    public void getValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValue'");
    }

    @Override
    public void getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }
    
    public void closeConnection(){
        try {
            client.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
