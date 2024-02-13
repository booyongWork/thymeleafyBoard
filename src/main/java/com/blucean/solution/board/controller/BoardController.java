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

// 게시판 서비스(BoardService)를 가져오는 import 선언
import com.blucean.solution.board.service.BoardService;

//NOTE. 롬복(Lombok)은 getter, setter, equals, hashCode 등의 메서드를 자동으로 생성
// SLF4J(Simple Logging Facade for Java)는 자바 애플리케이션에서 로깅을 위한 간단한 인터페이스
import lombok.extern.slf4j.Slf4j;

//NOTE. Autowired는 스프링에서 사용되는 어노테이션으로, 객체 간의 연결을 자동화
// 이를 통해 코드를 간결하게 작성하고, 의존성 주입을 편리하게 관리
// 주로 필드, 생성자, 메서드의 파라미터에 사용되며, 해당하는 객체를 스프링이 자동으로 주입
import org.springframework.beans.factory.annotation.Autowired;

//NOTE. Resource는 스프링 프레임워크에서 파일이나 클래스패스 내의 리소스에 접근하는 데 사용되는 도구
// 설정 파일이나 이미지 파일과 같은 리소스를 로드하거나 조작할 때 사용
import org.springframework.core.io.Resource;

// Spring의 MediaType 클래스를 가져오는 import 선언[이미지]
import org.springframework.http.MediaType;
// Spring의 MediaTypeFactory를 가져오는 import 선언[비디오]
import org.springframework.http.MediaTypeFactory;

//NOTE. ResponseEntity 스프링 프레임워크 내 웹 응용 프로그램에서 HTTP 응답을 생성하는데 사용되는 클래스
// ResponseEntity는 HTTP 응답 본문, 헤더, 상태 코드 및 다른 응답 요소를 캡슐화하고 제어할 수 있는 강력한 도구
import org.springframework.http.ResponseEntity;

//NOTE. Controller 어노테이션은 스프링 프레임워크에서 컨트롤러 역할을 하는 클래스에 지정
// 클라이언트의 요청을 처리하고 응답을 생성하는 데 사용
// @Controller 어노테이션은 스프링 MVC의 핵심 요소 중 하나로, 클라이언트의 요청을 처리하는 비즈니스 로직과 뷰를 결합하여 전체적인 웹 애플리케이션의 동작을 제어
import org.springframework.stereotype.Controller;

//NOTE. Model은 스프링이 지원하는 기능으로써, key와 value로 이루어져있는 HashMap
// Model의 .addAttribute()를 통해 view에 전달할 데이터를 저장할 수 있다.
import org.springframework.ui.Model;

//NOTE. @GetMapping은 스프링 프레임워크에서 사용하는 어노테이션
// 이 어노테이션은 HTTP GET 요청을 특정 메서드와 매핑하기 위해서 사용
// HTTP GET 요청은 일반적으로 서버에서 정보를 조회하는 데 사용
import org.springframework.web.bind.annotation.GetMapping;

//NOTE. @RequestMapping 어노테이션은 Spring MVC에서 사용되며, HTTP 요청을 컨트롤러의 특정 메서드에 매핑할 때 사용
// 특정 URL에 대한 요청을 처리할 수 있는 메서드를 지정 @RequestMapping은 클래스 수준과 메서드 수준에서 사용할 수 있음.
// 클래스 수준에서 사용할 때: 클래스 수준에서 @RequestMapping 어노테이션을 사용하면 해당 컨트롤러의 모든 요청 처리 메서드에 대해 공통된 URL로 정의
// 메서드 수준에서 사용할 때: 메서드 수준에서 @RequestMapping 어노테이션을 사용하면 특정 URL에 대한 요청을 처리할 메서드를 지정할 수 있음.
import org.springframework.web.bind.annotation.RequestMapping;

//NOTE. REST API에서 URI에 변수가 들어가는걸 실무에서 많이 볼 수 있다.
// 예를 들면, 아래 URI에서 밑줄 친 부분이 @PathVariable로 처리해줄 수 있는 부분
// http://localhost:8080/api/user/1234
import org.springframework.web.bind.annotation.PathVariable;

//NOTE. 단일 HTTP 요청 파라미터의 값을 메소드 파라미터에 넣어주는 어노테이션
// 해당 파라미터가 반드시 존재해야하며, 없으면 HTTP 400 - Bad Request 발생
import org.springframework.web.bind.annotation.RequestParam;

//NOTE. HTTP 응답에 대한 기능을 제공
// HTTP 응답 헤더 설정: setHeader(), addHeader() 메서드를 사용하여 HTTP 응답 헤더를 설정
// 예를 들어, Content-Type 헤더를 설정하여 응답의 컨텐츠 유형을 지정
// 응답의 상태 코드 설정: setStatus() 메서드를 사용하여 HTTP 응답의 상태 코드를 설정
// 이를 통해 서버는 클라이언트에게 요청이 성공했는지, 실패했는지 등을 알려줄 수 있다.
// 파일 다운로드를 지원
import javax.servlet.http.HttpServletResponse;

//NOTE. MalformedURLException 클래스는 URL(String) 생성자나 setURL(String) 메서드에서 발생할 수 있는 예외를 처리하기 위해 사용
// 주로 네트워크 관련 프로그래밍에서 URL을 다룰 때 사용. MalformedURLException은 URL이 유효하지 않을 때 발생하므로 이를 적절히 처리하여 프로그램이 예기치 않게 종료되지 않도록 할 수 있다.
// 이를 통해 프로그램이 더 견고하고 안정적으로 동작
import java.net.MalformedURLException;

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
@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    /**
     * "/list" 경로에 대한 GET 요청을 처리하는 메서드
     * 게시판 목록 페이지를 표시하기 위해 "board/list" 뷰를 반환
     */
    @GetMapping("/list")
    public String list() {return "board/list";}

    /**
     * "/view" 경로에 대한 GET 요청을 처리하는 메서드
     * 게시물의 상세 정보를 표시하기 위해 사용
     * 요청된 게시물이 수정 요청인 경우, 해당 게시물의 상세 정보를 조회하고 조회수를 증가
     * 조회된 게시물 상세 정보와 수정 여부를 모델에 추가하여 "board/view" 뷰를 반환
     * @param model 화면에 전달할 데이터를 담는 모델 객체
     * @param boardDTO 사용자가 전달한 게시물 데이터를 담고 있는 DTO 객체
     */
    @GetMapping("/view")
    public String view(Model model, BoardDTO boardDTO) {
        // 게시판 상세 정보를 담을 객체 생성
        BoardVO boardVODtl = new BoardVO();
        // 수정 여부를 설정
        boardVODtl.setModifyYn(boardDTO.getModifyYn());

        //NOTE. 비정상적인 종료를 막기 위해 try-catch를 사용
        try {
            // 수정 요청인 경우, 게시물 상세 정보를 조회하여 모델에 추가하고 조회수를 증가
            if ("Y".equals(boardDTO.getModifyYn())) {
                boardVODtl = boardService.boardDetail(boardDTO);
                boardService.boardViewCountUpdate(boardDTO.getBbsSeq());
            }
        } catch (Exception e) {
            // 예외가 발생한 경우 런타임 예외로 처리
            throw new RuntimeException(e);
            //NOTE. RuntimeException은 주로 프로그래머의 실수에 의해 발생될 수 있는 예외를 의미
        }
        // 게시물 상세 정보와 수정 여부를 모델에 추가
        model.addAttribute("boardDetail", boardVODtl);
        model.addAttribute("modifyYn", boardDTO.getModifyYn());
        return "board/view";
    }

    /**
     * "/form" 경로에 대한 GET 요청을 처리하는 메서드
     * 게시물 작성 또는 수정 폼을 표시하기 위해 사용
     * @param model 화면에 전달할 데이터를 담는 모델 객체
     * @param boardDTO 사용자가 전달한 게시물 데이터를 담고 있는 DTO 객체
     * @return "board/form" 뷰를 반환
     */
    @GetMapping("/form")
    public String form(Model model, BoardDTO boardDTO) {
        // 게시물 상세 정보를 담을 객체 생성
        BoardVO boardVODtl = new BoardVO();
        // 수정 여부를 설정
        boardVODtl.setModifyYn(boardDTO.getModifyYn());

        try {
            // 수정 요청인 경우, 게시물 상세 정보를 조회하여 모델에 추가
            if ("Y".equals(boardDTO.getModifyYn())) {
                boardVODtl = boardService.boardDetail(boardDTO);
            }
        } catch (Exception e) {
            // 예외가 발생한 경우 런타임 예외로 처리
            throw new RuntimeException(e);
        }

        // 게시물 상세 정보와 수정 여부를 모델에 추가
        model.addAttribute("boardDetail", boardVODtl);
        model.addAttribute("modifyYn", boardDTO.getModifyYn());

        // "board/form" 뷰를 반환합니다.
        return "board/form";
    }


    /**
     * "/downloadFile" 경로에 대한 GET 요청을 처리하는 메서드
     * @param res HTTP 응답 객체
     * @param bbsSeq 게시물의 일련번호
     * @param attachFileSeq 다운로드할 파일의 일련번호 기본값은 0
     * @throws Exception 파일 다운로드 중 발생할 수 있는 예외
     */
    @GetMapping("/downloadFile")
    public void downloadFile(HttpServletResponse res, @RequestParam("bbsSeq") int bbsSeq, @RequestParam(value = "attachFileSeq", required = false, defaultValue = "0") int attachFileSeq) throws Exception {
        // boardService의 downloadFile 메서드를 호출하여 파일 다운로드를 수행
        boardService.downloadFile(res, bbsSeq, attachFileSeq);
    }

    /**
     * 미디어 요청을 처리하는 컨트롤러 메서드
     * 이미지 또는 비디오 파일을 제공
     * @param type 미디어의 타입을 나타내는 문자열 "image" 또는 "video"
     * @param filename 요청된 미디어 파일의 이름
     * @return ResponseEntity<Resource> HTTP 응답으로 반환될 리소스 객체
     * @throws MalformedURLException 파일 URL이 잘못된 경우 발생할 수 있는 예외
     */
    @GetMapping(value = "/media/{type}/{filename}")
    public ResponseEntity<Resource> showMedia(@PathVariable String type, @PathVariable String filename) throws MalformedURLException {
        // 미디어 타입에 따라 적절한 리소스를 로드하여 반환
        if (type.equalsIgnoreCase("image")) {
            // 이미지 리소스를 로드
            Resource imageResource = boardService.loadImage(filename);
            // 이미지 리소스를 HTTP 응답으로 반환
            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(imageResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(imageResource);
        } else if (type.equalsIgnoreCase("video")) {
            // 비디오 리소스를 로드
            Resource videoResource = boardService.loadVideo(filename);
            // 비디오 리소스를 HTTP 응답으로 반환
            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(videoResource);
        } else {
            // 지원되지 않는 미디어 타입인 경우 IllegalArgumentException
            throw new IllegalArgumentException("Unsupported media type: " + type);
        }
    }
}