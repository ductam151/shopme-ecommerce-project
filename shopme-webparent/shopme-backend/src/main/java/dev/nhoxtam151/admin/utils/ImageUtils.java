package dev.nhoxtam151.admin.utils;

import java.io.InputStream;

public interface ImageUtils {
    public void uploadImage(String url, String token, byte[] image);

    public InputStream retrieveImage(String url, String token);
}
