package com.wwsl.mdsj.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :
 * @date : 2020/6/17 16:49
 * @description : xxxxx
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonHomePageChangeEvent {
    private String uid;
}
