package sportsmanager.core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLeague implements ILeague {
    private static final long serialVersionUID = 2L;

    protected String name;
    protected List<ITeam> teams;
    protected List<IMatch> scheduledMatches;
    protected ITeam managedTeam;

    public AbstractLeague(String name) {
        this.name = name;
        this.teams = new ArrayList<>();
        this.scheduledMatches = new ArrayList<>();
    }

    @Override public String getName() { return name; }
    @Override public void addTeam(ITeam team) { teams.add(team); }
    @Override public List<ITeam> getTeams() { return teams; }
    @Override public List<IMatch> getScheduledMatches() { return scheduledMatches; }

    @Override public void setManagedTeam(ITeam team) { this.managedTeam = team; }
    @Override public ITeam getManagedTeam() { return managedTeam; }

    @Override
    public IMatch getNextMatchForManaged() {
        if (managedTeam == null) return null;
        for (IMatch m : scheduledMatches) {
            if (m.isPlayed()) continue;
            if (m.getHomeTeam() == managedTeam || m.getAwayTeam() == managedTeam) return m;
        }
        return null;
    }

    @Override
    public int getRoundOf(IMatch match) {
        int idx = scheduledMatches.indexOf(match);
        if (idx < 0) return 0;
        int matchesPerRound = Math.max(1, teams.size() / 2);
        return idx / matchesPerRound + 1;
    }

    @Override
    public void autoFinishOtherMatchesInRound(IMatch reference) {
        int idx = scheduledMatches.indexOf(reference);
        if (idx < 0) return;
        int matchesPerRound = Math.max(1, teams.size() / 2);
        int round = idx / matchesPerRound;
        int from = round * matchesPerRound;
        int to = Math.min(scheduledMatches.size(), from + matchesPerRound);
        for (int i = from; i < to; i++) {
            IMatch m = scheduledMatches.get(i);
            if (m == reference || m.isPlayed()) continue;
            // Never auto-play the managed team's match; the user controls it.
            if (managedTeam != null
                    && (m.getHomeTeam() == managedTeam || m.getAwayTeam() == managedTeam)) {
                continue;
            }
            m.start();
            m.tickToEnd();
        }
    }

    @Override
    public List<ITeam> getStandings() {
        List<ITeam> standings = new ArrayList<>(teams);
        standings.sort((t1, t2) -> Integer.compare(t2.getPoints(), t1.getPoints()));
        return standings;
    }

    @Override public abstract void scheduleMatches();
    @Override public abstract void playNextRound();

    /** Subclasses must implement to create a swapped (home/away reversed) copy of the given match. */
    protected abstract IMatch createReversedMatch(IMatch original);

    protected void scheduleBalancedDoubleRoundRobin(List<IMatch> leg1, List<IMatch> leg2, int matchesPerRound) {
        scheduledMatches.clear();
        if (leg1.isEmpty() || leg2.isEmpty() || matchesPerRound <= 0) return;

        List<List<IMatch>> leg1Rounds = splitIntoRounds(leg1, matchesPerRound);
        List<List<IMatch>> leg2Rounds = splitIntoRounds(leg2, matchesPerRound);
        int rounds = Math.min(leg1Rounds.size(), leg2Rounds.size());
        List<List<IMatch>> candidates = new ArrayList<>();

        for (int shift = 0; shift < rounds; shift++) {
            List<IMatch> interleaved = new ArrayList<>();
            List<IMatch> reversedInterleaved = new ArrayList<>();
            List<IMatch> mirrored = new ArrayList<>();
            List<IMatch> block = new ArrayList<>();
            List<IMatch> reverseBlock = new ArrayList<>();

            for (int r = 0; r < rounds; r++) {
                addRound(interleaved, leg1Rounds, r);
                addRound(interleaved, leg2Rounds, (r + shift) % rounds);

                addRound(reversedInterleaved, leg2Rounds, (r + shift) % rounds);
                addRound(reversedInterleaved, leg1Rounds, r);

                addRound(mirrored, leg1Rounds, r);
                addRound(mirrored, leg2Rounds, (rounds - 1 - r + shift + rounds) % rounds);

                addRound(block, leg1Rounds, r);
                addRound(reverseBlock, leg2Rounds, (r + shift) % rounds);
            }
            for (int r = 0; r < rounds; r++) {
                addRound(block, leg2Rounds, (r + shift) % rounds);
                addRound(reverseBlock, leg1Rounds, r);
            }

            candidates.add(interleaved);
            candidates.add(reversedInterleaved);
            candidates.add(mirrored);
            candidates.add(block);
            candidates.add(reverseBlock);
        }

        chooseBestSchedule(candidates);
    }

    private List<List<IMatch>> splitIntoRounds(List<IMatch> matches, int matchesPerRound) {
        List<List<IMatch>> rounds = new ArrayList<>();
        for (int i = 0; i < matches.size(); i += matchesPerRound) {
            rounds.add(new ArrayList<>(matches.subList(i, Math.min(matches.size(), i + matchesPerRound))));
        }
        return rounds;
    }

    private void addRound(List<IMatch> target, List<List<IMatch>> rounds, int round) {
        target.addAll(rounds.get(round));
    }

    private void chooseBestSchedule(List<List<IMatch>> candidates) {
        List<IMatch> best = null;
        int bestPenalty = Integer.MAX_VALUE;
        for (List<IMatch> candidate : candidates) {
            scheduledMatches = new ArrayList<>(candidate);
            int penalty = homeAwayPenalty();
            if (penalty < bestPenalty) {
                bestPenalty = penalty;
                best = new ArrayList<>(scheduledMatches);
                if (bestPenalty == 0) break;
            }
        }
        if (best != null) {
            scheduledMatches = best;
            optimizeHomeAwayBalance();
        }
        assignKickoffTimes();
    }

    private void assignKickoffTimes() {
        String[] eveningSlots = { "18:00", "19:45", "21:30", "20:00" };
        int matchesPerRound = Math.max(1, teams.size() / 2);
        for (int i = 0; i < scheduledMatches.size(); i++) {
            int slot = i % matchesPerRound;
            scheduledMatches.get(i).setKickoffTime(eveningSlots[slot % eveningSlots.length]);
        }
    }

    private int homeAwayPenalty() {
        int penalty = 0;
        Map<ITeam, List<Boolean>> sequences = new HashMap<>();
        for (ITeam team : teams) {
            sequences.put(team, new ArrayList<>());
        }
        for (IMatch match : scheduledMatches) {
            sequences.get(match.getHomeTeam()).add(true);
            sequences.get(match.getAwayTeam()).add(false);
        }
        for (List<Boolean> sequence : sequences.values()) {
            for (int i = 1; i < sequence.size(); i++) {
                if (sequence.get(i).equals(sequence.get(i - 1))) penalty++;
                if (i >= 2
                        && sequence.get(i).equals(sequence.get(i - 1))
                        && sequence.get(i - 1).equals(sequence.get(i - 2))) {
                    penalty += 1000;
                }
            }
        }
        return penalty;
    }

    /**
     * Post-process the schedule to break 3+ consecutive home/away streaks for any team.
     * Uses paired local swaps so fixture generation stays fast even with larger leagues.
     */
    protected void optimizeHomeAwayBalance() {
        if (scheduledMatches.isEmpty()) return;
        fixupHomeAwayClusters();
    }

    /**
     * Local fallback that breaks 3+ consecutive home/away streaks for any team.
     * It swaps the offending match and its paired reverse fixture together.
     */
    protected void fixupHomeAwayClusters() {
        int safety = 200;
        boolean fixed = true;
        while (fixed && safety-- > 0) {
            fixed = false;
            for (ITeam team : teams) {
                List<Integer> idx = new ArrayList<>();
                List<Boolean> isHome = new ArrayList<>();
                for (int i = 0; i < scheduledMatches.size(); i++) {
                    IMatch m = scheduledMatches.get(i);
                    if (m.getHomeTeam() == team) { idx.add(i); isHome.add(true); }
                    else if (m.getAwayTeam() == team) { idx.add(i); isHome.add(false); }
                }
                for (int k = 0; k <= isHome.size() - 3; k++) {
                    if (isHome.get(k).equals(isHome.get(k + 1))
                            && isHome.get(k + 1).equals(isHome.get(k + 2))) {
                        int swapIdx = idx.get(k + 1);
                        swapPairedHomeAway(swapIdx);
                        fixed = true;
                        break;
                    }
                }
                if (fixed) break;
            }
        }
    }

    private void swapPairedHomeAway(int index) {
        IMatch original = scheduledMatches.get(index);
        int counterpart = findCounterpart(index, original);
        scheduledMatches.set(index, createReversedMatch(original));
        if (counterpart >= 0) {
            IMatch pair = scheduledMatches.get(counterpart);
            scheduledMatches.set(counterpart, createReversedMatch(pair));
        }
    }

    private int findCounterpart(int index, IMatch original) {
        for (int i = 0; i < scheduledMatches.size(); i++) {
            if (i == index) continue;
            IMatch other = scheduledMatches.get(i);
            boolean samePair =
                    (other.getHomeTeam() == original.getHomeTeam() && other.getAwayTeam() == original.getAwayTeam())
                    || (other.getHomeTeam() == original.getAwayTeam() && other.getAwayTeam() == original.getHomeTeam());
            if (samePair) return i;
        }
        return -1;
    }
}
