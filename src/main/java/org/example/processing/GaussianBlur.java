package org.example.processing;

public class GaussianBlur {
    private double[][] gaussianKernel = {
            {1.0/256, 4.0/256, 6.0/256, 4.0/256, 1.0/256},
            {4.0/256, 16.0/256, 24.0/256, 16.0/256, 4.0/256},
            {6.0/256, 24.0/256, 36.0/256, 24.0/256, 6.0/256},
            {4.0/256, 16.0/256, 24.0/256, 16.0/256, 4.0/256},
            {1.0/256, 4.0/256, 6.0/256, 4.0/256, 1.0/256}
    };

    public double[][] gaussianblur(double[][] image) {
        Convolution convolution = new Convolution();
        return convolution.ConvolutionHelper(image, gaussianKernel);
    }
}