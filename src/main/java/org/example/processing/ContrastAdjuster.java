package org.example.processing;

public class ContrastAdjuster {
    public double[][] adjustContrast(double[][] image, double contrast) {
        if (contrast < 0) {
            throw new IllegalArgumentException("Hey!! Contrast must not be negative!");
        }
        double[][] result = new double[image.length][image[0].length];
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                double pixel = image[y][x];
                double adjusted =  contrast * (pixel - 128) + 128;
                adjusted = Math.max(0, Math.min(255, adjusted));
                result[y][x] = adjusted;
            }
        }

        return result;
    }
}
