package it.unimib.sd2025.DatabaseClient;

import java.io.IOException;
import java.util.HashMap;

public class DatabaseClientTest {

    public static void main(String[] args) {
        try {
            // Create a DatabaseClient instance and connect to the server
            DatabaseClient client = new DatabaseClient();
            
            // Test addSchema method
            System.out.println("Testing addSchema...");
            client.addSchema("TestSchema");
            
            // Test addPair method
            System.out.println("Testing addPair...");
            client.addPair("TestSchema", "key1", "value1");
            client.addPair("TestSchema", "key2", "value2");
            
            // Test getValue method
            System.out.println("Testing getValue...");
            String value = client.getValue("TestSchema", "key1");
            System.out.println("Value for key1: " + value);
            
            // Test getAll method
            System.out.println("Testing getAll...");
            HashMap<String, String> allValues = client.getAll("TestSchema");
            System.out.println(allValues.toString());
            System.out.println("All values in schema:");
            for (String key : allValues.keySet()) {
                System.out.println(key + " = " + allValues.get(key));
            }

            // Test updatePair method
            System.out.println("Testing updatePair...");
            client.updatePair("TestSchema", "key1", "updatedValue1");
            String updatedValue = client.getValue("TestSchema", "key1");
            System.out.println("Updated value for key1: " + updatedValue);
            
            // Test removePair method
            System.out.println("Testing removePair...");
            client.removePair("TestSchema", "key2");
            String removedValue = client.getValue("TestSchema", "key2");
            System.out.println("Value for removed key2: " + removedValue);

            // Close the connection
            client.closeConnection();
            System.out.println("Connection closed.");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
