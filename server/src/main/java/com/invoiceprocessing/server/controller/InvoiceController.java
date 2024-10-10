package com.invoiceprocessing.server.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.invoiceprocessing.server.model.Invoice;
import com.invoiceprocessing.server.services.InvoiceService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


@RestController
@CrossOrigin
public class InvoiceController {

    @Autowired
     InvoiceService invoiceService;

    @Autowired
    private JavaMailSender mailSender;


    @PostMapping("/invoice")
    public Invoice addInvoice(@RequestBody Invoice invoice) {
        return this.invoiceService.addInvoice(invoice);
    }

    @GetMapping("/invoice")
    public List<Invoice> getInvoices() {
        return this.invoiceService.getInvoices();
    }

    @DeleteMapping("/invoice/{invoiceId}")
    public Invoice deleteInvoice(@PathVariable String invoiceId){
        return this.invoiceService.deleteinvoice(Long.parseLong(invoiceId));
    }

    @GetMapping("/invoice/pdf/{invoiceId}")
    public void generateInvoicePDF(@PathVariable long invoiceId, HttpServletResponse response) throws IOException, DocumentException {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        if (invoice == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Document document = new Document(PageSize.A4, 50, 50, 50, 50); // A4 size with margins
        response.setContentType("application/pdf");
        PdfWriter.getInstance(document, response.getOutputStream());

        // Open the document to add content
        document.open();

        // Add Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Invoice", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add a line separator
        LineSeparator ls = new LineSeparator();
        document.add(new Chunk(ls));

        // Add company name and logo (if available)
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Paragraph companyName = new Paragraph("VTS Enterprises India", headerFont);
        Paragraph companyAddress = new Paragraph("First Floor, SRP Startford,\nRajiv Gandhi Salai, PTK Nagar\nOMR Road, Thiruvanmiyur,\nChennai, 600041\nPhone: 7824036322\nEmail: admin@vtsenterprises.com", FontFactory.getFont(FontFactory.HELVETICA, 10));
        companyName.setAlignment(Element.ALIGN_LEFT);
        companyAddress.setAlignment(Element.ALIGN_LEFT);
        document.add(companyName);
        document.add(companyAddress);

        // Add space before invoice details
        document.add(Chunk.NEWLINE);

        // Create a table for invoice details
        PdfPTable table = new PdfPTable(2); // 2 columns
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Set column widths
        float[] columnWidths = {1f, 2f};
        table.setWidths(columnWidths);

        // Add table headers
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        PdfPCell cell1 = new PdfPCell(new Phrase("Field", tableHeaderFont));
        cell1.setBackgroundColor(BaseColor.DARK_GRAY);
        cell1.setPadding(5);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("Details", tableHeaderFont));
        cell2.setBackgroundColor(BaseColor.DARK_GRAY);
        cell2.setPadding(5);
        table.addCell(cell2);

        // Add invoice details
        addTableRow(table, "Invoice ID", String.valueOf(invoice.getId()));
        addTableRow(table, "Vendor", invoice.getVendor());
        addTableRow(table, "Product", invoice.getProduct());
        addTableRow(table, "Amount", String.valueOf(invoice.getAmount()));
        addTableRow(table, "Date", invoice.getDate().toString());
        addTableRow(table, "Status", invoice.getAction());

        document.add(table);

        // Add Footer
        Paragraph footer = new Paragraph("Thank you for your business!", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        // Close the document
        document.close();
    }

    // Helper method to add rows to the table
    private void addTableRow(PdfPTable table, String field, String value) {
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        PdfPCell fieldCell = new PdfPCell(new Phrase(field, cellFont));
        fieldCell.setPadding(5);
        table.addCell(fieldCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, cellFont));
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }



        


    @PatchMapping("/invoice/approve/{invoiceId}")
    public Invoice approveInvoice(@PathVariable long invoiceId){
        try {
            return invoiceService.approveInvoice(invoiceId);
        }catch(Exception e){
            logger.error("Error approving with ID {}: {} ",invoiceId,e.getMessage());
            throw e;
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    private void sendPaymentReminder(String email, Invoice invoice) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Payment Reminder: Invoice #" + invoice.getId());
        message.setText("Dear " + invoice.getVendor() + ",\n\n"
                + "This is a reminder for the payment of your invoice for " + invoice.getProduct() + "."
                + "\nAmount: " + invoice.getAmount() + " Rs"
                + "\nDue Date: " + invoice.getDate()
                + "\n\nThank you.");
        mailSender.send(message);

        logger.info("Payment reminder sent to: {}", email);
    }
}
