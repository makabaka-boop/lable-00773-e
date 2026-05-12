package com.finance.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {

    private List<T> list;
    private long total;
    private int page;
    private int size;
    private long totalPages;

    public PageResult() {
    }

    public PageResult(List<T> list, long total, int page, int size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (total + size - 1) / size;
    }

    public static <T> PageResult<T> of(List<T> list, long total, int page, int size) {
        return new PageResult<>(list, total, page, size);
    }

    public static int calculateOffset(int page, int size) {
        return (page - 1) * size;
    }
}
