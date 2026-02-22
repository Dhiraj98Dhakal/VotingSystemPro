package votingsystempro.utils;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OtpUtil {
    
    private static final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_DURATION = 10 * 60 * 1000; // 10 minutes
    
    // Store OTPs: key = email/phone, value = OtpData
    private static final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    
    /**
     * Generate a 6-digit OTP
     */
    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    /**
     * Store OTP for a user (email or phone)
     */
    public static void storeOtp(String key, String otp) {
        // Remove any existing OTP for this key
        otpStore.remove(key);
        
        // Store new OTP with timestamp
        otpStore.put(key, new OtpData(otp, System.currentTimeMillis()));
        System.out.println("📱 OTP stored for " + maskKey(key) + ": " + otp);
    }
    
    /**
     * Validate OTP
     */
    public static boolean validateOtp(String key, String otp) {
        OtpData data = otpStore.get(key);
        
        if (data == null) {
            System.out.println("❌ No OTP found for " + maskKey(key));
            return false;
        }
        
        // Check if expired
        if (System.currentTimeMillis() - data.timestamp > OTP_VALIDITY_DURATION) {
            otpStore.remove(key);
            System.out.println("⏰ OTP expired for " + maskKey(key));
            return false;
        }
        
        // Check if OTP matches
        boolean valid = data.otp.equals(otp);
        if (valid) {
            // Remove OTP after successful validation
            otpStore.remove(key);
            System.out.println("✅ OTP validated for " + maskKey(key));
        } else {
            System.out.println("❌ Invalid OTP for " + maskKey(key) + ". Expected: " + data.otp + ", Got: " + otp);
        }
        
        return valid;
    }
    
    /**
     * Get OTP data class
     */
    private static class OtpData {
        String otp;
        long timestamp;
        
        OtpData(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * Clear expired OTPs (can be called periodically)
     */
    public static void clearExpiredOtps() {
        long now = System.currentTimeMillis();
        otpStore.entrySet().removeIf(entry -> 
            now - entry.getValue().timestamp > OTP_VALIDITY_DURATION);
        System.out.println("🧹 Cleared expired OTPs");
    }
    
    /**
     * Get remaining validity time in seconds for an OTP
     */
    public static long getRemainingTime(String key) {
        OtpData data = otpStore.get(key);
        if (data == null) {
            return 0;
        }
        
        long elapsed = System.currentTimeMillis() - data.timestamp;
        long remaining = OTP_VALIDITY_DURATION - elapsed;
        
        return remaining > 0 ? remaining / 1000 : 0;
    }
    
    /**
     * Check if OTP exists and is valid
     */
    public static boolean hasValidOtp(String key) {
        OtpData data = otpStore.get(key);
        if (data == null) {
            return false;
        }
        
        return System.currentTimeMillis() - data.timestamp <= OTP_VALIDITY_DURATION;
    }
    
    /**
     * Mask key for logging (show only first/last few characters)
     */
    private static String maskKey(String key) {
        if (key == null || key.length() < 8) {
            return "***";
        }
        
        if (key.contains("@")) {
            // Email: show first 2 chars + *** + domain
            String[] parts = key.split("@");
            if (parts[0].length() > 2) {
                return parts[0].substring(0, 2) + "***@" + parts[1];
            }
        } else if (key.matches(".*\\d.*")) {
            // Phone: show first 3 and last 3 digits
            if (key.length() >= 7) {
                return key.substring(0, 3) + "***" + key.substring(key.length() - 3);
            }
        }
        
        return "***";
    }
    
    /**
     * Get statistics about OTP store
     */
    public static String getStats() {
        int total = otpStore.size();
        long now = System.currentTimeMillis();
        
        StringBuilder stats = new StringBuilder();
        stats.append("📊 OTP Store Statistics:\n");
        stats.append("   Total OTPs: ").append(total).append("\n");
        
        if (total > 0) {
            stats.append("   Active OTPs:\n");
            otpStore.forEach((key, data) -> {
                long remaining = (OTP_VALIDITY_DURATION - (now - data.timestamp)) / 1000;
                stats.append("      • ").append(maskKey(key))
                     .append(": ").append(remaining).append("s remaining\n");
            });
        }
        
        return stats.toString();
    }
}