package com.yz.framework.doc;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public abstract class ExcelUtil {

    /**
     * 导出列表数据到指定位置
     *
     * @param title        要到处的Excel标题
     * @param headerMap    要导出的列标题
     * @param dataList     要导出的数据
     * @param clazz        导出的数据对应的class
     * @param saveFilePath 保存文件位置
     * @throws Exception
     */
    public static <E> void export(String title, Map<String, String> headerMap, List<E> dataList, Class<E> clazz, String saveFilePath) throws Exception {
        FileOutputStream fileOut = null;
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            int rowIndx = 0;
            List<Field> fiels = getFieldsForExport(clazz, headerMap);
            HSSFSheet sheet = null;
            if (StringUtils.hasText(title)) {
                sheet = workbook.createSheet(title);
            } else {
                sheet = workbook.createSheet();
            }
            HSSFRow row = null;
            rowIndx = getRowIndx(title, workbook, rowIndx, sheet, row, false, null, fiels);
            row = sheet.createRow(rowIndx++);
            HSSFCellStyle rowHeaderStyle = workbook.createCellStyle();
            rowHeaderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            rowHeaderStyle.setFillBackgroundColor(new HSSFColor.GREY_80_PERCENT().getIndex());
            rowHeaderStyle.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
            setBorder(rowHeaderStyle);
            for (int i = 0; i < fiels.size(); i++) {
                Field filed = fiels.get(i);
                HSSFCell cell = row.createCell(i, HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(headerMap.get(filed.getName()));
                cell.setCellStyle(rowHeaderStyle);
            }
            for (E e : dataList) {
                row = sheet.createRow(rowIndx++);
                for (int i = 0; i < fiels.size(); i++) {
                    Field filed = fiels.get(i);
                    filed.setAccessible(true);
                    HSSFCell cell = row.createCell(i, HSSFCell.CELL_TYPE_STRING);
                    Object object = filed.get(e);
                    if (object != null) {
                        cell.setCellValue(object.toString());
                    }
                    cell.setCellStyle(rowHeaderStyle);
                }
            }
            for (int i = 0; i < fiels.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            // 新建一输出文件流
            fileOut = new FileOutputStream(saveFilePath);
            // 把相应的Excel 工作簿存盘
            workbook.write(fileOut);
            fileOut.flush();
            // 操作结束，关闭文件
            fileOut.close();
            workbook.close();
            workbook = null;

        } catch (Exception e) {
            throw e;
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
            if (workbook != null) {
                workbook.close();
                workbook = null;
            }
        }
    }

    /**
     * 导出excel
     *
     * @param title        导出的excel的标题，可以为空
     * @param dataList     要到处的数据列表
     * @param saveFilePath 保存位置
     * @throws Exception
     */
    public static <E> void export(String title, List<Map<String, Object>> dataList, String saveFilePath) throws Exception {
        FileOutputStream fileOut = null;
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            int rowIndx = 0;
            HSSFSheet sheet = null;
            if (StringUtils.hasText(title)) {
                sheet = workbook.createSheet(title);
            } else {
                sheet = workbook.createSheet();
            }

            Map<String, Object> firstRow = null;
            if (dataList == null || dataList.isEmpty()) {
                firstRow = new HashMap<String, Object>();
                firstRow.put("数据", "没有数据");
            } else {
                firstRow = dataList.get(0);
            }

            HSSFRow row = null;
            rowIndx = getRowIndx(title, workbook, rowIndx, sheet, row, true, firstRow, null);
            row = sheet.createRow(rowIndx++);
            HSSFCellStyle rowHeaderStyle = workbook.createCellStyle();
            rowHeaderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            rowHeaderStyle.setFillBackgroundColor(new HSSFColor.GREY_80_PERCENT().getIndex());
            rowHeaderStyle.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
            setBorder(rowHeaderStyle);
            int colIndex = 0;
            Set<String> keys = firstRow.keySet();
            for (String key : keys) {
                HSSFCell cell = row.createCell(colIndex, HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(firstRow.get(key).toString());
                cell.setCellStyle(rowHeaderStyle);
                colIndex++;
            }
            if (dataList != null && !dataList.isEmpty()) {
                for (Map<String, Object> e : dataList) {
                    row = sheet.createRow(rowIndx++);
                    colIndex = 0;
                    for (String key : keys) {
                        HSSFCell cell = row.createCell(colIndex, HSSFCell.CELL_TYPE_STRING);
                        Object object = e.get(key);
                        if (object != null) {
                            cell.setCellValue(object.toString());
                        }
                        cell.setCellStyle(rowHeaderStyle);
                        sheet.autoSizeColumn(colIndex);
                        colIndex++;
                    }
                }
            }

            // 新建一输出文件流
            fileOut = new FileOutputStream(saveFilePath);
            // 把相应的Excel 工作簿存盘
            workbook.write(fileOut);
            fileOut.flush();
            // 操作结束，关闭文件
            fileOut.close();
            workbook.close();
            workbook = null;

        } catch (Exception e) {
            throw e;
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
            if (workbook != null) {
                workbook.close();
                workbook = null;
            }
        }
    }

    private static int getRowIndx(String title, HSSFWorkbook workbook, int rowIndx, HSSFSheet sheet, HSSFRow row, boolean flag, Map<String, Object> firstRow, List<Field> fiels) {
        if (StringUtils.hasText(title)) {
            HSSFCellStyle titleHeaderStyle = workbook.createCellStyle();
            setBorder(titleHeaderStyle);
            titleHeaderStyle.setFillBackgroundColor(new HSSFColor.DARK_BLUE().getIndex());
            titleHeaderStyle.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
            titleHeaderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            HSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 16);
            titleHeaderStyle.setFont(font);
            row = sheet.createRow(rowIndx++);
            // 在索引0的位置创建单元格（左上端）
            HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
            // 定义单元格为字符串类型
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, flag ? firstRow.size() : fiels.size() - 1));
            cell.setCellValue(title);
            cell.setCellStyle(titleHeaderStyle);

        }
        return rowIndx;
    }


    public static void aaaaa() {


    }


    private static void setBorder(HSSFCellStyle rowHeaderStyle) {
        rowHeaderStyle.setBorderBottom((short) 1);
        rowHeaderStyle.setBorderLeft((short) 1);
        rowHeaderStyle.setBorderRight((short) 1);
        rowHeaderStyle.setBorderTop((short) 1);
    }

    private static <E> List<Field> getFieldsForExport(Class<E> clazz, Map<String, String> headerMap) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> fieldList = new ArrayList<Field>();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (fieldType.isPrimitive() || Number.class.isAssignableFrom(fieldType) || Date.class.isAssignableFrom(fieldType)
                    || Calendar.class.isAssignableFrom(fieldType)
                    || Character.class.isAssignableFrom(fieldType) || CharSequence.class.isAssignableFrom(fieldType)) {

                if (headerMap != null) {
                    if (headerMap.containsKey(field.getName())) {
                        fieldList.add(field);
                    }
                } else {
                    fieldList.add(field);
                }
            }

        }
        return fieldList;
    }

    public static List<Map<String, Object>> getExcelData(String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(fileName);
        return getExcelData(inputStream, fileName.endsWith(".xlsx"));
    }

    public static List<Map<String, Object>> getExcelData(byte[] data, Boolean xlsx) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(data);
        return getExcelData(inputStream, xlsx);
    }

    public static List<Map<String, Object>> getExcelData(InputStream inputStream, Boolean xlsx) throws IOException {
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        Workbook workbook = null;
        try {
            workbook = getWorkBook(inputStream, xlsx);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getFirstRowNum() >= 0) {
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                Row firstRow = sheet.getRow(sheet.getFirstRowNum());
                HashMap<Integer, String> columnNames = new HashMap<Integer, String>();
                for (Cell cell : firstRow) {
                    columnNames.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
                for (int i = (firstRowNum + 1); i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    Map<String, Object> dataRow = new HashMap<String, Object>();
                    for (Cell cell : row) {
                        Object cellValue = getCellValue(cell);
                        dataRow.put(columnNames.get(cell.getColumnIndex()), cellValue);
                    }
                    datas.add(dataRow);
                }
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return datas;
    }

    private static Workbook getWorkBook(InputStream inputStream, boolean xlsx) throws IOException {
        Workbook workbook = xlsx ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream);
        return workbook;
    }

    private static Object getCellValue(Cell cell) {

        int cellType = cell.getCellType();
        return getValue(cell, cellType);
    }

    private static Object getValue(Cell cell, int cellType) {
        switch (cellType) {
            case Cell.CELL_TYPE_BLANK:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_FORMULA:
                int formulaResultType = cell.getCachedFormulaResultType();
                return getValue(cell, formulaResultType);
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_ERROR:
                return cell.getStringCellValue();
            default:
                return cell.getStringCellValue();
        }
    }
}
