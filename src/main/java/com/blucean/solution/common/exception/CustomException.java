package com.blucean.solution.common.exception;

import lombok.Getter;

import java.sql.SQLSyntaxErrorException;

/**
 * [Class 설명]
 *
 * @author 
 * @since 
 * @version 
 * @see
 *
 * <pre>
 *
 * << 개정이력(Modification information) >>
 *
 *   수정일     수정자      수정내용
 * -----------  ---------    ------------------------
 * 
 *
 * </pre>
 */

public class CustomException extends Exception {
	private static final long serialVersionUID = 3559302713942492181L;

	@Getter
	private String errorCode;
	
	@Getter
	private String errorMsg; 
	
	/**
	 * 생성자(throwable를 받아서 exception 종류에 따라 에러 코드 정의하는 클래스)
	 * 에러코드 처리 부분은 향후 db에서 에러코드 읽어와서 매핑하는 것으로
	 * 변경 가능성 있음
	 * @param throwable
	 */
	public CustomException(Throwable throwable) {
		Throwable rootThrowable = getRootCause(throwable);
		
		// TODO 에러 코드는 상세 정의해서 define 해야 함
		if(rootThrowable instanceof SQLSyntaxErrorException) {
			this.errorCode = "100";
			this.errorMsg = "SQL 오류";
		} else if(rootThrowable instanceof ArithmeticException) {
			this.errorCode = "200";
			this.errorMsg = "연산 오류";
		} else {
			this.errorCode = "900";
			this.errorMsg = "Unknown Error";
		}
	}
	
	/**
	 * 생성자(에러메시지만 받을 경우 에러코드를 0을 default로 세팅)
	 * @param errorMsg
	 */
	public CustomException(String errorMsg) {
		super(errorMsg);
		this.errorCode = "0";
		this.errorMsg = errorMsg;
	}
	
	/**
	 * 생성자(errorcode, errmsg를 받아 세팅, 소스코드에서 편하게 사용하기 위해
	 * 에러코드를 int 값으로 받으면 string으로 변환하여 저장)
	 * @param errorCode
	 * @param errorMsg
	 */
	public CustomException(int errorCode, String errorMsg) {
		super(errorMsg);
		this.errorCode = String.valueOf(errorCode);
		this.errorMsg = errorMsg;
	}

	/**
	 * 생성자(에러코드, 에러메시지를 받아 세팅)
	 * @param errorCode
	 * @param errorMsg
	 */
	public CustomException(String errorCode, String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode; 
		this.errorMsg = errorMsg;
	}
	
	/**
	 * stack에서 에러코드 중 root cause를 찾아내는 메소드
	 * @param throwable
	 * @return
	 */
	private Throwable getRootCause(Throwable throwable) {
		Throwable cause = throwable.getCause();
		
		if (cause == null) return throwable;
		
		while (true) {
			
			if(cause.getCause() == null) {
				break;
			} else {
				cause = cause.getCause();
			}
		}
		
		return cause;
	} 
}

