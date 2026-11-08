package org.example.evaluation;

import java.util.ArrayList;
import java.util.List;

public class BoundaryMatcher {
    public int countMatches(double[][] detected, double[][] reference, int tolerance) {
        validateInputs(detected, reference, tolerance);
        int height = detected.length;
        int width = detected[0].length;

        int[][] referenceIndex = new int[height][width];
        for (int y = 0; y < height; y++) {
            java.util.Arrays.fill(referenceIndex[y], -1);}
        int referenceCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (reference[y][x] == 255)
                    referenceIndex[y][x] = referenceCount++;
            }
        }
        List<List<Integer>> graph = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (detected[y][x] != 255)
                    continue;
                List<Integer> neighbors = new ArrayList<>();

                int minY = Math.max(0, y - tolerance);
                int maxY = Math.min(height - 1, y + tolerance);
                int minX = Math.max(0, x - tolerance);
                int maxX = Math.min(width - 1, x + tolerance);

                for (int ry = minY; ry <= maxY; ry++) {
                    for (int rx = minX; rx <= maxX; rx++) {
                        int dx = x - rx;
                        int dy = y - ry;

                        if (dx * dx + dy * dy <= tolerance * tolerance && referenceIndex[ry][rx] != -1)
                            neighbors.add(referenceIndex[ry][rx]);
                    }
                }

                graph.add(neighbors);
            }
        }
        int[] matchedTo = new int[referenceCount];
        java.util.Arrays.fill(matchedTo, -1);

        int matches = 0;
        for (int detectedIndex = 0;
             detectedIndex < graph.size();
             detectedIndex++) {
            boolean[] visited = new boolean[referenceCount];

            if (findMatch(detectedIndex, graph, matchedTo, visited)) {
                matches++;
            }
        }
        return matches;
    }

    private boolean findMatch(int detectedIndex, List<List<Integer>> graph, int[] matchedTo, boolean[] visited) {
        for (int referenceIndex : graph.get(detectedIndex)) {
            if (visited[referenceIndex])
                continue;
            visited[referenceIndex] = true;

            if (matchedTo[referenceIndex] == -1 || findMatch(matchedTo[referenceIndex], graph, matchedTo, visited)) {
                matchedTo[referenceIndex] = detectedIndex;
                return true;
            }
        }
        return false;
    }

    private void validateInputs(double[][] detected, double[][] reference, int tolerance) {

        if (detected == null || reference == null || detected.length == 0 || reference.length == 0 || detected.length != reference.length || tolerance < 0) {
            throw new IllegalArgumentException("Invalid images or tolerance.");
        }

        int width = detected[0].length;

        if (width == 0 || reference[0].length != width) {
            throw new IllegalArgumentException("Image dimensions must match.");
        }

        for (int y = 0; y < detected.length; y++) {
            if (detected[y] == null || reference[y] == null ||
                    detected[y].length != width ||
                    reference[y].length != width) {
                throw new IllegalArgumentException(
                        "Images must be rectangular and have matching dimensions.");
            }
        }
    }
}
