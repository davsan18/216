package sportsmanager.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DataManager {

    public static void saveGame(ILeague league, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(league);
        }
    }

    public static ILeague loadGame(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (ILeague) ois.readObject();
        }
    }

    /** Save with a user-defined display name. Writes a sidecar `.meta` file. */
    public static void saveGameNamed(ILeague league, String filePath, String displayName) throws IOException {
        saveGame(league, filePath);
        Properties props = new Properties();
        props.setProperty("name", displayName == null ? "" : displayName);
        props.setProperty("timestamp", String.valueOf(System.currentTimeMillis()));
        props.setProperty("league", league.getName() == null ? "" : league.getName());
        if (league.getManagedTeam() != null) {
            props.setProperty("managedTeam", league.getManagedTeam().getName());
        }
        int played = 0;
        for (IMatch m : league.getScheduledMatches()) if (m.isPlayed()) played++;
        props.setProperty("matchesPlayed", String.valueOf(played));
        props.setProperty("matchesTotal", String.valueOf(league.getScheduledMatches().size()));

        try (Writer w = new OutputStreamWriter(new FileOutputStream(filePath + ".meta"), StandardCharsets.UTF_8)) {
            props.store(w, "SportsManager save metadata");
        }
    }

    public static SaveMeta loadMeta(String filePath) {
        File meta = new File(filePath + ".meta");
        if (!meta.exists()) return null;
        try (Reader r = new InputStreamReader(new FileInputStream(meta), StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(r);
            SaveMeta m = new SaveMeta();
            m.name = props.getProperty("name", "");
            m.timestamp = parseLong(props.getProperty("timestamp"));
            m.league = props.getProperty("league", "");
            m.managedTeam = props.getProperty("managedTeam", "");
            m.matchesPlayed = parseInt(props.getProperty("matchesPlayed"));
            m.matchesTotal = parseInt(props.getProperty("matchesTotal"));
            return m;
        } catch (IOException ex) {
            return null;
        }
    }

    private static long parseLong(String s) {
        try { return s == null ? 0 : Long.parseLong(s); } catch (NumberFormatException e) { return 0; }
    }
    private static int parseInt(String s) {
        try { return s == null ? 0 : Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }

    public static class SaveMeta {
        public String name = "";
        public long timestamp;
        public String league = "";
        public String managedTeam = "";
        public int matchesPlayed;
        public int matchesTotal;
    }
}
