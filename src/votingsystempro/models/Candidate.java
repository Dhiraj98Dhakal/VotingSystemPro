package votingsystempro.models;

public class Candidate {
    private int candidateId;
    private String candidateName;
    private int partyId;
    private String partyName;
    private String photoPath;
    private String position; // 'fptp' or 'pr'
    private int provinceId;
    private String provinceName;
    private int districtId;
    private String districtName;
    private int constituencyId;
    private String constituencyNumber;
    private String biography;
    private String createdAt;
    private int totalVotes;
    
    public Candidate() {}
    
    public Candidate(int candidateId, String candidateName, int partyId, String partyName,
                    String photoPath, String position, int provinceId, int districtId,
                    int constituencyId, String biography, String createdAt) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.partyId = partyId;
        this.partyName = partyName;
        this.photoPath = photoPath;
        this.position = position;
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.constituencyId = constituencyId;
        this.biography = biography;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
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
    
    public String getPhotoPath() {
        return photoPath;
    }
    
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
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
    
    public String getBiography() {
        return biography;
    }
    
    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getTotalVotes() {
        return totalVotes;
    }
    
    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }
    
    public String getPositionDisplay() {
        return position.equals("fptp") ? "FPTP Candidate" : "PR Candidate";
    }
    
    public String getLocationDisplay() {
        StringBuilder sb = new StringBuilder();
        if (constituencyNumber != null && !constituencyNumber.isEmpty()) {
            sb.append(constituencyNumber);
        }
        if (districtName != null && !districtName.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(districtName);
        }
        if (provinceName != null && !provinceName.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(provinceName);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return candidateName + " (" + partyName + ")";
    }
}