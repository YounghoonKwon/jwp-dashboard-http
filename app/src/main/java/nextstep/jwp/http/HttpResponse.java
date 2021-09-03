package nextstep.jwp.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class HttpResponse {
    private final OutputStream outputStream;
    private HttpStatus status;
    private String body;
    private String path;
    private String redirectUrl;
    private HttpSession session;
    private Map<String, String> responseMap;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public HttpResponse(Builder builder) {
        this.outputStream = builder.outputStream;
        this.status = builder.status;
        this.body = builder.body;
        this.path = builder.path;
        this.redirectUrl = builder.redirectUrl;
        this.session = builder.session;
        this.responseMap = builder.responseMap;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void forward() throws IOException {
        String response = response();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String response() {
        List<String> values = new LinkedList<>(List.of("HTTP/1.1 " + status.number + " " + status.name + " "));
        values.addAll(responseMap.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.toList()));
        values.add("");
        values.add(body);

        return String.join("\r\n", values);
    }

    public static class Builder {
        private OutputStream outputStream;
        private HttpStatus status;
        private String body;
        private String path;
        private String redirectUrl;
        private HttpSession session;
        private final Map<String, String> responseMap = new HashMap<>();

        public Builder outputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
            return this;
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            this.responseMap.put("Content-Length", String.valueOf(body.getBytes().length));
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            responseMap.put("Content-Type", ContentTypeMapper.extractContentType(path));
            return this;
        }

        public Builder redirectUrl(String url) {
            this.redirectUrl = url;
            responseMap.put("Location", "http://localhost:8080" + redirectUrl);
            return this;
        }

        public Builder session(HttpSession session) {
            this.session = session;
            responseMap.put("Set-Cookie", "JSESSIONID=" + session.getId());
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
