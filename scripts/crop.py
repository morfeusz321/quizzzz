import json
import math
import os
from PIL import Image

# Setup
desired_aspect_ratio = "1:1"
# End Setup

if __name__ == "__main__":
    f = open("activities.json")
    activities = json.load(f)

    for a in activities:
        image_path = a["image_path"]
        img = Image.open(image_path)
        width, height = img.size

        if width == height:
            continue

        if width < height:
            left = 0
            top = (float(height) / 2.0) - (float(width) / 2.0)
            right = width
            bottom = (float(height) / 2.0) + (float(width) / 2.0)

        if width > height:
            left = (float(width) / 2.0) - (float(height) / 2.0)
            top = 0
            right = (float(width) / 2.0) + (float(height) / 2.0)
            bottom = height

        cropped_image = img.crop((math.ceil(left), math.ceil(top), math.ceil(right), math.ceil(bottom)))
        
        new_file_path = os.path.splitext(image_path)[0] + "-crop" + os.path.splitext(image_path)[1]
        cropped_image.save(new_file_path)
