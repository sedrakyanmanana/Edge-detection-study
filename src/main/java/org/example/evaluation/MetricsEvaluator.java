package org.example.evaluation;

public class MetricsEvaluator {
    private final BoundaryMatcher matcher = new BoundaryMatcher();
    private void validateDimensions(double[][] detected, double[][] reference) {
        if (detected == null || reference == null || detected.length == 0 || reference.length == 0)
            throw new IllegalArgumentException("Images cannot be null or empty.");

        if (detected.length != reference.length)
            throw new IllegalArgumentException("Image heights must match.");

        int width = detected[0].length;

        if (width == 0 || reference[0].length != width)
            throw new IllegalArgumentException("Image widths must match and cannot be zero.");


        for (int y = 0; y < detected.length; y++) {
            if (detected[y] == null || reference[y] == null ||
                    detected[y].length != width ||
                    reference[y].length != width) {
                throw new IllegalArgumentException("Images must be rectangular and have matching dimensions.");
            }
        }
    }

    public double calculatePrecision(double[][] detected, double[][] reference) {

        validateDimensions(detected, reference);

        int TP = 0;
        int FP = 0;

        for (int y = 0; y < detected.length; y++) {
            for (int x = 0; x < detected[y].length; x++) {
                if (detected[y][x] == 255 && reference[y][x] == 255) {
                    TP++;
                } else if (detected[y][x] == 255 && reference[y][x] == 0) {
                    FP++;
                }
            }
        }
        if (TP + FP == 0)
            return 0.0;

        return (double) TP/(TP + FP);
    }

    public double calculateRecall(double[][] detected, double[][] reference) {
        validateDimensions(detected, reference);

        int TP = 0;
        int FN = 0;

        for (int y = 0; y < detected.length; y++) {
            for (int x = 0; x < detected[y].length; x++) {
                if (detected[y][x] == 255 && reference[y][x] == 255)
                    TP++;
                 else if (detected[y][x] == 0 && reference[y][x] == 255)
                    FN++;

            }
        }

        if (TP+FN == 0)
            return 0.0;

        return (double) TP / (TP + FN);
    }

    public double calculateF1(double precision, double recall) {

        if (precision + recall == 0)
            return 0.0;
        return 2 * (precision * recall) / (precision + recall);
    }
    public double calculatePrecisionWithTolerance(
            double[][] detected,
            double[][] reference,
            int tolerance) {

        int TP = matcher.countMatches(detected, reference, tolerance);
        int detectedEdges = countEdges(detected);
        if (detectedEdges == 0)
            return 0.0;

        return (double) TP / detectedEdges;
    }

    public double calculateRecallWithTolerance(double[][] detected, double[][] reference, int tolerance) {
        int TP = matcher.countMatches(detected, reference, tolerance);
        int referenceEdges = countEdges(reference);
        if (referenceEdges == 0)
            return 0.0;

        return (double) TP / referenceEdges;
    }

    private int countEdges(double[][] image) {
        int count = 0;
        for (double[] row : image) {
            for (double pixel : row) {
                if (pixel == 255) {
                    count++;
                }
            }
        }
        return count;
    }
}
