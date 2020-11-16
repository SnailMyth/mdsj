package com.wwsl.mdsj.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OutActivityBean implements Serializable {

    /**
     * post_title : 直播标签一
     * post_content : <p>你好和好不错好账号和或或或或或或或做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做做</p>
     * smeta : {"template":"page","thumb":"20200504\/5eaf79e0c50c6.png"}
     * icon : http://s3-cn-south-1.qiniucs.com/20200504/5eaf79e0c50c6.png
     */

    @SerializedName("post_title")
    private String postTitle;
    @SerializedName("post_content")
    private String postContent;
    @SerializedName("smeta")
    private String meta;
    private String icon;

    @SerializedName("post_excerpt")
    private String postExcerpt;

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPostExcerpt() {
        return postExcerpt;
    }

    public void setPostExcerpt(String postExcerpt) {
        this.postExcerpt = postExcerpt;
    }
}
