package com.blucean.solution.board.dto;

import com.blucean.solution.model.base.BaseCommonEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BoardDTO extends BaseCommonEntity {

    /*업로드 된 파일 정보*/
    private List<AttchDTO> uploadBoardFile;

    /*기존 업로드 된 파일 seq*/
    private List<Integer> attachFileSeqList;

    private String fileUrl;

    /*게시글 번호*/
    private int bbsSeq;

    /* 제목 */
    private String title;

    /* 내용 */
    private String contn;

    /* 작성자 ID */
    private String wrtrId;

    /* 작성자 이름 */
    private String wrtrNm;

    /* 작성일자 */
    private String wrtrDd;

    /* 작성시간 */
    private String wrtrTm;

    /* 조회수 */
    private int inqCnt;

    /* 최종 수정자 ID */
    private String lstUpdUsrId;

    /* 최종 수정자명 */
    private String lstUpdUsrNm;

    /* 최종 수정일시 */
    private String lstUpdDdtm;

    /* 사용 여부 */
    private String useYn;

    /* 첨부 파일명 */
    private String attachFileNm;

    private String imgYn;
    private String type;
}
