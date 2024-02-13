// 게시판(Board)과 관련된 컨트롤러(Controller) 클래스
package com.blucean.solution.board.controller;

import com.blucean.solution.board.dto.BoardDTO;
//NOTE. DTO(Data Transfer Object)는 Spring Boot 애플리케이션에서 데이터를 주고받는 '택배 상자'와 같다. 이 택배 상자는 데이터 전송을 담당하며,
// 데이터베이스나 다른 서비스로부터 가져온 데이터를 담아 비즈니스 로직 간에 이동
// Spring Boot에서는 이러한 DTO를 통해 데이터의 구조와 내용을 캡슐화하고, 서로 다른 계층 간의 통신을 효율적으로 관리

//NOTE. VO는 값 객체(Value Object)
// Spring Boot에서의 VO(값 객체)는 말 그대로 "봉투"와 같다.
// 예를 들어, 편지를 보낼 때 우리는 편지 내용을 봉투 안에 넣고 봉투를 통해 전달.
// 이때 편지 내용은 변경되지 않고 그대로 유지되는데, 이런 봉투가 바로 VO
// Spring Boot 애플리케이션에서도 데이터를 전달할 때 이런 값 객체를 사용하여 데이터를 포장,
// 여러 곳에서 안전하게 전달 VO는 일종의 보호막 역할을 하여 데이터의 무결성을 유지하고,
// 응용 프로그램의 안전성을 높이는 데 도움
import com.blucean.solution.board.dto.BoardVO;

//NOTE. 성공 또는 실패한 작업의 결과를 나타내는 응답 객체를 생성하고 반환하는 다양한 정적 메서드를 제공
import com.blucean.solution.board.dto.ResponseVO;

// 게시판 서비스(BoardService)를 가져오는 import 선언
import com.blucean.solution.board.service.BoardService;

//NOTE. 공통으로 사용되는 상수를 정의하는 클래스입니다. 이 클래스는 프로젝트 전반에서 사용되는 성공 및 실패 코드, 메시지 등을 상수로 정의. 이러한 상수는 프로그램 전체에서 일관된 코드 및 메시지를 사용할 수 있도록 도와줌
import com.blucean.solution.common.CommonConst;

//NOTE. 롬복(Lombok)은 getter, setter, equals, hashCode 등의 메서드를 자동으로 생성
// SLF4J(Simple Logging Facade for Java)는 자바 애플리케이션에서 로깅을 위한 간단한 인터페이스
import lombok.extern.slf4j.Slf4j;

//NOTE. Autowired는 스프링에서 사용되는 어노테이션으로, 객체 간의 연결을 자동화
// 이를 통해 코드를 간결하게 작성하고, 의존성 주입을 편리하게 관리
// 주로 필드, 생성자, 메서드의 파라미터에 사용되며, 해당하는 객체를 스프링이 자동으로 주입
import org.springframework.beans.factory.annotation.Autowired;

//NOTE. MultipartFile 클래스는 웹 애플리케이션에서 파일 업로드를 처리할 때 사용되는 인터페이스
// Spring에서는 이를 통해 클라이언트로부터 전송된 파일을 서버에 업로드하고 처리하는 기능을 제공
// 웹 애플리케이션에서 파일을 업로드하려면 클라이언트에서 서버로 파일 데이터를 전송 이때 사용되는 HTML 폼의 enctype 속성은 multipart/form-data로 설정
// 이러한 형식의 요청을 처리하기 위해 Spring은 MultipartFile 인터페이스를 제공하며, 이를 통해 업로드된 파일의 정보를 읽고 저장
// MultipartFile은 다음과 같은 주요 메서드를 포함하고 있다
// String getOriginalFilename(): 업로드된 파일의 원본 파일 이름을 반환
// String getName(): 파일 업로드 필드의 이름을 반환
// long getSize(): 업로드된 파일의 크기를 반환
// byte[] getBytes(): 업로드된 파일의 내용을 byte 배열로 반환
// void transferTo(File dest): 업로드된 파일을 지정된 파일로 전송
// 따라서 MultipartFile 클래스는 Spring 기반의 웹 애플리케이션에서 파일 업로드를 처리하는 데 사용되며, 클라이언트에서 전송된 파일을 효과적으로 다룰 수 있도록 도와준다.
import org.springframework.web.multipart.MultipartFile;
//Spring Framework에서 제공하는 org.springframework.web.bind.annotation 패키지에 있는 모든 클래스와 인터페이스를 사용할 때 필요한 패키지를 지정
import org.springframework.web.bind.annotation.*;

//NOTE. List는 Java에서 가장 일반적으로 사용되는 컬렉션 인터페이스 중 하나.
// 컬렉션은 여러 요소를 담을 수 있는 객체로, 배열과 유사하지만 보다 다양한 기능과 유연성을 제공. List 인터페이스는 순서가 있는 요소들의 집합을 나타내며, 중복된 요소를 허용.
import java.util.List;

/**
 * [Class 설명]
 *
 * @author 정부용
 * @since 2024.02.05
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

@Slf4j
@RestController
@RequestMapping("/board")
class BoardRestController {
    @Autowired
    private BoardService boardService;

    /**
     * AJAX 요청을 처리하여 게시판 목록을 반환하는 메소드
     *
     * @param boardDTO 게시판 DTO 객체
     * @return ResponseVO 객체. 게시판 목록이 담긴 ResponseVO
     * @throws RuntimeException 게시판 목록 조회 중 발생한 예외가 런타임 예외처리
     */
    @PostMapping("/listAjax")
    public ResponseVO<List<BoardVO>> listAjax(@ModelAttribute BoardDTO boardDTO) {
        // 게시판 목록을 담을 리스트 변수를 선언
        List<BoardVO> boardVOS;

        try {
            // 게시판 서비스를 통해 게시판 목록을 조회하고 리스트에 저장
            boardVOS = boardService.boardList(boardDTO);
        } catch (Exception e) {
            // 예외가 발생한 경우, 런타임 예외로 전환하여 다시 던짐
            throw new RuntimeException(e);
        }
        // 게시판 목록이 담긴 ResponseVO 객체를 반환
        return ResponseVO.ok(boardVOS, CommonConst.SUCCESS_CODE, "검색 완료");
    }

    /**
     * AJAX 요청을 처리하여 게시글 수정 양식을 반환하거나 게시글을 수정하는 메소드
     *
     * @param boardDTO 게시글 DTO 객체
     * @param multiFileList 게시글에 첨부된 파일 리스트
     * @return ResponseVO 객체. 성공 또는 실패 여부를 포함한 ResponseVO
     * @throws Exception 게시글 수정 과정에서 발생한 예외 처리
     */
    @PostMapping("/updateFormAjax")
    public ResponseVO<String> updateForm(@ModelAttribute BoardDTO boardDTO, @RequestParam(value = "boardFile", required = false) List<MultipartFile> multiFileList) throws Exception {

        // 게시글을 수정하거나 새로 작성
        boardService.boardWrite(boardDTO, multiFileList);

        // 수정 여부에 따라 다른 메시지를 반환
        if ("N".equals(boardDTO.getModifyYn())) {
            return ResponseVO.ok(null, CommonConst.SUCCESS_CODE, "저장되었습니다.");
        } else {
            return ResponseVO.ok(null, CommonConst.SUCCESS_CODE, "수정되었습니다.");
        }
    }

    /**
     * AJAX 요청을 처리하여 게시글을 삭제하는 메소드
     *
     * @param bbsSeq 삭제할 게시글의 일련번호
     * @return ResponseVO 객체. 삭제 결과를 포함한 ResponseVO
     */
    @PostMapping("/deleteFormAjax")
    public ResponseVO<String> deleteBoard(@RequestParam int bbsSeq) {
        // 게시글을 삭제
        boardService.boardDelete(bbsSeq);
        // 삭제 성공 메시지를 반환
        return ResponseVO.ok(null, CommonConst.SUCCESS_CODE, "삭제되었습니다.");
    }
}