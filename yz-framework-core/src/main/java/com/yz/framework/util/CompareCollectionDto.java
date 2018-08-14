package com.yz.framework.util;

import java.util.Collection;

/**
 * 集合类型的属性的比较包装类
 *
 * @param <T>
 * @author yazhong.qi
 */

public class CompareCollectionDto<T> {
    private static final long serialVersionUID = -4977550220859897197L;
    private String filedName;
    private Collection<T> oldCollection;
    private Collection<T> newCollection;

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public void setOldCollection(Collection<T> oldCollection) {
        this.oldCollection = oldCollection;
    }

    public void setNewCollection(Collection<T> newCollection) {
        this.newCollection = newCollection;
    }

    public static class CompareCollectionDtoBuilder<T> {
        private String filedName;
        private Collection<T> oldCollection;
        private Collection<T> newCollection;

        @Override
        public String toString() {
            return "CompareCollectionDto.CompareCollectionDtoBuilder(filedName=" + this.filedName + ", oldCollection=" + this.oldCollection + ", newCollection=" + this.newCollection + ")";
        }

        public CompareCollectionDto<T> build() {
            return new CompareCollectionDto(this.filedName, this.oldCollection, this.newCollection);
        }

        public CompareCollectionDtoBuilder<T> newCollection(Collection<T> newCollection) {
            this.newCollection = newCollection;
            return this;
        }

        public CompareCollectionDtoBuilder<T> oldCollection(Collection<T> oldCollection) {
            this.oldCollection = oldCollection;
            return this;
        }

        public CompareCollectionDtoBuilder<T> filedName(String filedName) {
            this.filedName = filedName;
            return this;
        }
    }

    public static <T> CompareCollectionDtoBuilder<T> builder() {
        return new CompareCollectionDtoBuilder();
    }

    public CompareCollectionDto(String filedName, Collection<T> oldCollection, Collection<T> newCollection) {
        this.filedName = filedName;
        this.oldCollection = oldCollection;
        this.newCollection = newCollection;
    }

    public String getFiledName() {
        return this.filedName;
    }

    public Collection<T> getOldCollection() {
        return this.oldCollection;
    }

    public Collection<T> getNewCollection() {
        return this.newCollection;
    }

    public CompareCollectionDto() {
    }
}
