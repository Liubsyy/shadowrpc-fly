package com.liubs.shadowrpcfly.protocol;

import com.liubs.shadowrpcfly.annotation.ShadowEntity;
import com.liubs.shadowrpcfly.annotation.ShadowField;
import com.liubs.shadowrpcfly.constant.ResultCode;

/**
 * @author Liubsyy
 * @date 2024/8/17
 **/
@ShadowEntity
public class Result {

    @ShadowField(1)
    private int code = ResultCode.SUCCESS;
    @ShadowField(2)
    private String message;

    @ShadowField(3)
    private Object result;

    public static Result success(Object result){
        Result r = new Result();
        r.setCode(ResultCode.SUCCESS);
        r.setResult(result);
        return r;
    }

    public static Result fail(String message){
       return fail(ResultCode.FAIL,message);
    }
    public static Result fail(int code,String message){
        Result r = new Result();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public boolean isSuccess(){
        return code == ResultCode.SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
