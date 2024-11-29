package dev.nhoxtam151.admin.services;

import dev.nhoxtam151.shopmecommon.models.User;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface UserExporter {
    void export(String fileName, List<User> users, HttpServletResponse response) throws IOException;
}
