package com.wwsl.mdsj.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor()
@NoArgsConstructor
@Builder
public class VideoPlayEvent {
    private String videoId;
    private int state;
}
