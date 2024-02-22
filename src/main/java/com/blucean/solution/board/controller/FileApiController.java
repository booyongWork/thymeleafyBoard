package com.blucean.solution.board.controller;

import com.blucean.solution.board.dto.AttchDTO;
import com.blucean.solution.board.mapper.BoardMapper;
import com.blucean.solution.board.mapper.FileApiMapper;
import com.blucean.solution.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/fileApi")
public class FileApiController {
    @Autowired
    private FileApiMapper fileApiMapper;

    @GetMapping("/page")
    public String list(Model model) {
        List<AttchDTO> dataList = fileApiMapper.selectAllData();
        List<AttchDTO> otherdataList = fileApiMapper.otherSelectAllData();
        model.addAttribute("dataList", dataList);
        model.addAttribute("otherdataList", otherdataList);
        return "fileApiTest";}
}
