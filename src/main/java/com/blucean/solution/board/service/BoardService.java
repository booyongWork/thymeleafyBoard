// 게시판(Board)과 관련된 서비스(Service) 클래스
package com.blucean.solution.board.service;

import com.blucean.solution.board.dto.AttchDTO;
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
import com.blucean.solution.board.dto.AttchVO;
import com.blucean.solution.board.dto.BoardVO;

import com.blucean.solution.board.mapper.BoardMapper;

//NOTE. 롬복(Lombok)은 getter, setter, equals, hashCode 등의 메서드를 자동으로 생성
// SLF4J(Simple Logging Facade for Java)는 자바 애플리케이션에서 로깅을 위한 간단한 인터페이스
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

//NOTE. Autowired는 스프링에서 사용되는 어노테이션으로, 객체 간의 연결을 자동화
// 이를 통해 코드를 간결하게 작성하고, 의존성 주입을 편리하게 관리
// 주로 필드, 생성자, 메서드의 파라미터에 사용되며, 해당하는 객체를 스프링이 자동으로 주입
import org.springframework.beans.factory.annotation.Autowired;

//@Value annotation은 설정파일(.properties, .yml)에 설정한 내용을 주입시켜주는 어노테이션
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

@Service
@Slf4j
public class BoardService {

    @Value("${blucean.file.board}")
    private String filePath;

    @Autowired
    private BoardMapper boardMapper;

    /**
     * 게시판 목록을 조회하는 메서드.
     *
     * @param boardDTO 게시판 조회에 필요한 조건을 담은 DTO 객체
     * @return 조회된 게시판 목록을 담은 리스트
     */
    public List<BoardVO> boardList(BoardDTO boardDTO) {
        log.debug("################ BoardService.boardList ################");

        // 게시판 목록을 데이터베이스에서 조회
        List<BoardDTO> boardList = boardMapper.boardList(boardDTO);

        // 결과를 담을 ReqBoardVO 객체와 리스트를 초기화
        BoardVO resultVO;
        List<BoardVO> result = new ArrayList<>();

        // 조회된 게시판 목록을 반복문을 통해 ReqBoardVO 객체에 값을 저장하고, 리스트에 추가
        for(BoardDTO tempDto : boardList) {
            resultVO = new BoardVO();
            resultVO.setBbsSeq(tempDto.getBbsSeq());
            resultVO.setTitle(tempDto.getTitle());
            resultVO.setWrtrDd(tempDto.getWrtrDd());
            resultVO.setLstUpdDdtm(tempDto.getLstUpdDdtm());
            resultVO.setRowNum(tempDto.getRowNum());
            resultVO.setRecordTotal(tempDto.getRecordTotal());
            resultVO.setPageIndex(tempDto.getPageIndex());
            resultVO.setPageSize(tempDto.getPageSize());
            resultVO.setStartRow(tempDto.getStartRow());
            resultVO.setEndRow(tempDto.getEndRow());
            resultVO.setSearchText(tempDto.getSearchText());
            resultVO.setInqCnt(tempDto.getInqCnt());
            resultVO.setImgYn(tempDto.getImgYn());

            result.add(resultVO);
        }

        // 결과 리스트를 반환
        return result;
    }


    /**
     * 게시글 상세 정보를 조회하는 메서드.
     *
     * @param boardDTO 조회할 게시글의 정보를 담은 DTO 객체
     * @return 조회된 게시글의 상세 정보를 담은 VO 객체
     */
    public BoardVO boardDetail(BoardDTO boardDTO) {
        log.debug("################ BoardService.boardDetail ################");

        // 게시글 상세 정보를 데이터베이스에서 조회
        BoardDTO boardDetail = boardMapper.boardDetail(boardDTO);

        // 조회된 정보를 담을 BoardVO 객체를 생성
        BoardVO resultVO = new BoardVO();
        resultVO.setBbsSeq(boardDetail.getBbsSeq());
        resultVO.setTitle(boardDetail.getTitle());
        resultVO.setContn(boardDetail.getContn());
        resultVO.setWrtrId(boardDetail.getWrtrId());
        resultVO.setWrtrDd(boardDetail.getWrtrDd());
        resultVO.setWrtrTm(boardDetail.getWrtrTm());
        resultVO.setLstUpdUsrId(boardDetail.getLstUpdUsrId());
        resultVO.setLstUpdUsrNm(boardDetail.getLstUpdUsrNm());
        resultVO.setLstUpdDdtm(boardDetail.getLstUpdDdtm());
        resultVO.setUseYn(boardDetail.getUseYn());

        // 첨부파일 정보를 조회
        List<AttchDTO> fileList = boardMapper.selectBoardFileList(boardDTO.getBbsSeq());

        List<AttchVO> result = new ArrayList<>();

        // 조회된 첨부파일 정보를 AttchVO 객체에 담아 리스트에 추가
        for(AttchDTO attchDTO : fileList) {
            AttchVO attchVO = new AttchVO();
            attchVO.setBbsSeq(attchDTO.getBbsSeq());
            attchVO.setAttachFileNm(attchDTO.getAttachFileNm());
            attchVO.setSaveFileNm(attchDTO.getSaveFileNm());
            attchVO.setFilePath(attchDTO.getFilePath());
            attchVO.setAttachFileSeq(attchDTO.getAttachFileSeq());

            // 파일 확장자에 따라 파일 유형 설정
            String saveFileNm = attchDTO.getSaveFileNm();
            // 첨부파일이 존재할때만
            if (saveFileNm != null && !saveFileNm.isEmpty()) {
                String extension = saveFileNm.substring(saveFileNm.lastIndexOf(".") + 1).toLowerCase();
                if (extension.equals("jpg") || extension.equals("png") || extension.equals("gif")) {
                    attchVO.setType("image");
                } else if (extension.equals("mp3")) {
                    attchVO.setType("audio");
                } else if (extension.equals("mp4") || extension.equals("avi") || extension.equals("mov")) {
                    attchVO.setType("video");
                } else {
                    attchVO.setType("other");
                }
            } else {
                attchVO.setType("unknown");
            }

            result.add(attchVO);
        }

        // 첨부파일이 존재하면 VO 객체에 추가 정보 설정
        if (!CollectionUtils.isEmpty(result)) {
            // 업로드 된 파일 정보
            resultVO.setUploadBoardFile(result);
            // 첨부 파일의 저장 파일명이 비어있지 않고 파일 유형이 null이 아닌 경우
            if (!result.get(0).getSaveFileNm().isEmpty() && result.get(0).getType() != null) {
//                resultVO.setFileUrl(result.get(0).getSaveFileNm());
                resultVO.setType(result.get(0).getType());
            }
        }

        return resultVO;
    }



    /**
     * 게시글을 등록 또는 수정하는 메서드.
     *
     * @param boardDTO       등록 또는 수정할 게시글의 정보를 담은 DTO 객체
     * @param multiFileList  게시글에 첨부할 파일 목록을 담은 리스트
     * @throws Exception 예외 발생 시 처리
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void boardWrite(BoardDTO boardDTO, List<MultipartFile> multiFileList) throws Exception {
        log.debug("################ BoardService.boardWrite ################");
        // 첨부파일 정보 객체 생성
        AttchDTO attchDTO = new AttchDTO();
        if ("Y".equals(boardDTO.getModifyYn())) {
            // 게시글 수정
            this.boardUpdate(boardDTO);

            //실제폴더에 담겨 있는 데이터 값 삭제
            List<AttchDTO> selectFileListNotIn = boardMapper.selectFileListNotIn(boardDTO);
            for (AttchDTO file : selectFileListNotIn) {
                new File(file.getFilePath() + "/" + file.getSaveFileNm()).delete();
            }

            // 수정모드일 경우 기존 첨부파일 삭제 처리
            attchDTO.setBbsSeq(boardDTO.getBbsSeq());
            attchDTO.setAttachFileSeqList(boardDTO.getAttachFileSeqList());
            boardMapper.fileDelete(attchDTO);

            //새로운 파일 추가 시
            if (!CollectionUtils.isEmpty(multiFileList)) {
                // 파일 정보 저장을 위한 리스트 생성
                List<AttchDTO> fileList = new ArrayList<>();

                // 파일 업로드 처리 및 정보 저장
                String uploadPath = filePath; // 업로드 경로 설정

                File uploadDir = new File(uploadPath); // 업로드 디렉토리
                if (!uploadDir.exists()) {
                    uploadDir.mkdir(); // 디렉토리가 존재하지 않으면 생성
                }

                for(int i = 0; i < multiFileList.size(); i++) {
                    attchDTO = new AttchDTO();
                    String originalFilename = multiFileList.get(i).getOriginalFilename();
                    String uploadFilename = UUID.randomUUID() + "_" + originalFilename;
                    // 확장자 추출
                    String fileExtension = getFileExtension(originalFilename);
                    // 업로드 경로 설정 - 각 확장자별로 폴더 생성
                    String uploadPathWithExtension = uploadPath + "/" + fileExtension;
                    File uploadDirWithExtension = new File(uploadPathWithExtension);

                    // 해당 확장자의 폴더가 없으면 생성
                    if (!uploadDirWithExtension.exists()) {
                        uploadDirWithExtension.mkdir();
                    }

                    attchDTO.setAttachFileNm(originalFilename);
                    attchDTO.setFilePath(uploadPathWithExtension);
                    attchDTO.setSaveFileNm(uploadFilename);
                    fileList.add(attchDTO);
                }

                // 첨부파일 정보 DB에 저장
                for (AttchDTO file : fileList) {
                    file.setBbsSeq(boardDTO.getBbsSeq());
                    boardMapper.insertBoardFileList(file);
                }

                // 파일업로드
                try {
                    for (int i = 0; i < multiFileList.size(); i++) {
                        attchDTO = fileList.get(i);
                        String originalFilename = attchDTO.getAttachFileNm();
                        String uploadFilename = attchDTO.getSaveFileNm();
                        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

                        // 확장자별로 폴더 생성
                        String extensionFolder = uploadPath + File.separator + fileExtension;
                        File extensionDir = new File(extensionFolder);
                        if (!extensionDir.exists()) {
                            extensionDir.mkdirs();
                        }

                        // 전체 파일 경로
                        String fullFilePath = extensionFolder + File.separator + uploadFilename;

                        File uploadFile = new File(fullFilePath);
                        multiFileList.get(i).transferTo(uploadFile);
                    }
                    System.out.println("다중 파일 업로드 성공");
                } catch (IllegalStateException | IOException e) {
                    System.out.println("다중 파일 업로드 실패");

                    // 만약 업로드 실패하면 파일 삭제
                    for (int i = 0; i < multiFileList.size(); i++) {
                        attchDTO = fileList.get(i);
                        String fullFilePath = uploadPath + File.separator + attchDTO.getSaveFileNm();
                        new File(fullFilePath).delete();
                    }

                    throw new Exception(e.getMessage());
                }
            }
        } else { // "N".equals(fileBoardDTO.getModifyYn())인 경우 (새로운 글 등록)
            boardMapper.boardWrite(boardDTO);

            // 파일 정보 저장을 위한 리스트 생성
            List<AttchDTO> fileList = new ArrayList<>();

            if (!CollectionUtils.isEmpty(multiFileList)) {
                // 파일 업로드 처리 및 정보 저장
                String uploadPath = filePath; // 업로드 경로 설정
                File uploadDir = new File(uploadPath); // 업로드 디렉토리
                if (!uploadDir.exists()) {
                    uploadDir.mkdir(); // 디렉토리가 존재하지 않으면 생성
                }

                for(int i = 0; i < multiFileList.size(); i++) {
                    attchDTO = new AttchDTO();
                    String originalFilename = multiFileList.get(i).getOriginalFilename();
                    String uploadFilename = UUID.randomUUID() + "_" + originalFilename;
                    // 확장자 추출
                    String fileExtension = getFileExtension(originalFilename);
                    // 업로드 경로 설정 - 각 확장자별로 폴더 생성
                    String uploadPathWithExtension = uploadPath + "/" + fileExtension;
                    File uploadDirWithExtension = new File(uploadPathWithExtension);

                    // 해당 확장자의 폴더가 없으면 생성
                    if (!uploadDirWithExtension.exists()) {
                        uploadDirWithExtension.mkdir();
                    }

                    attchDTO.setAttachFileNm(originalFilename);
                    attchDTO.setFilePath(uploadPathWithExtension);
                    attchDTO.setSaveFileNm(uploadFilename);
                    fileList.add(attchDTO);
                }

                for (AttchDTO file : fileList) {
                    file.setBbsSeq(boardDTO.getBbsSeq());
                    boardMapper.insertBoardFileList(file);
                }

                // 파일업로드
                try {
                    for (int i = 0; i < multiFileList.size(); i++) {
                        attchDTO = fileList.get(i);
                        String originalFilename = attchDTO.getAttachFileNm();
                        String uploadFilename = attchDTO.getSaveFileNm();
                        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

                        // 확장자별로 폴더 생성
                        String extensionFolder = uploadPath + File.separator + fileExtension;
                        File extensionDir = new File(extensionFolder);
                        if (!extensionDir.exists()) {
                            extensionDir.mkdirs();
                        }

                        // 전체 파일 경로
                        String fullFilePath = extensionFolder + File.separator + uploadFilename;

                        File uploadFile = new File(fullFilePath);
                        multiFileList.get(i).transferTo(uploadFile);
                    }
                    System.out.println("다중 파일 업로드 성공");
                } catch (IllegalStateException | IOException e) {
                    System.out.println("다중 파일 업로드 실패");

                    // 만약 업로드 실패하면 파일 삭제
                    for (int i = 0; i < multiFileList.size(); i++) {
                        attchDTO = fileList.get(i);
                        String fullFilePath = uploadPath + File.separator + attchDTO.getSaveFileNm();
                        new File(fullFilePath).delete();
                    }

                    throw new Exception(e.getMessage());
                }
            }
        }
    }

    /**
     * 주어진 파일 이름에서 확장자를 추출하는 메서드.
     *
     * @param fileName 파일 이름
     * @return 추출된 확장자. 확장자가 없는 경우 빈 문자열 반환
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return ""; // 확장자가 없는 경우
    }

    /**
     * 게시글을 수정하는 메서드.
     *
     * @param boardDTO 수정할 게시글의 정보를 담은 DTO 객체
     * @throws Exception 예외 발생 시 처리
     */
    public void boardUpdate(BoardDTO boardDTO) {
        log.debug("################ BoardService.boardUpdate ################");
        try {
            boardMapper.boardUpdate(boardDTO);
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: {}", e.getMessage());
        }
    }


    /**
     * 게시글을 삭제하는 메서드.
     *
     * @param bbsSeq 삭제할 게시글의 일련번호
     */
    public void boardDelete(int bbsSeq) {
        log.debug("################ BoardService.boardDelete ################");
        AttchDTO attchDTO = new AttchDTO();
        attchDTO.setBbsSeq(bbsSeq);
        if (attchDTO.getAttachFileSeqList() == null || attchDTO.getAttachFileSeqList().isEmpty()) {
            attchDTO.setAttachFileSeqList(Collections.singletonList(0)); // or any other default value
        }
        boardMapper.fileDelete(attchDTO);
        boardMapper.boardDelete(bbsSeq);
    }

    /**
     * 게시글의 조회수를 업데이트하는 메서드.
     *
     * @param bbsSeq 조회수를 업데이트할 게시글의 일련번호
     */
    public void boardViewCountUpdate(int bbsSeq) {
        log.debug("################ BoardService.boardViewCountUpdate ################");
        try {
            boardMapper.notiViewCountUpdate(bbsSeq);

        } catch (Exception e) {
            log.error("text : {}", e.getMessage());
        }
    }

    /**
     * 첨부 파일의 상세 정보를 가져오는 메서드.
     *
     * @param attchDTO 첨부 파일의 상세 정보를 담은 DTO 객체
     * @return 가져온 첨부 파일의 상세 정보
     */
    public AttchDTO fileDetail(AttchDTO attchDTO) {
        log.debug("################ BoardService.fileDetail ################");
        return boardMapper.fileDetail(attchDTO);
    }

    /**
     * 이미지 파일을 로드하는 메서드.
     *
     * @param filename 로드할 이미지 파일의 이름
     * @return 로드한 이미지 파일의 리소스
     * @throws MalformedURLException 파일 경로 생성 중 오류 발생 시 처리
     */
    public Resource loadImage(String filename) throws MalformedURLException {
        // 이미지 파일의 실제 경로를 가져옴
        // 확장자 추출
        String extension = filename.substring(filename.lastIndexOf('.') + 1);

        // 확장자에 해당하는 폴더 경로 생성
        String folderPath = Paths.get(this.filePath, extension).toString();

        // 파일 경로 생성
        Path filePath = Paths.get(folderPath, filename).normalize();

        // 리소스 로드
        Resource resource = new UrlResource(filePath.toUri());
        //NOTE. Resource "리소스"는 프로그래밍에서 다양한 종류의 데이터나 정보를 가리키는 말이다.
        // 일반적으로는 소프트웨어나 애플리케이션에서 사용되는 파일, 데이터베이스의 레코드, 네트워크 상의 자원 등을 가리킨다.

        // 이미지 파일이 존재하고 읽을 수 있는지 확인
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("이미지 파일을 읽을 수 없습니다.");
        }

        return resource;
    }

    /**
     * 비디오 파일을 로드하는 메서드.
     *
     * @param filename 로드할 비디오 파일의 이름
     * @return 로드한 비디오 파일의 리소스
     * @throws MalformedURLException 파일 경로 생성 중 오류 발생 시 처리
     */
    public Resource loadVideo(String filename) throws MalformedURLException {
        // 비디오 파일의 실제 경로를 가져옴
        // 확장자 추출
        String extension = filename.substring(filename.lastIndexOf('.') + 1);

        // 확장자에 해당하는 폴더 경로 생성
        String folderPath = Paths.get(this.filePath, extension).toString();

        // 파일 경로 생성
        Path filePath = Paths.get(folderPath, filename).normalize();

        // 리소스 로드
        Resource resource = new UrlResource(filePath.toUri());

        // 비디오 파일이 존재하고 읽을 수 있는지 확인
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("비디오 파일을 읽을 수 없습니다.");
        }

        return resource;
    }

    /**
     * 파일을 다운로드하는 메서드.
     *
     * @param res HttpServletResponse 객체
     * @param bbsSeq 게시글의 일련번호
     * @param attachFileSeq 첨부 파일의 일련번호
     * @throws Exception 파일 다운로드 중 오류 발생 시 처리
     */
    public void downloadFile(HttpServletResponse res, int bbsSeq, int attachFileSeq) throws Exception {
        // fileNo를 이용해 파일 정보를 조회
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBbsSeq(bbsSeq);
        BoardVO reqBoardDetailVO = this.boardDetail(boardDTO);

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
            AttchDTO fileDetail = this.fileDetail(attchDTO);

            // 파일 경로와 파일명을 합쳐 전체 경로를 생성
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

    public void apiFileUpload(MultipartFile file) {
        // 파일 정보 저장을 위한 리스트 생성
        List<AttchDTO> fileList = new ArrayList<>();

        // 파일 업로드 처리 및 정보 저장
        String uploadPath = filePath; // 업로드 경로 설정

        File uploadDir = new File(uploadPath); // 업로드 디렉토리
        if (!uploadDir.exists()) {
            uploadDir.mkdir(); // 디렉토리가 존재하지 않으면 생성
        }
// 첨부파일 정보 객체 생성
        AttchDTO attchDTO = new AttchDTO();
        String originalFilename = file.getOriginalFilename();
        String uploadFilename = UUID.randomUUID() + "_" + originalFilename;
        // 확장자 추출
        String fileExtension = getFileExtension(originalFilename);
        // 업로드 경로 설정 - 각 확장자별로 폴더 생성
        String uploadPathWithExtension = uploadPath + "/" + fileExtension;
        File uploadDirWithExtension = new File(uploadPathWithExtension);

        // 해당 확장자의 폴더가 없으면 생성
        if (!uploadDirWithExtension.exists()) {
            uploadDirWithExtension.mkdir();
        }
        attchDTO.setAttachFileNm(originalFilename);
        attchDTO.setFilePath(uploadPathWithExtension);
        attchDTO.setSaveFileNm(uploadFilename);

        boardMapper.apiFileUpload(attchDTO);
    }
}
