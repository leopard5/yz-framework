package com.yz.framework.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ExcelExportData implements Serializable {

    /**
     * @Fields serialVersionUID : 序列化版本号
     */
    private static final long serialVersionUID = 3408989967079910779L;

    /**
     * 导出数据 key:String 表示每个Sheet的名称 value:List<?> 表示每个Sheet里的所有数据行
     */
    private LinkedHashMap<String, List<?>> dataMap;

    /**
     * 每个Sheet里的顶部大标题
     */
    private String[] titles;

    /**
     * 单个sheet里的数据列标题
     */
    private List<String[]> columnNames;

    /**
     * 单个sheet里每行数据的列对应的对象属性名称
     */
    private List<String[]> fieldNames;

    /**
     * 总计
     */
    private List<String[]> total;


    public List<String[]> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String[]> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public List<String[]> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String[]> columnNames) {
        this.columnNames = columnNames;
    }

    public LinkedHashMap<String, List<?>> getDataMap() {
        return dataMap;
    }

    public void setDataMap(LinkedHashMap<String, List<?>> dataMap) {
        this.dataMap = dataMap;
    }

    public List<String[]> getTotal() {
        return total;
    }

    public void setTotal(List<String[]> total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ExcelExportData[dataMap=" + dataMap + ", titles=" + Arrays.toString(titles) + ", columnNames="
                + columnNames + ", fieldNames=" + fieldNames + ", total=" + total + "]";
    }

}
