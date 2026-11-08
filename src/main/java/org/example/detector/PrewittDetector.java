package org.example.detector;

import org.example.processing.Convolution;

public class PrewittDetector {
    double[][] prewittX = {
            {-1,0,1},
            {-1,0,1},
            {-1,0,1}
    };

    double[][] prewittY = {
            {-1,-1,-1},
            { 0,0,0},
            { 1,1,1}
    };
    Convolution convolution = new Convolution();
    public double[][] gradientMagnitude(double[][] image){
        double[][] gradientX = convolution.ConvolutionHelper(image, prewittX);
        double[][] gradientY = convolution.ConvolutionHelper(image, prewittY);


        double[][] magnitude = new double[image.length][image[0].length];

        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                magnitude[y][x] = Math.sqrt(gradientX[y][x] * gradientX[y][x] + gradientY[y][x] * gradientY[y][x]);
            }
        }
        return magnitude;
    }

    public double[][] applyThreshold(double[][] magnitude, double threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must be a positive number!");
        }

        double[][] edges = new double[magnitude.length][magnitude[0].length];
        for (int y = 0; y < magnitude.length; y++) {
            for (int x = 0; x < magnitude[0].length; x++) {
                edges[y][x] = magnitude[y][x] >= threshold ? 255 : 0;
            }
        }
        return edges;
    }

    public double[][] detectEdges(double[][] image, double threshold) {
        double[][] magnitude = gradientMagnitude(image);
        return applyThreshold(magnitude, threshold);
    }

}