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

    // Süper Lig 2024-25 — 18 teams
    static final String[] FOOTBALL_TEAMS = {
            "Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor",
            "Başakşehir", "Sivasspor", "Samsunspor", "Alanyaspor",
            "Gaziantep FK", "Kasımpaşa", "Antalyaspor", "Kayserispor",
            "Çaykur Rizespor", "Ankaragücü", "Konyaspor", "Bodrum FK",
            "Eyüpspor", "Göztepe"
    };

    // Sultanlar Ligi / Efeler Ligi — 12 teams
    static final String[] VOLLEYBALL_TEAMS = {
            "Vakıfbank", "Fenerbahçe Medicana", "Galatasaray HDI",
            "Eczacıbaşı Dynavit", "Halkbank Ankara", "Beşiktaş Akatel",
            "Türk Hava Yolları", "Ziraat Bankkart", "Nilüfer Belediyespor",
            "Arkas Spor", "İnegöl Belediyespor", "PTT Spor"
    };

    static String stadium(String team) { return STADIUMS.getOrDefault(team, "—"); }
    static String coach(String team)   { return COACHES.getOrDefault(team, "Teknik Direktör"); }
    static int coachBonus(String team) { return COACH_BONUS.getOrDefault(team, 5); }

    static {
        // ── Stadiums ──────────────────────────────────────────────────────────
        STADIUMS.put("Galatasaray",          "Rams Park");
        STADIUMS.put("Fenerbahçe",           "Şükrü Saracoğlu");
        STADIUMS.put("Beşiktaş",             "Tüpraş Stadyumu");
        STADIUMS.put("Trabzonspor",          "Şenol Güneş Stadı");
        STADIUMS.put("Başakşehir",           "Fatih Terim Stadı");
        STADIUMS.put("Sivasspor",            "Yeni 4 Eylül Stadı");
        STADIUMS.put("Samsunspor",           "Yeni Samsun İlkadım");
        STADIUMS.put("Alanyaspor",           "Alanya Stadyumu");
        STADIUMS.put("Gaziantep FK",         "Naci Topçuoğlu Stadı");
        STADIUMS.put("Kasımpaşa",            "Recep Tayyip Erdoğan Stadı");
        STADIUMS.put("Antalyaspor",          "Antalya Stadı");
        STADIUMS.put("Kayserispor",          "Kadir Has Stadyumu");
        STADIUMS.put("Çaykur Rizespor",      "Çaykur Didi Stadı");
        STADIUMS.put("Ankaragücü",           "Eryaman Stadyumu");
        STADIUMS.put("Konyaspor",            "Konya Büyükşehir Stadı");
        STADIUMS.put("Bodrum FK",            "Bodrum Stadı");
        STADIUMS.put("Eyüpspor",             "Eyüp Sultan Stadı");
        STADIUMS.put("Göztepe",              "Gürsel Aksel Stadı");

        STADIUMS.put("Vakıfbank",            "Vakıfbank Spor Sarayı");
        STADIUMS.put("Fenerbahçe Medicana",  "Caferağa Spor Salonu");
        STADIUMS.put("Galatasaray HDI",      "Burhan Felek Salonu");
        STADIUMS.put("Eczacıbaşı Dynavit",   "Eczacıbaşı Spor Salonu");
        STADIUMS.put("Halkbank Ankara",      "Başkent Voleybol Salonu");
        STADIUMS.put("Beşiktaş Akatel",      "BJK Akatlar Arena");
        STADIUMS.put("Türk Hava Yolları",    "THY Spor Salonu");
        STADIUMS.put("Ziraat Bankkart",      "TVF Başkent Salonu");
        STADIUMS.put("Nilüfer Belediyespor", "Nilüfer Spor Salonu");
        STADIUMS.put("Arkas Spor",           "Arkas Arena");
        STADIUMS.put("İnegöl Belediyespor",  "İnegöl Spor Salonu");
        STADIUMS.put("PTT Spor",             "PTT Spor Salonu");

        // ── Football coaches ──────────────────────────────────────────────────
        COACHES.put("Galatasaray",     "Okan Buruk");          COACH_BONUS.put("Galatasaray",     10);
        COACHES.put("Fenerbahçe",      "José Mourinho");       COACH_BONUS.put("Fenerbahçe",      10);
        COACHES.put("Beşiktaş",        "Giovanni van Bronckhorst"); COACH_BONUS.put("Beşiktaş",    8);
        COACHES.put("Trabzonspor",     "Abdullah Avcı");       COACH_BONUS.put("Trabzonspor",      9);
        COACHES.put("Başakşehir",      "Çağdaş Atan");         COACH_BONUS.put("Başakşehir",       7);
        COACHES.put("Sivasspor",       "Rıza Çalımbay");       COACH_BONUS.put("Sivasspor",        7);
        COACHES.put("Samsunspor",      "Hüseyin Eroğlu");      COACH_BONUS.put("Samsunspor",       7);
        COACHES.put("Alanyaspor",      "Liviu Ciobotariu");    COACH_BONUS.put("Alanyaspor",       7);
        COACHES.put("Gaziantep FK",    "Erol Bulut");          COACH_BONUS.put("Gaziantep FK",      6);
        COACHES.put("Kasımpaşa",       "Şota Arveladze");      COACH_BONUS.put("Kasımpaşa",         6);
        COACHES.put("Antalyaspor",     "Sergen Yalçın");       COACH_BONUS.put("Antalyaspor",       7);
        COACHES.put("Kayserispor",     "Recep Uçar");          COACH_BONUS.put("Kayserispor",       6);
        COACHES.put("Çaykur Rizespor", "İlhan Palut");         COACH_BONUS.put("Çaykur Rizespor",   6);
        COACHES.put("Ankaragücü",      "Emre Belözoğlu");      COACH_BONUS.put("Ankaragücü",        7);
        COACHES.put("Konyaspor",       "Ali Çamdalı");         COACH_BONUS.put("Konyaspor",         6);
        COACHES.put("Bodrum FK",       "Samet Aybaba");        COACH_BONUS.put("Bodrum FK",         5);
        COACHES.put("Eyüpspor",        "Cüneyt Dumlupınar");   COACH_BONUS.put("Eyüpspor",          5);
        COACHES.put("Göztepe",         "Stanimir Stoilov");    COACH_BONUS.put("Göztepe",           5);

        // ── Volleyball coaches ────────────────────────────────────────────────
        COACHES.put("Vakıfbank",            "Giovanni Guidetti");    COACH_BONUS.put("Vakıfbank",            10);
        COACHES.put("Fenerbahçe Medicana",  "Daniele Santarelli");   COACH_BONUS.put("Fenerbahçe Medicana",  10);
        COACHES.put("Galatasaray HDI",      "Bülent Karslıoğlu");    COACH_BONUS.put("Galatasaray HDI",       9);
        COACHES.put("Eczacıbaşı Dynavit",   "Ferhat Akbaş");         COACH_BONUS.put("Eczacıbaşı Dynavit",    9);
        COACHES.put("Halkbank Ankara",      "Slobodan Kovač");       COACH_BONUS.put("Halkbank Ankara",       8);
        COACHES.put("Beşiktaş Akatel",      "Cesar Hernandez");      COACH_BONUS.put("Beşiktaş Akatel",       7);
        COACHES.put("Türk Hava Yolları",    "Massimo Barbolini");    COACH_BONUS.put("Türk Hava Yolları",     7);
        COACHES.put("Ziraat Bankkart",      "Ahmet Şahin");          COACH_BONUS.put("Ziraat Bankkart",       7);
        COACHES.put("Nilüfer Belediyespor", "Yusuf Alptekin");       COACH_BONUS.put("Nilüfer Belediyespor",  6);
        COACHES.put("Arkas Spor",           "Stefan Büscher");       COACH_BONUS.put("Arkas Spor",            7);
        COACHES.put("İnegöl Belediyespor",  "Mehmet Kaya");          COACH_BONUS.put("İnegöl Belediyespor",   5);
        COACHES.put("PTT Spor",             "Ayhan Eriş");           COACH_BONUS.put("PTT Spor",              5);

        // ── Football rosters — 18 players each ───────────────────────────────
        // Format: {name, position, skill}  |  1 GK, 5 DEF, 5 MID, 3 FWD, 4 bench

        FOOTBALL_ROSTERS.put("Galatasaray", new String[][] {
                {"Muslera",      "Kaleci",    "86"},
                {"Sanchez",      "Defans",    "87"},
                {"Bardakcı",     "Defans",    "83"},
                {"Boey",         "Defans",    "82"},
                {"Angeliño",     "Defans",    "83"},
                {"Nelsson",      "Defans",    "80"},
                {"Torreira",     "Orta Saha", "86"},
                {"Demirbay",     "Orta Saha", "83"},
                {"Mertens",      "Orta Saha", "85"},
                {"Aktürkoğlu",   "Orta Saha", "82"},
                {"Icardi",       "Forvet",    "91"},
                {"Ziyech",       "Forvet",    "86"},
                {"Tetê",         "Forvet",    "83"},
                {"Lemina",       "Orta Saha", "82"},
                {"Akgün",        "Orta Saha", "80"},
                {"Çakır",        "Kaleci",    "74"},
                {"Köhn",         "Defans",    "77"},
                {"Bakambu",      "Forvet",    "79"},
        });

        FOOTBALL_ROSTERS.put("Fenerbahçe", new String[][] {
                {"Livakovic",    "Kaleci",    "84"},
                {"Djiku",        "Defans",    "83"},
                {"Becao",        "Defans",    "83"},
                {"Kadıoğlu",     "Defans",    "82"},
                {"Osayi",        "Defans",    "81"},
                {"Söyüncü",      "Defans",    "81"},
                {"Tadic",        "Orta Saha", "86"},
                {"Fred",         "Orta Saha", "83"},
                {"Szymanski",    "Orta Saha", "83"},
                {"Krunic",       "Orta Saha", "82"},
                {"Dzeko",        "Forvet",    "89"},
                {"En-Nesyri",    "Forvet",    "86"},
                {"Kahveci",      "Orta Saha", "80"},
                {"Mert Hakan",   "Orta Saha", "78"},
                {"Yandaş",       "Forvet",    "76"},
                {"Bayındır",     "Kaleci",    "74"},
                {"Tisserand",    "Defans",    "76"},
                {"İrfan Can",    "Orta Saha", "79"},
        });

        FOOTBALL_ROSTERS.put("Beşiktaş", new String[][] {
                {"Mert Günok",   "Kaleci",    "82"},
                {"Saiss",        "Defans",    "82"},
                {"Mustafi",      "Defans",    "80"},
                {"Paulista",     "Defans",    "79"},
                {"Onur Bulut",   "Defans",    "78"},
                {"Necip",        "Defans",    "77"},
                {"Ndidi",        "Orta Saha", "83"},
                {"Al Musrati",   "Orta Saha", "79"},
                {"Ghezzal",      "Orta Saha", "80"},
                {"Rashica",      "Orta Saha", "80"},
                {"Immobile",     "Forvet",    "86"},
                {"Aboubakar",    "Forvet",    "80"},
                {"Muleka",       "Forvet",    "79"},
                {"Salih Uçan",   "Orta Saha", "76"},
                {"Zaynutdinov",  "Orta Saha", "77"},
                {"Ersin",        "Kaleci",    "74"},
                {"Hakan Arslan", "Defans",    "75"},
                {"Topal",        "Orta Saha", "76"},
        });

        FOOTBALL_ROSTERS.put("Trabzonspor", new String[][] {
                {"Uğurcan",      "Kaleci",    "84"},
                {"Bartra",       "Defans",    "82"},
                {"Lawrence",     "Defans",    "80"},
                {"Hüseyin",      "Defans",    "79"},
                {"Pepe",         "Defans",    "77"},
                {"Pınar",        "Defans",    "77"},
                {"Visca",        "Orta Saha", "81"},
                {"Bouchalakis",  "Orta Saha", "80"},
                {"Trezeguet",    "Orta Saha", "79"},
                {"Cardoso",      "Orta Saha", "79"},
                {"Onuachu",      "Forvet",    "83"},
                {"Bakasetas",    "Forvet",    "82"},
                {"Fofana",       "Orta Saha", "77"},
                {"Kanga",        "Orta Saha", "80"},
                {"Doğacan",      "Orta Saha", "75"},
                {"Çakır Tan",    "Kaleci",    "73"},
                {"Stefano",      "Defans",    "75"},
                {"Eren",         "Forvet",    "75"},
        });

        FOOTBALL_ROSTERS.put("Başakşehir", new String[][] {
                {"Volkan B.",    "Kaleci",    "78"},
                {"Duarte",       "Defans",    "80"},
                {"Hasic",        "Defans",    "77"},
                {"Opoku",        "Defans",    "76"},
                {"Ali Şaşal",    "Defans",    "75"},
                {"Tekdemir",     "Defans",    "74"},
                {"Bertolacci",   "Orta Saha", "78"},
                {"Gürler",       "Orta Saha", "76"},
                {"Türüç",        "Orta Saha", "75"},
                {"Çolak",        "Orta Saha", "74"},
                {"Crivelli",     "Forvet",    "81"},
                {"Selke",        "Forvet",    "79"},
                {"Adekanye",     "Forvet",    "77"},
                {"Bajic",        "Orta Saha", "73"},
                {"Edomwonyi",    "Forvet",    "76"},
                {"Babacan",      "Kaleci",    "70"},
                {"Şengezer",     "Defans",    "73"},
                {"Ömer",         "Defans",    "71"},
        });

        FOOTBALL_ROSTERS.put("Sivasspor", new String[][] {
                {"Çobanoğlu",    "Kaleci",    "79"},
                {"Goutas",       "Defans",    "79"},
                {"Bamba",        "Defans",    "78"},
                {"Hafız",        "Defans",    "76"},
                {"Tekdemir M.",  "Defans",    "75"},
                {"Kamış",        "Defans",    "74"},
                {"Kayode",       "Orta Saha", "80"},
                {"Üstündağ",     "Orta Saha", "78"},
                {"Cülük",        "Orta Saha", "77"},
                {"Yusuf Erdoğan","Orta Saha", "75"},
                {"Gradel",       "Forvet",    "83"},
                {"Ziyad",        "Forvet",    "79"},
                {"Kone",         "Forvet",    "77"},
                {"Fidan",        "Orta Saha", "74"},
                {"Oğuz",         "Defans",    "71"},
                {"Akgün S.",     "Kaleci",    "70"},
                {"Atakan",       "Orta Saha", "72"},
                {"Erhan",        "Forvet",    "73"},
        });

        FOOTBALL_ROSTERS.put("Samsunspor", new String[][] {
                {"Kaan Kanak",   "Kaleci",    "78"},
                {"Manga",        "Defans",    "78"},
                {"Çekiç",        "Defans",    "76"},
                {"Recep",        "Defans",    "75"},
                {"Bilal",        "Defans",    "74"},
                {"Tayfur",       "Defans",    "73"},
                {"Khazri",       "Orta Saha", "82"},
                {"Yatabaré",     "Orta Saha", "79"},
                {"Koita",        "Orta Saha", "78"},
                {"Emre Demir",   "Orta Saha", "76"},
                {"Musa Çağıran", "Forvet",    "80"},
                {"Youssouf",     "Forvet",    "77"},
                {"Süleyman",     "Orta Saha", "75"},
                {"Rıdvan",       "Orta Saha", "74"},
                {"İbrahim",      "Forvet",    "72"},
                {"Zafer",        "Kaleci",    "69"},
                {"Mert Kaya",    "Defans",    "71"},
                {"Emir",         "Forvet",    "72"},
        });

        FOOTBALL_ROSTERS.put("Alanyaspor", new String[][] {
                {"Ruben",        "Kaleci",    "77"},
                {"Welinton",     "Defans",    "78"},
                {"Isamotu",      "Defans",    "77"},
                {"İsmail",       "Defans",    "75"},
                {"Berke",        "Defans",    "74"},
                {"Gökhan",       "Defans",    "73"},
                {"Mensah M.",    "Orta Saha", "78"},
                {"Caulker",      "Orta Saha", "77"},
                {"Fransen",      "Orta Saha", "76"},
                {"Öztürk A.",    "Orta Saha", "74"},
                {"Munir",        "Forvet",    "82"},
                {"Traoré A.",    "Forvet",    "77"},
                {"Efkan",        "Forvet",    "76"},
                {"Hakan C.",     "Orta Saha", "73"},
                {"Serhat",       "Forvet",    "72"},
                {"Yiğit K.",     "Kaleci",    "68"},
                {"Uğur",         "Orta Saha", "70"},
                {"Emir A.",      "Defans",    "71"},
        });

        FOOTBALL_ROSTERS.put("Gaziantep FK", new String[][] {
                {"Taha Yalçıner","Kaleci",    "76"},
                {"Lucas",        "Defans",    "77"},
                {"Boubacar",     "Defans",    "76"},
                {"Shengelia",    "Defans",    "75"},
                {"Serkan G.",    "Defans",    "74"},
                {"Burak D.",     "Defans",    "73"},
                {"Amavi",        "Orta Saha", "78"},
                {"Kalu",         "Orta Saha", "77"},
                {"Soner",        "Orta Saha", "75"},
                {"Halil Akbunar","Orta Saha", "74"},
                {"Olayinka",     "Forvet",    "81"},
                {"Yılmaz A.",    "Forvet",    "77"},
                {"Ayaz",         "Forvet",    "74"},
                {"Umut Bozok",   "Orta Saha", "73"},
                {"Furkan",       "Forvet",    "71"},
                {"Erkin",        "Kaleci",    "68"},
                {"Cem",          "Defans",    "70"},
                {"Bahadır",      "Orta Saha", "69"},
        });

        FOOTBALL_ROSTERS.put("Kasımpaşa", new String[][] {
                {"Erce Kardeşler","Kaleci",   "75"},
                {"Tawamba",      "Defans",    "77"},
                {"Mensah K.",    "Defans",    "76"},
                {"Çağlar",       "Defans",    "74"},
                {"Mert Ö.",      "Defans",    "73"},
                {"Mehmet Aydın", "Defans",    "72"},
                {"Diatta",       "Orta Saha", "78"},
                {"Wague",        "Orta Saha", "77"},
                {"Özcan",        "Orta Saha", "74"},
                {"Onur Kıvrak",  "Orta Saha", "72"},
                {"Bamba K.",     "Forvet",    "80"},
                {"Sidibe",       "Forvet",    "76"},
                {"Şahin",        "Forvet",    "73"},
                {"Süleyman A.",  "Orta Saha", "72"},
                {"Can",          "Forvet",    "70"},
                {"Fatih",        "Kaleci",    "67"},
                {"Emir K.",      "Defans",    "70"},
                {"Burak K.",     "Orta Saha", "69"},
        });

        FOOTBALL_ROSTERS.put("Antalyaspor", new String[][] {
                {"Faruk Bayar",  "Kaleci",    "76"},
                {"Ndiaye",       "Defans",    "77"},
                {"Vrsajevic",    "Defans",    "76"},
                {"Crespo",       "Defans",    "75"},
                {"İlhan",        "Defans",    "74"},
                {"Emre C.",      "Defans",    "73"},
                {"Fayçal",       "Orta Saha", "78"},
                {"Naidoo",       "Orta Saha", "77"},
                {"Karahan",      "Orta Saha", "74"},
                {"Emirhan",      "Orta Saha", "72"},
                {"Nwakaeme",     "Forvet",    "82"},
                {"Cengiz",       "Forvet",    "77"},
                {"Akbaba",       "Forvet",    "75"},
                {"Nazim",        "Orta Saha", "73"},
                {"Uğur A.",      "Forvet",    "71"},
                {"Serdar",       "Kaleci",    "67"},
                {"Okan",         "Defans",    "70"},
                {"Ali E.",       "Orta Saha", "69"},
        });

        FOOTBALL_ROSTERS.put("Kayserispor", new String[][] {
                {"Serkan Ateş",  "Kaleci",    "76"},
                {"Mensah D.",    "Defans",    "77"},
                {"Yıldız",       "Defans",    "76"},
                {"Atakan Y.",    "Defans",    "74"},
                {"Sven",         "Defans",    "73"},
                {"Kalaba",       "Defans",    "74"},
                {"Nunnally",     "Orta Saha", "78"},
                {"İyiola",       "Orta Saha", "77"},
                {"Özgür",        "Orta Saha", "74"},
                {"Murat Yıldırım","Orta Saha","73"},
                {"Pereira",      "Forvet",    "81"},
                {"Safranko",     "Forvet",    "77"},
                {"Doğukan",      "Forvet",    "74"},
                {"Zeki",         "Orta Saha", "72"},
                {"Hasan",        "Forvet",    "70"},
                {"Burak E.",     "Kaleci",    "67"},
                {"Talha",        "Defans",    "69"},
                {"Emre E.",      "Orta Saha", "68"},
        });

        FOOTBALL_ROSTERS.put("Çaykur Rizespor", new String[][] {
                {"Aykut",        "Kaleci",    "75"},
                {"Da Silva",     "Defans",    "76"},
                {"Fesic",        "Defans",    "75"},
                {"Faruk R.",     "Defans",    "74"},
                {"Hüseyin R.",   "Defans",    "72"},
                {"Muhammed",     "Defans",    "73"},
                {"Mabunda",      "Orta Saha", "77"},
                {"Adama",        "Orta Saha", "75"},
                {"Deniz",        "Orta Saha", "74"},
                {"Yasin R.",     "Orta Saha", "73"},
                {"Rasheed",      "Forvet",    "80"},
                {"Gueye",        "Forvet",    "76"},
                {"Akın",         "Forvet",    "73"},
                {"Tuna",         "Orta Saha", "72"},
                {"Tolga R.",     "Forvet",    "70"},
                {"Koç",          "Kaleci",    "66"},
                {"Mert R.",      "Defans",    "68"},
                {"Selim R.",     "Orta Saha", "68"},
        });

        FOOTBALL_ROSTERS.put("Ankaragücü", new String[][] {
                {"Yılmaz A.",    "Kaleci",    "76"},
                {"Mbengue",      "Defans",    "77"},
                {"Angelov",      "Defans",    "76"},
                {"Thiago A.",    "Defans",    "75"},
                {"Yasin A.",     "Defans",    "74"},
                {"Mustafa A.",   "Defans",    "73"},
                {"Coulibaly",    "Orta Saha", "78"},
                {"Anderson A.",  "Orta Saha", "77"},
                {"Korkut",       "Orta Saha", "74"},
                {"Ballı",        "Orta Saha", "73"},
                {"N'Doye",       "Forvet",    "81"},
                {"Kouao",        "Forvet",    "76"},
                {"Silas",        "Forvet",    "74"},
                {"Alıcı",        "Orta Saha", "72"},
                {"Caner A.",     "Forvet",    "70"},
                {"Sütçü",        "Kaleci",    "67"},
                {"Enes A.",      "Defans",    "70"},
                {"Emirhan A.",   "Orta Saha", "69"},
        });

        FOOTBALL_ROSTERS.put("Konyaspor", new String[][] {
                {"Bahadır K.",   "Kaleci",    "75"},
                {"Skubic",       "Defans",    "77"},
                {"Calusic",      "Defans",    "76"},
                {"Yasir",        "Defans",    "74"},
                {"Bytyqi",       "Defans",    "74"},
                {"Asan",         "Defans",    "71"},
                {"Hadziahmetovic","Orta Saha","77"},
                {"Sokol",        "Orta Saha", "75"},
                {"Knapik",       "Orta Saha", "74"},
                {"Boyalı",       "Orta Saha", "71"},
                {"Dimata",       "Forvet",    "79"},
                {"Bajic K.",     "Forvet",    "76"},
                {"Ndao",         "Forvet",    "74"},
                {"Endo",         "Orta Saha", "73"},
                {"Kravets",      "Forvet",    "73"},
                {"Kerem K.",     "Kaleci",    "69"},
                {"Marko K.",     "Defans",    "71"},
                {"Yatabaré K.",  "Orta Saha", "72"},
        });

        FOOTBALL_ROSTERS.put("Bodrum FK", new String[][] {
                {"Selçuk Özer",  "Kaleci",    "73"},
                {"Erman",        "Defans",    "74"},
                {"Ahmet B.",     "Defans",    "73"},
                {"Osman",        "Defans",    "72"},
                {"Fatih B.",     "Defans",    "72"},
                {"Cavit",        "Defans",    "71"},
                {"Arda B.",      "Orta Saha", "75"},
                {"Ufuk",         "Orta Saha", "74"},
                {"Özkan",        "Orta Saha", "73"},
                {"Tuncay",       "Orta Saha", "72"},
                {"Hamit",        "Forvet",    "78"},
                {"Sercan",       "Forvet",    "74"},
                {"Barış B.",     "Orta Saha", "72"},
                {"Volkan B2",    "Forvet",    "72"},
                {"Kemal",        "Forvet",    "68"},
                {"Hayri",        "Kaleci",    "64"},
                {"Yusuf B.",     "Defans",    "67"},
                {"Emre B.",      "Orta Saha", "66"},
        });

        FOOTBALL_ROSTERS.put("Eyüpspor", new String[][] {
                {"İrfan",        "Kaleci",    "74"},
                {"Mamadou",      "Defans",    "75"},
                {"Ferhan",       "Defans",    "74"},
                {"Ogün",         "Defans",    "73"},
                {"Doğan",        "Defans",    "72"},
                {"Sezer",        "Defans",    "71"},
                {"Diallo",       "Orta Saha", "76"},
                {"Cenk E.",      "Orta Saha", "75"},
                {"Erencan",      "Orta Saha", "73"},
                {"Kadir",        "Orta Saha", "72"},
                {"Neyou",        "Forvet",    "79"},
                {"Koita E.",     "Forvet",    "74"},
                {"Muhammed E.",  "Orta Saha", "72"},
                {"Ozan E.",      "Forvet",    "73"},
                {"Metin",        "Forvet",    "69"},
                {"Atakan E.",    "Kaleci",    "65"},
                {"Murat E.",     "Defans",    "68"},
                {"Sefa",         "Orta Saha", "67"},
        });

        FOOTBALL_ROSTERS.put("Göztepe", new String[][] {
                {"Ufuk G.",      "Kaleci",    "74"},
                {"Luiz G.",      "Defans",    "75"},
                {"Bayram",       "Defans",    "74"},
                {"Serhat G.",    "Defans",    "73"},
                {"Özgür G.",     "Defans",    "72"},
                {"Erman G.",     "Defans",    "71"},
                {"Mak",          "Orta Saha", "76"},
                {"Mensah G.",    "Orta Saha", "75"},
                {"Soner G.",     "Orta Saha", "73"},
                {"Altan",        "Orta Saha", "72"},
                {"Pajac",        "Forvet",    "79"},
                {"Manaj",        "Forvet",    "76"},
                {"Berkan",       "Forvet",    "73"},
                {"Umut G.",      "Orta Saha", "72"},
                {"Yusuf G.",     "Forvet",    "69"},
                {"Kerem G.",     "Kaleci",    "65"},
                {"Ali G.",       "Defans",    "68"},
                {"Burak G.",     "Orta Saha", "67"},
        });

        // ── Volleyball rosters — 12 players each ─────────────────────────────
        // Format: {name, position, skill}  |  6 starters + 6 bench

        VOLLEYBALL_ROSTERS.put("Vakıfbank", new String[][] {
                {"Boskovic",     "Pasör Çaprazı", "92"},
                {"Gabi",         "Smaçör",        "87"},
                {"Foluke",       "Orta Oyuncu",   "84"},
                {"Akman",        "Orta Oyuncu",   "82"},
                {"Çiçek",        "Pasör",         "84"},
                {"Aydın",        "Libero",        "79"},
                {"Pelin",        "Smaçör",        "76"},
                {"Cansu V.",     "Orta Oyuncu",   "80"},
                {"Tuğba",        "Orta Oyuncu",   "74"},
                {"Bahar",        "Pasör",         "73"},
                {"Gizem V.",     "Pasör Çaprazı", "75"},
                {"Buse V.",      "Libero",        "72"},
        });

        VOLLEYBALL_ROSTERS.put("Fenerbahçe Medicana", new String[][] {
                {"Vargas",       "Smaçör",        "93"},
                {"Arina",        "Smaçör",        "88"},
                {"Melissa",      "Pasör Çaprazı", "86"},
                {"Zehra",        "Orta Oyuncu",   "81"},
                {"Cansu Ö.",     "Pasör",         "85"},
                {"Simge",        "Libero",        "80"},
                {"Eda E.",       "Orta Oyuncu",   "83"},
                {"Tijana",       "Smaçör",        "79"},
                {"Yasemin F.",   "Pasör",         "73"},
                {"Berfu",        "Orta Oyuncu",   "74"},
                {"Sude",         "Pasör Çaprazı", "75"},
                {"Merve F.",     "Libero",        "72"},
        });

        VOLLEYBALL_ROSTERS.put("Galatasaray HDI", new String[][] {
                {"Karakurt",     "Smaçör",        "92"},
                {"Naz",          "Pasör",         "86"},
                {"Ayça",         "Pasör Çaprazı", "85"},
                {"Cansu G.",     "Orta Oyuncu",   "83"},
                {"Eda G.",       "Orta Oyuncu",   "82"},
                {"İlkin",        "Libero",        "79"},
                {"Buse G.",      "Smaçör",        "84"},
                {"Meryem",       "Smaçör",        "77"},
                {"Sıla",         "Pasör",         "74"},
                {"Hande G.",     "Orta Oyuncu",   "75"},
                {"Selin",        "Pasör Çaprazı", "76"},
                {"Burcu",        "Libero",        "73"},
        });

        VOLLEYBALL_ROSTERS.put("Eczacıbaşı Dynavit", new String[][] {
                {"Eda Erdem",    "Orta Oyuncu",   "90"},
                {"Ebrar",        "Smaçör",        "89"},
                {"Kim",          "Pasör Çaprazı", "88"},
                {"Hande E.",     "Smaçör",        "86"},
                {"Elif Şahin",   "Pasör",         "85"},
                {"Şeyda",        "Libero",        "81"},
                {"Saadet",       "Orta Oyuncu",   "84"},
                {"Kübra",        "Smaçör",        "77"},
                {"Hacer",        "Orta Oyuncu",   "75"},
                {"Zehra E.",     "Pasör",         "73"},
                {"Aybüke",       "Pasör Çaprazı", "74"},
                {"Duygu",        "Libero",        "71"},
        });

        VOLLEYBALL_ROSTERS.put("Halkbank Ankara", new String[][] {
                {"Earvin",       "Smaçör",        "88"},
                {"Adis",         "Smaçör",        "86"},
                {"Burutay",      "Pasör Çaprazı", "84"},
                {"Murat H.",     "Pasör",         "83"},
                {"Selim H.",     "Orta Oyuncu",   "81"},
                {"Eren H.",      "Libero",        "78"},
                {"Volkan H.",    "Orta Oyuncu",   "80"},
                {"Yiğit H.",     "Smaçör",        "76"},
                {"Bedirhan",     "Orta Oyuncu",   "74"},
                {"Onur H.",      "Pasör",         "72"},
                {"Berkay H.",    "Pasör Çaprazı", "74"},
                {"Mert H.",      "Libero",        "71"},
        });

        VOLLEYBALL_ROSTERS.put("Beşiktaş Akatel", new String[][] {
                {"Tatiana",      "Pasör Çaprazı", "83"},
                {"Tess",         "Smaçör",        "84"},
                {"Aslı",         "Pasör",         "81"},
                {"Beyza",        "Orta Oyuncu",   "80"},
                {"Saliha",       "Orta Oyuncu",   "78"},
                {"Beyzanur",     "Libero",        "77"},
                {"Mihriban",     "Smaçör",        "79"},
                {"Berra",        "Smaçör",        "74"},
                {"İrem",         "Pasör",         "71"},
                {"Gizem B.",     "Orta Oyuncu",   "72"},
                {"Ecem",         "Pasör Çaprazı", "73"},
                {"Damla",        "Libero",        "70"},
        });

        VOLLEYBALL_ROSTERS.put("Türk Hava Yolları", new String[][] {
                {"Marko",        "Smaçör",        "84"},
                {"Drazen",       "Smaçör",        "82"},
                {"Tomas",        "Pasör Çaprazı", "81"},
                {"Ferhat T.",    "Pasör",         "80"},
                {"Emre T.",      "Orta Oyuncu",   "79"},
                {"Tarık",        "Libero",        "77"},
                {"Mert T.",      "Orta Oyuncu",   "78"},
                {"Bertuğ",       "Smaçör",        "75"},
                {"Kaan T.",      "Pasör",         "71"},
                {"Cahit",        "Orta Oyuncu",   "73"},
                {"Yusuf T.",     "Pasör Çaprazı", "72"},
                {"Özgür T.",     "Libero",        "70"},
        });

        VOLLEYBALL_ROSTERS.put("Ziraat Bankkart", new String[][] {
                {"Plotnytski",   "Smaçör",        "84"},
                {"Cebecioğlu",   "Smaçör",        "83"},
                {"Arslan Z.",    "Pasör",          "79"},
                {"Yiğit Z.",     "Pasör Çaprazı", "81"},
                {"Mert Z.",      "Orta Oyuncu",   "80"},
                {"Hakan Z.",     "Libero",        "75"},
                {"Berkay Z.",    "Orta Oyuncu",   "78"},
                {"Doğukan",      "Smaçör",        "73"},
                {"Tolga Z.",     "Pasör",         "70"},
                {"Caner Z.",     "Orta Oyuncu",   "72"},
                {"Hüseyin Z.",   "Pasör Çaprazı", "71"},
                {"Furkan Z.",    "Libero",        "69"},
        });

        VOLLEYBALL_ROSTERS.put("Nilüfer Belediyespor", new String[][] {
                {"Zehra N.",     "Smaçör",        "81"},
                {"Esra",         "Pasör Çaprazı", "80"},
                {"Gizem N.",     "Pasör",         "79"},
                {"Özge",         "Smaçör",        "78"},
                {"Beyza N.",     "Orta Oyuncu",   "77"},
                {"Nur",          "Libero",        "75"},
                {"Yasemin N.",   "Orta Oyuncu",   "76"},
                {"Melis",        "Smaçör",        "73"},
                {"Pınar N.",     "Pasör",         "70"},
                {"Seda N.",      "Orta Oyuncu",   "71"},
                {"Dila",         "Pasör Çaprazı", "72"},
                {"Büşra N.",     "Libero",        "68"},
        });

        VOLLEYBALL_ROSTERS.put("Arkas Spor", new String[][] {
                {"Nimir",        "Pasör Çaprazı", "82"},
                {"Kamil",        "Smaçör",        "83"},
                {"Leon",         "Smaçör",        "81"},
                {"Güçlü",        "Pasör",         "80"},
                {"Doğan A.",     "Orta Oyuncu",   "79"},
                {"Yiğit A.",     "Libero",        "76"},
                {"Kerem A.",     "Orta Oyuncu",   "77"},
                {"Alp",          "Smaçör",        "74"},
                {"Burak A.",     "Pasör",         "71"},
                {"Furkan A.",    "Orta Oyuncu",   "72"},
                {"Cenk A.",      "Pasör Çaprazı", "73"},
                {"İlker",        "Libero",        "69"},
        });

        VOLLEYBALL_ROSTERS.put("İnegöl Belediyespor", new String[][] {
                {"Fatma",        "Smaçör",        "79"},
                {"Didem",        "Pasör Çaprazı", "76"},
                {"Ayşe",         "Pasör",         "77"},
                {"Hatice",       "Smaçör",        "75"},
                {"Merve İ.",     "Orta Oyuncu",   "75"},
                {"Serap",        "Libero",        "72"},
                {"Büşra İ.",     "Orta Oyuncu",   "73"},
                {"Elif İ.",      "Smaçör",        "71"},
                {"Havva",        "Pasör Çaprazı", "69"},
                {"Seda İ.",      "Pasör",         "68"},
                {"Duygu İ.",     "Orta Oyuncu",   "70"},
                {"Neslihan",     "Libero",        "66"},
        });

        VOLLEYBALL_ROSTERS.put("PTT Spor", new String[][] {
                {"İpek",         "Smaçör",        "78"},
                {"Özlem",        "Pasör Çaprazı", "75"},
                {"Ceren",        "Pasör",         "76"},
                {"Sevgi",        "Smaçör",        "74"},
                {"Tuğçe",        "Orta Oyuncu",   "74"},
                {"Nuray",        "Libero",        "71"},
                {"Gamze",        "Orta Oyuncu",   "72"},
                {"Arzu",         "Smaçör",        "69"},
                {"Feray",        "Pasör",         "67"},
                {"Gönül",        "Orta Oyuncu",   "68"},
                {"Şule",         "Pasör Çaprazı", "68"},
                {"Bengü",        "Libero",        "65"},
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
