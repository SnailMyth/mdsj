package com.wwsl.mdsj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author sushi
 * @description
 * @date 2020/7/22.
 */
public class PartnerCityBean implements Serializable {

    /**
     * id : 1
     * name : 北京市
     * parent_id : 3263
     * level : 1
     * children : [{"id":"2","name":"北京城区","parent_id":"1","level":"2","children":[{"id":"3","name":"东城区","parent_id":"2","level":"3"},{"id":"4","name":"西城区","parent_id":"2","level":"3"},{"id":"5","name":"朝阳区","parent_id":"2","level":"3"},{"id":"6","name":"丰台区","parent_id":"2","level":"3"},{"id":"7","name":"石景山区","parent_id":"2","level":"3"},{"id":"8","name":"海淀区","parent_id":"2","level":"3"},{"id":"9","name":"门头沟区","parent_id":"2","level":"3"},{"id":"10","name":"房山区","parent_id":"2","level":"3"},{"id":"11","name":"通州区","parent_id":"2","level":"3"},{"id":"12","name":"顺义区","parent_id":"2","level":"3"},{"id":"13","name":"昌平区","parent_id":"2","level":"3"},{"id":"14","name":"大兴区","parent_id":"2","level":"3"},{"id":"15","name":"怀柔区","parent_id":"2","level":"3"},{"id":"16","name":"平谷区","parent_id":"2","level":"3"},{"id":"17","name":"密云区","parent_id":"2","level":"3"},{"id":"18","name":"延庆区","parent_id":"2","level":"3"}]}]
     */

    private String id;
    private String name;
    private String parent_id;
    private String level;
    private List<ChildrenBeanX> children;

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

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<ChildrenBeanX> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBeanX> children) {
        this.children = children;
    }

    public static class ChildrenBeanX {
        /**
         * id : 2
         * name : 北京城区
         * parent_id : 1
         * level : 2
         * children : [{"id":"3","name":"东城区","parent_id":"2","level":"3"},{"id":"4","name":"西城区","parent_id":"2","level":"3"},{"id":"5","name":"朝阳区","parent_id":"2","level":"3"},{"id":"6","name":"丰台区","parent_id":"2","level":"3"},{"id":"7","name":"石景山区","parent_id":"2","level":"3"},{"id":"8","name":"海淀区","parent_id":"2","level":"3"},{"id":"9","name":"门头沟区","parent_id":"2","level":"3"},{"id":"10","name":"房山区","parent_id":"2","level":"3"},{"id":"11","name":"通州区","parent_id":"2","level":"3"},{"id":"12","name":"顺义区","parent_id":"2","level":"3"},{"id":"13","name":"昌平区","parent_id":"2","level":"3"},{"id":"14","name":"大兴区","parent_id":"2","level":"3"},{"id":"15","name":"怀柔区","parent_id":"2","level":"3"},{"id":"16","name":"平谷区","parent_id":"2","level":"3"},{"id":"17","name":"密云区","parent_id":"2","level":"3"},{"id":"18","name":"延庆区","parent_id":"2","level":"3"}]
         */

        private String id;
        private String name;
        private String parent_id;
        private String level;
        private List<ChildrenBean> children;

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

        public String getParent_id() {
            return parent_id;
        }

        public void setParent_id(String parent_id) {
            this.parent_id = parent_id;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public List<ChildrenBean> getChildren() {
            return children;
        }

        public void setChildren(List<ChildrenBean> children) {
            this.children = children;
        }

        public static class ChildrenBean {
            /**
             * id : 3
             * name : 东城区
             * parent_id : 2
             * level : 3
             */

            private String id;
            private String name;
            private String parent_id;
            private String level;

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

            public String getParent_id() {
                return parent_id;
            }

            public void setParent_id(String parent_id) {
                this.parent_id = parent_id;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }
        }
    }
}
