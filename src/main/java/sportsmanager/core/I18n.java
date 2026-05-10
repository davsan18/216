package sportsmanager.core;

import java.util.HashMap;
import java.util.Map;

/** Bilingual (Turkish / English) string lookup table. Add a new string by calling put() once. */
public final class I18n {

    public enum Lang { TR, EN }

    private static Lang current = Lang.TR;
    private static final Map<String, String> TR = new HashMap<>();
    private static final Map<String, String> EN = new HashMap<>();

    private I18n() {}

    public static Lang getLanguage() { return current; }
    public static void setLanguage(Lang l) { if (l != null) current = l; }
    public static void toggleLanguage() { current = (current == Lang.TR ? Lang.EN : Lang.TR); }
    public static boolean isTr() { return current == Lang.TR; }

    /** Lookup a translated string. */
    public static String t(String key) {
        Map<String, String> map = (current == Lang.TR) ? TR : EN;
        return map.getOrDefault(key, key);
    }

    /** Lookup + format with args. */
    public static String f(String key, Object... args) {
        return String.format(t(key), args);
    }

    private static void put(String key, String tr, String en) {
        TR.put(key, tr);
        EN.put(key, en);
    }

    static {
        // ===== MAIN MENU =====
        put("main.title",         "SPOR MENAJERİ", "SPORTS MANAGER");
        put("main.selectSport",   "Lütfen antrenör olmak istediğiniz sporu seçin",
                                  "Please choose the sport you want to coach");
        put("main.football",      "⚽ FUTBOL", "⚽ FOOTBALL");
        put("main.volleyball",    "🏐 VOLEYBOL", "🏐 VOLLEYBALL");
        put("main.mySaves",       "📂 Kayıtlarım", "📂 My Saves");
        put("main.lang.label",    "🌐 Dil:", "🌐 Language:");
        put("league.name",        "%s Süper Ligi", "%s Super League");

        // ===== SAVES MANAGER =====
        put("saves.title",        "📂 KAYITLARIM", "📂 MY SAVES");
        put("saves.empty",        "boş", "empty");
        put("saves.matches",      "%d/%d maç", "%d/%d matches");
        put("saves.load",         "Yükle", "Load");
        put("saves.delete",       "Sil", "Delete");
        put("saves.deleteConfirmTitle", "Kaydı Sil", "Delete Save");
        put("saves.deleteConfirmBody",  "Bu kayıt kalıcı olarak silinecek. Emin misiniz?",
                                        "This save will be permanently deleted. Are you sure?");
        put("saves.loadOk",       "✓ Kayıt yüklendi.", "✓ Save loaded.");
        put("saves.loadError",    "Yükleme hatası: ", "Load error: ");
        put("saves.back",         "⬅ Ana Menü", "⬅ Main Menu");

        // ===== TEAM SELECT =====
        put("teamSelect.title",   "Antrenör olacağınız takımı seçin", "Pick the team you will coach");
        put("teamSelect.avg",     "Ortalama", "Average");
        put("teamSelect.players", "oyuncu", "players");
        put("teamSelect.stadium", "Stad", "Stadium");
        put("teamSelect.pick",    "Bu Takımı Yönet ▶", "Coach This Team ▶");
        put("teamSelect.back",    "⬅ Geri", "⬅ Back");

        // ===== DASHBOARD =====
        put("dash.myTeam",        "Takımım", "My Team");
        put("dash.standings",     "📊 Puan Durumu", "📊 Standings");
        put("dash.fixture",       "📅 Fikstür", "📅 Fixture");
        put("dash.injuries",      "⚕ Sakatlar", "⚕ Injuries");
        put("dash.noInjuries",    "Şu anda sakat oyuncu yok.", "No injured players right now.");
        put("dash.stats",         "📈 İstatistikler", "📈 Stats");
        put("dash.noStats",       "Henüz istatistik oluşmadı.", "No stats yet.");
        put("dash.statsGoals",    "Goller", "Goals");
        put("dash.statsPoints",   "Sayılar", "Points");
        put("dash.statsCards",    "Kartlar", "Cards");
        put("dash.statsAvailability", "Eksikler", "Missing");
        put("dash.noGoals",       "Henüz gol istatistiği yok.", "No goal stats yet.");
        put("dash.noPoints",      "Henüz sayı istatistiği yok.", "No point stats yet.");
        put("dash.noCards",       "Henüz kart istatistiği yok.", "No card stats yet.");
        put("dash.noAvailability","Şu anda sakat veya cezalı oyuncu yok.", "No injured or suspended players right now.");
        put("dash.standingsHeader","Takım", "Team");
        put("dash.points",        "Puan", "Pts");
        put("dash.previewBtn",    "▶ Maç Önizleme", "▶ Match Preview");
        put("dash.newSeasonBtn",  "🔄 Tekrar Oyna (Yeni Sezon)", "🔄 Play Again (New Season)");
        put("dash.nextMatch",     "Sıradaki Maç (Hafta %d, %s):  %s  vs  %s%s",
                                  "Next Match (Week %d, %s):  %s  vs  %s%s");
        put("dash.home",          "  (İÇ SAHA)", "  (HOME)");
        put("dash.away",          "  (DEPLASMAN)", "  (AWAY)");
        put("dash.seasonDone",    "✓ Sezon tamamlandı!", "✓ Season finished!");
        put("dash.champion",      "  ·  Şampiyon: %s", "  ·  Champion: %s");
        put("dash.ready",         "Hazır.", "Ready.");
        put("dash.save",          "💾 Kaydet", "💾 Save");
        put("dash.mainMenuBtn",   "⬅ Ana Menü", "⬅ Main Menu");
        put("dash.confirmExitTitle","Ana menüye dön", "Return to main menu");
        put("dash.confirmExitBody", "Ana menüye dönülecek. Kaydedilmemiş ilerleme kaybolacak. Devam edilsin mi?",
                                    "Returning to main menu. Unsaved progress will be lost. Continue?");
        put("champ.title",        "ŞAMPİYON!", "CHAMPIONS!");
        put("champ.body",         "%s sezonu zirvede bitirdi. Harika iş, hocam!", "%s finished the season on top. Brilliant work, coach!");
        put("champ.continue",     "Devam", "Continue");

        // ===== FIXTURE =====
        put("fixture.weekHeader", "═══  Hafta %d  ═══", "═══  Week %d  ═══");
        put("fixture.noMatches",  "Henüz oynanmış maç yok.", "No matches played yet.");

        // ===== PRE-MATCH =====
        put("pm.title",           "Maç Önizlemesi  ·  Hafta %d", "Match Preview  ·  Week %d");
        put("pm.host",            "⌂ %s ev sahibi", "⌂ %s is the host");
        put("pm.stadium",         "🏟 Stad: %s", "🏟 Stadium: %s");
        put("pm.startingFootball","İlk 11", "Starting XI");
        put("pm.startingVolley",  "İlk 6", "Starting 6");
        put("pm.bench",           "Yedekler", "Bench");
        put("pm.unavailable",     "Eksikler", "Missing");
        put("pm.noMissing",       "Eksik yok", "No missing players");
        put("pm.formation",       "Diziliş", "Formation");
        put("pm.discipline",      "Disiplin", "Discipline");
        put("pm.applyPlan",       "Planı Uygula", "Apply Plan");
        put("pm.swap",            "Oyuncu Değiştir", "Swap Players");
        put("pm.swapFailed",      "Bu değişiklik yapılamaz. Futbolda sahada mutlaka kaleci olmalı.",
                                  "This change is not allowed. Football lineups must keep a goalkeeper on the field.");
        put("pm.back",            "⬅ Geri", "⬅ Back");
        put("pm.startMatch",      "▶ Maça Başla", "▶ Start Match");

        // ===== MATCH VIEW =====
        put("mv.leave",           "⬅ Maçtan Çık", "⬅ Leave Match");
        put("mv.fullTime",        "Maç Bitti", "Full Time");
        put("mv.events",          "📜 Maç Olayları", "📜 Match Events");
        put("mv.squads",          "🧑‍🤝‍🧑 Kadrolar", "🧑‍🤝‍🧑 Squads");
        put("mv.ourSquad",        "Bizim Kadro", "Our Squad");
        put("mv.oppSquad",        "Rakip Kadro", "Opponent Squad");
        put("mv.formation",       "Diziliş", "Formation");
        put("mv.startingFootball","İlk 11", "Starting XI");
        put("mv.startingVolley",  "İlk 6", "Starting 6");
        put("mv.startingFootballOpp","İlk 11 (rakip)", "Starting XI (opp.)");
        put("mv.startingVolleyOpp",  "İlk 6 (rakip)", "Starting 6 (opp.)");
        put("mv.bench",           "Yedekler", "Bench");
        put("mv.benchOpp",        "Yedekler (rakip)", "Bench (opp.)");
        put("mv.unavailable",     "Çıkanlar", "Exited");
        put("mv.quickFinish",     "⏭ Hızlı Bitir", "⏭ Quick Finish");
        put("mv.startSecondHalf", "▶ 2. Yarıya Başla", "▶ Start Second Half");
        put("mv.startNextSet",    "▶ %d. Sete Geç", "▶ Start Set %d");
        put("mv.substitute",      "🔁 Oyuncu Değişikliği", "🔁 Substitution");
        put("mv.substituteWithLimit", "🔁 Oyuncu Değişikliği %d/%d", "🔁 Substitution %d/%d");
        put("mv.continueBtn",     "✓ Devam (Panele Dön)", "✓ Continue (Back to Dashboard)");
        put("mv.subsBadge",       "🔁 Değişiklik %d/%d", "🔁 Subs %d/%d");
        put("mv.confirmLeaveTitle","Maçtan çık", "Leave match");
        put("mv.confirmLeaveBody", "Maçtan çıkarsanız kalan kısım otomatik oynanır. Emin misiniz?",
                                   "If you leave, the rest of the match will be auto-played. Sure?");

        // ===== SUBSTITUTION DIALOG =====
        put("sub.dialogTitle",    "Oyuncu Değişikliği", "Substitution");
        put("sub.forcedTitle",    "Zorunlu Değişiklik", "Forced Substitution");
        put("sub.pickBoth",       "Çıkacak ve girecek oyuncuyu seçin.", "Pick the outgoing and incoming players.");
        put("sub.forcedNamed",    "⚕ %s (%s, %d) sahadan çıktı. Yerine kim girsin?",
                                  "⚕ %s (%s, %d) left the field. Who comes on?");
        put("sub.forcedAnon",     "Bir oyuncu çıktı. Yerine kim girsin?",
                                  "A player left the field. Who comes on?");
        put("sub.remaining",      "Kalan değişiklik: %d/%d", "Subs remaining: %d/%d");
        put("sub.outHeaderFootball","İlk 11", "Starting XI");
        put("sub.outHeaderVolley",  "İlk 6", "Starting 6");
        put("sub.inHeader",       "Yedekler", "Bench");
        put("sub.skip",           "Şimdilik Geç", "Skip for now");
        put("sub.cancel",         "İptal", "Cancel");
        put("sub.apply",          "Değişikliği Yap", "Apply Substitution");
        put("sub.failed",         "Değişiklik yapılamadı (seçim eksik veya limit doldu).",
                                  "Substitution failed (incomplete selection or limit reached).");
        put("sub.noBench",        "Yedek oyuncu yok.", "No bench players.");
        put("sub.noField",        "Sahada oyuncu yok.", "No field players.");

        // ===== SAVE DIALOG =====
        put("save.dialogTitle",   "Kayıt Slotu Seç", "Pick Save Slot");
        put("save.header",        "Kayıt Slotu Seç (%s)", "Pick Save Slot (%s)");
        put("save.nameLabel",     "Kayıt Adı:", "Save Name:");
        put("save.namePrompt",    "örn. Şampiyonluk Kovalama", "e.g. Title Chase");
        put("save.cancel",        "İptal", "Cancel");
        put("save.btn",           "Kaydet", "Save");
        put("save.overwriteTitle","Üzerine yaz", "Overwrite");
        put("save.overwriteBody", "Slot %d dolu. Üzerine yazılsın mı?", "Slot %d is full. Overwrite?");
        put("save.ok",            "✓ '%s' Slot %d'e kaydedildi.", "✓ '%s' saved to slot %d.");
        put("save.fail",          "✗ Kaydetme hatası: %s", "✗ Save error: %s");
        put("save.unnamed",       "(adsız)", "(unnamed)");
        put("save.empty",         "boş", "empty");
        put("save.sportFootball", "Futbol", "Football");
        put("save.sportVolleyball","Voleybol", "Volleyball");

        // ===== MATCH EVENT TEXTS =====
        put("ev.kickoffFootball",  "Maç başladı: %s - %s", "Kick-off: %s - %s");
        put("ev.kickoffVolley",    "Set 1 başladı: %s - %s", "Set 1 starts: %s - %s");
        put("ev.goalFirst",        "GOL! %s (%s)  →  %d-%d", "GOAL! %s (%s)  →  %d-%d");
        put("ev.goalNth",          "GOL! %s (%d. golü) (%s)  →  %d-%d", "GOAL! %s (goal #%d) (%s)  →  %d-%d");
        put("ev.yellowCard",       "Sarı kart: %s (%s)", "Yellow card: %s (%s)");
        put("ev.secondYellow",     "İkinci sarı: %s", "Second yellow: %s");
        put("ev.redOff",           "KIRMIZI! %s oyundan atıldı (%s)", "RED! %s sent off (%s)");
        put("ev.directRed",        "DİREKT KIRMIZI! %s (%s) — 1 maç ceza",
                                   "DIRECT RED! %s (%s) — 1-match suspension");
        put("ev.yellowSuspension", "%s %d. sarı kartını gördü — 1 maç ceza",
                                   "%s received yellow card #%d — 1-match suspension");
        put("ev.injury",           "SAKATLIK: %s (%s) oyundan çıkıyor", "INJURY: %s (%s) leaves the field");
        put("ev.subSwap",          "Değişiklik: %s ↔ %s (%s)", "Substitution: %s ↔ %s (%s)");
        put("ev.subIn",            "%s oyuna girdi (%s)", "%s came on (%s)");
        put("ev.stoppage",         "Hakem %d dakika uzatma verdi", "Referee added %d minutes of stoppage time");
        put("ev.halftime",         "İlk yarı sonu (%d-%d)", "Half-time (%d-%d)");
        put("ev.fulltimeWin",      "Maç bitti: %s %d-%d %s — %s kazandı",
                                   "Full-time: %s %d-%d %s — %s won");
        put("ev.fulltimeDraw",     "Maç bitti: %s %d-%d %s — Beraberlik",
                                   "Full-time: %s %d-%d %s — Draw");
        put("ev.scorePoint",       "Sayı: %s (%s)", "Point: %s (%s)");
        put("ev.setEnd",           "Set %d bitti: %d-%d  (Setler: %d-%d)",
                                   "Set %d ended: %d-%d  (Sets: %d-%d)");
        put("ev.setStart",         "Set %d başlıyor", "Set %d starts");
        put("ev.matchEnd",         "Maç bitti: %s %d-%d %s — %s kazandı",
                                   "Match ended: %s %d-%d %s — %s won");
        put("ev.redPenaltyVolley", "Kırmızı kart: %s (%s) — rakip takıma ceza puanı",
                                   "Red card: %s (%s) — point penalty awarded to opponent");
        put("ev.yellowWarnVolley", "Sarı kart: %s (%s) — uyarı",
                                   "Yellow card: %s (%s) — warning");

        // ===== PLAYER STATUS =====
        put("status.injured",      "sakat", "injured");
        put("status.suspendedShort","cezalı", "suspended");
        put("status.suspended",    "kart cezalısı (%d maç)", "suspended (%d match)");
        put("status.exited",       "oyundan çıktı", "left the game");
        put("stats.goalShort",     "GOL", "GOAL");
        put("stats.pointShort",    "SAYI", "PTS");
    }
}
