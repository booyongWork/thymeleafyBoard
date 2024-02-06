package com.blucean.solution.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AttchVO {

    /*기존 업로드 된 파일 seq*/
    private List<Integer> attachFileSeqList;

    /*게시글 순번*/
    private int bbsSeq;

    /*게시글 ID*/
    private String bbsId;

    /*첨부파일 순번*/
    private int attachFileSeq;

    /*첨부파일명*/
    private String attachFileNm;

    /*물리 저장 파일 명*/
    private String saveFileNm;

    /*파일경로*/
    private String filePath;

    /*사용여부*/
    private String useYn;
    private String type;
}
