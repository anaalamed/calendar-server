package calendar.controller.response;

public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;

    private BaseResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(true, null, data);
    }

    public static <T> BaseResponse<T> failure(String message) {
        return new BaseResponse<T>(false, message, null);
    }

    public static <T> BaseResponse<T> noContent(boolean success, String message) {
        return new BaseResponse<T>(success, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}