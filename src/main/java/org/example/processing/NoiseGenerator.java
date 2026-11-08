package org.example.processing;

import java.util.Random;

public class NoiseGenerator {
    public double[][] generateNoise(double[][] image, double salt, double pepper){
        Random random = new Random();
        double[][] result = new double[image.length][image[0].length];
        for(int y=0;y<image.length;y++){
            for(int x=0;x<image[0].length;x++){
                double r = random.nextDouble();
                if(r<salt)
                    result[y][x] = 255;
                else if(r<pepper+salt)
                    result[y][x] = 0;
                else
                    result[y][x] = image[y][x];
            }
        }
        return result;
    }
}