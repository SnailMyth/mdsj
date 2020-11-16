package com.wwsl.mdsj.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor()
@Builder
@ToString
public class RewardShowBean {
    private String id;
    private String type;
    private String name;
    private String issueNum;
    private String numbers;
    private boolean showArrows;
}
