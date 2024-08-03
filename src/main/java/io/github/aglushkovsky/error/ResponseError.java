package io.github.aglushkovsky.error;

public class ResponseError {
    private final int code;
    private final String message;

    public static ResponseError of(int code, String message) {
        return new ResponseError(code, message);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private ResponseError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
