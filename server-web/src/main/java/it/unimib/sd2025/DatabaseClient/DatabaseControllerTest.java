package it.unimib.sd2025.DatabaseClient;

public class DatabaseControllerTest {
    public static void main(String[] args) {
        // Create a DatabaseController instance
        DatabaseController dbController = DatabaseController.get();

        dbController.popolaDatabase();
    }
}
