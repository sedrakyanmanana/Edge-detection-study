
package org.example;

import org.example.detector.SobelDetector;
import org.example.detector.PrewittDetector;
import org.example.detector.LaplacianDetector;
import org.example.detector.CannyDetector;

import org.example.image.ImageLoader;
import org.example.image.GrayscaleConverter;

import org.example.processing.NoiseGenerator;
import org.example.processing.GaussianBlur;
import org.example.processing.ContrastAdjuster;

import org.example.evaluation.MetricsEvaluator;
import org.example.evaluation.RuntimeTimer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


// For testing, the script of Main class was written with an assistance of an AI tool.
public class Main {

    // BSDS500 dataset folders
    private static final String BSDS_IMAGE_ROOT =
            "data/BSR/BSDS500/data/images/";

    private static final String BSDS_GROUND_TRUTH_ROOT =
            "data/bsds500/ground_truth_png/";

    // New CSV: keeps the original baseline CSV untouched
    private static final String RESULTS_CSV =
            "data/output/bsds500_degradation_results.csv";

    // Ten images with converted ground-truth annotations
    private static final String[] BSDS_IMAGES = {
            "10081",
            "100007",
            "100039",
            "100099",
            "101027",
            "101084",
            "102062",
            "103006",
            "103029",
            "103078"
    };

    // Evaluation settings
    private static final int MATCH_TOLERANCE = 1;

    private static final int WARMUP_RUNS = 5;
    private static final int MEASURED_RUNS = 20;

    private static final double SOBEL_THRESHOLD = 50.0;
    private static final double PREWITT_THRESHOLD = 50.0;
    private static final double LAPLACIAN_THRESHOLD = 50.0;

    private static final double CANNY_LOW_THRESHOLD = 50.0;
    private static final double CANNY_HIGH_THRESHOLD = 100.0;


    public static void main(String[] args) {

        try {
            runBsdsDegradationEvaluation();

        } catch (Exception e) {
            System.out.println("An error occurred:");
            e.printStackTrace();
        }
    }


    // ============================================================
    // MAIN EXPERIMENT
    // ============================================================

    public static void runBsdsDegradationEvaluation()
            throws IOException {

        // Create detectors
        SobelDetector sobel = new SobelDetector();
        PrewittDetector prewitt = new PrewittDetector();
        LaplacianDetector laplacian = new LaplacianDetector();
        CannyDetector canny = new CannyDetector();

        // Create processing tools
        NoiseGenerator noiseGenerator = new NoiseGenerator();
        GaussianBlur gaussianBlur = new GaussianBlur();
        ContrastAdjuster contrastAdjuster = new ContrastAdjuster();

        // Evaluation tools
        MetricsEvaluator metrics = new MetricsEvaluator();
        RuntimeTimer runtime = new RuntimeTimer();

        // Prepare CSV
        Path csvPath = Path.of(RESULTS_CSV);

        Files.createDirectories(csvPath.getParent());

        List<String> csvLines = new ArrayList<>();

        csvLines.add(
                "Image,Detector,Condition,Severity," +
                        "Precision,Recall,F1," +
                        "AverageRuntimeMs,MedianRuntimeMs," +
                        "StdDevRuntimeMs,Tolerance"
        );

        System.out.println("==========================================");
        System.out.println("BSDS500 DEGRADATION EXPERIMENT");
        System.out.println("==========================================");

        // Process each BSDS500 image
        for (String imageName : BSDS_IMAGES) {

            System.out.println("\n==========================================");
            System.out.println("IMAGE: " + imageName);
            System.out.println("==========================================");

            // 1. Locate original image
            Path imagePath = findImage(
                    BSDS_IMAGE_ROOT,
                    imageName
            );

            // 2. Locate ground truth
            Path groundTruthPath = findFile(
                    BSDS_GROUND_TRUTH_ROOT,
                    imageName + ".png"
            );

            // 3. Load images
            BufferedImage original =
                    ImageLoader.loadImage(imagePath.toString());

            BufferedImage groundTruthImage =
                    ImageLoader.loadImage(groundTruthPath.toString());

            // 4. Convert to grayscale
            double[][] grayscale =
                    GrayscaleConverter.convert(original);

            double[][] reference =
                    GrayscaleConverter.convert(groundTruthImage);

            // 5. Verify dimensions
            validateMatchingDimensions(grayscale, reference);

            // 6. Create degraded versions
            double[][] noise5 =
                    noiseGenerator.generateNoise(
                            grayscale, 0.025, 0.025
                    );

            double[][] noise10 =
                    noiseGenerator.generateNoise(
                            grayscale, 0.05, 0.05
                    );

            double[][] blurred =
                    gaussianBlur.gaussianblur(grayscale);

            double[][] lowContrast =
                    contrastAdjuster.adjustContrast(
                            grayscale, 0.5
                    );

            // 7. Define experimental conditions
            List<Condition> conditions = new ArrayList<>();

            conditions.add(
                    new Condition("Original", "None", grayscale)
            );

            conditions.add(
                    new Condition("SaltPepperNoise", "5%", noise5)
            );

            conditions.add(
                    new Condition("SaltPepperNoise", "10%", noise10)
            );

            conditions.add(
                    new Condition("GaussianBlur", "DefaultKernel", blurred)
            );

            conditions.add(
                    new Condition("ReducedContrast", "0.5", lowContrast)
            );

            // 8. Evaluate every condition
            for (Condition condition : conditions) {

                System.out.println(
                        "\nCondition: " + condition.name +
                                " | Severity: " + condition.severity
                );

                double[][] testImage = condition.image;

                // Sobel
                evaluateDetector(
                        imageName,
                        "Sobel",
                        condition,
                        () -> sobel.detectEdges(
                                testImage,
                                SOBEL_THRESHOLD
                        ),
                        reference,
                        metrics,
                        runtime,
                        csvLines
                );

                // Prewitt
                evaluateDetector(
                        imageName,
                        "Prewitt",
                        condition,
                        () -> prewitt.detectEdges(
                                testImage,
                                PREWITT_THRESHOLD
                        ),
                        reference,
                        metrics,
                        runtime,
                        csvLines
                );

                // Laplacian
                evaluateDetector(
                        imageName,
                        "Laplacian",
                        condition,
                        () -> laplacian.applyThreshold(
                                laplacian.gradientMagnitude(testImage),
                                LAPLACIAN_THRESHOLD
                        ),
                        reference,
                        metrics,
                        runtime,
                        csvLines
                );

                // Canny
                evaluateDetector(
                        imageName,
                        "Canny",
                        condition,
                        () -> canny.detectEdges(
                                testImage,
                                CANNY_LOW_THRESHOLD,
                                CANNY_HIGH_THRESHOLD
                        ),
                        reference,
                        metrics,
                        runtime,
                        csvLines
                );
            }

            System.out.println(
                    "\nFinished image: " + imageName
            );
        }

        // 9. Save CSV
        Files.write(csvPath, csvLines);

        System.out.println("\n==========================================");
        System.out.println("EXPERIMENT COMPLETED!");
        System.out.println("==========================================");

        System.out.println("Images: " + BSDS_IMAGES.length);
        System.out.println("Conditions per image: 5");
        System.out.println("Detectors: 4");

        System.out.println(
                "Total evaluations: " +
                        (BSDS_IMAGES.length * 5 * 4)
        );

        System.out.println("CSV saved to: " + csvPath);
    }


    // ============================================================
    // CONDITION CLASS
    // ============================================================

    private static class Condition {

        String name;
        String severity;
        double[][] image;

        Condition(
                String name,
                String severity,
                double[][] image
        ) {
            this.name = name;
            this.severity = severity;
            this.image = image;
        }
    }


    // ============================================================
    // DETECTOR EVALUATION
    // ============================================================

    private static void evaluateDetector(
            String imageName,
            String detectorName,
            Condition condition,
            Supplier<double[][]> operation,
            double[][] reference,
            MetricsEvaluator metrics,
            RuntimeTimer runtime,
            List<String> csvLines
    ) {

        System.out.println("\nRunning " + detectorName + "...");

        // Generate edge map
        double[][] detected = operation.get();

        validateMatchingDimensions(detected, reference);
        validateBinaryImage(detected);

        // Calculate precision
        double precision =
                metrics.calculatePrecisionWithTolerance(
                        detected,
                        reference,
                        MATCH_TOLERANCE
                );

        // Calculate recall
        double recall =
                metrics.calculateRecallWithTolerance(
                        detected,
                        reference,
                        MATCH_TOLERANCE
                );

        // Calculate F1
        double f1 = metrics.calculateF1(
                precision,
                recall
        );

        // Measure detector runtime
        RuntimeTimer.RuntimeStats stats =
                runtime.measureRuntime(
                        operation,
                        WARMUP_RUNS,
                        MEASURED_RUNS
                );

        // Display results
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1: " + f1);

        System.out.println(
                "Average runtime: " + stats.averageMs + " ms"
        );

        System.out.println(
                "Median runtime: " + stats.medianMs + " ms"
        );

        System.out.println(
                "Standard deviation: " +
                        stats.standardDeviationMs + " ms"
        );

        // Add results to CSV
        String row = String.join(",",
                imageName,
                detectorName,
                condition.name,
                condition.severity,
                Double.toString(precision),
                Double.toString(recall),
                Double.toString(f1),
                Double.toString(stats.averageMs),
                Double.toString(stats.medianMs),
                Double.toString(stats.standardDeviationMs),
                Integer.toString(MATCH_TOLERANCE)
        );

        csvLines.add(row);
    }


    // ============================================================
    // DATASET FILE SEARCH
    // ============================================================

    private static Path findImage(
            String rootFolder,
            String imageName
    ) throws IOException {

        try (var paths = Files.walk(Path.of(rootFolder))) {

            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> {

                        String fileName =
                                path.getFileName().toString();

                        int dot = fileName.lastIndexOf('.');

                        if (dot < 0) {
                            return false;
                        }

                        String nameWithoutExtension =
                                fileName.substring(0, dot);

                        return nameWithoutExtension.equals(imageName);
                    })
                    .findFirst()
                    .orElseThrow(() -> new IOException(
                            "Original image not found: " + imageName
                    ));
        }
    }


    private static Path findFile(
            String rootFolder,
            String fileName
    ) throws IOException {

        try (var paths = Files.walk(Path.of(rootFolder))) {

            return paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName().toString()
                                    .equals(fileName)
                    )
                    .findFirst()
                    .orElseThrow(() -> new IOException(
                            "Ground-truth file not found: " + fileName
                    ));
        }
    }


    // ============================================================
    // VALIDATION
    // ============================================================

    private static void validateMatchingDimensions(
            double[][] image,
            double[][] reference
    ) {

        if (image == null || reference == null ||
                image.length == 0 || reference.length == 0) {

            throw new IllegalArgumentException(
                    "Images cannot be null or empty."
            );
        }

        if (image.length != reference.length) {

            throw new IllegalArgumentException(
                    "Image and ground truth have different heights."
            );
        }

        for (int y = 0; y < image.length; y++) {

            if (image[y] == null || reference[y] == null ||
                    image[y].length != reference[y].length) {

                throw new IllegalArgumentException(
                        "Image and ground truth have different widths."
                );
            }
        }
    }


    private static void validateBinaryImage(
            double[][] image
    ) {

        if (image == null || image.length == 0) {

            throw new IllegalArgumentException(
                    "Detector returned an empty image."
            );
        }

        for (double[] row : image) {

            if (row == null) {

                throw new IllegalArgumentException(
                        "Detector returned an invalid image row."
                );
            }

            for (double pixel : row) {

                if (pixel != 0.0 && pixel != 255.0) {

                    throw new IllegalArgumentException(
                            "Detector output must contain only 0 and 255. " +
                                    "Found: " + pixel
                    );
                }
            }
        }
    }
}
