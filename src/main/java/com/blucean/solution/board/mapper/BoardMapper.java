package com.blucean.solution.board.mapper;

import com.blucean.solution.board.dto.AttchDTO;
import com.blucean.solution.board.dto.BoardDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
    List<BoardDTO> boardList(BoardDTO boardDTO);

    BoardDTO boardDetail(BoardDTO boardDTO);

    int boardWrite(BoardDTO boardDTO);

    void boardUpdate(BoardDTO boardDTO);

    void insertBoardFileList(AttchDTO attchDTO);

    List<AttchDTO> selectBoardFileList(int attachFileSeq);

    void fileDelete(AttchDTO attchDTO);

    void boardDelete(BoardDTO boardDTO);

//    List<BoardDTO> boardExcelList(int bbsSeq);

    void notiViewCountUpdate(int bbsSeq);

    List<BoardDTO> selectNoticeList(Map map);

    List<AttchDTO> selectFileListNotIn(BoardDTO boardDTO);

    AttchDTO fileDetail(AttchDTO attchDTO);
}