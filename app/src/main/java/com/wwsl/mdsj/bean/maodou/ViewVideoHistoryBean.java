package com.wwsl.mdsj.bean.maodou;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class ViewVideoHistoryBean {

    private String id;
    private String title;
    private String percent;
    private String time;

}
