package it.unimib.sd2025.DatabaseClient;

import it.unimib.sd2025.model.Buono;
import it.unimib.sd2025.model.Utente;

import java.util.List;

public class TestBuonoController {

    public static void main(String[] args) {
        // Inizializzazione del DatabaseController
        DatabaseController databaseController = DatabaseController.get();

        // Creazione di un utente di test
        Utente utente = new Utente("ABC12345");

        // Creazione di un buono di test
        Buono buono1 = new Buono("1", "ABC12345", 50.0, "Sconto", "2025-06-01", null, "Attivo");
        Buono buono2 = new Buono("2", "ABC12345", 100.0, "Promozione", "2025-06-01", null, "Attivo");

        // Test: Aggiungere buoni
        System.out.println("Aggiungendo i buoni...");
        databaseController.addBuono(buono1);
        databaseController.addBuono(buono2);

        // Recupera i buoni dell'utente
        List<Buono> buoniList = databaseController.getAllBuoni(utente.getCodiceFiscale());
        System.out.println("Buoni dopo aggiunta:");
        for (Buono b : buoniList) {
            System.out.println(b.getIdBuono() + " - " + b.getImporto());
        }

        // Test: Aggiornare un buono
        System.out.println("\nAggiornando l'importo del buono 1...");
        buono1.setImporto(75.0); // Modifica l'importo
        databaseController.updateBuono(buono1);

        // Recupera di nuovo i buoni dell'utente dopo l'aggiornamento
        buoniList = databaseController.getAllBuoni(utente.getCodiceFiscale());
        System.out.println("Buoni dopo aggiornamento:");
        for (Buono b : buoniList) {
            System.out.println(b.getIdBuono() + " - " + b.getImporto());
        }

        // Test: Recupero di un buono specifico tramite ID
        System.out.println("\nRecupero del buono con ID '1'...");
        Buono buonoRecuperato = databaseController.getBuonoById("1");
        if (buonoRecuperato != null) {
            System.out.println("Buono recuperato: " + buonoRecuperato.getIdBuono() + " - " + buonoRecuperato.getImporto());
        } else {
            System.out.println("Buono non trovato.");
        }

        // Test: Rimuovere un buono
        System.out.println("\nRimuovendo il buono 2...");
        databaseController.removeBuono(buono2);

        // Recupera i buoni dell'utente dopo la rimozione
        buoniList = databaseController.getAllBuoni(utente.getCodiceFiscale());
        System.out.println("Buoni dopo rimozione:");
        for (Buono b : buoniList) {
            System.out.println(b.getIdBuono() + " - " + b.getImporto());
        }
    }
}
