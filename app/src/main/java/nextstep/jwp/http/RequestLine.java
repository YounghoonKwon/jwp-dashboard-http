package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {
    private String method;
    private String path;

    public RequestLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            return;
        }
        String[] firstLine = line.split(" ");
        method = firstLine[0];
        path = firstLine[1];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}