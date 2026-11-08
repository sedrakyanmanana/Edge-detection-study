
package org.example.detector;

import java.util.LinkedList;
import java.util.Queue;

public class EdgeHysteresis {

    public double[][] apply(double[][] thresholded) {
        int height = thresholded.length;
        int width = thresholded[0].length;
        double[][] result = new double[height][width];
        Queue<int[]> queue = new LinkedList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (thresholded[y][x] == 255) {
                    result[y][x] = 255;
                    queue.add(new int[]{y, x});
                }
            }
        }
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int y = current[0];
            int x = current[1];
            for (int i = 0; i < 8; i++) {
                int neighborY = y + dy[i];
                int neighborX = x + dx[i];

                if (neighborY < 0 || neighborY >= height || neighborX < 0 || neighborX >= width)
                    continue;

                if (thresholded[neighborY][neighborX] == 75 && result[neighborY][neighborX] == 0) {
                    result[neighborY][neighborX] = 255;
                    queue.add(new int[]{neighborY, neighborX});
                }
            }
        }
        return result;
    }
}
