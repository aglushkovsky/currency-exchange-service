package io.github.aglushkovsky.util;

import io.github.aglushkovsky.error.ResponseError;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

public class ResponseUtils {
    public static <T> void sendAllFromList(List<T> list, HttpServletResponse resp) throws IOException {
        PrintWriter responseWriter = resp.getWriter();
        String jsonFromList = JsonUtils.getJsonFromList(list);
        resp.setStatus(SC_OK);
        responseWriter.write(jsonFromList);
    }

    public static <T> void sendObject(T object, int responseCode, HttpServletResponse resp) throws IOException {
        PrintWriter responseWriter = resp.getWriter();
        String jsonFromObject = JsonUtils.getJsonFromObject(object);
        resp.setStatus(responseCode);
        responseWriter.write(jsonFromObject);
    }

    public static <T> void sendObject(T object, HttpServletResponse resp) throws IOException {
        sendObject(object, SC_OK, resp);
    }

    public static void sendError(ResponseError error, HttpServletResponse resp) throws IOException {
        PrintWriter responseWriter = resp.getWriter();
        resp.setStatus(error.getCode());
        String jsonErrorMessage = JsonUtils.getJsonErrorMessage(error.getMessage());
        responseWriter.write(jsonErrorMessage);
    }

    private ResponseUtils() {
    }
}
