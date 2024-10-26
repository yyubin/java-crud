package http.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.annotations.Component;
import config.annotations.Priority;
import config.annotations.Requires;
import exception.BadRequestException;
import http.response.ResponseCode;
import message.ExceptionMessage;

import java.util.HashMap;
import java.util.Map;

@Component
@Priority(value = 1)
@Requires(dependsOn = {ObjectMapperConfig.class})
public class HttpRequestParser {

    private final ObjectMapperConfig objectMapper;

    public HttpRequestParser(ObjectMapperConfig objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HttpRequest<Map<String, Object>> parse(String rawRequest) throws IllegalAccessError, Exception {
        String[] requestLines = rawRequest.split("\n");
        String[] requestParts = requestLines[0].split(" ");

        if (requestParts.length < 2) {
            throw new BadRequestException(ExceptionMessage.BAD_REQUEST, ResponseCode.BAD_REQUEST);
        }

        String method = requestParts[0].toUpperCase();
        String path = requestParts[1];

        Map<String, Object> body = null;
        Map<String, String> queryParams = new HashMap<>();

        String[] pathParts = path.split("\\?");
        path = pathParts[0];

        if (pathParts.length > 1) {
            String queryString = pathParts[1];
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }

        if (HttpMethod.POST.getMethod().equals(method) || HttpMethod.PUT.getMethod().equals(method) || HttpMethod.DELETE.getMethod().equals(method)) {
            if (requestLines.length > 1) {
                body = parseBodyToMap(requestLines[1].trim());
            }
        }

        return new HttpRequest<>(HttpMethod.valueOf(method), path, body, queryParams);
    }

    private Map<String, Object> parseBodyToMap(String body) throws Exception {
        return objectMapper.getObjectMapper().readValue(body, new TypeReference<Map<String, Object>>() {});
    }

}
