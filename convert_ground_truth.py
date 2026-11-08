
from pathlib import Path
import numpy as np
from scipy.io import loadmat
from PIL import Image

# --------------------------------------------------
# 1. EXACT DATASET PATHS
# --------------------------------------------------

IMAGE_FOLDER = Path(
    r"C:\Users\sedra\OneDrive\Desktop\VoiceQuest\edge-detection-research\data\BSR\BSDS500\data\images"
)

MAT_FOLDER = Path(
    r"C:\Users\sedra\OneDrive\Desktop\VoiceQuest\edge-detection-research\data\BSR\BSDS500\data\groundTruth"
)

OUTPUT_FOLDER = Path(
    r"C:\Users\sedra\OneDrive\Desktop\VoiceQuest\edge-detection-research\data\bsds500\ground_truth_png"
)

OUTPUT_FOLDER.mkdir(parents=True, exist_ok=True)


# --------------------------------------------------
# 2. VERIFY FOLDERS
# --------------------------------------------------

if not IMAGE_FOLDER.exists():
    raise FileNotFoundError(f"Images folder not found: {IMAGE_FOLDER}")

if not MAT_FOLDER.exists():
    raise FileNotFoundError(f"Ground truth folder not found: {MAT_FOLDER}")


# --------------------------------------------------
# 3. FIND ANNOTATION FILES
# --------------------------------------------------

mat_files = sorted(MAT_FOLDER.rglob("*.mat"))

print(f"Found {len(mat_files)} MAT files.")

if not mat_files:
    raise FileNotFoundError("No MAT files found!")


# --------------------------------------------------
# 4. CONVERT FIRST 10 MATCHING PAIRS
# --------------------------------------------------

processed = 0

for mat_file in mat_files:

    # Find the JPG with the same filename
    image_matches = list(
        IMAGE_FOLDER.rglob(mat_file.stem + ".jpg")
    )

    if not image_matches:
        print(f"Skipping {mat_file.stem}: matching JPG not found.")
        continue

    try:
        # Load MATLAB annotation
        data = loadmat(
            mat_file,
            squeeze_me=True,
            struct_as_record=False
        )

        annotations = np.atleast_1d(data["groundTruth"])

        # Select the first annotator
        first_annotation = annotations.flat[0]

        # Extract human-marked boundaries
        boundaries = np.asarray(first_annotation.Boundaries)

        # Convert to black and white PNG
        boundary_image = np.where(
            boundaries > 0, 255, 0
        ).astype(np.uint8)

        output_file = OUTPUT_FOLDER / f"{mat_file.stem}.png"

        Image.fromarray(boundary_image).save(output_file)

        processed += 1
        print(f"Converted: {mat_file.stem}")

    except Exception as error:
        print(f"Error processing {mat_file.name}: {error}")

    if processed == 10:
        break


# --------------------------------------------------
# 5. FINAL REPORT
# --------------------------------------------------

print("\n================================")
print("CONVERSION FINISHED")
print("================================")
print(f"Converted: {processed}/10")
print(f"Saved in: {OUTPUT_FOLDER}")

if processed == 10:
    print("SUCCESS! All 10 boundary maps were created.")
else:
    print("WARNING: Fewer than 10 maps were created.")
