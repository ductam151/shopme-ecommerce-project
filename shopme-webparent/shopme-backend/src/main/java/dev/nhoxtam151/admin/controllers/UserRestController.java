package dev.nhoxtam151.admin.controllers;

import dev.nhoxtam151.admin.services.UserService;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/check_email")
    public String existByEmail(@RequestParam(name = "id") String id, @RequestParam(name = "email") String email) {
        if (id.isEmpty()) {
            return userService.existsByEmail(email) ? "Duplicated" : "OK";
        }
        return "OK";
    }

    @GetMapping("/photo/{photoName}")
    public byte[] showUserPhoto(@PathVariable String photoName) throws IOException {
        int targetHeight = 150;
        int targetWidth = 150;  // Example width
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage image = resizeImage(ImageIO.read(userService.retrieveImage(photoName)), targetWidth, targetHeight);
        //BufferedImage image = ImageIO.read(userService.retrieveImage(photoName));
        ImageIO.write(image, "jpg", os);
        return os.toByteArray();
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // Create a temporary image with the new size
        Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        // Create a BufferedImage to draw the resized image onto
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        // Draw the scaled image onto the BufferedImage
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose(); // Release resources

        return resized;
    }

}
