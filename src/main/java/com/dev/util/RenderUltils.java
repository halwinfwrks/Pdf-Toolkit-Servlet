package com.dev.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class RenderUltils {

    private static RenderUltils instance;

    private RenderUltils() {
    }

    public static RenderUltils getInstance() {
        if (instance == null) {
            synchronized (RenderUltils.class) {
                if (instance == null) {
                    instance = new RenderUltils();
                }
            }
        }
        return instance;
    }

    public String encodeToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public byte[] decodeFromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static byte[] imageToByteArray(BufferedImage image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stream.toByteArray();
    }
}
