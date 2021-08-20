package com.liangzhicheng.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 自定义异常
 * @author liangzhicheng
 * @since 2021-07-30
 */
@Data
@NoArgsConstructor
public class CustomizeException extends IllegalArgumentException {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 具体信息
     */
    private String message;

    public CustomizeException(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

}
