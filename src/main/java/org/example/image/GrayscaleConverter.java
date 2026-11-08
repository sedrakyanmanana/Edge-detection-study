package org.example.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GrayscaleConverter{
    public static double[][] convert(BufferedImage inputImage){
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        double[][] grayscale = new double[height][width];

        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                int pixel = inputImage.getRGB(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                double gray = 0.299 * red + 0.587 * green + 0.114 * blue;

                grayscale[y][x] = gray;
            }
        }
        return grayscale;
    }

    public static BufferedImage GrayscalePreview(double[][] grayscale){
        int height = grayscale.length;
        int width = grayscale[0].length;

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                int gray = (int) Math.round(grayscale[y][x]);
                int grayPixel = (gray << 16) | (gray << 8) | gray;
                outputImage.setRGB(x, y, grayPixel);
            }
        }
        return outputImage;
    }
}