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

    /* 게시판 ID (notice:공지사항, request:요청사항게시판, pds:자료실) */
    private String bbsId;

    /* 게시글 그룹 번호 */
    private int bbsGrpSeq;

    /* 부모 게시글 번호 */
    private int upperBbsSeq;

    /* 게시판 그룹 일련번호(공지사항:1, 요청사항게시판:2, 자료실:3) */
    private int bbsGrpIdx;

    /* 그룹 깊이 */
    private int bbsDepth;

    /* 카테고리 코드1 */
    private String categoryCd1;

    /* 카테고리 코드2 */
    private String categoryCd2;

    /* 제목 */
    private String title;

    /* 내용 */
    private String contn;

    /* 작성자 ID */
    private String wrtrId;

    /* 작성자 이름 */
    private String wrtrNm;

    /* 작성자IP */
    private String wrtrIp;

    /* 작성일자 */
    private String wrtrDd;

    /* 작성시간 */
    private String wrtrTm;

    /* 조회수 */
    private int inqCnt;

    /* 추천수 */
    private int rcmdCnt;

    /* 메인 공지 여부 */
    private char mainNotiYn;

    /* 공개일자 */
    private String openDd;

    /* 사용자 정의필드1 */
    private String usrDefine1;

    /* 사용자 정의필드2 */
    private String usrDefine2;

    /* 사용자 정의필드3 */
    private String usrDefine3;

    /* 사용자 정의필드4 */
    private String usrDefine4;

    /* 최종 수정자 ID */
    private String lstUpdUsrId;

    /* 최종 수정자명 */
    private String lstUpdUsrNm;

    /* 최종 수정일시 */
    private String lstUpdDdtm;

    /* 공개여부 */
    private String opnYn;

    /* 사용 여부 */
    private String useYn;

    /* 첨부 파일명 */
    private String attachFileNm;

    private String authCode;

    private String userId;

    private String userNm;

    private String imgYn;
}
