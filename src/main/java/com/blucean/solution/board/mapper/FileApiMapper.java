package com.blucean.solution.board.mapper;

import com.blucean.solution.board.dto.AttchDTO;
import com.blucean.solution.board.dto.BoardDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileApiMapper {
    List<AttchDTO> selectAllData();
    List<AttchDTO> otherSelectAllData();
    void insertOtherAttach(AttchDTO attchDTO);
    int countAttachFileNm(String attachFileNm);
    int countOtherAttachFileNm(String attachFileNm);
    void updateOtherAttach(AttchDTO attchDTO);
    void apiFileDelete(String attachFileNm);
}
