package com.blucean.solution.board.controller;

import com.blucean.solution.board.dto.AttchDTO;
import com.blucean.solution.board.dto.AttchVO;
import com.blucean.solution.board.dto.BoardDTO;
import com.blucean.solution.board.dto.BoardVO;
import com.blucean.solution.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
    public String list(Model model, BoardDTO boardDTO) {
        boardDTO.setBbsId("Board");


        return "board/list";
    }

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
//        boardVODtl.setBbsId(BoardType.REQUEST.name());
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
    public void downloadFile(HttpServletRequest req, HttpServletResponse res, @RequestParam("bbsSeq") int bbsSeq, @RequestParam(value = "attachFileSeq", required = false, defaultValue = "0") int attachFileSeq) throws Exception {
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