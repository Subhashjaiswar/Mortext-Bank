package com.sanviitech.mortextBank.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sanviitech.mortextBank.entity.Transaction;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PDFGenerator {
    
    public byte[] generateStatement(List<Transaction> transactions, String accountNumber, String accountHolder) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            
            Paragraph title = new Paragraph("Account Statement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            document.add(Chunk.NEWLINE);
            
            Paragraph accountInfo = new Paragraph("Account Number: " + accountNumber, normalFont);
            document.add(accountInfo);
            
            Paragraph holderInfo = new Paragraph("Account Holder: " + accountHolder, normalFont);
            document.add(holderInfo);
            
            document.add(Chunk.NEWLINE);
            
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            PdfPCell cell1 = new PdfPCell(new Phrase("Transaction ID", headerFont));
            PdfPCell cell2 = new PdfPCell(new Phrase("Date", headerFont));
            PdfPCell cell3 = new PdfPCell(new Phrase("Type", headerFont));
            PdfPCell cell4 = new PdfPCell(new Phrase("Amount", headerFont));
            PdfPCell cell5 = new PdfPCell(new Phrase("Balance", headerFont));
            PdfPCell cell6 = new PdfPCell(new Phrase("Status", headerFont));
            
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (Transaction transaction : transactions) {
                table.addCell(new PdfPCell(new Phrase(transaction.getTransactionId(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(transaction.getTransactionDate().format(formatter), normalFont)));
                table.addCell(new PdfPCell(new Phrase(transaction.getTransactionType().name(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(transaction.getAmount().toString(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(transaction.getBalanceAfter().toString(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(transaction.getStatus().name(), normalFont)));
            }
            
            document.add(table);
            document.close();
            
        } catch (DocumentException e) {
            throw e;
        }
        
        return outputStream.toByteArray();
    }
}
