package com.wwsl.mdsj.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor()
@NoArgsConstructor
@Builder
public class DialogShowEvent {
    private String tag;
    private boolean show;
}
