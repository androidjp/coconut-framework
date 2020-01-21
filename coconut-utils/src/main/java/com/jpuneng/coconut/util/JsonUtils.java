package com.jpuneng.coconut.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jpuneng.coconut.cmn.exception.JsonValidationException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.alibaba.fastjson.JSONPath.eval;

/**
 * @author Jasper Wu
 * @date 1/21/2020
 **/
public class JsonUtils {
    private JsonUtils() {
    }

    public static <T> String objectToJson(T obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return JSON.toJSONString(obj, SerializerFeature.IgnoreNonFieldGetter, SerializerFeature.WriteEnumUsingToString);
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        if (StringUtils.hasText(json)) {
            try {
                return JSONObject.parseObject(json, clazz);
            } catch (JSONException e) {
                return JSONObject.parseObject("\"" + json + "\"", clazz);
            }
        } else {
            return null;
        }
    }

    public static Object validateAndEval(JSONObject jsonObject, String evalPath) {
        Object result = eval(jsonObject, evalPath);
        if (result == null) {
            throw new JsonValidationException("schema format validate error", evalPath, jsonObject);
        }
        return result;
    }

    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        if (StringUtils.hasText(json)) {
            return JSON.parseArray(json, clazz);
        } else {
            return new ArrayList<>();
        }
    }

    public static String zipJson(String json) {
        return json.replaceAll("\\s*\\n\\s*", "");
    }

    public static String removeNullItemFromList(String json){
        return zipJson(json).replaceAll("(?<=\\[)\\s*null\\s*(?=\\])","")
                .replaceAll("\\s*,+\\s*null\\s*(?=,|\\])","")
                .replaceAll("(?<=\\[)\\s*null\\s*,\\s*","");

    }

    public static String splitJsonValue(String json, String key) {
        String splitKey = "\"" + key + "\"";
        return json.contains(splitKey) ? json.split(splitKey)[1].split("\"")[1] : null;
    }

    public static JSONObject parseJsonObjectBothJsonObjectAndJsonArray(String json) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(json)) {
            json = zipJson(json);
            try {
                return JSON.parseObject(json);
            } catch (Exception e) {
                JSONArray jsonArray = JSONArray.parseArray(json);
                return jsonArray.isEmpty() ? null : jsonArray.getJSONObject(0);
            }
        }

        return new JSONObject();
    }
}
