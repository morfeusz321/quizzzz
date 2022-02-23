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

@Controller
@RequestMapping("api/img/")
public class ActivityImageController {

    @GetMapping("/{imagePathGroup}/{imagePathFileName}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable("imagePathGroup") String imagePathGroup,
                                                        @PathVariable("imagePathFileName") String imagePathFileName) {

        URI imageURI = null;
        try {
            imageURI = ActivityImageController.class.getClassLoader().getResource(imagePathGroup + "/" + imagePathFileName).toURI();
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
