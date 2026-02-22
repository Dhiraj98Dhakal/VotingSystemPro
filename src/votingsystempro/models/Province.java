package votingsystempro.models;

public class Province {
    private int provinceId;
    private String provinceName;
    private int provinceNumber;
    private int totalDistricts;
    private int totalConstituencies;
    
    public Province() {}
    
    public Province(int provinceId, String provinceName, int provinceNumber) {
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.provinceNumber = provinceNumber;
    }
    
    // Getters and Setters
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
    
    public int getProvinceNumber() {
        return provinceNumber;
    }
    
    public void setProvinceNumber(int provinceNumber) {
        this.provinceNumber = provinceNumber;
    }
    
    public int getTotalDistricts() {
        return totalDistricts;
    }
    
    public void setTotalDistricts(int totalDistricts) {
        this.totalDistricts = totalDistricts;
    }
    
    public int getTotalConstituencies() {
        return totalConstituencies;
    }
    
    public void setTotalConstituencies(int totalConstituencies) {
        this.totalConstituencies = totalConstituencies;
    }
    
    public String getDisplayName() {
        return "Province " + provinceNumber + ": " + provinceName;
    }
    
    @Override
    public String toString() {
        return provinceName;
    }
}