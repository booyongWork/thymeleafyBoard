// 게시판(Board)과 관련된 컨트롤러(Controller) 클래스
package com.blucean.solution.board.controller;

import com.blucean.solution.board.dto.BoardDTO;
import com.blucean.solution.board.dto.AttchDTO;
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
import com.blucean.solution.board.dto.AttchVO;

// 게시판 서비스(BoardService)를 가져오는 import 선언
import com.blucean.solution.board.service.BoardService;

//NOTE. 롬복(Lombok)은 getter, setter, equals, hashCode 등의 메서드를 자동으로 생성
// SLF4J(Simple Logging Facade for Java)는 자바 애플리케이션에서 로깅을 위한 간단한 인터페이스
import lombok.extern.slf4j.Slf4j;

//NOTE. Autowired는 스프링에서 사용되는 어노테이션으로, 객체 간의 연결을 자동화
// 이를 통해 코드를 간결하게 작성하고, 의존성 주입을 편리하게 관리
// 주로 필드, 생성자, 메서드의 파라미터에 사용되며, 해당하는 객체를 스프링이 자동으로 주입
import org.springframework.beans.factory.annotation.Autowired;

// Apache Commons FileUtils를 가져오는 import 선언
import org.apache.commons.io.FileUtils;

//NOTE. Resource는 스프링 프레임워크에서 파일이나 클래스패스 내의 리소스에 접근하는 데 사용되는 도구
// 설정 파일이나 이미지 파일과 같은 리소스를 로드하거나 조작할 때 사용
import org.springframework.core.io.Resource;

// HTTP 상태 코드를 가져오는 import 선언
import org.springframework.http.HttpStatus;
// Spring의 MediaType 클래스를 가져오는 import 선언[이미지]
import org.springframework.http.MediaType;
// Spring의 MediaTypeFactory를 가져오는 import 선언[비디오]
import org.springframework.http.MediaTypeFactory;

//NOTE. ResponseEntity 스프링 프레임워크 내 웹 응용 프로그램에서 HTTP 응답을 생성하는데 사용되는 클래스
// ResponseEntity는 HTTP 응답 본문, 헤더, 상태 코드 및 다른 응답 요소를 캡슐화하고 제어할 수 있는 강력한 도구입니다.
import org.springframework.http.ResponseEntity;

//NOTE. Controller 어노테이션은 스프링 프레임워크에서 컨트롤러 역할을 하는 클래스에 지정
// 클라이언트의 요청을 처리하고 응답을 생성하는 데 사용
// @Controller 어노테이션은 스프링 MVC의 핵심 요소 중 하나로, 클라이언트의 요청을 처리하는 비즈니스 로직과 뷰를 결합하여 전체적인 웹 애플리케이션의 동작을 제어
import org.springframework.stereotype.Controller;

//NOTE. Model은 스프링이 지원하는 기능으로써, key와 value로 이루어져있는 HashMap이다.
// Model의 .addAttribute()를 통해 view에 전달할 데이터를 저장할 수 있다.
import org.springframework.ui.Model;

//NOTE. @GetMapping 은 스프링 프레임워크에서 사용하는 어노테이션이다.
// 이 어노테이션은 HTTP GET 요청을 특정 메서드와 매핑하기 위해서 사용된다.
// HTTP GET 요청은 일반적으로 서버에서 정보를 조회하는 데 사용된다.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//NOTE. REST API에서 URI에 변수가 들어가는걸 실무에서 많이 볼 수 있다.
// 예를 들면, 아래 URI에서 밑줄 친 부분이 @PathVariable로 처리해줄 수 있는 부분이다.
// http://localhost:8080/api/user/1234
import org.springframework.web.bind.annotation.PathVariable;

//NOTE. 단일 HTTP 요청 파라미터의 값을 메소드 파라미터에 넣어주는 어노테이션
// 해당 파라미터가 반드시 존재해야하며, 없으면 HTTP 400 - Bad Request 발생
import org.springframework.web.bind.annotation.RequestParam;

// Servlet API의 HttpServletResponse를 가져오는 import 선언
import javax.servlet.http.HttpServletResponse;
// Java IO의 File 클래스를 가져오는 import 선언
import java.io.File;
// Java IO의 FileInputStream 클래스를 가져오는 import 선언
import java.io.FileInputStream;
// Java IO의 FileNotFoundException 클래스를 가져오는 import 선언
import java.io.FileNotFoundException;
// Java net의 MalformedURLException 클래스를 가져오는 import 선언
import java.net.MalformedURLException;
// Java net의 URLEncoder 클래스를 가져오는 import 선언
import java.net.URLEncoder;
// Java nio.charset의 StandardCharsets 클래스를 가져오는 import 선언
import java.nio.charset.StandardCharsets;
// Java util의 List 인터페이스를 가져오는 import 선언
import java.util.List;
// Java util.zip의 ZipEntry 클래스를 가져오는 import 선언
import java.util.zip.ZipEntry;
// Java util.zip의 ZipOutputStream 클래스를 가져오는 import 선언
import java.util.zip.ZipOutputStream;



@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
    public String list() {return "board/list";}

    @GetMapping("/view")
    public String view(Model model, BoardDTO boardDTO) {
        BoardVO boardVODtl = new BoardVO();
        boardVODtl.setModifyYn(boardDTO.getModifyYn());

        try {
            // 수정 요청인 경우, 게시물 상세 정보를 조회하여 모델에 추가
            if ("Y".equals(boardDTO.getModifyYn())) {
                boardVODtl = boardService.boardDetail(boardDTO);
                boardService.boardViewCountUpdate(boardDTO.getBbsSeq());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("boardDetail", boardVODtl);
        model.addAttribute("modifyYn", boardDTO.getModifyYn());
        return "board/view";
    }

    @GetMapping("/form")
    public String form(Model model, BoardDTO boardDTO) {
        BoardVO boardVODtl = new BoardVO();
        boardVODtl.setModifyYn(boardDTO.getModifyYn());

        try {
            // 수정 요청인 경우, 게시물 상세 정보를 조회하여 모델에 추가
            if ("Y".equals(boardDTO.getModifyYn())) {
                boardVODtl = boardService.boardDetail(boardDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("boardDetail", boardVODtl);
        model.addAttribute("modifyYn", boardDTO.getModifyYn());
        return "board/form";
    }

    /**
     * 게시판 파일 다운로드
     */
    @GetMapping("/downloadFile")
    public void downloadFile(HttpServletResponse res, @RequestParam("bbsSeq") int bbsSeq, @RequestParam(value = "attachFileSeq", required = false, defaultValue = "0") int attachFileSeq) throws Exception {
        // fileNo를 이용해 파일 정보를 조회
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBbsSeq(bbsSeq);
        BoardVO reqBoardDetailVO = boardService.boardDetail(boardDTO);

        // 파일 정보가 존재하지 않으면 404 에러를 반환
        if (reqBoardDetailVO == null) {
            res.sendError(HttpStatus.NOT_FOUND.value());
            return;
        }

        //전체 다운로드
        if (attachFileSeq == 0) {
            //압축할 파일
            List<AttchVO> fileList = reqBoardDetailVO.getUploadBoardFile();

            //생성되는 zip 파일명
            String fileName = reqBoardDetailVO.getTitle() + "File";

            res.setContentType("application/octet-stream");
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            res.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".zip" + ";");
            res.setStatus(HttpServletResponse.SC_OK);

            try (ZipOutputStream zos = new ZipOutputStream(res.getOutputStream())) {

                for (AttchVO files : fileList) {

                    String fullPath = files.getFilePath() + "/" + files.getSaveFileNm();
                    String originalFileName = files.getAttachFileNm();

                    try (FileInputStream fis = new FileInputStream(fullPath)) {

                        ZipEntry zipEntry = new ZipEntry(originalFileName);

                        zos.putNextEntry(zipEntry);

                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                        zos.closeEntry();
                    }
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();
            }
        } else { //개별 다운로드
            //attachFileSeq의 파일 상세 내용 조회
            AttchDTO attchDTO = new AttchDTO();
            attchDTO.setBbsSeq(bbsSeq);
            attchDTO.setAttachFileSeq(attachFileSeq);
            AttchDTO fileDetail = boardService.fileDetail(attchDTO);

            // 파일 경로와 파일명을 합쳐 전체 경로를 생성
            // String fullPath = fileDTO.getFilePath() + "/" + fileDTO.getFileName();
            String fullPath = fileDetail.getFilePath() + "/" + fileDetail.getSaveFileNm();
            String originalFileName = fileDetail.getAttachFileNm();

            log.info("fullPath : {}", fullPath);

            // 파일 객체를 생성하고 파일을 byte 배열로 읽음
            File file = new File(fullPath);
            byte[] fileByte = FileUtils.readFileToByteArray(file);

            // 파일의 MIME 타입을 지정하고 파일 크기를 지정
            res.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            res.setContentLength(fileByte.length);

            // 파일명을 URL 인코딩
            String encFileName = URLEncoder.encode(originalFileName, "UTF-8").replace("+", " ");

            // Content-Disposition 헤더에 파일 다운로드에 필요한 정보를 추가
            res.setHeader("Content-Disposition", "attachment; fileName=\"" + encFileName + "\";");
            res.setHeader("Content-Transfer-Encoding", "binary");

            // 파일을 출력합니다.
            res.getOutputStream().write(fileByte);
        }
    }

    // 이미지 요청을 처리하는 컨트롤러
    @GetMapping(value = "/media/{type}/{filename}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<Resource> showMedia(@PathVariable String type, @PathVariable String filename) throws MalformedURLException {

        if (type.equalsIgnoreCase("image")) {
            Resource imageResource = boardService.loadImage(filename);
            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(imageResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(imageResource);
        } else if (type.equalsIgnoreCase("video")) {
            Resource videoResource = boardService.loadVideo(filename);
            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(videoResource);
        } else {
            throw new IllegalArgumentException("Unsupported media type: " + type);
        }
    }
}