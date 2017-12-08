import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.fasterxml.jackson.core.JsonToken.*;

/**
 * @author
 */
public class JsonConvertApiResponse {
    private File json;
    private JsonParser jsonParser;
    private JsonToken prevToken;
    private JsonToken currentToken;

    int startMark = 0;
    boolean isStart = false;

    public JsonConvertApiResponse(File json) {
        this.json = json;
    }

    public void init() {
        JsonFactory jasonFactory = new JsonFactory();
        try {
            jsonParser = jasonFactory.createParser(json);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void convert() {
        this.init();
        StringBuilder sb = new StringBuilder();
        List<ApiResponse> apis = new LinkedList<>();
        ApiResponse api = null;
        try {
            while (nextToken() != null) {
                if (startMark == 2 && isStart) {
                    if (api != null) {
                        apis.add(api);
                    }
                    api = new ApiResponse();
                }

                if (jsonParser.currentToken() == FIELD_NAME) {
                    if ("status".equals(jsonParser.getCurrentName()) && startMark == 2) {
                        nextToken();
                        api.setStatus(jsonParser.getValueAsInt());
                    } else if ("body".equals(jsonParser.getCurrentName())) {
                        api.setBody(new NodeToString().toString());
                    }
                }


            }
            if (api != null) {
                apis.add(api);
            }
            System.out.println(sb);
            System.out.println(apis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonToken nextToken() throws IOException {
        prevToken = jsonParser.currentToken();
        currentToken = jsonParser.nextToken();
        if (currentToken != null) {
            switch (currentToken) {
                case START_ARRAY:
                case START_OBJECT:
                    startMark++;
                    isStart = true;
                    break;
                case END_ARRAY:
                case END_OBJECT:
                    startMark--;
                    isStart = false;
                    break;
                default:
                    isStart = false;
            }
        }

        return currentToken;
    }

    private class NodeToString {
        private StringBuilder sb = new StringBuilder();
        private int deep = 0;

        public NodeToString() {
            this.init();
        }

        private JsonToken nextToken() throws IOException {
            JsonConvertApiResponse.this.nextToken();
            switch (currentToken) {
                case START_ARRAY:
                case START_OBJECT:
                    deep++;
                    break;
                case END_ARRAY:
                case END_OBJECT:
                    deep--;
                    break;
            }

            return currentToken;
        }

        private void init() {
            try {
                while (nextToken() != null && deep > 0 || (jsonParser.currentToken() == END_OBJECT)) {
                    switch (jsonParser.currentToken()) {
                        case END_ARRAY:
                        case END_OBJECT:
                            sb.append(jsonParser.getText());
                            break;
                        case START_OBJECT:
                        case START_ARRAY:
                            if (prevToken == END_OBJECT || prevToken == END_ARRAY) {
                                sb.append(",");
                            }
                            sb.append(jsonParser.getText());
                            break;
                        case FIELD_NAME:
                            sb.append(getPrefix() + jsonParser.getText() + getSuffix());
                            break;
                        case VALUE_NULL:
                            sb.append("null");
                            break;
                        case VALUE_TRUE:
                            sb.append("true");
                            break;
                        case VALUE_FALSE:
                            sb.append("false");
                            break;
                        case VALUE_STRING:
                            sb.append("\"" + jsonParser.getText() + "\"");
                            break;
                        case NOT_AVAILABLE:
                        case VALUE_EMBEDDED_OBJECT:
                        case VALUE_NUMBER_INT:
                        case VALUE_NUMBER_FLOAT:
                            sb.append(jsonParser.getText());
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getPrefix() {
            return prevToken == START_ARRAY || prevToken == START_OBJECT ? "\"" : ",\"";
        }

        public String getSuffix() {
            return "\"" + ":";
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
