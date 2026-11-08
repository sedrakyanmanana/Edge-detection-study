package org.example.detector;
import org.example.processing.Convolution;

public class CannyDetector {
    private final SobelDetector sobelDetector = new SobelDetector();
    private final NonMaximumSuppression nonMaximumSuppression = new NonMaximumSuppression();
    private final EdgeHysteresis edgeHysteresis = new EdgeHysteresis();
    private final Convolution convolution = new Convolution();

    private double[][] gaussianKernel = {
            {1.0/256, 4.0/256, 6.0/256, 4.0/256, 1.0/256},
            {4.0/256, 16.0/256, 24.0/256, 16.0/256, 4.0/256},
            {6.0/256, 24.0/256, 36.0/256, 24.0/256, 6.0/256},
            {4.0/256, 16.0/256, 24.0/256, 16.0/256, 4.0/256},
            {1.0/256, 4.0/256, 6.0/256, 4.0/256, 1.0/256}
    };

    public double[][] gaussianSmoothing(double[][] image) {
        return convolution.ConvolutionHelper(image, gaussianKernel);
    }
    public double[][] calculateGradientX(double[][] image) {
        return convolution.ConvolutionHelper(image, sobelDetector.sobelX);
    }
    public double[][] calculateGradientY(double[][] image) {
        return convolution.ConvolutionHelper(image, sobelDetector.sobelY);
    }

    public double[][] gradientMagnitude(double[][] gradientX, double[][] gradientY) {
        double[][] magnitude = new double[gradientX.length][gradientX[0].length];
        for (int y = 0; y < gradientX.length; y++) {
            for (int x = 0; x < gradientX[0].length; x++) {
                magnitude[y][x] = Math.sqrt(gradientX[y][x] * gradientX[y][x] + gradientY[y][x] * gradientY[y][x]);
            }
        }

        return magnitude;
    }

    public double[][] gradientDirection(double[][] gradientX, double[][] gradientY) {
        double[][] direction = new double[gradientX.length][gradientX[0].length];
        for (int y=0;y<gradientX.length;y++) {
            for (int x=0;x<gradientX[0].length;x++) {
                double angle = Math.toDegrees(Math.atan2(gradientY[y][x], gradientX[y][x]));
                direction[y][x] = (angle+180)%180;
            }
        }
        return direction;
    }
    public double[][] doubleThreshold(double[][] suppressed, double lowThreshold, double highThreshold){
        if(lowThreshold < 0 ||  highThreshold<= lowThreshold){
            throw new IllegalArgumentException("Invalid threshold!!");
        }

        double[][] result = new double[suppressed.length][suppressed[0].length];
        for (int y = 0; y < suppressed.length; y++) {
            for (int x = 0; x < suppressed[0].length; x++) {
                double threshold = suppressed[y][x];
                if(threshold >= highThreshold){result[y][x]=255;}
                else if(threshold >= lowThreshold){result[y][x]=75;}
                else{result[y][x]=0;}
            }
        }
        return result;
    }
    public double[][] detectEdges(double[][] image, double lowThreshold, double highThreshold) {
        double[][] smoothed = gaussianSmoothing(image);
        double[][] gradientX = calculateGradientX(smoothed);
        double[][] gradientY = calculateGradientY(smoothed);
        double[][] magnitude = gradientMagnitude(gradientX, gradientY);

        double[][] direction = gradientDirection(gradientX, gradientY);

        double[][] suppressed = nonMaximumSuppression.apply(magnitude, direction);
        double[][] thresholded = doubleThreshold(suppressed, lowThreshold, highThreshold);

        return edgeHysteresis.apply(thresholded);
    }
}