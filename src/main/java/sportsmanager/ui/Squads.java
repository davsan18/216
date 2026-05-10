package sportsmanager.ui;

import sportsmanager.core.ISportFactory;
import sportsmanager.core.ITeam;

import java.util.HashMap;
import java.util.Map;

/** Hard-coded rosters per team. First N entries become starters. */
final class Squads {

    private Squads() {}

    static final Map<String, String[][]> FOOTBALL_ROSTERS = new HashMap<>();
    static final Map<String, String[][]> VOLLEYBALL_ROSTERS = new HashMap<>();
    static final Map<String, String> STADIUMS = new HashMap<>();
    static final Map<String, String> COACHES = new HashMap<>();
    static final Map<String, Integer> COACH_BONUS = new HashMap<>();

    static final String[] FOOTBALL_TEAMS = {
            "Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor",
            "Başakşehir", "Konyaspor"
    };
    static final String[] VOLLEYBALL_TEAMS = {
            "Galatasaray HDI", "Fenerbahçe Medicana", "Beşiktaş Akatel",
            "Halkbank Ankara", "Ziraat Bankkart", "Vakıfbank"
    };

    static String stadium(String team) { return STADIUMS.getOrDefault(team, "—"); }
    static String coach(String team)   { return COACHES.getOrDefault(team, "Teknik Direktör"); }
    static int coachBonus(String team) { return COACH_BONUS.getOrDefault(team, 5); }

    static {
        // Stadyumlar
        STADIUMS.put("Galatasaray", "Rams Park");
        STADIUMS.put("Fenerbahçe", "Şükrü Saracoğlu");
        STADIUMS.put("Beşiktaş", "Tüpraş Stadyumu");
        STADIUMS.put("Trabzonspor", "Şenol Güneş");
        STADIUMS.put("Başakşehir", "Başakşehir Fatih Terim");
        STADIUMS.put("Konyaspor", "Konya Büyükşehir");
        STADIUMS.put("Galatasaray HDI", "Burhan Felek Salonu");
        STADIUMS.put("Fenerbahçe Medicana", "Caferağa Spor Salonu");
        STADIUMS.put("Beşiktaş Akatel", "BJK Akatlar Arena");
        STADIUMS.put("Halkbank Ankara", "Başkent Volleyball Hall");
        STADIUMS.put("Ziraat Bankkart", "TVF Başkent Salonu");
        STADIUMS.put("Vakıfbank", "Vakıfbank Spor Sarayı");

        // Teknik Direktörler (futbol)
        COACHES.put("Galatasaray", "Okan Buruk");      COACH_BONUS.put("Galatasaray", 10);
        COACHES.put("Fenerbahçe", "İsmail Kartal");    COACH_BONUS.put("Fenerbahçe", 9);
        COACHES.put("Beşiktaş", "Fernando Santos");    COACH_BONUS.put("Beşiktaş", 8);
        COACHES.put("Trabzonspor", "Abdullah Avcı");   COACH_BONUS.put("Trabzonspor", 9);
        COACHES.put("Başakşehir", "Çağdaş Atan");      COACH_BONUS.put("Başakşehir", 7);
        COACHES.put("Konyaspor", "Ali Çamdalı");       COACH_BONUS.put("Konyaspor", 6);
        // Teknik Direktörler (voleybol)
        COACHES.put("Galatasaray HDI", "Bülent Karslıoğlu"); COACH_BONUS.put("Galatasaray HDI", 9);
        COACHES.put("Fenerbahçe Medicana", "Marcello Abbondanza"); COACH_BONUS.put("Fenerbahçe Medicana", 10);
        COACHES.put("Beşiktaş Akatel", "Cesar Hernandez"); COACH_BONUS.put("Beşiktaş Akatel", 7);
        COACHES.put("Halkbank Ankara", "Slobodan Kovac"); COACH_BONUS.put("Halkbank Ankara", 8);
        COACHES.put("Ziraat Bankkart", "Ferhat Akbaş"); COACH_BONUS.put("Ziraat Bankkart", 7);
        COACHES.put("Vakıfbank", "Giovanni Guidetti"); COACH_BONUS.put("Vakıfbank", 10);

        // 18 oyuncu / takım: 1 K + 5 D + 5 OS + 3 F + 4 yedek
        FOOTBALL_ROSTERS.put("Galatasaray", new String[][] {
                {"Muslera","Kaleci","85"},
                {"Sanchez","Defans","86"},
                {"Bardakcı","Defans","82"},
                {"Boey","Defans","81"},
                {"Angeliño","Defans","83"},
                {"Nelsson","Defans","79"},
                {"Torreira","Orta Saha","85"},
                {"Demirbay","Orta Saha","82"},
                {"Mertens","Orta Saha","84"},
                {"Aktürkoğlu","Orta Saha","81"},
                {"Icardi","Forvet","90"},
                {"Ziyech","Forvet","85"},
                {"Bakambu","Forvet","78"},
                {"Akgün","Orta Saha","80"},
                {"Lemina","Orta Saha","81"},
                {"Çakır","Kaleci","74"},
                {"Tetê","Forvet","82"},
                {"Köhn","Defans","76"},
        });
        FOOTBALL_ROSTERS.put("Fenerbahçe", new String[][] {
                {"Livakovic","Kaleci","83"},
                {"Djiku","Defans","82"},
                {"Becao","Defans","82"},
                {"Osayi","Defans","80"},
                {"Ferdi","Defans","79"},
                {"Söyüncü","Defans","80"},
                {"Tadic","Orta Saha","85"},
                {"Fred","Orta Saha","82"},
                {"Krunic","Orta Saha","81"},
                {"Szymanski","Orta Saha","82"},
                {"Dzeko","Forvet","88"},
                {"En-Nesyri","Forvet","85"},
                {"Tisserand","Defans","75"},
                {"Kahveci","Orta Saha","79"},
                {"Mert Hakan","Orta Saha","77"},
                {"Kadıoğlu","Defans","80"},
                {"Bayındır","Kaleci","73"},
                {"Yandaş","Forvet","74"},
        });
        FOOTBALL_ROSTERS.put("Beşiktaş", new String[][] {
                {"Mert Günok","Kaleci","81"},
                {"Necip","Defans","78"},
                {"Saiss","Defans","81"},
                {"Mustafi","Defans","79"},
                {"Onur","Defans","78"},
                {"Paulista","Defans","77"},
                {"Salih","Orta Saha","80"},
                {"Ndidi","Orta Saha","82"},
                {"Rashica","Orta Saha","80"},
                {"Ghezzal","Orta Saha","79"},
                {"Muleka","Forvet","78"},
                {"Immobile","Forvet","85"},
                {"Topal","Orta Saha","75"},
                {"Zaynutdinov","Orta Saha","76"},
                {"Hakan Arslan","Defans","75"},
                {"Ersin","Kaleci","74"},
                {"Al Musrati","Orta Saha","78"},
                {"Aboubakar","Forvet","79"},
        });
        FOOTBALL_ROSTERS.put("Trabzonspor", new String[][] {
                {"Uğurcan","Kaleci","83"},
                {"Lawrence","Defans","79"},
                {"Bartra","Defans","81"},
                {"Hüseyin","Defans","78"},
                {"Pınar","Defans","77"},
                {"Stefano","Defans","75"},
                {"Bouchalakis","Orta Saha","79"},
                {"Visca","Orta Saha","80"},
                {"Kanga","Orta Saha","79"},
                {"Cardoso","Orta Saha","78"},
                {"Onuachu","Forvet","82"},
                {"Bakasetas","Forvet","81"},
                {"Doğacan","Orta Saha","75"},
                {"Trezeguet","Orta Saha","78"},
                {"Pepe","Defans","76"},
                {"Çakır","Kaleci","73"},
                {"Eren","Forvet","74"},
                {"Fofana","Orta Saha","76"},
        });
        FOOTBALL_ROSTERS.put("Başakşehir", new String[][] {
                {"Volkan B.","Kaleci","78"},
                {"Hasic","Defans","76"},
                {"Duarte","Defans","79"},
                {"Opoku","Defans","75"},
                {"Ali Şaşal","Defans","74"},
                {"Bertolacci","Orta Saha","77"},
                {"Tekdemir","Orta Saha","78"},
                {"Crivelli","Forvet","80"},
                {"Selke","Forvet","78"},
                {"Gürler","Orta Saha","75"},
                {"Adekanye","Forvet","76"},
                {"Şengezer","Defans","73"},
                {"Çolak","Orta Saha","73"},
                {"Türüç","Orta Saha","74"},
                {"Edomwonyi","Forvet","75"},
                {"Babacan","Kaleci","70"},
                {"Bajic","Orta Saha","72"},
                {"Ömer","Defans","70"},
        });
        FOOTBALL_ROSTERS.put("Konyaspor", new String[][] {
                {"Bahadır","Kaleci","75"},
                {"Skubic","Defans","76"},
                {"Yasir","Defans","73"},
                {"Calusic","Defans","75"},
                {"Bytyqi","Defans","74"},
                {"Dimata","Forvet","78"},
                {"Sokol","Orta Saha","74"},
                {"Hadziahmetovic","Orta Saha","76"},
                {"Knapik","Orta Saha","73"},
                {"Bajic","Forvet","75"},
                {"Endo","Orta Saha","72"},
                {"Ndao","Forvet","73"},
                {"Kravets","Forvet","72"},
                {"Asan","Defans","71"},
                {"Boyalı","Orta Saha","70"},
                {"Kerem","Kaleci","69"},
                {"Marko","Defans","70"},
                {"Yatabaré","Orta Saha","72"},
        });

        // 12 oyuncu / takım: 6 starter + 6 yedek
        VOLLEYBALL_ROSTERS.put("Galatasaray HDI", new String[][] {
                {"Naz","Pasör","85"},
                {"Karakurt","Smaçör","92"},
                {"Buse","Smaçör","83"},
                {"Cansu","Orta","82"},
                {"Eda","Orta","81"},
                {"Ayça","Karşı","84"},
                {"İlkin","Libero","78"},
                {"Meryem","Smaçör","76"},
                {"Hande","Orta","74"},
                {"Sıla","Pasör","73"},
                {"Selin","Karşı","75"},
                {"Burcu","Libero","72"},
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
                {"Yasemin","Pasör","72"},
                {"Sude","Karşı","74"},
                {"Merve","Libero","71"},
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
                {"İrem","Pasör","70"},
                {"Ecem","Karşı","72"},
                {"Damla","Libero","69"},
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
                {"Onur","Pasör","71"},
                {"Berkay","Karşı","73"},
                {"Mert","Libero","70"},
        });
        VOLLEYBALL_ROSTERS.put("Ziraat Bankkart", new String[][] {
                {"Arslan","Pasör","78"},
                {"Cebecioğlu","Smaçör","82"},
                {"Plotnytski","Smaçör","83"},
                {"Mert","Orta","79"},
                {"Berkay","Orta","77"},
                {"Yiğit","Karşı","80"},
                {"Hakan","Libero","74"},
                {"Doğukan","Smaçör","72"},
                {"Caner","Orta","71"},
                {"Tolga","Pasör","69"},
                {"Hüseyin","Karşı","70"},
                {"Furkan","Libero","68"},
        });
        VOLLEYBALL_ROSTERS.put("Vakıfbank", new String[][] {
                {"Foluke","Pasör","83"},
                {"Boskovic","Smaçör","91"},
                {"Gabi","Smaçör","86"},
                {"Akman","Orta","81"},
                {"Cansu","Orta","80"},
                {"Çiçek","Karşı","84"},
                {"Aydın","Libero","78"},
                {"Pelin","Smaçör","75"},
                {"Tuğba","Orta","73"},
                {"Bahar","Pasör","72"},
                {"Gizem","Karşı","74"},
                {"Buse","Libero","71"},
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
