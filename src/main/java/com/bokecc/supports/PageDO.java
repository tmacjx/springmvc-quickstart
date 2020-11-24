package com.bokecc.supports;

import lombok.Data;

import java.util.List;

@Data
public class PageDO<T> {
    private long total;
    private int size;
    private int current;
    private int pages;
    private List<T> records;

    /**
     * 总页数
     * fixed github /issues/309
     */
    public long getPages() {
        if (this.size == 0) {
            return 0L;
        }
        long pages = this.total / this.size;
        if (this.total % this.size != 0) {
            pages++;
        }
        return pages;
    }

}
