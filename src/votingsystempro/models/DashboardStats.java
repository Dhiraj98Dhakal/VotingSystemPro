package votingsystempro.models;

public class DashboardStats {
    private int totalVoters;
    private int approvedVoters;
    private int pendingVoters;
    private int totalParties;
    private int totalCandidates;
    private int fptpVotes;
    private int prVotes;
    private int totalVotes;
    private double voterTurnout;
    
    public DashboardStats() {}
    
    // Getters and Setters
    public int getTotalVoters() {
        return totalVoters;
    }
    
    public void setTotalVoters(int totalVoters) {
        this.totalVoters = totalVoters;
    }
    
    public int getApprovedVoters() {
        return approvedVoters;
    }
    
    public void setApprovedVoters(int approvedVoters) {
        this.approvedVoters = approvedVoters;
    }
    
    public int getPendingVoters() {
        return pendingVoters;
    }
    
    public void setPendingVoters(int pendingVoters) {
        this.pendingVoters = pendingVoters;
    }
    
    public int getTotalParties() {
        return totalParties;
    }
    
    public void setTotalParties(int totalParties) {
        this.totalParties = totalParties;
    }
    
    public int getTotalCandidates() {
        return totalCandidates;
    }
    
    public void setTotalCandidates(int totalCandidates) {
        this.totalCandidates = totalCandidates;
    }
    
    public int getFptpVotes() {
        return fptpVotes;
    }
    
    public void setFptpVotes(int fptpVotes) {
        this.fptpVotes = fptpVotes;
    }
    
    public int getPrVotes() {
        return prVotes;
    }
    
    public void setPrVotes(int prVotes) {
        this.prVotes = prVotes;
    }
    
    public int getTotalVotes() {
        return totalVotes;
    }
    
    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }
    
    public double getVoterTurnout() {
        return voterTurnout;
    }
    
    public void setVoterTurnout(double voterTurnout) {
        this.voterTurnout = voterTurnout;
    }
    
    public int getApprovalRate() {
        if (totalVoters > 0) {
            return (int) ((approvedVoters * 100.0) / totalVoters);
        }
        return 0;
    }
    
    public int getVotingRate() {
        if (approvedVoters > 0) {
            return (int) ((totalVotes * 100.0) / (approvedVoters * 2)); // Each voter can vote twice
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Dashboard Stats:\n" +
            "Total Voters: %d\n" +
            "Approved Voters: %d\n" +
            "Pending Voters: %d\n" +
            "Total Parties: %d\n" +
            "Total Candidates: %d\n" +
            "FPTP Votes: %d\n" +
            "PR Votes: %d\n" +
            "Total Votes: %d\n" +
            "Voter Turnout: %.1f%%",
            totalVoters, approvedVoters, pendingVoters, totalParties,
            totalCandidates, fptpVotes, prVotes, totalVotes, voterTurnout
        );
    }
}