package com.blucean.solution.board.dto;

import com.blucean.solution.common.CommonConst;
import lombok.*;

/**
 * 공통 Response 객체
 * @param <T>
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ResponseVO<T> {
    private static final String SUCCESS_CODE = CommonConst.SUCCESS_CODE;
    private static final String ERROR_CODE = CommonConst.ERROR_CODE;

    private String resultCd;
    private String resultMsg;
    private T data;

    public static ResponseVO<Void> ok() {
        return new ResponseVO();
    }

    public static <T> ResponseVO<T> ok(T data) {
        return ResponseVO.<T>builder()
                .resultCd(SUCCESS_CODE)
                .resultMsg(null)
                .data(data)
                .build();
    }

    public static <T> ResponseVO<T> ok(T data, String code) {
        return ResponseVO.<T>builder()
                .resultCd(code)
                .resultMsg(null)
                .data(data)
                .build();
    }

    public static <T> ResponseVO<T> ok(T data, String code, String message) {
        return ResponseVO.<T>builder()
                .resultCd(code)
                .resultMsg(message)
                .data(data)
                .build();
    }

    public static <T> ResponseVO<T> error(String message) {
        return ResponseVO.<T>builder()
                .resultCd(ERROR_CODE)
                .resultMsg(message)
                .data(null)
                .build();
    }

    public static <T> ResponseVO<T> error(String code, String message) {
        return ResponseVO.<T>builder()
                .resultCd(code)
                .resultMsg(message)
                .data(null)
                .build();
    }
}
