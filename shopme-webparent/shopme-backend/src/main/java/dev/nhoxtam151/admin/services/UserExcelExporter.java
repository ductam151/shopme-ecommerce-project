package dev.nhoxtam151.admin.services;

import dev.nhoxtam151.shopmecommon.models.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class UserExcelExporter extends AbstractUserExporter {

    private void writeHeadersLine(XSSFSheet sheet, XSSFWorkbook workbook, XSSFRow row) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);
        String[] headers = {"User Id", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        int headerLength = headers.length;
        for (int i = 0; i < headerLength; i++) {
            createCell(sheet, row, i, headers[i], cellStyle);
        }
    }

    private void createCell(XSSFSheet sheet, XSSFRow row, int columnIndex, Object value, XSSFCellStyle cellStyle) {
        sheet.autoSizeColumn(columnIndex);
        XSSFCell cell = row.createCell(columnIndex);
        switch (value) {
            case Integer i -> cell.setCellValue(i);
            case Double d -> cell.setCellValue(d);
            case Float f -> cell.setCellValue(f);
            case Boolean b -> cell.setCellValue(b);
            case String s -> cell.setCellValue(s);
            case Long l -> cell.setCellValue(l);
            default -> {
            }
        }
        cell.setCellStyle(cellStyle);

    }

    private void writeDataLines(XSSFWorkbook workbook, XSSFSheet sheet, List<User> users) {
        int rowIndex = 1;
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);
        for (User user : users) {
            int columnIndex = 0;
            XSSFRow row = sheet.createRow(rowIndex++);
            createCell(sheet, row, columnIndex++, user.getId(), cellStyle);
            createCell(sheet, row, columnIndex++, user.getEmail(), cellStyle);
            createCell(sheet, row, columnIndex++, user.getFirstName(), cellStyle);
            createCell(sheet, row, columnIndex++, user.getLastName(), cellStyle);
            createCell(sheet, row, columnIndex++, user.getRoles().toString(), cellStyle);
            createCell(sheet, row, columnIndex, user.getEnabled(), cellStyle);
        }
    }

    @Override
    public void export(String fileName, List<User> users, HttpServletResponse response) throws IOException {
        super.setResponseHeader(fileName, ExtensionType.xlsx, "application/octet-stream", response);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Users");
            XSSFRow row = sheet.createRow(0);
            writeHeadersLine(sheet, workbook, row);
            writeDataLines(workbook, sheet, users);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        }
    }
}
