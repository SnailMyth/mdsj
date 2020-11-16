package com.wwsl.mdsj.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor( )
@NoArgsConstructor
@ToString
public class ImageBean {
    String id;
    String url;
    String name;
}
