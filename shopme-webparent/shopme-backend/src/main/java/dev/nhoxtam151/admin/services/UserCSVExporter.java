package dev.nhoxtam151.admin.services;

import dev.nhoxtam151.admin.models.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Component
public class UserCSVExporter extends AbstractUserExporter implements UserExporter {

    @Override
    public void export(String fileName, List<User> users, HttpServletResponse response) throws IOException {

//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment;filename=" + fileName;
//        response.setHeader(headerKey, headerValue);
//
        super.setResponseHeader(fileName, ExtensionType.csv, "application/csv", response);
        ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User ID", "First Name", "Last Name", "Email", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "firstName", "email", "lastName", "roles", "enabled"};
        writer.writeHeader(csvHeader);

        for (User user : users) {
            writer.write(user, fieldMapping);
        }
        writer.close();
    }
}
