package io.github.androidjp.coconut.cmn.exception;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;

/**
 * @author Jasper Wu
 * @date 1/21/2020
 **/
public class JsonValidationException extends RuntimeException {
    private String evalPath;
    private JSONObject jsonObject;

    public JsonValidationException(String message, String evalPath, JSONObject jsonObject) {
        super(message);
        this.evalPath = evalPath;
        this.jsonObject = jsonObject;
    }

    @Override
    public String toString() {
        return "JSONValidateException{" +
                "evalPath='" + evalPath + '\'' +
                ", jsonObject=" + jsonObject +
                ", message='" + super.getMessage() + '\'' +
                '}';
    }
}
