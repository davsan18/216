package sportsmanager.ui;

import sportsmanager.core.ISportFactory;
import sportsmanager.core.ITeam;

import java.util.HashMap;
import java.util.Map;

/** Hard-coded rosters per team for both sports. Keeps MainApp lean. */
final class Squads {

    private Squads() {}

    /** Each row: {name, position, skillLevel} — first 11 (football) or 6 (volleyball) become starters. */
    static final Map<String, String[][]> FOOTBALL_ROSTERS = new HashMap<>();
    static final Map<String, String[][]> VOLLEYBALL_ROSTERS = new HashMap<>();

    static final String[] FOOTBALL_TEAMS = { "Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor" };
    static final String[] VOLLEYBALL_TEAMS = {
            "Galatasaray HDI", "Fenerbahçe Medicana", "Beşiktaş Akatel", "Halkbank Ankara"
    };

    static {
        FOOTBALL_ROSTERS.put("Galatasaray", new String[][] {
                {"Muslera","Kaleci","82"},
                {"Sanchez","Defans","85"},
                {"Bardakcı","Defans","82"},
                {"Boey","Defans","81"},
                {"Angeliño","Defans","83"},
                {"Torreira","Orta Saha","85"},
                {"Demirbay","Orta Saha","82"},
                {"Mertens","Orta Saha","84"},
                {"Aktürkoğlu","Orta Saha","81"},
                {"Icardi","Forvet","90"},
                {"Ziyech","Forvet","85"},
                {"Bakambu","Forvet","78"},
                {"Akgün","Orta Saha","80"},
                {"Lemina","Orta Saha","81"},
        });
        FOOTBALL_ROSTERS.put("Fenerbahçe", new String[][] {
                {"Livakovic","Kaleci","83"},
                {"Djiku","Defans","82"},
                {"Becao","Defans","82"},
                {"Osayi","Defans","80"},
                {"Ferdi","Defans","79"},
                {"Tadic","Orta Saha","85"},
                {"Fred","Orta Saha","82"},
                {"Krunic","Orta Saha","81"},
                {"Szymanski","Orta Saha","82"},
                {"Dzeko","Forvet","88"},
                {"En-Nesyri","Forvet","85"},
                {"Kahveci","Orta Saha","79"},
                {"Mert Hakan","Orta Saha","77"},
                {"Kadıoğlu","Defans","80"},
        });
        FOOTBALL_ROSTERS.put("Beşiktaş", new String[][] {
                {"Mert Günok","Kaleci","81"},
                {"Necip","Defans","78"},
                {"Saiss","Defans","81"},
                {"Mustafi","Defans","79"},
                {"Onur","Defans","78"},
                {"Salih","Orta Saha","80"},
                {"Ndidi","Orta Saha","82"},
                {"Rashica","Orta Saha","80"},
                {"Ghezzal","Orta Saha","79"},
                {"Muleka","Forvet","78"},
                {"Immobile","Forvet","85"},
                {"Topal","Orta Saha","75"},
                {"Zaynutdinov","Orta Saha","76"},
                {"Hakan Arslan","Defans","75"},
        });
        FOOTBALL_ROSTERS.put("Trabzonspor", new String[][] {
                {"Uğurcan","Kaleci","83"},
                {"Lawrence","Defans","79"},
                {"Bartra","Defans","81"},
                {"Hüseyin","Defans","78"},
                {"Pınar","Defans","77"},
                {"Bouchalakis","Orta Saha","79"},
                {"Visca","Orta Saha","80"},
                {"Kanga","Orta Saha","79"},
                {"Cardoso","Orta Saha","78"},
                {"Onuachu","Forvet","82"},
                {"Bakasetas","Forvet","81"},
                {"Doğacan","Orta Saha","75"},
                {"Trezeguet","Orta Saha","78"},
                {"Pepe","Defans","76"},
        });

        VOLLEYBALL_ROSTERS.put("Galatasaray HDI", new String[][] {
                {"Naz","Pasör","85"},
                {"Karakurt","Smaçör","90"},
                {"Buse","Smaçör","83"},
                {"Cansu","Orta","82"},
                {"Eda","Orta","81"},
                {"Ayça","Karşı","84"},
                {"İlkin","Libero","78"},
                {"Meryem","Smaçör","76"},
                {"Hande","Orta","74"},
        });
        VOLLEYBALL_ROSTERS.put("Fenerbahçe Medicana", new String[][] {
                {"Cansu Ö.","Pasör","84"},
                {"Vargas","Smaçör","92"},
                {"Arina","Smaçör","87"},
                {"Eda E.","Orta","82"},
                {"Zehra","Orta","80"},
                {"Melissa","Karşı","85"},
                {"Simge","Libero","79"},
                {"Tijana","Smaçör","78"},
                {"Berfu","Orta","73"},
        });
        VOLLEYBALL_ROSTERS.put("Beşiktaş Akatel", new String[][] {
                {"Aslı","Pasör","80"},
                {"Tess","Smaçör","83"},
                {"Mihriban","Smaçör","78"},
                {"Beyza","Orta","79"},
                {"Saliha","Orta","77"},
                {"Tatiana","Karşı","82"},
                {"Beyzanur","Libero","76"},
                {"Berra","Smaçör","73"},
                {"Gizem","Orta","71"},
        });
        VOLLEYBALL_ROSTERS.put("Halkbank Ankara", new String[][] {
                {"Murat","Pasör","82"},
                {"Adis","Smaçör","85"},
                {"Earvin","Smaçör","87"},
                {"Selim","Orta","80"},
                {"Volkan","Orta","79"},
                {"Burutay","Karşı","83"},
                {"Eren","Libero","77"},
                {"Yiğit","Smaçör","75"},
                {"Bedirhan","Orta","73"},
        });
    }

    static void fillRoster(ITeam team, ISportFactory factory, boolean football) {
        Map<String, String[][]> source = football ? FOOTBALL_ROSTERS : VOLLEYBALL_ROSTERS;
        String[][] roster = source.get(team.getName());
        if (roster == null) return;
        for (String[] row : roster) {
            int skill = Integer.parseInt(row[2]);
            team.addPlayer(factory.createPlayer(row[0], row[1], skill));
        }
    }
}
