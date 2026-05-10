package sportsmanager.football;

import sportsmanager.core.AbstractMatch;
import sportsmanager.core.IPlayer;
import sportsmanager.core.ITeam;
import sportsmanager.core.MatchEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FootballMatch extends AbstractMatch {
    private static final long serialVersionUID = 3L;

    private int regulationMinute = 0;     // 0..90
    private int stoppageMinute = 0;       // counted within current half's stoppage
    private int firstHalfStoppage = -1;   // computed once at end of 1st half
    private int secondHalfStoppage = -1;  // computed once at end of 2nd half
    private boolean inStoppage = false;
    private boolean halftimeReported = false;

    public FootballMatch(ITeam home, ITeam away) {
        super(home, away);
    }

    @Override public int getStartingSize() { return 11; }
    @Override public int getMaxSubs() { return 5; }

    @Override
    public String getClockDisplay() {
        if (!started) return "0'";
        if (played) return "MS";
        if (inStoppage && regulationMinute == 45) return "45+" + stoppageMinute + "'";
        if (inStoppage && regulationMinute == 90) return "90+" + stoppageMinute + "'";
        return regulationMinute + "'";
    }

    @Override protected String getKickoffLabel() { return "Maç"; }
    @Override protected int getAutoChunk() { return 15; }

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
            boolean playedMinute = advanceOneMinute();
            if (played) break;
            if (playedMinute) {
                simulateMinute(homeTeam, true);
                autoResolveOpponentSubs();
                if (!played && !isPaused()) {
                    simulateMinute(awayTeam, false);
                    autoResolveOpponentSubs();
                }
            }
            if (isPaused()) break;
        }
        chunk.addAll(events.subList(startIdx, events.size()));
        return chunk;
    }

    /** Advances one minute of the clock; returns true if events should be simulated for this minute. */
    private boolean advanceOneMinute() {
        if (regulationMinute < 45) {
            regulationMinute++;
            inStoppage = false;
            return true;
        }
        if (regulationMinute == 45 && !halftimeReported) {
            if (firstHalfStoppage < 0) {
                firstHalfStoppage = 1 + rand.nextInt(4); // 1-4 dk
                events.add(new MatchEvent(MatchEvent.Type.HALFTIME, "45'",
                        null, null, null, "Hakem " + firstHalfStoppage + " dakika uzatma verdi"));
            }
            if (stoppageMinute < firstHalfStoppage) {
                stoppageMinute++;
                inStoppage = true;
                return true;
            }
            // Halftime
            events.add(new MatchEvent(MatchEvent.Type.HALFTIME, getClockDisplay(),
                    null, null, null, "İlk yarı sonu (" + homeScore + "-" + awayScore + ")"));
            halftimeReported = true;
            stoppageMinute = 0;
            inStoppage = false;
            return false;
        }
        if (regulationMinute < 90) {
            regulationMinute++;
            inStoppage = false;
            return true;
        }
        if (regulationMinute == 90) {
            if (secondHalfStoppage < 0) {
                secondHalfStoppage = 2 + rand.nextInt(5); // 2-6 dk
                events.add(new MatchEvent(MatchEvent.Type.HALFTIME, "90'",
                        null, null, null, "Hakem " + secondHalfStoppage + " dakika uzatma verdi"));
            }
            if (stoppageMinute < secondHalfStoppage) {
                stoppageMinute++;
                inStoppage = true;
                return true;
            }
            finish();
            return false;
        }
        return false;
    }

    private void simulateMinute(ITeam team, boolean isHome) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return;

        int ourPower = computePower(team, isHome);
        int oppPower = computePower(isHome ? awayTeam : homeTeam, !isHome);
        double ratio = (double) ourPower / Math.max(1, ourPower + oppPower);

        if (rand.nextDouble() < 0.045 * ratio * 1.4) {
            IPlayer scorer = pickScorer(s.onField);
            scorer.addGoalThisMatch(getClockDisplay());
            if (isHome) homeScore++; else awayScore++;
            String goals = scorer.getGoalsThisMatch() > 1
                    ? " (" + scorer.getGoalsThisMatch() + ". golü)" : "";
            events.add(new MatchEvent(MatchEvent.Type.GOAL, getClockDisplay(), team, scorer, null,
                    "GOL! " + scorer.getName() + goals + " (" + team.getName() + ")  →  "
                            + homeScore + "-" + awayScore));
        }
        if (rand.nextDouble() < 0.018) {
            IPlayer p = pickRandom(s.onField);
            p.addYellowCard();
            if (p.getYellowCards() >= 2) {
                p.giveRedCard();
                events.add(new MatchEvent(MatchEvent.Type.YELLOW_CARD, getClockDisplay(), team, p, null,
                        "İkinci sarı: " + p.getName()));
                events.add(new MatchEvent(MatchEvent.Type.RED_CARD, getClockDisplay(), team, p, null,
                        "KIRMIZI! " + p.getName() + " oyundan atıldı (" + team.getName() + ")"));
                removeFromField(team, p, false);
            } else {
                events.add(new MatchEvent(MatchEvent.Type.YELLOW_CARD, getClockDisplay(), team, p, null,
                        "Sarı kart: " + p.getName() + " (" + team.getName() + ")"));
            }
        }
        if (rand.nextDouble() < 0.0015) {
            IPlayer p = pickRandom(s.onField);
            p.giveRedCard();
            events.add(new MatchEvent(MatchEvent.Type.RED_CARD, getClockDisplay(), team, p, null,
                    "DİREKT KIRMIZI! " + p.getName() + " (" + team.getName() + ")"));
            removeFromField(team, p, false);
        }
        if (rand.nextDouble() < 0.005) {
            IPlayer p = pickRandom(s.onField);
            p.setInjured(true);
            events.add(new MatchEvent(MatchEvent.Type.INJURY, getClockDisplay(), team, p, null,
                    "SAKATLIK: " + p.getName() + " (" + team.getName() + ") oyundan çıkıyor"));
            removeFromField(team, p, true);
        }
    }

    private int computePower(ITeam team, boolean isHome) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return 1;
        int sum = 0;
        for (IPlayer p : s.onField) sum += p.getSkillLevel();
        int avg = sum / s.onField.size();
        int homeAdv = isHome ? 8 : 0;            // İç saha avantajı
        int sizePenalty = Math.max(0, 11 - s.onField.size()) * 4;
        return Math.max(1, avg + homeAdv - sizePenalty);
    }

    private IPlayer pickRandom(Set<IPlayer> pool) {
        int idx = rand.nextInt(pool.size());
        Iterator<IPlayer> it = pool.iterator();
        for (int i = 0; i < idx; i++) it.next();
        return it.next();
    }

    private IPlayer pickScorer(Set<IPlayer> pool) {
        int total = 0;
        for (IPlayer p : pool) total += weight(p);
        if (total <= 0) return pickRandom(pool);
        int r = rand.nextInt(total);
        int acc = 0;
        for (IPlayer p : pool) {
            acc += weight(p);
            if (r < acc) return p;
        }
        return pickRandom(pool);
    }

    private int weight(IPlayer p) {
        int w = p.getSkillLevel();
        String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
        if (pos.contains("forvet") || pos.contains("santrafor")) w += 30;
        else if (pos.contains("orta")) w += 10;
        else if (pos.contains("kale")) w = Math.max(1, w - 60);
        return Math.max(1, w);
    }

    private void finish() {
        played = true;
        if (homeScore > awayScore) winner = homeTeam;
        else if (awayScore > homeScore) winner = awayTeam;
        else winner = null;
        awardLeaguePoints();
        String w = (winner == null) ? "Beraberlik" : (winner.getName() + " kazandı");
        events.add(new MatchEvent(MatchEvent.Type.FULLTIME, getClockDisplay(), null, null, null,
                "Maç bitti: " + homeTeam.getName() + " " + homeScore + "-" + awayScore + " "
                        + awayTeam.getName() + " — " + w));
    }

    @Override
    protected void awardLeaguePoints() {
        if (winner == homeTeam) homeTeam.addPoints(3);
        else if (winner == awayTeam) awayTeam.addPoints(3);
        else { homeTeam.addPoints(1); awayTeam.addPoints(1); }
    }
}
