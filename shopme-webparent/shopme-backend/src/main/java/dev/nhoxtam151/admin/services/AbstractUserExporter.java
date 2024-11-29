package dev.nhoxtam151.admin.services;

import dev.nhoxtam151.shopmecommon.models.User;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public abstract class AbstractUserExporter implements UserExporter {
    public void setResponseHeader(String fileName, ExtensionType extension, String contentType, HttpServletResponse response) {
        response.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", fileName + extension.getText());
        response.setHeader(headerKey, headerValue);
    }

    @Override
    public abstract void export(String fileName, List<User> users, HttpServletResponse response) throws IOException;

    public enum ExtensionType {
        xlsx(".xlsx"),
        csv(".csv"),
        pdf(".pdf");
        private String text;

        ExtensionType(String s) {
            this.text = s;
        }

        public String getText() {
            return text;
        }
    }
}
