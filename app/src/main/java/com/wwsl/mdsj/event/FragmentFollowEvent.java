package com.wwsl.mdsj.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cxf on 2018/9/28.
 */
@Data
@AllArgsConstructor()
@NoArgsConstructor
@Builder
public class FragmentFollowEvent {

    private int triggerType;
    private String mToUid;
    private int mIsAttention;
}
