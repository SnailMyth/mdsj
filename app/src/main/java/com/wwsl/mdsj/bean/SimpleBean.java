package com.wwsl.mdsj.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleBean {
    private String id;
    private String content;
}
