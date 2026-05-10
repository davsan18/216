package sportsmanager.football;

import sportsmanager.core.AbstractMatch;
import sportsmanager.core.I18n;
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
    private boolean waitingForSecondHalf = false;

    public FootballMatch(ITeam home, ITeam away) {
        super(home, away);
    }

    @Override public int getStartingSize() { return 11; }
    @Override public int getMaxSubs() { return 5; }

    @Override
    public String getClockDisplay() {
        if (!started) return "0'";
        if (played) return I18n.isTr() ? "MS" : "FT";
        if (inStoppage && regulationMinute == 45) return "45+" + stoppageMinute + "'";
        if (inStoppage && regulationMinute == 90) return "90+" + stoppageMinute + "'";
        return regulationMinute + "'";
    }

    @Override protected int getAutoChunk() { return 15; }

    @Override
    protected void setupLineup(ITeam team, TeamState state) {
        state.onField.clear();
        state.bench.clear();
        state.removed.clear();
        state.missing.clear();
        state.suspended.clear();
        state.subsUsed = 0;
        state.pendingReplacements = 0;

        List<IPlayer> available = new ArrayList<>();
        for (IPlayer p : team.getPlayers()) {
            p.resetMatchCards();
            if (p.isInjured() && Math.random() < 0.35) p.setInjured(false);
            if (p.isSuspended()) {
                state.suspended.add(p);
                state.missing.add(p);
            } else if (p.isInjured()) {
                state.missing.add(p);
            } else {
                available.add(p);
            }
        }

        IPlayer goalkeeper = null;
        for (IPlayer p : available) {
            if (isGoalkeeper(p)) {
                goalkeeper = p;
                break;
            }
        }
        if (goalkeeper == null) {
            goalkeeper = recoverEmergencyGoalkeeper(team, state, available);
        }
        if (goalkeeper != null) state.onField.add(goalkeeper);
        for (IPlayer p : available) {
            if (p == goalkeeper) continue;
            if (state.onField.size() < getStartingSize()) state.onField.add(p);
            else state.bench.add(p);
        }
    }

    @Override
    protected void addKickoffEvent() {
        events.add(new MatchEvent(MatchEvent.Type.KICKOFF, getClockDisplay(),
                null, null, null,
                I18n.f("ev.kickoffFootball", homeTeam.getName(), awayTeam.getName())));
    }

    @Override
    public void play() {
        if (!started) start();
        tickToEnd();
    }

    @Override
    public boolean isPaused() {
        return waitingForSecondHalf || super.isPaused();
    }

    @Override
    public boolean isWaitingForSecondHalf() {
        return waitingForSecondHalf;
    }

    @Override
    public void startSecondHalf() {
        waitingForSecondHalf = false;
    }

    @Override
    public List<MatchEvent> tickToEnd() {
        List<MatchEvent> all = new ArrayList<>();
        if (!started) start();
        int safety = 10000;
        while (!played) {
            if (waitingForSecondHalf) startSecondHalf();
            if (super.isPaused()) autoResolveSubs();
            List<MatchEvent> chunk = tick(getAutoChunk());
            all.addAll(chunk);
            if (--safety <= 0) break;
        }
        return all;
    }

    @Override
    public boolean swapLineup(ITeam team, IPlayer out, IPlayer in) {
        TeamState s = stateOf(team);
        if (out == null || in == null) return false;
        if (!s.onField.contains(out) || !s.bench.contains(in)) return false;
        List<IPlayer> candidate = new ArrayList<>(s.onField);
        int idx = candidate.indexOf(out);
        candidate.set(idx, in);
        if (!hasGoalkeeper(candidate)) return false;
        return super.swapLineup(team, out, in);
    }

    @Override
    public boolean substitute(ITeam team, IPlayer out, IPlayer in) {
        TeamState s = stateOf(team);
        if (out == null || in == null) return false;
        if (!s.onField.contains(out) || !s.bench.contains(in)) return false;
        if (!keepsGoalkeeper(s, out, in)) return false;
        return super.substitute(team, out, in);
    }

    @Override
    public boolean replace(ITeam team, IPlayer in) {
        TeamState s = stateOf(team);
        if (!hasGoalkeeper(s.onField) && !isGoalkeeper(in)) return false;
        return super.replace(team, in);
    }

    @Override
    protected void autoResolveFor(ITeam team) {
        TeamState s = stateOf(team);
        while (s.pendingReplacements > 0 && !s.bench.isEmpty() && s.subsUsed < getMaxSubs()) {
            IPlayer best = pickBestBench(s.bench, !hasGoalkeeper(s.onField));
            if (best == null) break;
            replace(team, best);
        }
        if (s.pendingReplacements > 0) {
            s.pendingReplacements = 0;
        }
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
                firstHalfStoppage = 1 + rand.nextInt(4); // 1-4 min
                events.add(new MatchEvent(MatchEvent.Type.HALFTIME, "45'",
                        null, null, null, I18n.f("ev.stoppage", firstHalfStoppage)));
            }
            if (stoppageMinute < firstHalfStoppage) {
                stoppageMinute++;
                inStoppage = true;
                return true;
            }
            // Halftime
            events.add(new MatchEvent(MatchEvent.Type.HALFTIME, getClockDisplay(),
                    null, null, null, I18n.f("ev.halftime", homeScore, awayScore)));
            halftimeReported = true;
            waitingForSecondHalf = true;
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
                secondHalfStoppage = 2 + rand.nextInt(5); // 2-6 min
                events.add(new MatchEvent(MatchEvent.Type.HALFTIME, "90'",
                        null, null, null, I18n.f("ev.stoppage", secondHalfStoppage)));
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
            // VAR offside check: 8% of goals get reviewed; 20% of those are overturned.
            if (rand.nextDouble() < 0.08) {
                events.add(new MatchEvent(MatchEvent.Type.VAR_CHECK, getClockDisplay(), team, scorer, null,
                        I18n.f("ev.varOffside", team.getName())));
                if (rand.nextDouble() < 0.20) {
                    events.add(new MatchEvent(MatchEvent.Type.VAR_CHECK, getClockDisplay(), team, scorer, null,
                            I18n.t("ev.varGoalDisallowed")));
                    // Goal disallowed; stop here, no other minute events for this team.
                    return;
                }
            }
            scorer.addGoalThisMatch(getClockDisplay());
            if (isHome) homeScore++; else awayScore++;
            String text;
            if (scorer.getGoalsThisMatch() > 1) {
                text = I18n.f("ev.goalNth", scorer.getName(), scorer.getGoalsThisMatch(),
                        team.getName(), homeScore, awayScore);
            } else {
                text = I18n.f("ev.goalFirst", scorer.getName(), team.getName(), homeScore, awayScore);
            }
            events.add(new MatchEvent(MatchEvent.Type.GOAL, getClockDisplay(), team, scorer, null, text));
            scorer.addSeasonGoal();
        }
        // Free-kick attempts (~2.5% per team per minute, scaled by ratio).
        if (rand.nextDouble() < 0.025 * ratio) {
            attemptFreeKick(team, isHome);
        }
        // Penalty attempts (~0.4% per team per minute). Half of these go through VAR first.
        if (rand.nextDouble() < 0.004 * ratio) {
            boolean viaVar = rand.nextBoolean();
            if (viaVar) {
                IPlayer fouled = pickRandom(s.onField);
                String key = rand.nextBoolean() ? "ev.varHandball" : "ev.varFoul";
                events.add(new MatchEvent(MatchEvent.Type.VAR_CHECK, getClockDisplay(), team, fouled, null,
                        I18n.f(key, fouled.getName(), team.getName())));
                if (rand.nextDouble() < 0.50) {
                    events.add(new MatchEvent(MatchEvent.Type.VAR_CHECK, getClockDisplay(), team, null, null,
                            I18n.t("ev.varNoAction")));
                } else {
                    attemptPenalty(team, isHome);
                }
            } else {
                attemptPenalty(team, isHome);
            }
        }
        // Random VAR review (handball/dangerous tackle): 0.3% per team per minute.
        if (rand.nextDouble() < 0.003) {
            IPlayer p = pickRandom(s.onField);
            String key = rand.nextBoolean() ? "ev.varHandball" : "ev.varDanger";
            events.add(new MatchEvent(MatchEvent.Type.VAR_CHECK, getClockDisplay(), team, p, null,
                    I18n.f(key, p.getName(), team.getName())));
            double r = rand.nextDouble();
            if (r < 0.25) {
                attemptPenalty(team, isHome);
            } else if (r < 0.40) {
                p.giveRedCard();
                p.addSeasonRedCard();
                p.addSuspensionMatches(1);
                events.add(new MatchEvent(MatchEvent.Type.RED_CARD, getClockDisplay(), team, p, null,
                        I18n.f("ev.varRedConfirmed", p.getName())));
                removeFromField(team, p, false);
                restoreGoalkeeperIfNeeded(team);
            } else {
                events.add(new MatchEvent(MatchEvent.Type.VAR_CHECK, getClockDisplay(), team, null, null,
                        I18n.t("ev.varNoAction")));
            }
        }
        if (rand.nextDouble() < 0.018) {
            IPlayer p = pickRandom(s.onField);
            p.addYellowCard();
            boolean suspendedByYellowLimit = recordSeasonYellow(p);
            if (p.getYellowCards() >= 2) {
                p.giveRedCard();
                p.addSeasonRedCard();
                events.add(new MatchEvent(MatchEvent.Type.YELLOW_CARD, getClockDisplay(), team, p, null,
                        I18n.f("ev.secondYellow", p.getName())));
                events.add(new MatchEvent(MatchEvent.Type.RED_CARD, getClockDisplay(), team, p, null,
                        I18n.f("ev.redOff", p.getName(), team.getName())));
                removeFromField(team, p, false);
                restoreGoalkeeperIfNeeded(team);
            } else {
                events.add(new MatchEvent(MatchEvent.Type.YELLOW_CARD, getClockDisplay(), team, p, null,
                        I18n.f("ev.yellowCard", p.getName(), team.getName())));
            }
            if (suspendedByYellowLimit) {
                events.add(new MatchEvent(MatchEvent.Type.YELLOW_CARD, getClockDisplay(), team, p, null,
                        I18n.f("ev.yellowSuspension", p.getName(), p.getSeasonYellowCards())));
            }
        }
        if (rand.nextDouble() < 0.0015) {
            IPlayer p = pickRandom(s.onField);
            p.giveRedCard();
            p.addSeasonRedCard();
            p.addSuspensionMatches(1);
            events.add(new MatchEvent(MatchEvent.Type.RED_CARD, getClockDisplay(), team, p, null,
                    I18n.f("ev.directRed", p.getName(), team.getName())));
            removeFromField(team, p, false);
            restoreGoalkeeperIfNeeded(team);
        }
        if (rand.nextDouble() < 0.005) {
            IPlayer p = pickRandom(s.onField);
            p.setInjured(true);
            events.add(new MatchEvent(MatchEvent.Type.INJURY, getClockDisplay(), team, p, null,
                    I18n.f("ev.injury", p.getName(), team.getName())));
            removeFromField(team, p, true);
            restoreGoalkeeperIfNeeded(team);
        }
    }

    private void attemptPenalty(ITeam team, boolean isHome) {
        IPlayer taker = pickPenaltyTaker(team);
        if (taker == null) return;
        events.add(new MatchEvent(MatchEvent.Type.PENALTY, getClockDisplay(), team, taker, null,
                I18n.f("ev.penaltyAwarded", team.getName(), team.getName(), taker.getName())));
        double conv = 0.50 + (taker.getSkillLevel() / 100.0) * 0.35;
        if (rand.nextDouble() < conv) {
            taker.addGoalThisMatch(getClockDisplay());
            taker.addSeasonGoal();
            if (isHome) homeScore++; else awayScore++;
            events.add(new MatchEvent(MatchEvent.Type.GOAL, getClockDisplay(), team, taker, null,
                    I18n.f("ev.penaltyGoal", taker.getName(), team.getName())));
        } else {
            String key = rand.nextBoolean() ? "ev.penaltySaved" : "ev.penaltyMiss";
            events.add(new MatchEvent(MatchEvent.Type.PENALTY, getClockDisplay(), team, taker, null,
                    I18n.f(key, taker.getName(), team.getName())));
        }
    }

    private void attemptFreeKick(ITeam team, boolean isHome) {
        IPlayer taker = pickFreeKickTaker(team);
        if (taker == null) return;
        events.add(new MatchEvent(MatchEvent.Type.FREE_KICK, getClockDisplay(), team, taker, null,
                I18n.f("ev.freeKickAwarded", team.getName(), team.getName(), taker.getName())));
        double conv = 0.06 + (taker.getSkillLevel() / 100.0) * 0.10;
        if (rand.nextDouble() < conv) {
            taker.addGoalThisMatch(getClockDisplay());
            taker.addSeasonGoal();
            if (isHome) homeScore++; else awayScore++;
            events.add(new MatchEvent(MatchEvent.Type.GOAL, getClockDisplay(), team, taker, null,
                    I18n.f("ev.freeKickGoal", taker.getName(), team.getName())));
        } else {
            events.add(new MatchEvent(MatchEvent.Type.FREE_KICK, getClockDisplay(), team, taker, null,
                    I18n.f("ev.freeKickMiss", taker.getName(), team.getName())));
        }
    }

    private IPlayer pickPenaltyTaker(ITeam team) {
        return pickDesignatedOrFallback(team, team.getPenaltyTaker());
    }

    private IPlayer pickFreeKickTaker(ITeam team) {
        return pickDesignatedOrFallback(team, team.getFreeKickTaker());
    }

    private IPlayer pickDesignatedOrFallback(ITeam team, String designatedName) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return null;
        if (designatedName != null) {
            for (IPlayer p : s.onField) {
                if (designatedName.equals(p.getName())) return p;
            }
        }
        IPlayer best = null;
        for (IPlayer p : s.onField) {
            if (isGoalkeeper(p)) continue;
            if (best == null || p.getSkillLevel() > best.getSkillLevel()) best = p;
        }
        return best != null ? best : pickRandom(s.onField);
    }

    private int computePower(ITeam team, boolean isHome) {
        TeamState s = stateOf(team);
        if (s.onField.isEmpty()) return 1;
        int sum = 0;
        for (IPlayer p : s.onField) sum += p.getSkillLevel();
        int avg = sum / s.onField.size();
        int homeAdv = isHome ? 8 : 0;            // home advantage
        int tactic = tacticModifier(team);
        int sizePenalty = Math.max(0, 11 - s.onField.size()) * 4;
        return Math.max(1, avg + homeAdv + tactic - sizePenalty);
    }

    private int tacticModifier(ITeam team) {
        String tactic = team.getTactic() == null ? "" : team.getTactic().toLowerCase();
        if (tactic.contains("attack") || tactic.contains("hücum")) return 5;
        if (tactic.contains("defensive") || tactic.contains("savun")) return 3;
        return 0;
    }

    private boolean recordSeasonYellow(IPlayer p) {
        p.addSeasonYellowCard();
        if (p.getSeasonYellowCards() > 0 && p.getSeasonYellowCards() % 4 == 0) {
            p.addSuspensionMatches(1);
            return true;
        }
        return false;
    }

    private boolean isGoalkeeper(IPlayer p) {
        String pos = p.getPosition() == null ? "" : p.getPosition().toLowerCase();
        return pos.contains("kale") || pos.contains("goalkeeper") || pos.equals("gk");
    }

    private boolean hasGoalkeeper(Iterable<IPlayer> players) {
        for (IPlayer p : players) {
            if (isGoalkeeper(p)) return true;
        }
        return false;
    }

    private IPlayer recoverEmergencyGoalkeeper(ITeam team, TeamState state, List<IPlayer> available) {
        for (IPlayer p : team.getPlayers()) {
            if (!isGoalkeeper(p)) continue;
            p.setInjured(false);
            while (p.isSuspended()) p.decrementSuspensionMatches();
            state.missing.remove(p);
            state.suspended.remove(p);
            if (!available.contains(p)) available.add(0, p);
            return p;
        }
        return null;
    }

    private boolean keepsGoalkeeper(TeamState state, IPlayer out, IPlayer in) {
        for (IPlayer p : state.onField) {
            if (p != out && isGoalkeeper(p)) return true;
        }
        return isGoalkeeper(in);
    }

    private IPlayer pickBestBench(Iterable<IPlayer> bench, boolean mustBeGoalkeeper) {
        IPlayer best = null;
        for (IPlayer p : bench) {
            if (mustBeGoalkeeper && !isGoalkeeper(p)) continue;
            if (best == null || p.getSkillLevel() > best.getSkillLevel()) best = p;
        }
        return best;
    }

    private void restoreGoalkeeperIfNeeded(ITeam team) {
        TeamState s = stateOf(team);
        if (hasGoalkeeper(s.onField) || s.subsUsed >= getMaxSubs()) return;

        IPlayer goalkeeper = pickBestBench(s.bench, true);
        if (goalkeeper == null) return;

        if (s.pendingReplacements > 0) {
            replace(team, goalkeeper);
            return;
        }

        IPlayer out = pickLowestOutfield(s.onField);
        if (out == null) return;
        s.onField.remove(out);
        s.removed.add(out);
        s.bench.remove(goalkeeper);
        s.onField.add(goalkeeper);
        s.subsUsed++;
        goalkeeper.setSubInClock(getClockDisplay());
        events.add(new MatchEvent(MatchEvent.Type.SUBSTITUTION, getClockDisplay(),
                team, out, goalkeeper,
                I18n.f("ev.subSwap", out.getName(), goalkeeper.getName(), team.getName())));
    }

    private IPlayer pickLowestOutfield(Iterable<IPlayer> players) {
        IPlayer lowest = null;
        for (IPlayer p : players) {
            if (isGoalkeeper(p)) continue;
            if (lowest == null || p.getSkillLevel() < lowest.getSkillLevel()) lowest = p;
        }
        return lowest;
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
        if (pos.contains("kale") || pos.contains("goalkeeper") || pos.equals("gk")) return 1;
        if (pos.contains("forvet") || pos.contains("santrafor") || pos.contains("forward") || pos.contains("striker")) w += 30;
        else if (pos.contains("orta") || pos.contains("midfield")) w += 10;
        return Math.max(1, w);
    }

    private void finish() {
        played = true;
        if (homeScore > awayScore) winner = homeTeam;
        else if (awayScore > homeScore) winner = awayTeam;
        else winner = null;
        awardLeaguePoints();
        completeSuspensionService();
        String text = (winner == null)
                ? I18n.f("ev.fulltimeDraw", homeTeam.getName(), homeScore, awayScore, awayTeam.getName())
                : I18n.f("ev.fulltimeWin", homeTeam.getName(), homeScore, awayScore, awayTeam.getName(), winner.getName());
        events.add(new MatchEvent(MatchEvent.Type.FULLTIME, getClockDisplay(), null, null, null, text));
    }

    @Override
    protected void awardLeaguePoints() {
        if (winner == homeTeam) homeTeam.addPoints(3);
        else if (winner == awayTeam) awayTeam.addPoints(3);
        else { homeTeam.addPoints(1); awayTeam.addPoints(1); }
    }
}
