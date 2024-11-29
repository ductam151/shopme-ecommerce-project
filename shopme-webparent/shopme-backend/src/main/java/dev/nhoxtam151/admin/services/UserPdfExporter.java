package dev.nhoxtam151.admin.services;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import dev.nhoxtam151.shopmecommon.models.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@Component
public class UserPdfExporter extends AbstractUserExporter {
    @Override
    public void export(String fileName, List<User> users, HttpServletResponse response) throws IOException {
        super.setResponseHeader(fileName, ExtensionType.pdf, "application/pdf", response);
        try (Document document = new com.lowagie.text.Document(PageSize.A4);) {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(Color.BLUE);
            Paragraph paragraph = new Paragraph("List of Users", font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.setWidthPercentage(100f);
            pdfTable.setSpacingBefore(10);
            pdfTable.setWidths(new float[] {3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f});
            writeTableHeader(pdfTable);
            writeTableData(pdfTable, users);
            document.add(pdfTable);
        }

    }

    private void writeTableData(PdfPTable pdfTable, List<User> users) {
        for (User user : users) {
            pdfTable.addCell(String.valueOf(user.getId()));
            pdfTable.addCell(user.getEmail());
            pdfTable.addCell(user.getFirstName());
            pdfTable.addCell(user.getLastName());
            pdfTable.addCell(user.getRoles().toString());
            pdfTable.addCell(String.valueOf(user.getEnabled()));
        }
    }

    private void writeTableHeader(PdfPTable pdfTable) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("User-ID", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(cell);
        cell.setPhrase(new Phrase("E-mail", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(cell);
        cell.setPhrase(new Phrase("First Name", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(cell);
        cell.setPhrase(new Phrase("Last Name", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(cell);
        cell.setPhrase(new Phrase("Roles", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(cell);
        cell.setPhrase(new Phrase("Enabled", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(cell);
    }
}
