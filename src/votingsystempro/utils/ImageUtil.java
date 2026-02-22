package votingsystempro.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageUtil {
    
    private static final String BASE_PATH = System.getProperty("user.dir") + "/resources/images/";
    
    static {
        // Create directories if they don't exist
        createDirectory("voter_photos");
        createDirectory("party_logos");
        createDirectory("candidate_photos");
    }
    
    private static void createDirectory(String dirName) {
        File directory = new File(BASE_PATH + dirName);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("✅ Created directory: " + directory.getPath());
            }
        }
    }
    
    public static String saveImage(File sourceFile, String folderName) {
        // Check if file exists
        if (sourceFile == null) {
            System.err.println("❌ Source file is null");
            return null;
        }
        
        if (!sourceFile.exists()) {
            System.err.println("❌ File does not exist: " + sourceFile.getPath());
            return null;
        }
        
        if (!sourceFile.canRead()) {
            System.err.println("❌ Cannot read file (permission denied): " + sourceFile.getPath());
            return null;
        }
        
        // Check file size (max 10MB)
        if (sourceFile.length() > 10 * 1024 * 1024) {
            System.err.println("❌ File too large: " + sourceFile.length() + " bytes (max 10MB)");
            return null;
        }
        
        // Check if it's an image file by extension
        String fileName = sourceFile.getName().toLowerCase();
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
              fileName.endsWith(".png") || fileName.endsWith(".gif") || 
              fileName.endsWith(".bmp") || fileName.endsWith(".svg"))) {
            System.err.println("❌ Not a supported image format: " + fileName);
            return null;
        }
        
        try {
            // Try to read the image first to verify it's valid
            BufferedImage image = null;
            try {
                image = ImageIO.read(sourceFile);
            } catch (Exception e) {
                System.err.println("❌ Error reading image file (might be corrupted): " + e.getMessage());
            }
            
            if (image == null) {
                System.err.println("❌ Could not read image file (unsupported format or corrupted): " + sourceFile.getPath());
                return null;
            }
            
            // Create destination filename with timestamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String extension = getFileExtension(sourceFile.getName());
            if (extension == null) {
                extension = "png"; // default extension
            }
            
            String destFileName = timestamp + "_" + sourceFile.getName();
            File destFile = new File(BASE_PATH + folderName + "/" + destFileName);
            
            // Create parent directories if they don't exist
            destFile.getParentFile().mkdirs();
            
            // Write image
            boolean written = ImageIO.write(image, extension, destFile);
            
            if (written) {
                System.out.println("✅ Image saved to: " + destFile.getAbsolutePath());
                return destFile.getAbsolutePath();
            } else {
                System.err.println("❌ Failed to write image: " + destFile.getPath());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error saving image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static ImageIcon createImageIcon(String path, int width, int height) {
        if (path == null || path.isEmpty()) {
            System.err.println("❌ Image path is null or empty");
            return null;
        }
        
        try {
            File imgFile = new File(path);
            if (!imgFile.exists()) {
                System.err.println("❌ Image file not found: " + path);
                return null;
            }
            
            BufferedImage originalImage = ImageIO.read(imgFile);
            if (originalImage == null) {
                System.err.println("❌ Could not read image (unsupported format): " + path);
                return null;
            }
            
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
            
        } catch (Exception e) {
            System.err.println("❌ Error creating image icon: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return null;
    }
    
    public static boolean deleteImage(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        
        try {
            File file = new File(path);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("✅ Deleted image: " + path);
                } else {
                    System.err.println("❌ Failed to delete image: " + path);
                }
                return deleted;
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting image: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Helper method to validate if file is an image
    public static boolean isValidImage(File file) {
        if (file == null || !file.exists()) return false;
        
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
               name.endsWith(".png") || name.endsWith(".gif") || 
               name.endsWith(".bmp") || name.endsWith(".svg");
    }
}