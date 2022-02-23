/*
https://www.baeldung.com/jackson-deserialization
 */

package server.database;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import commons.Activity;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class FromSourceActivityDeserializer extends StdDeserializer<Activity> {

    public FromSourceActivityDeserializer() {
        this(null);
    }

    public FromSourceActivityDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Activity deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String id = node.get("id").asText();
        String title = node.get("title").asText();
        int consumption = (Integer) ((IntNode) node.get("consumption_in_wh")).numberValue();

        String[] idSplit = node.get("id").asText().split("-");
        String groupID = idSplit[0];
        String imageName = idSplit[1];
        File groupFolder = null;
        try {
            groupFolder = new File(FromSourceActivityDeserializer.class.getClassLoader().getResource(groupID).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        File[] allFiles = groupFolder.listFiles();
        for(File f : allFiles) {
            if(f.getName().split("\\.")[0].equals(imageName)) {
                imageName += "." + f.getName().split("\\.")[1];
                break;
            }
        }
        String imagePath = groupID + "/" + imageName;

        return new Activity(id, imagePath, title, consumption);
    }

}
