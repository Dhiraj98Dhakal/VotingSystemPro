package votingsystempro.models;

import java.util.Date;

public class Party {
    private int partyId;
    private String partyName;
    private String partyLogoPath;
    private String description;
    private Date establishedDate;
    private String createdAt;
    private int totalCandidates;
    private int totalVotes;
    
    public Party() {}
    
    public Party(int partyId, String partyName, String partyLogoPath, String description, 
                Date establishedDate, String createdAt) {
        this.partyId = partyId;
        this.partyName = partyName;
        this.partyLogoPath = partyLogoPath;
        this.description = description;
        this.establishedDate = establishedDate;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
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
    
    public String getPartyLogoPath() {
        return partyLogoPath;
    }
    
    public void setPartyLogoPath(String partyLogoPath) {
        this.partyLogoPath = partyLogoPath;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getEstablishedDate() {
        return establishedDate;
    }
    
    public void setEstablishedDate(Date establishedDate) {
        this.establishedDate = establishedDate;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getTotalCandidates() {
        return totalCandidates;
    }
    
    public void setTotalCandidates(int totalCandidates) {
        this.totalCandidates = totalCandidates;
    }
    
    public int getTotalVotes() {
        return totalVotes;
    }
    
    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }
    
    public String getFormattedEstablishedDate() {
        if (establishedDate != null) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(establishedDate);
        }
        return "N/A";
    }
    
    @Override
    public String toString() {
        return partyName;
    }
}