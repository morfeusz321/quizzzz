package server;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * An API controller for the activity images. Handles everything mapped to /api/img/...
 */
@Controller
@RequestMapping("api/img/")
public class ActivityImageController {

    /**
     * Returns the image associated with a certain activity from its imagePath (e.g. 00/shower.jpg)
     * @param imagePathGroup the group part of the image path (e.g. 00 in 00/shower.jpg)
     * @param imagePathFileName the file name part of the image path (e.g. shower.jpg in 00/shower.jpg)
     * @return 200 OK: InputStream of the image if it exists, 404: Not found otherwise. Can also return 500: Internal server error
     * in case of a URI syntax exception in the file path
     */
    @GetMapping("/{imagePathGroup}/{imagePathFileName}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable("imagePathGroup") String imagePathGroup,
                                                        @PathVariable("imagePathFileName") String imagePathFileName) {

        URI imageURI = null;
        try {
            imageURI = ActivityImageController.class.getClassLoader().getResource("activities/" + URLDecoder.decode(imagePathGroup, StandardCharsets.UTF_8) + "/" + URLDecoder.decode(imagePathFileName, StandardCharsets.UTF_8)).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

        File image = new File(imageURI);

        InputStream imageInputStream = null;
        try {
            imageInputStream = new FileInputStream(image);
        } catch (FileNotFoundException e) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(new InputStreamResource(imageInputStream));

    }

}
