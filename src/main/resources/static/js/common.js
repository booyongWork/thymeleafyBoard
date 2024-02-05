//공통 js 파일
(function(W, D) {
    W.$ = W.$ || {};
    W.$common = W.$common || {};

    $common.paging = function (list, callbackFn) {
        const pagingTargetId = "paging";
        let pagingTargetEl = $("#" + pagingTargetId);

        if (!Array.isArray(list)) {
            console.error("[$common.paging] Parameter is not Array!!");
            return false;
        }

        if (!callbackFn) {
            console.error("[$common.paging] callbackFn is not Exsist!!");
            return false;
        }

        if ($(pagingTargetEl).length === 0) {
            console.error(`[$common.paging] ID '${pagingTargetId}' does not exist among the paging elements.`);
            return false;
        }

        let row = list[0] || [];
        let _pageTot = 1;        // 전체 페이지 개수
        let _sPageNum = 1;       // 현재 시작 페이지 번호
        let _ePageNum = 10;      // 현재 마지막 페이지 번호
        let _nowPageRange = 10;  // 현재 선택된 페이지의 ePageNum
        let _pageIndex = parseInt($(pagingTargetEl).data("pageIndex")) || 1;
        let _recordTotal = row.recordTotal ? row.recordTotal : 0;
        let _pageSize = row.pageSize ? row.pageSize : 10;

        // 전체 페이지 개수
        _pageTot = Math.ceil(_recordTotal / _pageSize);
        _nowPageRange = Math.ceil(_pageIndex / _pageSize) * _pageSize;
        _sPageNum = _nowPageRange - (_pageSize-1);
        if (_nowPageRange > _pageTot) {
            _ePageNum = _pageTot;
        } else {
            _ePageNum = _nowPageRange;
        }
        let paging = $("<ul class='pagination justify-content-center'>");
        paging.append($("<li class='page-item'>").append($("<a>", {class: "page-link first", href: "javascript:void(0);",text :"First"})));
        paging.find("a.page-link.first").attr("onclick", `$('#${pagingTargetId}').data('pageIndex', ${1}); ${callbackFn}();`);
        paging.find("aa.page-link.first").attr("style", "cursor:pointer;");

        paging.append($("<li class='page-item'>").append($("<a>", {class: "page-link perv", href: "javascript:void(0);",text :"Perv"})));
        if (_sPageNum > _pageSize) { //이전 버튼 시작페이지- 페이지 사이즈
            paging.find("a.page-link.perv").attr("onclick", `$('#${pagingTargetId}').data('pageIndex', ${_sPageNum - _pageSize}); ${callbackFn}();`);
            paging.find("a.page-link.perv").attr("style", "cursor:pointer;");
        }
        for (let i = _sPageNum; i < _ePageNum + 1; i++) {
            paging.append($("<li  class='page-item'>").append($("<a>", {class: "page-link", href: "javascript:void(0);"})));

            if (_pageIndex == i) {
                paging.find("li:last-child>a").attr("style", "background-color: #007bff; color: white;");
            } else {
                paging.find("li:last-child>a").attr("style", "cursor:pointer;");
            }

            paging.find("li:last-child>a").attr("onclick", `$('#${pagingTargetId}').data('pageIndex', ${i}); ${callbackFn}();`);
            paging.find("li:last-child>a").text(i);
        }
        paging.append($("<li  class='page-item'>").append($("<a>", {class: "page-link next", href: "javascript:void(0);",text :"Next"})));
        if (_ePageNum < _pageTot) { //다음 버튼 시작페이지 + 페이지 사이즈
            paging.find("a.page-link.next").attr("onclick", `$('#${pagingTargetId}').data('pageIndex', ${_sPageNum + _pageSize}); ${callbackFn}();`);
            paging.find("a.page-link.next").attr("style", "cursor:pointer;");
        }
        paging.append($("<li  class='page-item'>").append($("<a>", {class: "page-link last", href: "javascript:void(0);",text :"Last"})));
        paging.find("a.page-link.last").attr("onclick", `$('#${pagingTargetId}').data('pageIndex', ${_pageTot}); ${callbackFn}();`);
        paging.find("a.page-link.last").attr("style", "cursor:pointer;");

        console.log(paging);
        $(pagingTargetEl).html(paging);
    }

    /**
     *
     * @param {string} formId
     * @returns {JSON}
     */
    $common.serializeObject = function(formId) { //Form Data Object로 직렬화
        let jqObj = $("#"+formId);
        let obj = null;
        try {
            if (jqObj[0].tagName && jqObj[0].tagName.toUpperCase() === "FORM") {
                let arr = jqObj.serializeArray();
                if (arr) {
                    obj = {};
                    $.each(arr, function() {
                        obj[this.name] = this.value;
                    });
                }
            }
        } catch (e) {
            console.error(e.message);
        }

        return obj;
    };
})(window, document);


