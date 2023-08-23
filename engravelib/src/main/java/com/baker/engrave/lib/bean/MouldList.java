package com.baker.engrave.lib.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Create by hsj55
 * 2020/3/10
 */
public class MouldList implements Serializable {
    private List<Mould> list;

    public List<Mould> getList() {
        return list;
    }

    public void setList(List<Mould> list) {
        this.list = list;
    }
}
