package org.example.detector;

import org.example.processing.Convolution;

public class GaussianSmooth {
    private final double[][] kernel = {
            {1.0/16, 2.0/16, 1.0/16},
            {2.0/16, 4.0/16, 2.0/16},
            {1.0/16, 2.0/16, 1.0/16}
    };

    private final Convolution convolution = new Convolution();

    public double[][] apply(double[][] image) {
        return convolution.ConvolutionHelper(image, kernel);
    }
}
