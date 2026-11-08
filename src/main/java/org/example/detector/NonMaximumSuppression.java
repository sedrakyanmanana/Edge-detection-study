package org.example.detector;

public class NonMaximumSuppression {
    public double[][] apply(double[][] magnitude, double[][] direction) {
        double[][] result = new double[magnitude.length][magnitude[0].length];
        for (int y = 1; y < magnitude.length - 1; y++) {
            for (int x = 1; x < magnitude[0].length - 1; x++) {
                double angle = direction[y][x];
                double current = magnitude[y][x];
                double neighbor1;
                double neighbor2;

                if (angle<22.5 || angle>=157.5) {
                    neighbor1 = magnitude[y][x-1];
                    neighbor2 = magnitude[y][x+1];
                }
                else if(angle<67.5){
                    neighbor1 = magnitude[y-1][x-1];
                    neighbor2 = magnitude[y+1][x+1];
                }
                else if(angle<112.5){
                    neighbor1 = magnitude[y-1][x];
                    neighbor2 = magnitude[y+1][x];
                }
                else{
                    neighbor1 = magnitude[y-1][x+1];
                    neighbor2 = magnitude[y+1][x-1];
                }

                if (current >= neighbor1 && current >= neighbor2) {
                    result[y][x] = current;
                } else {
                    result[y][x] = 0;
                }
            }
        }

        return result;
    }
}