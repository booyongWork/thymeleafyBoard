package com.blucean.solution.board.mapper;

import com.blucean.solution.board.dto.AttchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileApiMapper {
    List<AttchDTO> selectAllData();
    void insertBoardFileList(AttchDTO attchDTO);
}
