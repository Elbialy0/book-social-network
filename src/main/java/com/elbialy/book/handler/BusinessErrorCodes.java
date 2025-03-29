package com.elbialy.book.handler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessErrorCodes {
    NO_CODE(0,HttpStatus.NOT_IMPLEMENTED,"No code"),
    ACCOUNT_LOCKED(302,HttpStatus.FORBIDDEN,"Account locked"),
    INCORRECT_CURRENT_PASSWORD(300,HttpStatus.BAD_REQUEST,"Incorrect current password"),
    NEW_PASSWORD_DOES_NOT_MATCH(301,HttpStatus.BAD_REQUEST,"Password does not match"),
    ACCOUNT_DISABLED(303,HttpStatus.FORBIDDEN,"Account disabled"),
    BAD_CREDENTIALS(304,HttpStatus.FORBIDDEN,"Bad credentials"),

    ;

    private final int code;
    private final HttpStatus httpStatus;
    private final String description;







}
