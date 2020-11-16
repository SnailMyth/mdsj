package com.wwsl.mdsj.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor()
@NoArgsConstructor()
@Builder
public class TagAddBean implements Serializable {
    private String id;
    private String name;
    private boolean check;
}
