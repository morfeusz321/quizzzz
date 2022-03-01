import json
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
            new_size = width

        if width > height:
            new_size = height

        resized_image = img.resize((new_size, new_size))
        
        new_file_path = os.path.splitext(image_path)[0] + "-resize" + os.path.splitext(image_path)[1]
        resized_image.save(new_file_path)
