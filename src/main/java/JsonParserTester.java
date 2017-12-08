import java.io.File;
import java.net.URL;

/**
 * @author
 */
public class JsonParserTester {
    public static void main(String[] args) {
        URL resource = JsonParserTester.class.getClassLoader().getResource("api.json");
        JsonConvertApiResponse jsonConvertApiResponse = new JsonConvertApiResponse(new File(resource.getFile()));
        jsonConvertApiResponse.convert();
    }
}
