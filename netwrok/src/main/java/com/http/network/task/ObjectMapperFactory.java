package com.http.network.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangyx
 * Date 2017/9/29
 * email tangyx@live.com
 */

public class ObjectMapperFactory {
    private static ObjectMapperFactory INSTANCE;
    private ObjectMapper mMapper;

    private ObjectMapperFactory() {
        mMapper = new ObjectMapper();
        //解析的时候，如果没有对应的字段，直接略过。
        mMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        //换转的成json的时候如果值是null直接忽略
        mMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    public static ObjectMapperFactory getObjectMapper(){
        return INSTANCE==null?INSTANCE=new ObjectMapperFactory():INSTANCE;
    }
    /**
     * json转对象
     *
     * @param value
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T json2Model(String value, Class<T> obj) throws IOException {
        return mMapper.readValue(value, obj);
    }
    /**
     * 对象转json
     */
    public String model2JsonStr(Object model) {
        try {
            return mMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数据转list集合
     * @param value
     * @param obj
     * @return
     */
    public<T> List<T> json2List(String value, Class<T> obj) {
        List<T> list = null;
        JavaType javaType = mMapper.getTypeFactory().constructParametricType(ArrayList.class, obj);
        try {
            list = mMapper.readValue(value, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
