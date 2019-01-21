package com.example.shiro.utils;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/4.
 */
public class PaginationEntity<T> implements Serializable {
    private int pageSize = 20;// 每页大小
    private int currentPage = 1;// 当前第几页
    private int start = 0;  // 起始位置

    public int getPageSize() {
        if (pageSize > 20 || pageSize <= 0) {
            pageSize = 20;
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getStart() {
        start = (currentPage - 1) * pageSize;
        if (start < 0) {
            start = 0;
        }
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
