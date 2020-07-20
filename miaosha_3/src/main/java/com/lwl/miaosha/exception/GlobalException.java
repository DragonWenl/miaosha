package com.lwl.miaosha.exception;

import com.lwl.miaosha.result.CodeMsg;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-18
 */
/*
 * 全局异常处理
 */

public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
