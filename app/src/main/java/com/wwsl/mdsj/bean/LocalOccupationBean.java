package com.wwsl.mdsj.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalOccupationBean {


    @JSONField(name = "level0")
    private List<Level0Bean> firstData;
    @JSONField(name = "level1")
    private List<Level1Bean> secData;

    public List<Level0Bean> getFirstData() {
        return firstData;
    }

    public void setFirstData(List<Level0Bean> firstData) {
        this.firstData = firstData;
    }

    public List<Level1Bean> getSecData() {
        return secData;
    }

    public void setSecData(List<Level1Bean> secData) {
        this.secData = secData;
    }

    public static class Level0Bean {
        public Level0Bean() {
        }

        /**
         * id : 0
         * name : 销售
         */


        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Level0Bean(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class Level1Bean {
        /**
         * title : 销售代表
         * pid : 0
         */

        private String title;
        private String pid;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public Level1Bean() {
        }
    }

    public Map<String, List<String>> parseData() {
        Map<String, List<String>> map = new HashMap<>();

        for (int i = 0; i < firstData.size(); i++) {
            List<String> temp = new ArrayList<>();
            for (int j = 0; j < secData.size(); j++) {
                if (secData.get(j).getPid().equals(firstData.get(i).getId())) {
                    temp.add(secData.get(j).getTitle());
                }
            }
            map.put(firstData.get(i).getName(), temp);
        }

        return map;
    }

    public LocalOccupationBean() {
    }
}
