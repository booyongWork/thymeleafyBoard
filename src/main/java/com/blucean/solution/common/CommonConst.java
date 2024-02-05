package com.blucean.solution.common;

/**
 * 공통으로 사용되는 상수
 *
 */
public class CommonConst {
    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_MESSAGE = "SUCCESS";
    public static final String ERROR_CODE = "9999";
    public static final String ERROR_MESSAGE = "FAIL";

    /**
     * 로그 Aspect 관련 START
     *   - TODO.. 아래 LogginAspect에서 사용되는 변수 확인 필요
     */
    public static final String HOST_NAME = "PORTAL_HOST"; //호스트명 TODO 서버에서 확인
    public static final String SVC_NAME = "PORTAL"; //서비스명 TODO 서비스명 확인
    public static final String TRANSACTION_ID = "transactionId"; //트랜잭션ID
    public static final String REQUEST_TIME = "requestTime";
    public static final String REQUEST_URI = "requestUri";
    
    
    
    public static final String RESPONSE_TYPE_OK = "S";
    public static final String RESPONSE_TYPE_FAIL = "F";	//비밀번호 실패로 인한 로그인 실패 와 같은 비지니스 로직상 에러
    public static final String RESPONSE_TYPE_ERROR = "E";	//DB접속 실패 등의 시스템 실패 
    /**
     * 로그 Aspect 관련 END
     */
}
