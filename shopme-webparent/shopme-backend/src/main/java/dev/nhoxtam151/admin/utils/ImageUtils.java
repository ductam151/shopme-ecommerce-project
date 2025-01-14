package dev.nhoxtam151.admin.utils;

import java.io.InputStream;

public interface ImageUtils {
    void uploadImage(String url, byte[] image);

    InputStream retrieveImage(String url);
}
