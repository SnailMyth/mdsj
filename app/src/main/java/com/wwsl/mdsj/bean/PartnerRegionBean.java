package com.wwsl.mdsj.bean;

/**
 * @author sushi
 * @description
 * @date 2020/7/22.
 */

/**
 * 大区合伙人
 */
public class PartnerRegionBean {


    /**
     * id : 3260
     * name : 华东地区
     * parent_id : -1
     * level : -1
     */

    private String id;
    private String name;
    private String parent_id;
    private String level;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
