package votingsystempro.models;

import java.util.Date;

public class Vote {
    private int voteId;
    private int voterId;
    private String voterName;
    private String voteType; // 'fptp' or 'pr'
    private int candidateId;
    private String candidateName;
    private int partyId;
    private String partyName;
    private Date votingDate;
    private String ipAddress;
    
    public Vote() {}
    
    public Vote(int voteId, int voterId, String voteType, int candidateId, int partyId, 
                Date votingDate, String ipAddress) {
        this.voteId = voteId;
        this.voterId = voterId;
        this.voteType = voteType;
        this.candidateId = candidateId;
        this.partyId = partyId;
        this.votingDate = votingDate;
        this.ipAddress = ipAddress;
    }
    
    // Getters and Setters
    public int getVoteId() {
        return voteId;
    }
    
    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }
    
    public int getVoterId() {
        return voterId;
    }
    
    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }
    
    public String getVoterName() {
        return voterName;
    }
    
    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }
    
    public String getVoteType() {
        return voteType;
    }
    
    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }
    
    public int getCandidateId() {
        return candidateId;
    }
    
    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }
    
    public String getCandidateName() {
        return candidateName;
    }
    
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    
    public int getPartyId() {
        return partyId;
    }
    
    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }
    
    public String getPartyName() {
        return partyName;
    }
    
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
    
    public Date getVotingDate() {
        return votingDate;
    }
    
    public void setVotingDate(Date votingDate) {
        this.votingDate = votingDate;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getVoteTypeDisplay() {
        return voteType.equals("fptp") ? "FPTP Vote" : "PR Vote";
    }
    
    public String getVotedFor() {
        if (voteType.equals("fptp") && candidateName != null) {
            return candidateName;
        } else if (voteType.equals("pr") && partyName != null) {
            return partyName;
        }
        return "Unknown";
    }
    
    @Override
    public String toString() {
        return "Vote{" +
                "voteId=" + voteId +
                ", voteType='" + voteType + '\'' +
                ", votingDate=" + votingDate +
                '}';
    }
}