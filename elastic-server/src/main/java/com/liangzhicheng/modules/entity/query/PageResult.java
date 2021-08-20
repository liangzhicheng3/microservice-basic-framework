package com.liangzhicheng.modules.entity.query;

import lombok.Data;

import java.util.List;

/**
 * @description 分页相关封装
 * @author liangzhicheng
 * @since 2021-08-19
 */
@Data
public class PageResult<T> {

    /**
     * 当前页码
     */
    private Integer pageNo;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Integer total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 结果集
     */
    private List<T> records;

    /**
     * @description 分页结果集处理
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
            return;
        }
        this.pages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
    }

}
