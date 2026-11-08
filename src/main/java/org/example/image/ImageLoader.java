package org.example.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageLoader{
    public static BufferedImage loadImage(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("I didn't find the image file:  " + filePath);
        }
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("I cannot load the image file:  " + filePath);
        }
        return image;
    }
}