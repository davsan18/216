package sportsmanager.core;

public abstract class AbstractMatch implements IMatch {
    protected ITeam homeTeam;
    protected ITeam awayTeam;
    protected int homeScore = 0;
    protected int awayScore = 0;
    protected boolean played = false;
    protected ITeam winner = null;

    public AbstractMatch(ITeam homeTeam, ITeam awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    @Override public ITeam getHomeTeam() { return homeTeam; }
    @Override public ITeam getAwayTeam() { return awayTeam; }
    @Override public boolean isPlayed() { return played; }
    @Override public ITeam getWinner() { return winner; }
    @Override public String getScore() { return homeScore + " - " + awayScore; }

    @Override public abstract void play();
}