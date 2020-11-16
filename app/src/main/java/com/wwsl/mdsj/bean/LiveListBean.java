package com.wwsl.mdsj.bean;

import androidx.annotation.Nullable;

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
public class LiveListBean {
    private String uid;
    private LiveBean liveBean;
    private boolean isPlay;
    private boolean needPlay;


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof LiveListBean)) {
            return false;
        }

        LiveListBean o = (LiveListBean) obj;

        return o.getUid().equals(this.uid);
    }
}
