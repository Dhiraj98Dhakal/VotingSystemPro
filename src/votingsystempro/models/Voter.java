package votingsystempro.models;

import java.util.Date;

public class Voter {
    private int voterId;
    private int userId;
    private String fullName;
    private Date dateOfBirth;
    private int age;
    private String citizenshipNumber;
    private String fatherName;
    private String motherName;
    private String address;
    private String phoneNumber;
    private String email;
    private int provinceId;
    private String provinceName;
    private int districtId;
    private String districtName;
    private int constituencyId;
    private String constituencyNumber;
    private String photoPath;
    private boolean isApproved;
    private boolean hasVotedFptp;
    private boolean hasVotedPr;
    private String registrationDate;
    
    public Voter() {}
    
    public Voter(int voterId, int userId, String fullName, Date dateOfBirth, int age, 
                String citizenshipNumber, String fatherName, String motherName, 
                String address, String phoneNumber, String email, int provinceId, 
                int districtId, int constituencyId, String photoPath, boolean isApproved,
                boolean hasVotedFptp, boolean hasVotedPr, String registrationDate) {
        this.voterId = voterId;
        this.userId = userId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.citizenshipNumber = citizenshipNumber;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.constituencyId = constituencyId;
        this.photoPath = photoPath;
        this.isApproved = isApproved;
        this.hasVotedFptp = hasVotedFptp;
        this.hasVotedPr = hasVotedPr;
        this.registrationDate = registrationDate;
    }
    
    // Getters and Setters
    public int getVoterId() {
        return voterId;
    }
    
    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getCitizenshipNumber() {
        return citizenshipNumber;
    }
    
    public void setCitizenshipNumber(String citizenshipNumber) {
        this.citizenshipNumber = citizenshipNumber;
    }
    
    public String getFatherName() {
        return fatherName;
    }
    
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }
    
    public String getMotherName() {
        return motherName;
    }
    
    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
    
    public String getPhotoPath() {
        return photoPath;
    }
    
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    
    public boolean isApproved() {
        return isApproved;
    }
    
    public void setApproved(boolean approved) {
        isApproved = approved;
    }
    
    public boolean isHasVotedFptp() {
        return hasVotedFptp;
    }
    
    public void setHasVotedFptp(boolean hasVotedFptp) {
        this.hasVotedFptp = hasVotedFptp;
    }
    
    public boolean isHasVotedPr() {
        return hasVotedPr;
    }
    
    public void setHasVotedPr(boolean hasVotedPr) {
        this.hasVotedPr = hasVotedPr;
    }
    
    public String getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(address);
        if (districtName != null && !districtName.isEmpty()) {
            sb.append(", ").append(districtName);
        }
        if (provinceName != null && !provinceName.isEmpty()) {
            sb.append(", ").append(provinceName);
        }
        return sb.toString();
    }
    
    public boolean canVoteFPTP() {
        return isApproved && !hasVotedFptp;
    }
    
    public boolean canVotePR() {
        return isApproved && !hasVotedPr;
    }
    
    public boolean hasVotedAny() {
        return hasVotedFptp || hasVotedPr;
    }
    
    @Override
    public String toString() {
        return "Voter{" +
                "voterId=" + voterId +
                ", fullName='" + fullName + '\'' +
                ", citizenshipNumber='" + citizenshipNumber + '\'' +
                ", email='" + email + '\'' +
                ", isApproved=" + isApproved +
                '}';
    }
}