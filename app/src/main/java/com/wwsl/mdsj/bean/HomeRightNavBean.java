package com.wwsl.mdsj.bean;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class HomeRightNavBean {
    private String id;
    @JSONField(name = "post_title")
    private String title;
    private String smeta;
    @JSONField(name = "post_keywords")
    private String url;
}
