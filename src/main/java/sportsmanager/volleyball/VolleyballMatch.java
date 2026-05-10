package sportsmanager.volleyball;

import sportsmanager.core.AbstractMatch;
import sportsmanager.core.IPlayer;
import sportsmanager.core.ITeam;
import sportsmanager.core.MatchEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class VolleyballMatch extends AbstractMatch {
    private static final long serialVersionUID = 2L;
    private static final int SETS_TO_WIN = 3;
    private static final int MAX_SETS = 5;

    private int homeSetsWon = 0;
    private int awaySetsWon = 0;
    private int currentSet = 1;
    private int homeSetPoints = 0;
    private int awaySetPoints = 0;

    public VolleyballMatch(ITeam home, ITeam away) {
        super(home, away);
    }

    @Override public int getStartingSize() { return 6; }
    @Override public int getMaxSubs() { return 6; }
    @Override public String getClockDisplay() {
        if (!started) return "S1 0-0";
        if (played) return homeSetsWon + "-" + awaySetsWon + " (set)";
        return "S" + currentSet + " " + homeSetPoints + "-" + awaySetPoints;
    }
    @Override protected String getKickoffLabel() { return "Set 1"; }
    @Override protected int getAutoChunk() { return 8; }

    @Override
    public void play() {
        if (!started) start();
        tickToEnd();
    }

    @Override
    public List<MatchEvent> tick(int amount) {
        if (!started) start();
        if (rand == null) rand = new Random();
        List<MatchEvent> chunk = new ArrayList<>();
        if (played || isPaused()) return chunk;

        int startIdx = events.size();
        for (int i = 0; i < amount && !played; i++) {
            playRally();
            autoResolveOpponentSubs();
            if (isSetOver()) endSet();
            if (played) break;
            if (isPaused()) break;
        }
        chunk.addAll(events.subList(startIdx, events.size()));
        return chunk;
    }

    private void playRally() {
        int homePower = computePower(homeTeam, true);
        int awayPower = computePower(awayTeam, false);
        double homeProb = (double) homePower / Math.max(1, homePower + awayPower);

        boolean homePoint;
        // Random card check first — red card gives the OTHER team a point
        IPlayer carded = maybeCard(homeTeam);
        if (carded != null && carded.hasRedCard()) {
            // Penalty: away gets the point
            awaySetPoints++;
            events.add(new MatchEvent(MatchEvent.Type.RED_CARD, getClockDisplay(), homeTeam, carded, null,
                    "Kırmızı kart: " + carded.getName() + " (" + homeTeam.getName()
                    + ") — rakip takıma ceza puanı"));
            return;
        }
        carded = maybeCard(awayTeam);
        if (carded != null && carded.hasRedCard()) {
            homeSetPoints++;
            events.add(new MatchEvent(MatchEvent.Type.RED_CARD, getClockDisplay(), awayTeam, carded, null,
                    "Kırmızı kart: " + carded.getName() + " (" + awayTeam.getName()
                    + ") — rakip takıma ceza puanı"));
            return;
        }

        // Check injuries
        if (rand.nextDouble() < 0.004) {
            ITeam t = rand.nextBoolean() ? homeTeam : awayTeam;
            TeamState s = stateOf(t);
            if (!s.onField.isEmpty()) {
                IPlayer p = pickRandom(s.onField);
                p.setInjured(true);
                events.add(new MatchEvent(MatchEvent.Type.INJURY, getClockDisplay(), t, p, null,
                        "SAKATLIK: " + p.getName() + " (" + t.getName() + ") oyundan çıkıyor"));
                removeFromField(t, p, true);
            }
        }

        // Decide who wins the rally
        homePoint = rand.nextDouble() < homeProb;
        if (homePoint) {
            homeSetPoints++;
            IPlayer scorer = pickScorer(homeTeam);
            if (scorer != null) {
                scorer.addGoalThisMatch(getClockDisplay());
                events.add(new MatchEvent(MatchEvent.Type.GOAL, getClockDisplay(), homeTeam, scorer, null,
                        "Sayı: " + scorer.getName() + " (" + homeTeam.getName() + ")"));
            }
        } else {
            awaySetPoints++;
            IPlayer scorer = pickScorer(awayTeam);
            if (scorer != null) {
                scorer.addGoalThisMatch(getClockDisplay());
                events.add(new MatchEvent(MatchEvent.Type.GOAL, getClockDisplay(), awayTeam, scorer, null,
                        "Sayı: " + scorer.getName() + " (" + awayTeam.getName() + ")"));
            }
        }
    }

    /** ~1.5% per team per rally. Returns the carded player (with red flag set if escalated). */
    private IPlayer maybeCard(ITeam team) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return null;
        if (rand.nextDouble() < 0.012) {
            IPlayer p = pickRandom(s.onField);
            p.addYellowCard();
            if (p.getYellowCards() >= 2) {
                p.giveRedCard();
                events.add(new MatchEvent(MatchEvent.Type.YELLOW_CARD, getClockDisplay(), team, p, null,
                        "İkinci sarı: " + p.getName()));
                return p;
            }
            events.add(new MatchEvent(MatchEvent.Type.YELLOW_CARD, getClockDisplay(), team, p, null,
                    "Sarı kart: " + p.getName() + " (" + team.getName() + ") — uyarı"));
        }
        return null;
    }

    private boolean isSetOver() {
        int target = (currentSet == 5) ? 15 : 25;
        if (homeSetPoints >= target && homeSetPoints - awaySetPoints >= 2) return true;
        if (awaySetPoints >= target && awaySetPoints - homeSetPoints >= 2) return true;
        return false;
    }

    private void endSet() {
        boolean homeWon = homeSetPoints > awaySetPoints;
        if (homeWon) homeSetsWon++; else awaySetsWon++;
        events.add(new MatchEvent(MatchEvent.Type.SET_END, getClockDisplay(), null, null, null,
                "Set " + currentSet + " bitti: " + homeSetPoints + "-" + awaySetPoints
                + "  (Setler: " + homeSetsWon + "-" + awaySetsWon + ")"));

        if (homeSetsWon == SETS_TO_WIN || awaySetsWon == SETS_TO_WIN || currentSet == MAX_SETS) {
            finish();
            return;
        }
        currentSet++;
        homeSetPoints = 0;
        awaySetPoints = 0;
        events.add(new MatchEvent(MatchEvent.Type.SET_START, getClockDisplay(), null, null, null,
                "Set " + currentSet + " başlıyor"));
    }

    private void finish() {
        played = true;
        homeScore = homeSetsWon;
        awayScore = awaySetsWon;
        if (homeSetsWon > awaySetsWon) winner = homeTeam;
        else winner = awayTeam;
        awardLeaguePoints();
        events.add(new MatchEvent(MatchEvent.Type.MATCH_END, getClockDisplay(), null, null, null,
                "Maç bitti: " + homeTeam.getName() + " " + homeSetsWon + "-" + awaySetsWon + " "
                + awayTeam.getName() + " — " + winner.getName() + " kazandı"));
    }

    @Override
    protected void awardLeaguePoints() {
        boolean fiveSets = (homeSetsWon + awaySetsWon) == 5;
        if (winner == homeTeam) {
            homeTeam.addPoints(fiveSets ? 2 : 3);
            if (fiveSets) awayTeam.addPoints(1);
        } else if (winner == awayTeam) {
            awayTeam.addPoints(fiveSets ? 2 : 3);
            if (fiveSets) homeTeam.addPoints(1);
        }
    }

    private int computePower(ITeam team, boolean isHome) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return 1;
        int sum = 0;
        for (IPlayer p : s.onField) sum += p.getSkillLevel();
        int avg = sum / s.onField.size();
        int homeAdv = isHome ? 4 : 0;
        int sizePenalty = Math.max(0, 6 - s.onField.size()) * 5;
        return Math.max(1, avg + homeAdv - sizePenalty);
    }

    private IPlayer pickRandom(Set<IPlayer> pool) {
        int idx = rand.nextInt(pool.size());
        Iterator<IPlayer> it = pool.iterator();
        for (int i = 0; i < idx; i++) it.next();
        return it.next();
    }

    private IPlayer pickScorer(ITeam team) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return null;
        // Skill weighted; bonus for spikers (Smaçör/Pasör/Forvet)
        int total = 0;
        for (IPlayer p : s.onField) total += weight(p);
        if (total <= 0) return pickRandom(s.onField);
        int r = rand.nextInt(total);
        int acc = 0;
        for (IPlayer p : s.onField) {
            acc += weight(p);
            if (r < acc) return p;
        }
        return pickRandom(s.onField);
    }

    private int weight(IPlayer p) {
        int w = p.getSkillLevel();
        String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
        if (pos.contains("smaç") || pos.contains("forvet")) w += 25;
        else if (pos.contains("pasör")) w += 10;
        else if (pos.contains("libero")) w = Math.max(1, w - 30);
        return Math.max(1, w);
    }
}
