package com.yz.framework.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;


public class ExcelUtil {
    private static HSSFWorkbook wb;

    private static CellStyle titleStyle;   // 标题行样式
    private static Font titleFont;         // 标题行字体
    private static CellStyle dateStyle;    // 日期行样式
    private static Font dateFont;          // 日期行字体
    private static CellStyle headStyle;    // 表头行样式
    private static Font headFont;          // 表头行字体
    private static CellStyle contentStyle; // 内容行样式
    private static Font contentFont;       // 内容行字体

    /**
     * 导出文件
     *
     * @param setInfo
     * @param outputExcelFileName
     * @return
     * @throws IOException
     */
    public static boolean export2File(ExcelExportData setInfo, String outputExcelFileName) throws Exception {
        return FileUtil.write(outputExcelFileName, export2ByteArray(setInfo), true, true);
    }

    /**
     * 导出到byte数组
     *
     * @param setInfo
     * @return
     * @throws Exception
     */
    public static byte[] export2ByteArray(ExcelExportData setInfo) throws Exception {
        return export2Stream(setInfo).toByteArray();
    }

    /**
     * 导出到流
     *
     * @param setInfo
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream export2Stream(ExcelExportData setInfo) throws Exception {
        init();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Set<Entry<String, List<?>>> set = setInfo.getDataMap().entrySet();
        String[] sheetNames = new String[setInfo.getDataMap().size()];
        int sheetNameNum = 0;
        for (Entry<String, List<?>> entry : set) {
            sheetNames[sheetNameNum] = entry.getKey();
            sheetNameNum++;
        }
        HSSFSheet[] sheets = getSheets(setInfo.getDataMap().size(), sheetNames);
        int sheetNum = 0;
        for (Entry<String, List<?>> entry : set) {
            // Sheet
            List<?> objs = entry.getValue();

            // 标题行
            // createTableTitleRow(setInfo, sheets, sheetNum);

            // 日期行
            // createTableDateRow(setInfo, sheets, sheetNum);

            // 表头
            creatTableHeadRow(setInfo, sheets, sheetNum);

            // 总计
//			total(setInfo, sheets, sheetNum);

            // 表体
            String[] fieldNames = setInfo.getFieldNames().get(sheetNum);

            int rowNum = 2;
            for (Object obj : objs) {
                HSSFRow contentRow = sheets[sheetNum].createRow(rowNum);
                contentRow.setHeight((short) 300);
                HSSFCell[] cells = getCells(contentRow, setInfo.getFieldNames().get(sheetNum).length);
                int cellNum = 1; // 去掉一列序号，因此从1开始
                if (fieldNames != null) {
                    for (int num = 0; num < fieldNames.length; num++) {

                        Object value = ReflectionUtil.invokeGetterMethod(obj, fieldNames[num]);
                        cells[cellNum].setCellValue(value == null ? "" : value.toString());
                        cellNum++;
                    }
                }
                rowNum++;
            }
            adjustColumnSize(sheets, sheetNum, fieldNames); // 自动调整列宽
            sheetNum++;
        }
        wb.write(outputStream);
        return outputStream;
    }

    /**
     * @Description: 初始化
     */
    private static void init() {
        wb = new HSSFWorkbook();

        titleFont = wb.createFont();
        titleStyle = wb.createCellStyle();
        dateStyle = wb.createCellStyle();
        dateFont = wb.createFont();
        headStyle = wb.createCellStyle();
        headFont = wb.createFont();
        contentStyle = wb.createCellStyle();
        contentFont = wb.createFont();

        initTitleCellStyle();
        initTitleFont();
        initDateCellStyle();
        initDateFont();
        initHeadCellStyle();
        initHeadFont();
        initContentCellStyle();
        initContentFont();
    }

    /**
     * @Description: 自动调整列宽
     */
    private static void adjustColumnSize(HSSFSheet[] sheets, int sheetNum, String[] fieldNames) {
        for (int i = 0; i < fieldNames.length + 1; i++) {
            sheets[sheetNum].autoSizeColumn(i, true);
        }
    }

    /**
     * @Description: 创建标题行(需合并单元格)
     */
    // private static void createTableTitleRow(ExcelExportData setInfo,
    // HSSFSheet[] sheets, int sheetNum) {
    // CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0,
    // setInfo.getFieldNames().get(sheetNum).length);
    // sheets[sheetNum].addMergedRegion(titleRange);
    // HSSFRow titleRow = sheets[sheetNum].createRow(0);
    // titleRow.setHeight((short) 800);
    // HSSFCell titleCell = titleRow.createCell(0);
    // titleCell.setCellStyle(titleStyle);
    // titleCell.setCellValue(setInfo.getTitles()[sheetNum]);
    // }

    /**
     * @Description: 创建日期行(需合并单元格)
     */
    // private static void createTableDateRow(ExcelExportData setInfo,
    // HSSFSheet[] sheets, int sheetNum) {
    // CellRangeAddress dateRange = new CellRangeAddress(1, 1, 0,
    // setInfo.getFieldNames().get(sheetNum).length);
    // sheets[sheetNum].addMergedRegion(dateRange);
    // HSSFRow dateRow = sheets[sheetNum].createRow(1);
    // dateRow.setHeight((short) 350);
    // HSSFCell dateCell = dateRow.createCell(0);
    // dateCell.setCellStyle(dateStyle);
    // // dateCell.setCellValue("导出时间：" + new
    // // SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    // // .format(new Date()));
    // dateCell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(new
    // Date()));
    // }

    /**
     * @Description: 创建表头行(需合并单元格)
     */
    private static void creatTableHeadRow(ExcelExportData setInfo, HSSFSheet[] sheets, int sheetNum) {
        // 表头
        HSSFRow headRow = sheets[sheetNum].createRow(0);
        headRow.setHeight((short) 350);
        // 序号列
        HSSFCell snCell = headRow.createCell(0);
        snCell.setCellStyle(headStyle);
        snCell.setCellValue("序号");
        // 列头名称
        for (int num = 1, len = setInfo.getColumnNames().get(sheetNum).length; num <= len; num++) {
            HSSFCell headCell = headRow.createCell(num);
            headCell.setCellStyle(headStyle);
            headCell.setCellValue(setInfo.getColumnNames().get(sheetNum)[num - 1]);
        }
    }

    private static void total(ExcelExportData setInfo, HSSFSheet[] sheets, int sheetNum) {
        HSSFRow headRow = sheets[sheetNum].createRow(1);
        headRow.setHeight((short) 350);

        HSSFCell snCell = headRow.createCell(0);
        snCell.setCellStyle(headStyle);
        snCell.setCellValue("总计");
        // 总计数据
        for (int num = 1, len = setInfo.getTotal().get(sheetNum).length; num <= len; num++) {
            HSSFCell headCell = headRow.createCell(num);
            headCell.setCellStyle(headStyle);
            headCell.setCellValue(setInfo.getTotal().get(sheetNum)[num - 1]);
        }

    }

    /**
     * @Description: 创建所有的Sheet
     */
    private static HSSFSheet[] getSheets(int num, String[] names) {
        HSSFSheet[] sheets = new HSSFSheet[num];
        for (int i = 0; i < num; i++) {
            sheets[i] = wb.createSheet(names[i]);
        }
        return sheets;
    }

    /**
     * @Description: 创建内容行的每一列(附加一列序号)
     */
    private static HSSFCell[] getCells(HSSFRow contentRow, int num) {
        HSSFCell[] cells = new HSSFCell[num + 1];

        for (int i = 0, len = cells.length; i < len; i++) {
            cells[i] = contentRow.createCell(i);
            cells[i].setCellStyle(contentStyle);
        }

        // 设置序号列值，因为出去标题行和日期行，所有-2
        cells[0].setCellValue(contentRow.getRowNum() - 1);

        return cells;
    }

    /**
     * @Description: 初始化标题行样式
     */
    private static void initTitleCellStyle() {
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        titleStyle.setFont(titleFont);
        titleStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
    }

    /**
     * @Description: 初始化日期行样式
     */
    private static void initDateCellStyle() {
        dateStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        dateStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        dateStyle.setFont(dateFont);
        dateStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
    }

    /**
     * @Description: 初始化表头行样式
     */
    private static void initHeadCellStyle() {
        headStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headStyle.setFont(headFont);
        headStyle.setFillBackgroundColor(IndexedColors.YELLOW.index);
        headStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
        headStyle.setBorderRight(CellStyle.BORDER_THIN);
        headStyle.setTopBorderColor(IndexedColors.BLUE.index);
        headStyle.setBottomBorderColor(IndexedColors.BLUE.index);
        headStyle.setLeftBorderColor(IndexedColors.BLUE.index);
        headStyle.setRightBorderColor(IndexedColors.BLUE.index);
    }

    /**
     * @Description: 初始化内容行样式
     */
    private static void initContentCellStyle() {
        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        contentStyle.setFont(contentFont);
        contentStyle.setBorderTop(CellStyle.BORDER_THIN);
        contentStyle.setBorderBottom(CellStyle.BORDER_THIN);
        contentStyle.setBorderLeft(CellStyle.BORDER_THIN);
        contentStyle.setBorderRight(CellStyle.BORDER_THIN);
        contentStyle.setTopBorderColor(IndexedColors.BLUE.index);
        contentStyle.setBottomBorderColor(IndexedColors.BLUE.index);
        contentStyle.setLeftBorderColor(IndexedColors.BLUE.index);
        contentStyle.setRightBorderColor(IndexedColors.BLUE.index);
        contentStyle.setWrapText(true); // 字段换行
    }

    /**
     * @Description: 初始化标题行字体
     */
    private static void initTitleFont() {
        titleFont.setFontName("华文楷体");
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleFont.setCharSet(Font.DEFAULT_CHARSET);
        titleFont.setColor(IndexedColors.BLUE_GREY.index);
    }

    /**
     * @Description: 初始化日期行字体
     */
    private static void initDateFont() {
        dateFont.setFontName("隶书");
        dateFont.setFontHeightInPoints((short) 10);
        dateFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        dateFont.setCharSet(Font.DEFAULT_CHARSET);
        dateFont.setColor(IndexedColors.BLUE_GREY.index);
    }

    /**
     * @Description: 初始化表头行字体
     */
    private static void initHeadFont() {
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 10);
        headFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headFont.setCharSet(Font.DEFAULT_CHARSET);
        headFont.setColor(IndexedColors.BLUE_GREY.index);
    }

    /**
     * @Description: 初始化内容行字体
     */
    private static void initContentFont() {
        contentFont.setFontName("宋体");
        contentFont.setFontHeightInPoints((short) 10);
        contentFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        contentFont.setCharSet(Font.DEFAULT_CHARSET);
        contentFont.setColor(IndexedColors.BLUE_GREY.index);
    }

    public static String getLimit() {
        Properties prop = new Properties();
        String value = "";
        try {
            // 读取属性文件a.properties
            InputStream in = new BufferedInputStream(new FileInputStream("src/main/resources/export_file/export.properties"));
            prop.load(in); /// 加载属性列表
            Iterator<String> it = prop.stringPropertyNames().iterator();
            while (it.hasNext()) {
                value = prop.getProperty(it.next());
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}