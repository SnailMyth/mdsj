package com.wwsl.mdsj.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cxf on 2018/12/15.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoControlEvent {
    private int pageIndex;
    private boolean play;
}
