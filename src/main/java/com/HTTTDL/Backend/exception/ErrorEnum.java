package com.HTTTDL.Backend.exception;

public enum ErrorEnum {
    UNAUTHENTICATED(401,"Unauthenticated"),
    TOKEN_EXPIRE(401,"Token expired"),
    EMAIL_EXISTS(400,"Email already exists"),
    USER_NOT_FOUND(404,"User not found"),
    VERIFICATION_FAILED(400,"Verification failed"),
    VERIFICATION_EXPIRED(400,"Verification expired"),
    ACCOUNT_NOT_VERIFIED(401,"Account not verified"),
    ACCOUNT_VERIFIED(400,"Account verified"),
    LOGIN_FAILED(401,"Email or password is incorrect"),
    ;
    int status;
    String message;
     ErrorEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public int getStatus() {
         return status;
    }
    public String getMessage() {
         return message;
    }
}
