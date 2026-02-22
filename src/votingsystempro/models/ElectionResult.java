package votingsystempro.models;

public class ElectionResult {
    private String position; // 'fptp' or 'pr'
    private int candidateId;
    private String candidateName;
    private int partyId;
    private String partyName;
    private int constituencyId;
    private String constituencyNumber;
    private int districtId;
    private String districtName;
    private int provinceId;
    private String provinceName;
    private int totalVotes;
    private double votePercentage;
    private int rank;
    
    public ElectionResult() {}
    
    // Getters and Setters
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
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
    
    public int getConstituencyId() {
        return constituencyId;
    }
    
    public void setConstituencyId(int constituencyId) {
        this.constituencyId = constituencyId;
    }
    
    public String getConstituencyNumber() {
        return constituencyNumber;
    }
    
    public void setConstituencyNumber(String constituencyNumber) {
        this.constituencyNumber = constituencyNumber;
    }
    
    public int getDistrictId() {
        return districtId;
    }
    
    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }
    
    public String getDistrictName() {
        return districtName;
    }
    
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
    
    public int getProvinceId() {
        return provinceId;
    }
    
    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
    
    public String getProvinceName() {
        return provinceName;
    }
    
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    
    public int getTotalVotes() {
        return totalVotes;
    }
    
    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }
    
    public double getVotePercentage() {
        return votePercentage;
    }
    
    public void setVotePercentage(double votePercentage) {
        this.votePercentage = votePercentage;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    public String getPositionDisplay() {
        return position.equals("fptp") ? "FPTP Result" : "PR Result";
    }
    
    public boolean isWinner() {
        return rank == 1;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %d votes (%.1f%%)", 
            candidateName, partyName, totalVotes, votePercentage);
    }
}