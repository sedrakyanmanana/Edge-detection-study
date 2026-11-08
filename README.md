# Evaluating Classical Edge Detection Algorithms Under Image Degradation

## Overview

This project investigates how different types of image degradation affect the performance of four classical edge-detection algorithms:

- Sobel
- Prewitt
- Laplacian
- Canny

The main objective is to compare their quality, execution time  and computational performance under different image conditions.

The experiments use images from the **Berkeley Segmentation Dataset and Benchmark 500 (BSDS500)**. Detected edges are compared with human-provided reference boundary annotations using precision, recall, and F1-score. The execution time of each algorithm is also measured.

## Research Question

How do salt-and-pepper noise, Gaussian blur and contrast reduction affect the edge-detection quality and runtime of Sobel, Prewitt, Laplacian and Canny algorithms?

## Algorithms

### 1. Sobel
Uses horizontal and vertical convolution kernels to estimate image intensity gradients and identify edges.

### 2. Prewitt
Uses two convolution kernels to detect horizontal and vertical intensity changes.

### 3. Laplacian
Uses a second-order derivative to identify regions where image intensity changes rapidly.

### 4. Canny
Uses a multi-stage edge-detection process:
- Gaussian smoothing
- Gradient calculation
- Non-maximum suppression
- Double thresholding
- Edge hysteresis

## Experimental Conditions

Each algorithm is evaluated under five conditions:

Condition
- Original 
- Salt-and-Pepper Noise (5%) 
- Salt-and-Pepper Noise (10%)
- Gaussian Blur 
- Reduced Contrast 

The experiment uses 10 selected images from BSDS500. This results in 200 experimental combinations.

## Evaluation Metrics

The algorithms are evaluated using the following metrics:

- **Precision:** Measures how many detected edge pixels correctly match reference boundaries.
- **Recall:** Measures how many reference boundary pixels are successfully detected.
- **F1-score:** Combines Precision and Recall into a single measure of detection quality.
- **Runtime:** Measures the time required for each algorithm to process an image.

## Project Structure

```text
edge-detection-research/
│
├── data/
│   ├── bsds500/
│   │   ├── ground_truth_png/
│   │   └── BSR/
│   ├── input/
│   └── output/
│
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── example/
│                   ├── detector/
│                   │   ├── CannyDetector.java
│                   │   ├── EdgeHysteresis.java
│                   │   ├── GaussianSmooth.java
│                   │   ├── LaplacianDetector.java
│                   │   ├── NonMaximumSuppression.java
│                   │   ├── PrewittDetector.java
│                   │   └── SobelDetector.java
│                   │
│                   ├── evaluation/
│                   │   ├── BoundaryMatcher.java
│                   │   ├── MetricsEvaluator.java
│                   │   └── RuntimeTimer.java
│                   │
│                   ├── image/
│                   │   ├── GrayscaleConverter.java
│                   │   └── ImageLoader.java
│                   │
│                   ├── processing/
│                   │   ├── ContrastAdjuster.java
│                   │   ├── Convolution.java
│                   │   ├── GaussianBlur.java
│                   │   └── NoiseGenerator.java
│                   │
│                   └── Main.java
│
├── convert_ground_truth.py
├── pom.xml
└── README.md
```

## Technologies

- **Java** – implementation of the image-processing algorithms and experiments.
- **Maven** – project and dependency management.
- **Python** – conversion of BSDS500 ground-truth annotations into PNG images.
- **Excel** – data analysis, PivotTables, and result visualization.
- **IntelliJ IDEA** – development environment.

## Dataset Setup

This project uses the BSDS500 dataset.

1. Download the BSDS500 dataset.
2. Place the dataset inside:

   `data/bsds500/BSR/`

3. Ensure that the original images and their corresponding ground-truth annotations are available.
4. Convert the ground-truth annotations into PNG images using `convert_ground_truth.py`.
5. Place the converted annotations inside:

   `data/bsds500/ground_truth_png/`

The image and ground-truth filenames must correspond so that each image can be matched with its reference annotation.

## How to Run

### Requirements

- Java Development Kit (JDK) compatible with the version specified in `pom.xml`
- Maven
- IntelliJ IDEA (recommended)

### Running the Experiments

1. Clone or download this repository.
2. Open the project in IntelliJ IDEA.
3. Allow Maven to load the project dependencies.
4. Make sure the dataset and converted ground-truth images are in the correct directories.
5. Open `Main.java`.
6. Run the `main()` method.

The program processes the selected images, applies the degradation conditions, executes all four edge detectors, evaluates the results, and measures their execution times.

## Output

The experiment generates:

- CSV files containing evaluation metrics and runtime measurements.
- Processed images under different degradation conditions.
- Edge-detection output images for visual comparison.

The generated files are stored in the `data/output/` directory.

The CSV results can be imported into Excel for further analysis and visualization.

## Limitations

The experiment has several limitations:

- Only 10 images from BSDS500 are used.
- Only the first human boundary annotation is used for each image.
- A custom boundary-matching method is used instead of the official BSDS500 evaluation protocol.
- Only one Gaussian blur setting and one contrast-reduction level are tested.

These limitations should be considered when interpreting the experimental results.

## Author

An individual research project exploring classical computer vision techniques, image processing, and the evaluation of edge-detection algorithms.

## Remark
This README.md was created with an assistance of an AI tool. 
