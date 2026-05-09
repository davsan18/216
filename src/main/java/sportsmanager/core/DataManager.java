package sportsmanager.core;

import java.io.*;

public class DataManager {

    // Oyunu Kaydetme Metodu
    public static void saveGame(ILeague league, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(league);
        }
    }

    // Oyunu Yükleme Metodu
    public static ILeague loadGame(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (ILeague) ois.readObject();
        }
    }
}