package com.blucean.solution.board.service;

import com.blucean.solution.board.dto.AttchDTO;
import com.blucean.solution.board.dto.AttchVO;
import com.blucean.solution.board.dto.BoardDTO;
import com.blucean.solution.board.dto.BoardVO;
import com.blucean.solution.board.mapper.BoardMapper;
import com.blucean.solution.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class BoardService {

    @Value("${blucean.file.board}")
    private String filePath;

    @Autowired
    private BoardMapper boardMapper;

    /**
     * 게시판 목록 조회
     *
     */
    public List<BoardVO> boardList(BoardDTO boardDTO) throws CustomException {
        log.debug("################ BoardService.boardList ################");

        List<BoardDTO> boardList = boardMapper.boardList(boardDTO);

        // 결과를 담을 ReqBoardVO 객체와 리스트를 초기화
        BoardVO resultVO;
        List<BoardVO> result = new ArrayList<>();

        // 조회된 게시판 목록을 반복문을 통해 ReqBoardVO 객체에 값을 저장하고, 리스트에 추가
        for(BoardDTO tempDto : boardList) {
            resultVO = new BoardVO();
            resultVO.setBbsSeq(tempDto.getBbsSeq());
            resultVO.setBbsId(tempDto.getBbsId());
            resultVO.setBbsGrpSeq(tempDto.getBbsGrpSeq());
            resultVO.setBbsGrpIdx(tempDto.getBbsGrpIdx());
            resultVO.setWrtrIp(tempDto.getWrtrId());
            resultVO.setWrtrNm(tempDto.getWrtrNm());
            resultVO.setUsrDefine1(tempDto.getUsrDefine1());
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
     * 게시판 상세
     *
     */
    public BoardVO boardDetail(BoardDTO boardDTO) throws CustomException {
        log.debug("################ BoardService.boardDetail ################");
        BoardDTO boardDetail = boardMapper.boardDetail(boardDTO);
//        boardDetail.setContn(CommonUtil.getDeRplcAll(boardDetail.getContn()));
        // 조회된 정보를 담을 ReqBoardVO 객체를 생성
        BoardVO resultVO;
        resultVO = new BoardVO();
        resultVO.setBbsSeq(boardDetail.getBbsSeq());
        resultVO.setBbsId(boardDetail.getBbsId());
        resultVO.setBbsGrpSeq(boardDetail.getBbsGrpSeq());
        resultVO.setBbsGrpIdx(boardDetail.getBbsGrpIdx());
        resultVO.setBbsDepth(boardDetail.getBbsDepth());
        resultVO.setTitle(boardDetail.getTitle());
        resultVO.setContn(boardDetail.getContn());
        resultVO.setWrtrId(boardDetail.getWrtrId());
        resultVO.setWrtrNm(boardDetail.getWrtrNm());
        resultVO.setWrtrIp(boardDetail.getWrtrIp());
        resultVO.setWrtrDd(boardDetail.getWrtrDd());
        resultVO.setWrtrTm(boardDetail.getWrtrTm());
        resultVO.setUsrDefine1(boardDetail.getUsrDefine1());
        resultVO.setUsrDefine2(boardDetail.getUsrDefine2());
        resultVO.setLstUpdUsrId(boardDetail.getLstUpdUsrId());
        resultVO.setLstUpdUsrNm(boardDetail.getLstUpdUsrNm());
        resultVO.setLstUpdDdtm(boardDetail.getLstUpdDdtm());
        resultVO.setUseYn(boardDetail.getUseYn());

        // 첨부파일 정보를 조회
        List<AttchDTO> fileList = boardMapper.selectBoardFileList(boardDTO.getBbsSeq());

        AttchVO attchVO;
        List<AttchVO> result = new ArrayList<>();

        // 조회된 첨부파일 정보를 AttchVO 객체에 담아 리스트에 추가
        for(AttchDTO attchDTO : fileList) {
            attchVO = new AttchVO();
            attchVO.setBbsSeq(attchDTO.getBbsSeq());
            attchVO.setBbsId(attchDTO.getBbsId());
            attchVO.setAttachFileNm(attchDTO.getAttachFileNm());
            attchVO.setSaveFileNm(attchDTO.getSaveFileNm());
            attchVO.setFilePath(attchDTO.getFilePath());
            attchVO.setAttachFileSeq(attchDTO.getAttachFileSeq());

            result.add(attchVO);
        }
        // result 리스트를 resultVO의 fileList 필드에 저장하고 resultVO를 반환
        resultVO.setUploadBoardFile(result);
        resultVO.setFileUrl(result.get(0).getSaveFileNm());

        return resultVO;
    }

    /**
     * 게시판 등록
     *
     */
    @Transactional(rollbackFor = CustomException.class, propagation = Propagation.REQUIRED)
    public void boardWrite(BoardDTO boardDTO, List<MultipartFile> multiFileList) throws CustomException {
        log.debug("################ BoardService.boardWrite ################");
//        boardDTO.setContn(CommonUtil.getRplcAll(boardDTO.getContn()));
        // 첨부파일 정보 객체 생성
        AttchDTO attchDTO = new AttchDTO();
        if ("Y".equals(boardDTO.getModifyYn())) {
            // 게시글 수정
            this.boardUpdate(boardDTO);

            //실제폴더에 담겨 있는 데이터 값 삭제
            /*List<AttchDTO> selectFileListNotIn = boardMapper.selectFileListNotIn(boardDTO);
            for(int i = 0; i < selectFileListNotIn.size(); i++) {
                new File(selectFileListNotIn.get(i).getFilePath() +"/"+ selectFileListNotIn.get(i).getSaveFileNm()).delete();
            }*/

            List<AttchDTO> selectFileListNotIn = boardMapper.selectFileListNotIn(boardDTO);
            for (AttchDTO file : selectFileListNotIn) {
                new File(file.getFilePath() + "/" + file.getSaveFileNm()).delete();
            }

            // 수정모드일 경우 기존 첨부파일 삭제 처리
            attchDTO.setBbsSeq(boardDTO.getBbsSeq());
            attchDTO.setAttachFileSeqList(boardDTO.getAttachFileSeqList());
            attchDTO.setBbsId(boardDTO.getBbsId());
            boardMapper.fileDelete(attchDTO);


            //새로운 파일 추가 시
            if (!CollectionUtils.isEmpty(multiFileList)) {

                // 파일 정보 저장을 위한 리스트 생성
                List<AttchDTO> fileList = new ArrayList<>();

                // 파일 업로드 경로 지정 및 폴더 생성
                String uploadPath = filePath;

                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                // 파일 업로드 및 정보 저장
                for(int i = 0; i < multiFileList.size(); i++) {
                    String originalFilename = multiFileList.get(i).getOriginalFilename();
                    String uploadFilename = UUID.randomUUID() + "_" + originalFilename;

                    // 첨부파일 정보 저장
                    attchDTO = new AttchDTO();
                    attchDTO.setBbsId(boardDTO.getBbsId());
                    attchDTO.setAttachFileNm(originalFilename);
                    attchDTO.setFilePath(uploadPath);
                    attchDTO.setSaveFileNm(uploadFilename);
                    fileList.add(attchDTO);
                }

                // 첨부파일 정보 DB에 저장
                for (AttchDTO file : fileList) {
                    file.setBbsSeq(boardDTO.getBbsSeq());
                    boardMapper.insertBoardFileList(file);
                }

                // 파일 업로드 처리
                try {
                    for(int i = 0; i < multiFileList.size(); i++) {
                        File uploadFile = new File(uploadPath +"/"+ fileList.get(i).getSaveFileNm());
                        multiFileList.get(i).transferTo(uploadFile);
                    }
                    System.out.println("다중 파일 업로드 성공");

                } catch (IllegalStateException | IOException e) {
                    // 파일 업로드 실패 시, 업로드된 파일 삭제 및 예외 발생
                    System.out.println("다중 파일 업로드 실패");

                    for(int i = 0; i < multiFileList.size(); i++) {
                        new File(uploadPath +"/"+ fileList.get(i).getSaveFileNm()).delete();
                    }

                    throw new CustomException(e.getMessage());
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

                    attchDTO.setBbsId(boardDTO.getBbsId());
                    attchDTO.setAttachFileNm(originalFilename);
                    attchDTO.setFilePath(uploadPathWithExtension);
                    attchDTO.setSaveFileNm(uploadFilename);
                    fileList.add(attchDTO);
                }

                //log.error("getBbsSeq : {}", reqBoardVO.getBbsSeq());

                for (AttchDTO file : fileList) {
                    file.setBbsSeq(boardDTO.getBbsSeq());
                    file.setBbsId(boardDTO.getBbsId());
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

                    throw new CustomException(e.getMessage());
                }
            }
        }
    }

    // 파일 확장자를 추출하는 유틸리티 메서드
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return ""; // 확장자가 없는 경우
    }


    /**
     * 게시판 수정
     *
     */
    public void boardUpdate(BoardDTO boardDTO) throws CustomException {
        log.debug("################ BoardService.boardUpdate ################");
        try {
            boardMapper.boardUpdate(boardDTO);

        } catch (Exception e) {
            log.error("text : {}", e.getMessage());
        }
    }

//    /**
//     * 게시판 삭제
//     *
//     */
//    public void boardDelete(BoardDTO boardDTO) throws CustomException {
//        log.debug("################ BoardService.boardDelete ################");
//        AttchDTO attchDTO = new AttchDTO();
//        attchDTO.setBbsSeq(boardDTO.getBbsSeq());
//        boardMapper.fileDelete(attchDTO);
//        boardMapper.boardDelete(boardDTO);
//
//    }

    /**
     * 공지사항 조회수
     *
     */
    public void boardViewCountUpdate(int bbsSeq) throws CustomException {
        log.debug("################ BoardService.boardViewCountUpdate ################");
        try {
            boardMapper.notiViewCountUpdate(bbsSeq);

        } catch (Exception e) {
            log.error("text : {}", e.getMessage());
        }
    }

    public List<BoardDTO> getNoticeList(Map map) {
        log.debug("################ BoardService.getNoticeList ################");
        return boardMapper.selectNoticeList(map);
    }

    /**
     * 파일정보 상세
     *
     */
    public AttchDTO fileDetail(AttchDTO attchDTO) throws CustomException {
        log.debug("################ BoardService.fileDetail ################");
        return boardMapper.fileDetail(attchDTO);
    }

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

        // 이미지 파일이 존재하고 읽을 수 있는지 확인
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("이미지 파일을 읽을 수 없습니다.");
        }

        return resource;
    }
}
