package votingsystempro.utils;

import votingsystempro.models.Voter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Image;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.FileOutputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

public class PDFGenerator {
    
    // Colors - High contrast for visibility
    private static final BaseColor PRIMARY_DARK = new BaseColor(17, 24, 39);      // #111827
    private static final BaseColor ACCENT_BLUE = new BaseColor(37, 99, 235);      // #2563EB
    private static final BaseColor TEXT_PRIMARY = new BaseColor(0, 0, 0);         // #000000 (BLACK)
    private static final BaseColor TEXT_SECONDARY = new BaseColor(75, 85, 99);    // #4B5563
    private static final BaseColor CARD_BG = new BaseColor(255, 255, 255);        // #FFFFFF (WHITE)
    private static final BaseColor WHITE = new BaseColor(255, 255, 255);
    private static final BaseColor GREEN_VOTED = new BaseColor(22, 163, 74);      // #16A34A
    private static final BaseColor GRAY_PENDING = new BaseColor(156, 163, 175);   // #9CA3AF
    private static final BaseColor BORDER_COLOR = new BaseColor(209, 213, 219);   // #D1D5DB
    
    // Monospaced font for security serial
    private static Font monoFont;
    
    static {
        try {
            monoFont = FontFactory.getFont(FontFactory.COURIER, 8, TEXT_SECONDARY);
        } catch (Exception e) {
            monoFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_SECONDARY);
        }
    }
    
    public static boolean generateVoterInfoPDF(Voter voter, String filePath) {
        Document document = new Document(PageSize.A4, 36, 36, 50, 50);
        
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Add light watermark
            addSecurityWatermark(writer);
            
            // Add header
            addHeader(document, writer);
            
            // Hero Section - Voter ID
            addHeroSection(document, voter);
            
            // Bento Box Layout
            addBentoBox(document, writer, voter);
            
            // Voting Status Badges
            addVotingBadges(document, voter);
            
            // Security Footer
            addSecurityFooter(document, voter);
            
            document.close();
            System.out.println("✅ Modern 2026 PDF generated: " + filePath);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static void addSecurityWatermark(PdfWriter writer) {
        try {
            PdfContentByte canvas = writer.getDirectContentUnder();
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.03f); // Very light watermark
            
            canvas.saveState();
            canvas.setGState(gs);
            
            BaseFont font = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED);
            
            // Light watermark pattern
            for (int i = 0; i < 800; i += 100) {
                for (int j = 0; j < 1100; j += 100) {
                    canvas.beginText();
                    canvas.setFontAndSize(font, 16);
                    canvas.setRGBColorFill(200, 200, 200);
                    canvas.showTextAligned(Element.ALIGN_CENTER, "⚡", i, j, 0);
                    canvas.endText();
                }
            }
            
            canvas.restoreState();
        } catch (Exception e) {
            // Ignore watermark errors
        }
    }
    
    private static void addHeader(Document document, PdfWriter writer) {
        try {
            PdfContentByte canvas = writer.getDirectContent();
            
            // Light header background
            canvas.saveState();
            canvas.setRGBColorFill(PRIMARY_DARK.getRed(), PRIMARY_DARK.getGreen(), PRIMARY_DARK.getBlue());
            canvas.rectangle(0, document.getPageSize().getHeight() - 60, 
                           document.getPageSize().getWidth(), 60);
            canvas.fill();
            
            canvas.restoreState();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void addHeroSection(Document document, Voter voter) throws DocumentException {
        // Large Voter ID text - WHITE on dark header
        Paragraph heroText = new Paragraph("VOTER ID CARD", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, WHITE));
        heroText.setAlignment(Element.ALIGN_CENTER);
        heroText.setSpacingBefore(-35);
        document.add(heroText);
        
        // Document number
        Paragraph docNumber = new Paragraph("Document No: ECN-" + 
            new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + "-" + voter.getUserId(),
            FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(200, 200, 200)));
        docNumber.setAlignment(Element.ALIGN_CENTER);
        docNumber.setSpacingBefore(5);
        docNumber.setSpacingAfter(25);
        document.add(docNumber);
    }
    
    private static void addBentoBox(Document document, PdfWriter writer, Voter voter) throws DocumentException {
        // Main container table
        PdfPTable bentoTable = new PdfPTable(2);
        bentoTable.setWidthPercentage(98);
        bentoTable.setWidths(new float[]{1f, 2f});
        bentoTable.setSpacingBefore(20);
        bentoTable.setSpacingAfter(20);
        
        // Left Column - Photo + QR
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(15);
        leftCell.setBackgroundColor(WHITE);
        leftCell.setVerticalAlignment(Element.ALIGN_TOP);
        
        // Photo with frame
        addPhotoWithFrame(leftCell, writer, voter);
        
        // QR Code
        addQRWithFrame(leftCell, writer, voter);
        
        bentoTable.addCell(leftCell);
        
        // Right Column - Voter Details
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.BOX);
        rightCell.setBorderColor(BORDER_COLOR);
        rightCell.setBorderWidth(1);
        rightCell.setPadding(20);
        rightCell.setBackgroundColor(WHITE);
        rightCell.setVerticalAlignment(Element.ALIGN_TOP);
        
        addVoterDetails(rightCell, voter);
        
        bentoTable.addCell(rightCell);
        
        document.add(bentoTable);
    }
    
    private static void addPhotoWithFrame(PdfPCell cell, PdfWriter writer, Voter voter) {
        try {
            // Photo frame
            PdfPTable frameTable = new PdfPTable(1);
            frameTable.setWidthPercentage(80);
            frameTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            PdfPCell frameCell = new PdfPCell();
            frameCell.setBorder(Rectangle.BOX);
            frameCell.setBorderColor(ACCENT_BLUE);
            frameCell.setBorderWidth(2);
            frameCell.setPadding(10);
            frameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            frameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            frameCell.setFixedHeight(140);
            
            if (voter.getPhotoPath() != null && !voter.getPhotoPath().isEmpty()) {
                File photoFile = new File(voter.getPhotoPath());
                if (photoFile.exists()) {
                    Image photo = Image.getInstance(photoFile.getAbsolutePath());
                    photo.scaleToFit(110, 110);
                    frameCell.addElement(photo);
                } else {
                    frameCell.addElement(new Paragraph("📷", 
                        FontFactory.getFont(FontFactory.HELVETICA, 48, TEXT_SECONDARY)));
                }
            } else {
                frameCell.addElement(new Paragraph("📷", 
                    FontFactory.getFont(FontFactory.HELVETICA, 48, TEXT_SECONDARY)));
            }
            
            frameTable.addCell(frameCell);
            cell.addElement(frameTable);
            cell.addElement(new Paragraph(" "));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void addQRWithFrame(PdfPCell cell, PdfWriter writer, Voter voter) {
        try {
            PdfPTable qrFrame = new PdfPTable(1);
            qrFrame.setWidthPercentage(70);
            qrFrame.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            PdfPCell qrCell = new PdfPCell();
            qrCell.setBorder(Rectangle.BOX);
            qrCell.setBorderColor(BORDER_COLOR);
            qrCell.setBorderWidth(1);
            qrCell.setPadding(8);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            String qrContent = String.format(
                "VOTER VERIFICATION\nID: VOT%d\nName: %s\nFPTP: %s\nPR: %s",
                voter.getUserId(),
                voter.getFullName(),
                voter.isHasVotedFptp() ? "VOTED" : "PENDING",
                voter.isHasVotedPr() ? "VOTED" : "PENDING"
            );
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 100, 100);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            Image qrImage = Image.getInstance(baos.toByteArray());
            qrImage.scaleToFit(80, 80);
            qrCell.addElement(qrImage);
            
            qrFrame.addCell(qrCell);
            cell.addElement(qrFrame);
            
            // QR label
            Paragraph qrLabel = new Paragraph("Scan to Verify", 
                FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_SECONDARY));
            qrLabel.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(qrLabel);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void addVoterDetails(PdfPCell cell, Voter voter) throws DocumentException {
        // Title
        Paragraph title = new Paragraph("VOTER INFORMATION", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, PRIMARY_DARK));
        title.setSpacingAfter(20);
        cell.addElement(title);
        
        // Details with icons - BLACK text for visibility
        String[][] details = {
            {"🆔 Voter ID:", "VOT" + voter.getUserId()},
            {"👤 Full Name:", voter.getFullName()},
            {"📅 Date of Birth:", new SimpleDateFormat("dd MMMM yyyy").format(voter.getDateOfBirth())},
            {"🎂 Age:", voter.getAge() + " years"},
            {"🔢 Citizenship No:", voter.getCitizenshipNumber()},
            {"👨 Father's Name:", voter.getFatherName()},
            {"👩 Mother's Name:", voter.getMotherName()},
            {"📍 Address:", voter.getAddress()},
            {"📞 Phone:", voter.getPhoneNumber()},
            {"✉️ Email:", voter.getEmail()}
        };
        
        for (String[] detail : details) {
            PdfPTable rowTable = new PdfPTable(2);
            rowTable.setWidthPercentage(100);
            rowTable.setWidths(new float[]{1.2f, 2f});
            
            // Label (right aligned) - BLACK text
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_PRIMARY);
            PdfPCell labelCell = new PdfPCell(new Phrase(detail[0], labelFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelCell.setPadding(5);
            rowTable.addCell(labelCell);
            
            // Value (left aligned) - BLACK text
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_PRIMARY);
            PdfPCell valueCell = new PdfPCell(new Phrase(detail[1], valueFont));
            valueCell.setBorder(Rectangle.NO_BORDER);
            valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            valueCell.setPadding(5);
            rowTable.addCell(valueCell);
            
            cell.addElement(rowTable);
        }
    }
    
    private static void addVotingBadges(Document document, Voter voter) throws DocumentException {
        PdfPTable badgesTable = new PdfPTable(2);
        badgesTable.setWidthPercentage(80);
        badgesTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        badgesTable.setSpacingBefore(20);
        badgesTable.setSpacingAfter(20);
        badgesTable.setWidths(new float[]{1f, 1f});
        
        // FPTP Badge
        PdfPCell fptpCell = createStatusBadge(
            "FPTP VOTE", 
            voter.isHasVotedFptp(),
            voter.isHasVotedFptp() ? "✓ VOTED" : "⏳ PENDING"
        );
        badgesTable.addCell(fptpCell);
        
        // PR Badge
        PdfPCell prCell = createStatusBadge(
            "PR VOTE", 
            voter.isHasVotedPr(),
            voter.isHasVotedPr() ? "✓ VOTED" : "⏳ PENDING"
        );
        badgesTable.addCell(prCell);
        
        document.add(badgesTable);
    }
    
    private static PdfPCell createStatusBadge(String title, boolean isVoted, String status) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        
        // Title - BLACK text
        Paragraph titlePara = new Paragraph(title, 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, TEXT_PRIMARY));
        titlePara.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(titlePara);
        
        // Badge background
        PdfPTable badgeTable = new PdfPTable(1);
        badgeTable.setWidthPercentage(80);
        badgeTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        BaseColor bgColor = isVoted ? GREEN_VOTED : GRAY_PENDING;
        Font statusFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, WHITE);
        
        PdfPCell badgeCell = new PdfPCell(new Phrase(status, statusFont));
        badgeCell.setBackgroundColor(bgColor);
        badgeCell.setBorder(Rectangle.NO_BORDER);
        badgeCell.setPadding(8);
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        badgeTable.addCell(badgeCell);
        cell.addElement(badgeTable);
        
        return cell;
    }
    
    private static void addSecurityFooter(Document document, Voter voter) throws DocumentException {
        // Decorative line
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        line.getDefaultCell().setBorder(Rectangle.TOP);
        line.getDefaultCell().setBorderColor(ACCENT_BLUE);
        line.getDefaultCell().setBorderWidth(1.5f);
        line.addCell("");
        document.add(line);
        
        // Security Serial (monospaced) - Dark text
        String serial = String.format("SEC•%s•VOT%d•%s", 
            new SimpleDateFormat("yyyyMMddHHmm").format(new java.util.Date()),
            voter.getUserId(),
            generateChecksum(voter.getUserId())
        );
        
        Paragraph securitySerial = new Paragraph(serial, 
            FontFactory.getFont(FontFactory.COURIER, 8, TEXT_PRIMARY));
        securitySerial.setAlignment(Element.ALIGN_CENTER);
        securitySerial.setSpacingBefore(15);
        document.add(securitySerial);
        
        // Signature with stamp icon - Dark text
        Paragraph signature = new Paragraph("✍️ Authorized Signature / अधिकृत हस्ताक्षर", 
            FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_PRIMARY));
        signature.setAlignment(Element.ALIGN_LEFT);
        signature.setSpacingBefore(10);
        document.add(signature);
        
        Paragraph stampLine = new Paragraph("_________________________________________", 
            FontFactory.getFont(FontFactory.HELVETICA, 12, TEXT_SECONDARY));
        stampLine.setAlignment(Element.ALIGN_LEFT);
        stampLine.setSpacingBefore(2);
        document.add(stampLine);
        
        // Stamp icon - Dark text
        Paragraph stampIcon = new Paragraph("🔏 Election Commission of Nepal • निर्वाचन आयोग, नेपाल", 
            FontFactory.getFont(FontFactory.HELVETICA, 7, TEXT_SECONDARY));
        stampIcon.setAlignment(Element.ALIGN_RIGHT);
        stampIcon.setSpacingBefore(-15);
        document.add(stampIcon);
        
        // Disclaimer - Dark text
        Paragraph disclaimer = new Paragraph(
            "This is a digitally generated document. Valid for official purposes only.", 
            FontFactory.getFont(FontFactory.HELVETICA, 6, TEXT_SECONDARY));
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        disclaimer.setSpacingBefore(15);
        document.add(disclaimer);
    }
    
    private static String generateChecksum(int voterId) {
        String input = voterId + "ECN2026";
        int hash = input.hashCode();
        return String.format("%04X", Math.abs(hash) % 65536);
    }
}