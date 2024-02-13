package com.blucean.solution.board.dto;

import com.blucean.solution.model.base.BaseCommonEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

//NOTE. public, private, protected, default 등의 접근 제어자는 클래스 멤버(변수, 메서드, 생성자)의 접근 범위를 지정한다.
// 이 중에서도 public은 가장 넓은 범위로, 해당 멤버가 어디서든 접근 가능하도록 허용한다. 반면에 private은 가장 제한적인 범위로, 해당 멤버가 선언된 클래스 내부에서만 접근 가능하도록 제한한다.
// BoardDTO 클래스에서 private 접근 제어자를 사용하는 이유는 해당 클래스의 멤버 변수들이 외부에서 직접적으로 접근되어서는 안 되기 때문. 이 클래스는 데이터 전송 객체(Data Transfer Object, DTO)로 사용되며,
// 일반적으로 데이터를 담고 전달하는 용도로 사용됨.
// 이러한 경우 외부에서 직접적으로 접근하는 것보다는 해당 데이터를 채우고 읽는 등의 작업은 클래스 내부에서 이루어져야 하기 때문에 private 접근 제어자를 사용하여 캡슐화(encapsulation)를 유지하는 것이 일반적이다.
// 또한, 멤버 변수들을 public으로 선언할 경우 외부에서 마음대로 해당 변수를 변경할 수 있으며, 이는 객체의 상태를 불안정하게 만들고 예상치 못한 부작용을 초래할 수 있다.
// 그렇기 때문에 객체의 무결성과 안정성을 보장하기 위해 멤버 변수들을 private으로 선언하고, 필요한 경우에는 메서드를 통해 간접적으로 접근하도록 하는 것이 바람직하다.
@Getter
@Setter
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
