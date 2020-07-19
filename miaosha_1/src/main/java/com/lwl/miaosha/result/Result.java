package com.lwl.miaosha.result;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-16
 */
public class Result<T> {
    private int code;
    private T data;
    private String msg;

    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg cm) {
        if (cm == null)
            return;
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }

    //成功时候的调用
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    //失败时候的调用
    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
