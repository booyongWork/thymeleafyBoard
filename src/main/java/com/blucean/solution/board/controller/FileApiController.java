package com.blucean.solution.board.controller;

import com.blucean.solution.board.dto.AttchDTO;
import com.blucean.solution.board.mapper.BoardMapper;
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
    private BoardMapper boardMapper;

    @GetMapping("/page")
    public String list(Model model) {
        List<AttchDTO> dataList = boardMapper.selectAllData();
        model.addAttribute("dataList", dataList);
        return "fileApiTest";}
}
