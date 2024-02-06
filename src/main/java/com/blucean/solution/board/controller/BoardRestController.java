package com.blucean.solution.board.controller;

import com.blucean.solution.board.dto.BoardVO;
import com.blucean.solution.board.dto.ResponseVO;
import com.blucean.solution.common.CommonConst;
import com.blucean.solution.board.dto.BoardDTO;
import com.blucean.solution.board.service.BoardService;
import com.blucean.solution.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/board")
class BoardRestController {
    @Autowired
    private BoardService boardService;

    /**
     * 게시판 목록 조회
     *
     */
    @PostMapping("/listAjax")
    public ResponseVO<List<BoardVO>> listAjax(@ModelAttribute BoardDTO boardDTO) {
        List<BoardVO> boardVOS;

        try {
            boardVOS = boardService.boardList(boardDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseVO.ok(boardVOS, CommonConst.SUCCESS_CODE, "검색 완료");
    }

    /**
     * 게시판 등록|수정
     *
     */
    @PostMapping("/updateFormAjax")
    public ResponseVO<String> updateForm(@ModelAttribute BoardDTO boardDTO, @RequestParam(value = "boardFile", required = false)  List<MultipartFile> multiFileList) throws Exception {

        boardService.boardWrite(boardDTO, multiFileList);

        if("N".equals(boardDTO.getModifyYn())){
            return ResponseVO.ok(null, CommonConst.SUCCESS_CODE, "저장되었습니다."  );
        } else {
            return ResponseVO.ok(null, CommonConst.SUCCESS_CODE, "수정되었습니다."  );
        }

    }

    /**
     * 게시판 삭제
     *
     */
    @PostMapping("/deleteFormAjax")
    public ResponseVO<String> deleteBoard(@RequestParam int bbsSeq) throws CustomException {
        boardService.boardDelete(bbsSeq);
        return ResponseVO.ok(null, CommonConst.SUCCESS_CODE, "삭제되었습니다."  );
    }
}