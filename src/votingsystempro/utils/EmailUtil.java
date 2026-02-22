package votingsystempro.utils;

import java.util.Properties;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EmailUtil {
    
    private static String SMTP_HOST = "smtp.gmail.com";
    private static String SMTP_PORT = "587";
    private static String EMAIL_USERNAME = "";
    private static String EMAIL_PASSWORD = "";
    private static String EMAIL_FROM = "Election Commission of Nepal";
    
    // Use absolute path to ensure file is found
    private static final String CONFIG_FILE = "C:\\Users\\KKKKK\\Documents\\NetBeansProjects\\VotingSystemPro\\email-config.properties";
    
    static {
        System.out.println("🔍 Looking for email config at: " + CONFIG_FILE);
        loadConfig();
    }
    
    /**
     * Load email configuration from properties file
     */
    private static void loadConfig() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        
        System.out.println("📁 Config file exists? " + configFile.exists());
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                
                EMAIL_USERNAME = props.getProperty("email.username", "");
                EMAIL_PASSWORD = props.getProperty("email.password", "");
                SMTP_HOST = props.getProperty("smtp.host", "smtp.gmail.com");
                SMTP_PORT = props.getProperty("smtp.port", "587");
                EMAIL_FROM = props.getProperty("email.from", "Election Commission of Nepal");
                
                System.out.println("✅ Email configuration loaded successfully");
                System.out.println("📧 Username: " + EMAIL_USERNAME);
                System.out.println("📧 Password length: " + (EMAIL_PASSWORD.isEmpty() ? "EMPTY" : EMAIL_PASSWORD.length() + " chars"));
                
                // Test if credentials are placeholders
                if (EMAIL_USERNAME.equals("your-email@gmail.com") || EMAIL_PASSWORD.equals("your-app-password")) {
                    System.err.println("⚠️ Warning: Using placeholder credentials! Please update with real values.");
                }
                
            } catch (IOException e) {
                System.err.println("❌ Error loading email config: " + e.getMessage());
                e.printStackTrace();
                createDefaultConfig();
            }
        } else {
            System.err.println("❌ Email config file NOT FOUND at: " + CONFIG_FILE);
            System.out.println("📝 Creating default configuration file...");
            createDefaultConfig();
        }
    }
    
    /**
     * Create default configuration file
     */
    private static void createDefaultConfig() {
        Properties props = new Properties();
        props.setProperty("smtp.host", "smtp.gmail.com");
        props.setProperty("smtp.port", "587");
        props.setProperty("smtp.auth", "true");
        props.setProperty("smtp.starttls.enable", "true");
        props.setProperty("email.username", "dhirajdhakal460@gmail.com");
        props.setProperty("email.password", "YOUR_16_DIGIT_APP_PASSWORD_HERE");
        props.setProperty("email.from", "Election Commission of Nepal");
        
        File configFile = new File(CONFIG_FILE);
        
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "Email Configuration for Voting System");
            System.out.println("✅ Default email-config.properties created at: " + CONFIG_FILE);
            System.out.println("📝 IMPORTANT: Please update the file with your actual Gmail App Password!");
            System.out.println("📝 File location: " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error creating default config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send voter ID to approved voter's email
     */
    public static boolean sendVoterIdEmail(String toEmail, String voterId, String fullName) {
        // Check if email credentials are configured
        if (EMAIL_USERNAME.isEmpty() || EMAIL_PASSWORD.isEmpty()) {
            System.err.println("❌ Email credentials not configured. Please update email-config.properties");
            System.err.println("📁 File should be at: " + CONFIG_FILE);
            return false;
        }
        
        // Check for placeholder credentials
        if (EMAIL_USERNAME.equals("your-email@gmail.com") || EMAIL_PASSWORD.equals("your-app-password")) {
            System.err.println("❌ Using placeholder credentials! Please update with your actual Gmail App Password.");
            System.err.println("📁 Update file: " + CONFIG_FILE);
            return false;
        }
        
        try {
            // Email configuration
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");
            props.put("mail.debug", "true"); // Enable debug to see what's happening
            
            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            // Enable debug output
            session.setDebug(true);
            
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME, EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("✅ Voter Registration Approved - Your Voter ID");
            message.setSentDate(new java.util.Date());
            
            // Create HTML content
            String htmlContent = generateEmailContent(voterId, fullName);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send email
            System.out.println("📧 Sending email to: " + toEmail);
            Transport.send(message);
            
            System.out.println("✅ Voter ID email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
            
            // Provide helpful error messages
            String errorMsg = e.getMessage().toLowerCase();
            if (errorMsg.contains("username") || errorMsg.contains("password") || errorMsg.contains("authentication")) {
                System.err.println("🔑 Authentication failed! Please check your Gmail App Password.");
                System.err.println("📝 Remember: Use App Password (16 digits), NOT your regular Gmail password.");
                System.err.println("🔗 Get App Password: https://myaccount.google.com/apppasswords");
            } else if (errorMsg.contains("timeout") || errorMsg.contains("connect")) {
                System.err.println("🌐 Connection timeout! Check your internet connection.");
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== BULK EMAIL METHODS ====================
    
    /**
     * Send bulk emails to multiple recipients
     */
    public static boolean sendBulkEmails(List<String> emails, List<String> voterIds, List<String> fullNames) {
        if (emails == null || emails.isEmpty()) {
            System.err.println("❌ No email addresses provided for bulk email");
            return false;
        }
        
        if (emails.size() != voterIds.size() || emails.size() != fullNames.size()) {
            System.err.println("❌ Lists size mismatch");
            return false;
        }
        
        // Check if email credentials are configured
        if (EMAIL_USERNAME.isEmpty() || EMAIL_PASSWORD.isEmpty()) {
            System.err.println("❌ Email credentials not configured");
            return false;
        }
        
        System.out.println("📧 Starting bulk email to " + emails.size() + " recipients");
        
        int successCount = 0;
        int failCount = 0;
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
        
        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            String voterId = voterIds.get(i);
            String fullName = fullNames.get(i);
            
            try {
                System.out.println("📤 [" + (i+1) + "/" + emails.size() + "] Sending to: " + email);
                
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL_USERNAME, EMAIL_FROM));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("✅ Voter Registration Approved - Your Voter ID");
                message.setSentDate(new java.util.Date());
                
                String htmlContent = generateEmailContent(voterId, fullName);
                message.setContent(htmlContent, "text/html; charset=utf-8");
                
                Transport.send(message);
                
                System.out.println("   ✅ Sent successfully");
                successCount++;
                
                Thread.sleep(1000);
                
            } catch (Exception e) {
                System.err.println("   ❌ Failed: " + e.getMessage());
                failCount++;
            }
        }
        
        System.out.println("📊 Bulk Email Summary - Success: " + successCount + ", Failed: " + failCount);
        return successCount > 0;
    }
    
    /**
     * Send custom email
     */
    public static boolean sendCustomEmail(String toEmail, String subject, String htmlContent) {
        if (EMAIL_USERNAME.isEmpty() || EMAIL_PASSWORD.isEmpty()) {
            System.err.println("❌ Email credentials not configured");
            return false;
        }
        
        // Check for placeholder credentials
        if (EMAIL_USERNAME.equals("your-email@gmail.com") || EMAIL_PASSWORD.equals("your-app-password")) {
            System.err.println("❌ Using placeholder credentials! Please update with your actual Gmail App Password.");
            return false;
        }
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME, EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            System.out.println("✅ Custom email sent successfully to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error sending custom email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * TEST METHOD - Call this to test email configuration
     */
    public static void sendTestEmail() {
        String testEmail = "dhirajdhakal460@gmail.com";
        System.out.println("🧪 Sending test email to: " + testEmail);
        
        boolean sent = sendCustomEmail(
            testEmail,
            "Test Email from Voting System",
            "<h2>Test Email</h2>" +
            "<p>If you receive this, email configuration is working correctly!</p>" +
            "<p>Sent at: " + new java.util.Date() + "</p>"
        );
        
        if (sent) {
            System.out.println("✅ Test email sent successfully!");
        } else {
            System.err.println("❌ Test email failed! Check email configuration.");
        }
    }
    
    /**
     * Generate HTML email content for voter ID
     */
    private static String generateEmailContent(String voterId, String fullName) {
        String currentDate = new java.text.SimpleDateFormat("dd MMMM yyyy").format(new java.util.Date());
        
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<style>" +
               "body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }" +
               ".container { max-width: 600px; margin: 20px auto; background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.2); }" +
               ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }" +
               ".header h1 { margin: 0; font-size: 28px; }" +
               ".header p { margin: 10px 0 0; opacity: 0.9; }" +
               ".content { padding: 30px; background: white; }" +
               ".voter-id-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 25px; text-align: center; border-radius: 10px; margin: 25px 0; box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3); }" +
               ".voter-id-card h2 { margin: 0 0 15px; font-size: 18px; font-weight: normal; opacity: 0.9; }" +
               ".voter-id-card .id-number { font-size: 32px; font-weight: bold; letter-spacing: 2px; font-family: monospace; background: rgba(255,255,255,0.2); padding: 15px; border-radius: 8px; }" +
               ".info-box { background: #f8f9fa; border-left: 4px solid #667eea; padding: 15px; margin: 20px 0; border-radius: 0 8px 8px 0; }" +
               ".info-box ul { margin: 10px 0; padding-left: 20px; }" +
               ".info-box li { margin: 8px 0; color: #555; }" +
               ".button { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 12px 30px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: bold; margin: 20px 0; box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3); }" +
               ".footer { background: #2c3e50; color: white; padding: 20px; text-align: center; font-size: 13px; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1>🇳🇵 Election Commission of Nepal</h1>" +
               "<p>निर्वाचन आयोग, नेपाल</p>" +
               "</div>" +
               "<div class='content'>" +
               "<h2>Dear " + fullName + ",</h2>" +
               "<p>Congratulations! Your voter registration has been <strong style='color:#28a745;'>APPROVED</strong>.</p>" +
               "<div class='voter-id-card'>" +
               "<h2>Your Unique Voter ID</h2>" +
               "<div class='id-number'>" + voterId + "</div>" +
               "</div>" +
               "<div class='info-box'>" +
               "<h3 style='margin-top:0; color:#667eea;'>📋 Important Information</h3>" +
               "<ul>" +
               "<li><strong>Login:</strong> Use this Voter ID to login</li>" +
               "<li><strong>Password:</strong> Use the password you created during registration</li>" +
               "<li><strong>Voting Rights:</strong> You can vote once for FPTP and once for PR</li>" +
               "</ul>" +
               "</div>" +
               "<p style='color: #666; font-size: 14px; border-top: 1px solid #eee; padding-top: 20px;'>" +
               "<strong>Date:</strong> " + currentDate + "</p>" +
               "</div>" +
               "<div class='footer'>" +
               "<p>🏛️ Election Commission of Nepal</p>" +
               "<p>कन्ट्याक्ट: ०१-४२२७७९९ | ईमेल: support@election.gov.np</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Test email configuration
     */
    public static boolean testEmailConfiguration(String testEmail) {
        System.out.println("🔍 Testing email configuration...");
        System.out.println("📁 Config file: " + CONFIG_FILE);
        System.out.println("📧 Username: " + (EMAIL_USERNAME.isEmpty() ? "EMPTY" : EMAIL_USERNAME));
        System.out.println("📧 Password length: " + (EMAIL_PASSWORD.isEmpty() ? "EMPTY" : EMAIL_PASSWORD.length() + " chars"));
        
        if (EMAIL_USERNAME.isEmpty() || EMAIL_PASSWORD.isEmpty()) {
            System.err.println("❌ Email credentials not configured");
            return false;
        }
        
        // Check for placeholder credentials
        if (EMAIL_USERNAME.equals("your-email@gmail.com") || EMAIL_PASSWORD.equals("your-app-password")) {
            System.err.println("❌ Using placeholder credentials! Please update with your actual Gmail App Password.");
            System.err.println("📁 Update file: " + CONFIG_FILE);
            return false;
        }
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            // Just test connection, don't send email
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, EMAIL_USERNAME, EMAIL_PASSWORD);
            transport.close();
            
            System.out.println("✅ SMTP connection successful!");
            System.out.println("✅ Email configuration is working correctly!");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ SMTP connection failed: " + e.getMessage());
            
            // Provide helpful error messages
            String errorMsg = e.getMessage().toLowerCase();
            if (errorMsg.contains("username") || errorMsg.contains("password") || errorMsg.contains("authentication")) {
                System.err.println("🔑 Authentication failed! Please check your Gmail App Password.");
                System.err.println("📝 Remember: Use App Password (16 digits), NOT your regular Gmail password.");
                System.err.println("🔗 Get App Password: https://myaccount.google.com/apppasswords");
            } else if (errorMsg.contains("timeout") || errorMsg.contains("connect")) {
                System.err.println("🌐 Connection timeout! Check your internet connection.");
            }
            
            return false;
        }
    }
    
    /**
     * Update email configuration
     */
    public static boolean updateConfig(String username, String password) {
        try {
            Properties props = new Properties();
            props.setProperty("smtp.host", SMTP_HOST);
            props.setProperty("smtp.port", SMTP_PORT);
            props.setProperty("smtp.auth", "true");
            props.setProperty("smtp.starttls.enable", "true");
            props.setProperty("email.username", username);
            props.setProperty("email.password", password);
            props.setProperty("email.from", EMAIL_FROM);
            
            File configFile = new File(CONFIG_FILE);
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.store(fos, "Email Configuration for Voting System");
            }
            
            EMAIL_USERNAME = username;
            EMAIL_PASSWORD = password;
            
            System.out.println("✅ Email configuration updated successfully");
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error updating email config: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

/**
 * Helper class for email queue items
 */
class EmailQueueItem {
    private String email;
    private String subject;
    private String content;
    private String status;
    private String errorMessage;
    
    public EmailQueueItem(String email, String subject, String content) {
        this.email = email;
        this.subject = subject;
        this.content = content;
        this.status = "PENDING";
    }
    
    public String getEmail() { return email; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    
    public void setStatus(String status) { this.status = status; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}