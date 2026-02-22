package votingsystempro.models;

public class Constituency {
    private int constituencyId;
    private int constituencyNumber;
    private int districtId;
    private String districtName;
    private int provinceId;
    private String provinceName;
    private int totalVoters;
    private int totalCandidates;
    
    public Constituency() {}
    
    public Constituency(int constituencyId, int constituencyNumber, int districtId) {
        this.constituencyId = constituencyId;
        this.constituencyNumber = constituencyNumber;
        this.districtId = districtId;
    }
    
    // Getters and Setters
    public int getConstituencyId() {
        return constituencyId;
    }
    
    public void setConstituencyId(int constituencyId) {
        this.constituencyId = constituencyId;
    }
    
    public int getConstituencyNumber() {
        return constituencyNumber;
    }
    
    public void setConstituencyNumber(int constituencyNumber) {
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
    
    public int getTotalVoters() {
        return totalVoters;
    }
    
    public void setTotalVoters(int totalVoters) {
        this.totalVoters = totalVoters;
    }
    
    public int getTotalCandidates() {
        return totalCandidates;
    }
    
    public void setTotalCandidates(int totalCandidates) {
        this.totalCandidates = totalCandidates;
    }
    
    public String getDisplayName() {
        return "Constituency " + constituencyNumber;
    }
    
    public String getFullDisplay() {
        return "Constituency " + constituencyNumber + ", " + districtName + ", " + provinceName;
    }
    
    @Override
    public String toString() {
        return "Constituency " + constituencyNumber;
    }
}