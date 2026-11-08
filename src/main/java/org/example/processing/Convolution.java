package org.example.processing;

public class Convolution{
    public double[][] ConvolutionHelper(double[][] image, double[][] filter){
        if (image == null || filter == null || image.length == 0 || filter.length == 0) {
            throw new IllegalArgumentException("Image and filter cannot be empty :( ");
        }
        int height = image.length;
        int width = image[0].length;


        double[][] outputImage = new double[height][width];

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                double sum = 0;
                    for(int y2=0; y2<filter.length; y2++){
                        for(int x2=0; x2<filter[0].length; x2++){

                            int imageY = y+y2- filter.length/2;
                            int imageX = x+x2-filter[0].length/2;

                            if (imageY >= 0 && imageY < height && imageX >= 0 && imageX < width) {
                                sum += filter[y2][x2] * image[imageY][imageX];
                            }
                        }
                    }
                    outputImage[y][x] = sum;
            }
        }
        return outputImage;
    }
}