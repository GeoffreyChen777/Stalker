package com.sorry.api;

public class ApiResponse<T> {
    private String event;    // 返回码，0为成功
    private String msg;      // 返回信息
    private T obj;           // 单个对象
    private T objList;       // 数组对象

    public ApiResponse(String event, String msg) {
        this.event = event;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return event.equals("0");
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public T getObjList() {
        return objList;
    }

    public void setObjList(T objList) {
        this.objList = objList;
    }
}
