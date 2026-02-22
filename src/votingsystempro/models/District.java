package votingsystempro.models;

public class District {
    private int districtId;
    private String districtName;
    private int provinceId;
    private String provinceName;
    private int totalConstituencies;
    
    public District() {}
    
    public District(int districtId, String districtName, int provinceId) {
        this.districtId = districtId;
        this.districtName = districtName;
        this.provinceId = provinceId;
    }
    
    // Getters and Setters
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
    
    public int getTotalConstituencies() {
        return totalConstituencies;
    }
    
    public void setTotalConstituencies(int totalConstituencies) {
        this.totalConstituencies = totalConstituencies;
    }
    
    public String getFullDisplay() {
        return districtName + " (" + provinceName + ")";
    }
    
    @Override
    public String toString() {
        return districtName;
    }
}