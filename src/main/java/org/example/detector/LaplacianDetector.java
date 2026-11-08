package org.example.detector;

import org.example.processing.Convolution;

public class LaplacianDetector {
    double[][] laplaceKernel = {
            {0,1,0},
            {1,-4,1},
            {0,1,0}
    };

    Convolution convolution = new Convolution();
    public double[][] gradientMagnitude(double[][] image){
        double[][] resonpse = convolution.ConvolutionHelper(image, laplaceKernel);


        double[][] magnitude = new double[image.length][image[0].length];

        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                magnitude[y][x] = Math.abs(resonpse[y][x]);
            }
        }
        return magnitude;
    }
    public double[][] gradientMagnitudeSmooth(double[][] image){
        GaussianSmooth gaussianSmooth = new GaussianSmooth();
        double[][] smoothed = gaussianSmooth.apply(image);
        return gradientMagnitude(smoothed);

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