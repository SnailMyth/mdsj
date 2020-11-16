package com.wwsl.mdsj.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor( )
@NoArgsConstructor
@Builder
public class HomeActionBean {
    String icon;
    String name;
    String action;
}
