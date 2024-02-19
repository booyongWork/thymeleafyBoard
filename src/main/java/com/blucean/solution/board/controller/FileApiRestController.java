package com.blucean.solution.board.controller;

import com.blucean.solution.board.dto.ResponseVO;
import com.blucean.solution.board.service.BoardService;
import com.blucean.solution.board.service.FileApiService;
import com.blucean.solution.common.CommonConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/fileApi")
class FileApiRestController {
    @Autowired
    private FileApiService fileApiService;

    @PostMapping("/upload")
    public ResponseVO apiFileUpload() {
        fileApiService.apiFileInsert();
        return ResponseVO.ok(null, CommonConst.SUCCESS_CODE, "저장되었습니다.");
    }
}