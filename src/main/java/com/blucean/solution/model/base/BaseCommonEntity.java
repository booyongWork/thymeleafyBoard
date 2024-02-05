package com.blucean.solution.model.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseCommonEntity {

    private String modifyYn = "N"; // default= N
    private String searchText;
    private int rowNum;
    private int recordTotal;
    //노츨 row 수
    private int pageSize = 10;
    //현재 페이지 번호
    private Integer pageIndex = 1; // default= 1
    //시작 row
    private int startRow;
    // 끝 row
    private int endRow;


    //page size 설정
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize == 0 ? 10 : pageSize; //기본 10
        setPageIndex(this.pageIndex);
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageSize = getPageSize() == 0 ? 10: getPageSize(); //deFault = 10
        this.pageIndex = pageIndex == null || pageIndex == 0 ? 10 : pageIndex; // default= 1
        this.startRow = (this.pageSize * (this.pageIndex - 1)) + 1;
        this.endRow = (this.startRow - 1) + this.pageSize;
    }
}
