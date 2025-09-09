package co.com.pragma.model.shared;

public interface ErrorCode {
    String getAppCode();
    int getHttpCode();
    String getMessage();
}
