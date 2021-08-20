package com.liangzhicheng.common.page;

import lombok.Data;

import java.util.List;

/**
 * @description 分页相关封装
 * @author liangzhicheng
 * @since 2021-08-11
 */
@Data
public class PageResult<T> {
    /**
     * 当前页码
     */
    private int pageNo;

    /**
     * 页面大小(每页数量)
     */
    private int pageSize;

    /**
     * 总数量
     */
    private int total;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 结果输出列表
     */
    private List<T> records;

    /**
     * 上一页
     */
    private int prevPage;

    /**
     * 下一页
     */
    private int nextPage;


    /**
     * @description 分页处理
     * @param pageNo
     * @param pageSize
     * @param records
     * @param total
     */
    public PageResult(int pageNo, int pageSize, List<T> records, int total) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.records = records;
        this.total = total;
        if(total <= pageSize){
            this.pages = 1;
            this.prevPage = 1;
            this.nextPage = 1;
            return;
        }
        this.pages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        this.prevPage = this.pageNo - 1 >= 1 ? this.pageNo - 1 : 1;
        this.nextPage = this.pageNo + 1  <= this.pages ? this.pageNo + 1 : this.pages;
    }

}
