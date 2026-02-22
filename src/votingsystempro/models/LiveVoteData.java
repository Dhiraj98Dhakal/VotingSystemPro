package votingsystempro.models;

import java.util.List;
import java.util.ArrayList;

public class LiveVoteData {
    private int totalVotes;
    private int fptpVotes;
    private int prVotes;
    private int turnoutPercentage;
    private String leadingParty;
    private String leadingCandidate;
    private List<Object[]> fptpResults;
    private List<Object[]> prResults;
    
    public LiveVoteData() {
        this.fptpResults = new ArrayList<>();
        this.prResults = new ArrayList<>();
    }
    
    // Constructor with all fields
    public LiveVoteData(int totalVotes, int fptpVotes, int prVotes, int turnoutPercentage, 
                        String leadingParty, String leadingCandidate, 
                        List<Object[]> fptpResults, List<Object[]> prResults) {
        this.totalVotes = totalVotes;
        this.fptpVotes = fptpVotes;
        this.prVotes = prVotes;
        this.turnoutPercentage = turnoutPercentage;
        this.leadingParty = leadingParty;
        this.leadingCandidate = leadingCandidate;
        this.fptpResults = fptpResults != null ? fptpResults : new ArrayList<>();
        this.prResults = prResults != null ? prResults : new ArrayList<>();
    }
    
    // Getters and Setters
    public int getTotalVotes() { return totalVotes; }
    public void setTotalVotes(int totalVotes) { this.totalVotes = totalVotes; }
    
    public int getFptpVotes() { return fptpVotes; }
    public void setFptpVotes(int fptpVotes) { this.fptpVotes = fptpVotes; }
    
    public int getPrVotes() { return prVotes; }
    public void setPrVotes(int prVotes) { this.prVotes = prVotes; }
    
    public int getTurnoutPercentage() { return turnoutPercentage; }
    public void setTurnoutPercentage(int turnoutPercentage) { this.turnoutPercentage = turnoutPercentage; }
    
    public String getLeadingParty() { return leadingParty; }
    public void setLeadingParty(String leadingParty) { this.leadingParty = leadingParty; }
    
    public String getLeadingCandidate() { return leadingCandidate; }
    public void setLeadingCandidate(String leadingCandidate) { this.leadingCandidate = leadingCandidate; }
    
    public List<Object[]> getFptpResults() { return fptpResults; }
    public void setFptpResults(List<Object[]> fptpResults) { this.fptpResults = fptpResults; }
    
    public List<Object[]> getPrResults() { return prResults; }
    public void setPrResults(List<Object[]> prResults) { this.prResults = prResults; }
}