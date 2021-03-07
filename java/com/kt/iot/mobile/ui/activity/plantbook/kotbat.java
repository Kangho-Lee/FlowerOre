package com.kt.iot.mobile.ui.activity.plantbook;

/**
 * Created by 강호 리 on 2017-03-01.
 */

public class kotbat {

    private String id;
    private String name;
    private String engname;
    private String desc;
    private String type;
    private String goodliving;
    private String othername;
    private String flower;
    private String harvest;
    private String use;
    private String deco;
    private String url;

    public kotbat(String id, String name, String engname, String desc, String type, String goodliving, String othername, String flower, String harvest, String use, String deco, String url){

        this.setId(id);
        this.setName(name);
        this.setEngname(engname);
        this.setDesc(desc);
        this.setType(type);
        this.setGoodliving(goodliving);
        this.setOthername(othername);
        this.setFlower(flower);
        this.setHarvest(harvest);
        this.setUse(use);
        this.setDeco(deco);
        this.setUrl(url);
    }

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

    public String getEngname() {
        return engname;
    }

    public void setEngname(String engname) {
        this.engname = engname;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGoodliving() {
        return goodliving;
    }

    public void setGoodliving(String goodliving) {
        this.goodliving = goodliving;
    }

    public String getOthername() {
        return othername;
    }

    public void setOthername(String othername) {
        this.othername = othername;
    }

    public String getFlower() {
        return flower;
    }

    public void setFlower(String flower) {
        this.flower = flower;
    }

    public String getHarvest() {
        return harvest;
    }

    public void setHarvest(String harvest) {
        this.harvest = harvest;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getDeco() {
        return deco;
    }

    public void setDeco(String deco) {
        this.deco = deco;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
