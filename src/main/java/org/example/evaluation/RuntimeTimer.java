
package org.example.evaluation;

import java.util.Arrays;
import java.util.function.Supplier;

public class RuntimeTimer {

    public static class RuntimeStats {
        public final double averageMs;
        public final double medianMs;
        public final double standardDeviationMs;

        public RuntimeStats(double averageMs, double medianMs, double standardDeviationMs) {
            this.averageMs = averageMs;
            this.medianMs = medianMs;
            this.standardDeviationMs = standardDeviationMs;
        }
    }

    private volatile double checksum;
    public RuntimeStats measureRuntime(
            Supplier<double[][]> operation,
            int warmupRuns,
            int measuredRuns) {

        if (operation == null || warmupRuns < 0 || measuredRuns <= 0) {
            throw new IllegalArgumentException("Invalid measurement parameters.");
        }
        for (int i = 0; i < warmupRuns; i++) {
            double[][] result = operation.get();
            consumeResult(result);
        }

        long[] times = new long[measuredRuns];
        for (int i = 0; i < measuredRuns; i++) {
            long start = System.nanoTime();
            double[][] result = operation.get();
            long end = System.nanoTime();

            times[i] = end - start;
            consumeResult(result);
        }

        double total = 0;
        for (long time : times) {
            total += time;
        }
        double averageMs = total / measuredRuns / 1_000_000.0;
        Arrays.sort(times);
        double medianNs;

        if (measuredRuns % 2 == 0) {
            medianNs = (times[measuredRuns / 2 - 1]
                    + times[measuredRuns / 2]) / 2.0;
        } else {
            medianNs = times[measuredRuns / 2];
        }

        double medianMs = medianNs / 1_000_000.0;
        double squaredDifferences = 0;
        for (long time : times) {
            double difference = time - total / measuredRuns;
            squaredDifferences += difference * difference;
        }

        double standardDeviationMs = Math.sqrt(squaredDifferences / measuredRuns) / 1_000_000.0;

        return new RuntimeStats(averageMs, medianMs, standardDeviationMs);
    }

    private void consumeResult(double[][] result) {

        if (result == null)
            throw new IllegalStateException("Detector returned null.");
        double sum = 0;

        for (double[] row : result) {
            for (double pixel : row) {
                sum += pixel;
            }
        }
        checksum = sum;
    }
}
