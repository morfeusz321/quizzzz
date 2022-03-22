import json
import cv2

# Setup
check_title_infinitive_form = True
check_image_aspect_ratio = True
minimum_desired_aspect_ratio = "1:1"
maximum_desired_aspect_ratio = "1:1"
# End Setup


def is_infinitive_form(title):
    return title.split()[0][-3:] == "ing"

def check_image(image_path):
    img = cv2.imread(image_path)
    return is_desired_aspect_ratio(img)

def is_desired_aspect_ratio(img):
    h, w, c = img.shape

    aspect_ratio = w / h
    float_minimum_aspect_ratio = float(minimum_desired_aspect_ratio.split(":")[0]) / float(minimum_desired_aspect_ratio.split(":")[1])
    float_maximum_aspect_ratio = float(maximum_desired_aspect_ratio.split(":")[0]) / float(maximum_desired_aspect_ratio.split(":")[1])
    return aspect_ratio >= float_minimum_aspect_ratio and aspect_ratio <= float_maximum_aspect_ratio

def print_sep():
    print("=========================================")
    return

if __name__ == "__main__":

    print_sep()
    print("Data validator loading....")

    if check_title_infinitive_form:
        print("Title infinitive form check ENABLED!")
    else:
        print("Title infinitive form check DISABLED!")

    if check_image_aspect_ratio:
        print("Aspect ratio check ENABLED! Minimum aspect ratio: " + minimum_desired_aspect_ratio + ", maximum aspect ratio: " + maximum_desired_aspect_ratio)
    else:
        print("Aspect ratio check DISABLED!")

    f = open("activities.json")
    activities = json.load(f)

    num_activities = len(activities)

    print_sep()
    print("Read " + str(num_activities) + " activities")

    num_valid = 0
    num_fail_title = 0
    num_fail_aspect = 0
    num_fail_both = 0
    for a in activities:
        fail_title = False
        fail_aspect = False

        if check_title_infinitive_form and not(is_infinitive_form(a["title"])):
            fail_title = True
        if check_image_aspect_ratio and not(check_image(a["image_path"])):
            fail_aspect = True
        
        if fail_title and fail_aspect:
            num_fail_both += 1
            continue
        
        if fail_title:
            num_fail_title += 1
            continue

        if fail_aspect:
            num_fail_aspect += 1
            continue

        num_valid += 1


    percentage_valid = round(float(num_valid) / float(num_activities) * 100.00, 2)
    print("Valid activities: " + str(num_valid) + "/" + str(num_activities) + " (" + str(percentage_valid) + "%)")
    print_sep()

    if check_title_infinitive_form and check_image_aspect_ratio:
        print("Failed on both tests (title and aspect ratio): " + str(num_fail_both))

    if check_title_infinitive_form:
        print("Failed on title test only: " + str(num_fail_title))

    if check_image_aspect_ratio:
        print("Failed on aspect ratio test only: " + str(num_fail_aspect))

    print_sep()

    f.close()
