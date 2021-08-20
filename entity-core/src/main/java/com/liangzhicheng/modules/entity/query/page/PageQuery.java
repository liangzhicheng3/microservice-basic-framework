package com.liangzhicheng.modules.entity.query.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangzhicheng.common.utils.SysToolUtil;
import com.liangzhicheng.modules.entity.dto.basic.BaseDTO;

/**
 * @description 分页查询类
 * @author liangzhicheng
 * @since 2021-08-10
 */
public class PageQuery {

    public static Page queryDispose(BaseDTO baseDTO) {
        return new Page(SysToolUtil.getPageNo(baseDTO.getPageNo()),
                SysToolUtil.getPageSize(baseDTO.getPageSize()));
    }

}
