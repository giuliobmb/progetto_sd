package it.unimib.sd2025.DatabaseClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.HashMap;

public class DatabaseClient implements DatabaseClientInterface{

    private Socket client;

    public DatabaseClient() throws UnknownHostException, IOException{
            
    }

    @Override
    public void addSchema(String schemaName) {
        if (client == null || client.isClosed()) {
            try {
                client = new Socket("localhost", 3030);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out;
        BufferedReader in;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("ADDSCHEMA " + schemaName);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                String[] response = inputLine.split(" ");
                switch(response[0]){
                    case "OK":
                        out.println("END");
                        break;
                    case "ERROR":
                        out.println("END");
                        break;
                    case "OKEND":
                        break;
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
        if (client == null || client.isClosed()) {
            try {
                client = new Socket("localhost", 3030);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out;
        BufferedReader in;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("ADDPAIR " + schemaName + " " + key + " " + value);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                String[] response = inputLine.split(" ");
                switch(response[0]){
                    case "OK":
                        out.println("END");
                        break;
                    case "ERROR":
                        out.println("END");
                        break;
                    case "OKEND":
                        break;
                }
            }

            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePair(String schemaName, String key) {
        if (client == null || client.isClosed()) {
            try {
                client = new Socket("localhost", 3030);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out;
        BufferedReader in;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("REMOVEPAIR " + schemaName + " " + key);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                String[] response = inputLine.split(" ");
                switch(response[0]){
                    case "OK":
                        out.println("END");
                        break;
                    case "ERROR":
                        out.println("END");
                        break;
                    case "OKEND":
                        break;
                }
            }

            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePair(String schemaName, String key, String value) {
        if (client == null || client.isClosed()) {
            try {
                client = new Socket("localhost", 3030);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out;
        BufferedReader in;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("UPDATEPAIR " + schemaName + " " + key + " " + value);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                String[] response = inputLine.split(" ");
                switch(response[0]){
                    case "OK":
                        out.println("END");
                        break;
                    case "ERROR":
                        out.println("END");
                        break;
                    case "OKEND":
                        break;
                }
            }

            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getValue(String schemaName, String key) {
        if (client == null || client.isClosed()) {
            try {
                client = new Socket("localhost", 3030);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out;
        BufferedReader in;
        String value = null;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("GETVALUE " + schemaName + " " + key);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                String[] response = inputLine.split(" ");
                switch(response[0]){
                    case "VALUE":
                        value = response[1];
                        out.println("END");
                        break;
                    case "ERROR":
                        value = null;
                        out.println("END");
                        break;
                    case "OKEND":
                        break;
                }
            }

            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }

    @Override
    public HashMap<String,String> getAll(String schemaName) {
        if (client == null || client.isClosed()) {
            try {
                client = new Socket("localhost", 3030);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out;
        BufferedReader in;
        HashMap<String, String> values = new HashMap<>();
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println("GETALL " + schemaName);

            String inputLine;
            String response  = "";

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("ERROR")) {
                    return null;
                } else if (inputLine.equals("OKEND")) {
                    break;
                } else {
                    response = inputLine;
                    out.println("END");
                    break;
                }
            }

            String[] responses = response.split(";");

            for (String pair : responses) {
                String[] keyValue = pair.split(" ");
                if (keyValue.length == 2) {
                    values.put(keyValue[0], keyValue[1]);
                }
            }

            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }
    
    public void closeConnection(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
