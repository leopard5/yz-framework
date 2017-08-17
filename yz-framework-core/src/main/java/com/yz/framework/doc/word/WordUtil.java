package com.yz.framework.doc.word;

import com.yz.framework.util.FileUtil;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordUtil {

    public static final String IMAGE_ = "IMAGE_";
    public static final String TABLE_OFF_SN_ = "TABLE_OFF_SN_";
    public static final String TABLE_ON_SN_ = "TABLE_ON_SN_";
    public static final String TABLE_PERIOD_ = "TABLE_PERIOD_";
    private static final String EL_LEFT = "<";
    private static final String EL_RIGHT = ">";
    private XWPFDocument document = null;
    private List<String> noSNTable = null;
    private BigInteger numbering = null;


    public XWPFDocument generateWordFromTemplate(XWPFDocument templateDocument, BigInteger templateNumbering, Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {
        document = templateDocument;
        numbering = templateNumbering;
        readWordDocument(map, isHighlight);
        return document;
    }

    public byte[] generateWordFromTemplate(byte[] input, Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {

        byte[] out = null;
        document = new XWPFDocument(new ByteArrayInputStream(input));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (readWordDocument(map, isHighlight)) {
            document.write(os);
            out = os.toByteArray();
        }
        os.flush();
        os.close();
        return out;
    }

    /**
     * Read and write word by template
     *
     * @param templatePath The path of the word template
     * @param outputPath   The path of the result word file to write out
     * @param map          The rule that is how to read and write the word for example: e.g string value replace: The map's key is the text which is ready to repalce in the word and value of the map is a text insert into word. e.g image insert: The map's key
     *                     must start with 'IMAGE_' and value of the map must be put a path of a image. e.g list value insert: The map's key is the name of a table in word. The value of the map must be a instance of calss List and the members of list is a
     *                     instance of calss Map and this map as the string value replace map
     * @return 返回
     * @throws IOException            IOException
     * @throws InvalidFormatException InvalidFormatException
     */
    public boolean generateWordFromTemplate(String templatePath, String outputPath, Map<String, Object> map, boolean isHighlight) throws IOException, InvalidFormatException {

        if (templatePath.toUpperCase().startsWith("HTTP")) {
            document = new XWPFDocument(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            document = new XWPFDocument(POIXMLDocument.openPackage(templatePath));
        }
        return readWordDocument(map, outputPath, isHighlight);
    }

    /**
     * 数据流形式
     *
     * @param templatePath templatePath
     * @param os           os
     * @param map          map
     * @return 返回
     * @throws IOException            IOException
     * @throws InvalidFormatException InvalidFormatException
     */
    public boolean generateWordFromTemplate(String templatePath, OutputStream os, Map<String, Object> map, boolean isHighlight) throws IOException, InvalidFormatException {

        if (templatePath.toUpperCase().startsWith("HTTP")) {
            document = new XWPFDocument(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            document = new XWPFDocument(POIXMLDocument.openPackage(templatePath));
        }
        boolean isTrue = readWordDocument(map, isHighlight);
        document.write(os);
        return isTrue;
    }

    /**
     * 数据流形式
     *
     * @param templatePath templatePath
     * @param map          map
     * @return 返回
     * @throws InvalidFormatException InvalidFormatException
     * @throws IOException            IOException
     */
    public byte[] generateWordFromTemplate(String templatePath, Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {
        byte[] out = null;
        if (templatePath.toUpperCase().startsWith("HTTP")) {
            document = new XWPFDocument(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            document = new XWPFDocument(POIXMLDocument.openPackage(templatePath));
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (readWordDocument(map, isHighlight)) {
            document.write(os);
            out = os.toByteArray();
        }
        os.flush();
        os.close();
        return out;
    }

    /**
     * Read and write word by template in inputStream
     *
     * @param inputStream inputStream
     * @param outputPath  outputPath
     * @param map         map
     * @return 返回
     * @throws InvalidFormatException InvalidFormatException
     * @throws IOException            IOException
     */
    public boolean generateWordFromTemplate(InputStream inputStream, String outputPath, Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {
        document = new XWPFDocument(inputStream);
        return readWordDocument(map, outputPath, isHighlight);
    }

    /**
     * 数据流形式
     *
     * @param inputStream inputStream
     * @param os          os
     * @param map         map
     * @return 返回
     * @throws InvalidFormatException InvalidFormatException
     * @throws IOException            IOException
     */
    public boolean generateWordFromTemplate(InputStream inputStream, OutputStream os, Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {
        document = new XWPFDocument(inputStream);
        boolean isTrue = readWordDocument(map, isHighlight);
        document.write(os);
        return isTrue;
    }

    /**
     * 数据流形式
     *
     * @param inputStream inputStream
     * @param map         map
     * @return 返回
     * @throws InvalidFormatException InvalidFormatException
     * @throws IOException            IOException
     */
    public byte[] generateWordFromTemplate(InputStream inputStream, Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {
        byte[] out = null;
        document = new XWPFDocument(inputStream);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (readWordDocument(map, isHighlight)) {
            document.write(os);
            out = os.toByteArray();
        }
        os.flush();
        os.close();
        return out;
    }

    /**
     * 包装成文件输出
     *
     * @param map        map
     * @param outputPath outputPath
     * @return 返回
     * @throws InvalidFormatException InvalidFormatException
     * @throws IOException            IOException
     */
    private boolean readWordDocument(Map<String, Object> map, String outputPath, boolean isHighlight) throws InvalidFormatException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(outputPath));
        boolean isTrue = readWordDocument(map, isHighlight);
        document.write(fos);
        return isTrue;
    }

    /**
     * 核心处理方法
     *
     * @param map map
     * @return 返回
     * @throws InvalidFormatException InvalidFormatException
     * @throws IOException            IOException
     */
    private boolean readWordDocument(Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {
        // replace the text of header
        if (document.getHeaderList() != null && document.getHeaderList().size() > 0) {
            XWPFHeader header = document.getHeaderArray(0);
            List<XWPFParagraph> listHeader = header.getParagraphs();
            for (XWPFParagraph paragraph : listHeader) {
                replaceWordText(paragraph, map, isHighlight);
            }
        }
        // replace the text of body
//        Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
//        while (itPara.hasNext()) {
//            XWPFParagraph paragraph = itPara.next();
//            replaceWordText(paragraph, map, isHighlight);
//            // wordReadRule.addPictureInWord(document);
//        }
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (paragraphList != null && paragraphList.size() > 0) {
            for (int i = 0; i < paragraphList.size(); i++) {
                XWPFParagraph xwpfParagraph = paragraphList.get(i);
                replaceWordText(xwpfParagraph, map, isHighlight);
            }
        }

        // replace the text of table
        Iterator<XWPFTable> itTable = document.getTablesIterator();
        while (itTable.hasNext()) {
            XWPFTable table = itTable.next();
            readWordTableRangeRule(table, map, isHighlight);

        }

        return true;
    }


    /**
     * Read Word Table Range Rule
     *
     * @param table         table
     * @param mapProperties mapProperties
     * @throws InvalidFormatException InvalidFormatException
     */
    private void readWordTableRangeRule(XWPFTable table, Map<String, Object> mapProperties, boolean isHighlight) throws InvalidFormatException, IOException {
        List<Map<String, String>> hasCellContant = new ArrayList<>();
        int rcount = table.getNumberOfRows();
        boolean isListValue = false;
        boolean noSN = false;
        for (int i = 0; i < rcount; i++) {
            XWPFTableRow row = table.getRow(i);
            List<XWPFTableCell> cells = row.getTableCells();

            for (XWPFTableCell cell : cells) {
                repalceTableText(cell, mapProperties, isHighlight);
                repalceCellText(cell, mapProperties, isHighlight);
            }

            for (XWPFTableCell cell : cells) {
                String cellTextString = cell.getText();

                for (Map.Entry<String, Object> entry : mapProperties.entrySet()) {

                    String paramName = EL_LEFT + entry.getKey() + EL_RIGHT;
                    if (cellTextString.contains(paramName.replace(TABLE_ON_SN_, "").replace(TABLE_OFF_SN_, ""))) {

                        String replaceText = "";

                        if (entry.getValue() instanceof List) {

                            if (entry.getKey().startsWith(TABLE_ON_SN_)) {
                                noSN = true;
                            }
                            if (entry.getKey().startsWith(TABLE_OFF_SN_)) {
                                noSN = false;
                            }

                            @SuppressWarnings("unchecked")
                            List<Map<String, String>> list = (List<Map<String, String>>) entry.getValue();
                            hasCellContant.addAll(list);
                            isListValue = true;
                        } else {
                            if (null != entry.getValue()) {
                                replaceText = String.valueOf(entry.getValue());
                            }
                        }

                        repalceTitleText(cell, paramName.replace(TABLE_ON_SN_, "").replace(TABLE_OFF_SN_, ""), replaceText);

                        if (isListValue)
                            break;

                    }

                }
                if (isListValue)
                    break;
            }
            if (isListValue)
                break;
        }
        if (isListValue)
            addListValueInTable(hasCellContant, table, noSN, isHighlight);
    }

    @SuppressWarnings("deprecation")
    private void repalceTitleText(XWPFTableCell cell, String paramName, String replaceText) throws InvalidFormatException, IOException {

        List<XWPFParagraph> itParagraph = cell.getParagraphs();
        if (null != itParagraph) {
            for (XWPFParagraph paragraph : itParagraph) {

//                List<XWPFRun> runs = paragraph.getRuns();
//                for (int i = 0; i < runs.size(); i++) {
//                    XWPFRun run = runs.get(i);
//                    String runText = run.getText(run.getTextPosition());
//                    run.setText(runText.replace(paramName, ""), 0);
//                }

                replaceText(paragraph, paramName, replaceText, false, false);
            }
        }

    }

    private void repalceTableText(XWPFTableCell cell, Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {
        List<XWPFTable> itTable = cell.getTables();
        if (null != itTable) {
            for (XWPFTable table : itTable) {
                readWordTableRangeRule(table, map, isHighlight);
            }
        }
    }

    private void repalceCellText(XWPFTableCell cell, Map<String, Object> map, boolean isHighlight) throws InvalidFormatException, IOException {

        List<XWPFParagraph> itParagraph = cell.getParagraphs();
        if (null != itParagraph) {
            for (XWPFParagraph paragraph : itParagraph) {
                replaceWordText(paragraph, map, isHighlight);
            }
        }
    }

    private void addListValueInTable(List<Map<String, String>> hasCellContant, XWPFTable table, boolean noSN, boolean isHighlight) {
        // add text of list values in table
        Map<Integer, List<String>> insertTextIndex = new HashMap<>();

        //XWPFParagraph tmpP = null;
        if (hasCellContant.size() > 0) {
            for (int i = 0; i < hasCellContant.size(); i++) {
                XWPFTableRow row;
                List<XWPFTableCell> cells;
                Map<String, String> map = hasCellContant.get(i);
                if (i == 0) {
                    row = table.getRow(1);
                } else {
                    row = table.createRow();
                }
                cells = row.getTableCells();
                for (int j = 0; j < cells.size(); j++) {
                    XWPFTableCell cell = cells.get(j);
                    String text = cell.getText();

                    if (noSN) {
                        if (j == 0 && i > 0) {

                            text = String.valueOf(i + 1);
                            List<XWPFParagraph> ps = cell.getParagraphs();
                            for (XWPFParagraph p : ps) {
                                p.setAlignment(ParagraphAlignment.CENTER);
                            }

                        } else if (j != 0) {

                            text = addListValueInTable(insertTextIndex, i, map, j, text);

                        }
                        if (i == 0 && j != 0) {
                            cell.removeParagraph(0);
                        }
                        if (!(i == 0 && j == 0)) {

                            XWPFParagraph addParagraph = cell.addParagraph();
                            addParagraph.setAlignment(ParagraphAlignment.CENTER);
                            addParagraph.setVerticalAlignment(TextAlignment.CENTER);
                            XWPFRun run = addParagraph.createRun();
                            run.setText(text);

                            if (isHighlight) {
                                setHighlight(run);
                            }

                        }
                    } else {

                        text = addListValueInTable(insertTextIndex, i, map, j, text);

                        if (i == 0) {
                            cell.removeParagraph(0);
                        }

                        XWPFParagraph addParagraph = cell.addParagraph();
                        addParagraph.setAlignment(ParagraphAlignment.CENTER);
                        addParagraph.setVerticalAlignment(TextAlignment.CENTER);
                        XWPFRun run = addParagraph.createRun();
                        run.setText(text);

                        if (isHighlight) {
                            setHighlight(run);
                        }

                    }
                }
            }
        }

    }

    private String addListValueInTable(Map<Integer, List<String>> insertTextIndex, int i, Map<String, String> map, int j, String text) {
        List<String> listParamNames = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String paramName = EL_LEFT + entry.getKey() + EL_RIGHT;
            if (i == 0) {
                if (text.contains(paramName)) {
                    text = text.replace(paramName, entry.getValue() != null ? entry.getValue() : "      ");
                    listParamNames.add(entry.getKey());
                }
            } else {
                listParamNames = insertTextIndex.get(j);
                for (String name : listParamNames) {
                    if (name.equals(entry.getKey())) {
                        text += entry.getValue();
                    }
                }
            }
        }
        if (i == 0) {
            insertTextIndex.put(j, listParamNames);
        }
        return text;
    }


    /**
     * replace text by paragraph 由于原版本的方法replaceWordText按照run单位进行的匹配对模板要求非常高， 所以改写了一个按照paragraph单位进行匹配的方法，但是替换文字的处理比较复杂。
     *
     * @param paragraph     paragraph
     * @param mapProperties mapProperties
     * @throws InvalidFormatException InvalidFormatException
     */
    private void replaceWordText(XWPFParagraph paragraph, Map<String, Object> mapProperties, boolean isHighlight) throws InvalidFormatException, IOException {

        // 取段落文字
        String oneparaString = paragraph.getParagraphText();

        if (null != oneparaString) {

            for (Map.Entry<String, Object> entry : mapProperties.entrySet()) {

                replaceItemText(oneparaString, paragraph, entry, isHighlight);
            }
        }
    }

    private void replaceItemText(String oneparaString, XWPFParagraph paragraph, Map.Entry<String, Object> entry, boolean isHighlight) throws InvalidFormatException, IOException {

        boolean isImage = entry.getKey().startsWith(IMAGE_);


        String paramName = EL_LEFT + entry.getKey() + EL_RIGHT;
        // 每当段落文字中有需要替换的项目时
        if (oneparaString.contains(paramName.replace(IMAGE_, ""))) {
            String replaceText = "";
            if (null != entry.getValue()) {
                replaceText = String.valueOf(entry.getValue());
            }
            replaceText(paragraph, paramName.replace(IMAGE_, "").replace(TABLE_PERIOD_, ""), replaceText, isImage, isHighlight);
        }

        boolean isTablePeriod = entry.getKey().startsWith(TABLE_PERIOD_);
        if (oneparaString.contains(paramName.replace(TABLE_PERIOD_, ""))) {
            if (isTablePeriod) {
                List<String> stringList = null;
                if (null != entry.getValue()) {
                    stringList = (List<String>) entry.getValue();
                }
                replaceTextPeriod(paragraph, paramName.replace(TABLE_PERIOD_, ""), stringList, isTablePeriod, isHighlight);
            }
        }

//        boolean isImage = entry.getKey().startsWith(IMAGE_);
//        String paramName = EL_LEFT + entry.getKey() + EL_RIGHT;
//        //非图片
//        if (!isImage) {
//            // 每当段落文字中有需要替换的项目时
//            if (oneparaString.contains(paramName)) {
//                String replaceText = "";
//                if (null != entry.getValue()) {
//                    replaceText = String.valueOf(entry.getValue());
//                }
//
//                replaceText(paragraph, paramName, replaceText, isHighlight);
//
//            }
//
//            //tupian
//        } else {
//            List<XWPFRun> runs = paragraph.getRuns();
//            for (int i = 0; i < runs.size(); i++) {
//                XWPFRun run = runs.get(i);
//                String runText = run.getText(run.getTextPosition());
//
//
//                if ((!StringUtils.isEmpty(runText)) && runText.contains(paramName.replace(IMAGE_, ""))) {
//                    runText = "";
//                    CTInline inline = runs.get(i).getCTR().addNewDrawing().addNewInline();
//                    insertPicture(document, String.valueOf(entry.getValue()), inline, 100, 100);
//                    run.setText(runText);
//                }
//            }
//
//        }

    }

    //    private void replaceText(XWPFParagraph paragraph, String paramName, String replaceText, boolean isHighlight) {
//        // 拆解段落中的词素并循环
//        List<XWPFRun> runs = paragraph.getRuns();
//
//        for (int i = 0; i < runs.size(); i++) {
//            XWPFRun run = runs.get(i);
//            String runText = run.getText(run.getTextPosition());
//
//            System.out.println("paramName:" + paramName);
//            System.out.println("runText:" + runText);
//            System.out.println("replaceText:" + replaceText);
//
//            if(runText.contains(paramName)) {
//
//                System.out.println("写入:"+runText.replace(paramName, replaceText));
//
//                run.setText(runText.replace(paramName, replaceText),0);
//                if (isHighlight) {
//                    setHighlight(run);
//                }
//            }
//
//            System.out.println("---------------------------------------");
//
//        }
//    }
    private void replaceTextPeriod(XWPFParagraph paragraph, String paramName, List<String> stringList, boolean isImage, boolean isHighlight) throws InvalidFormatException, IOException {


        // 拆解段落中的词素并循环
        List<XWPFRun> runs = paragraph.getRuns();
        int beginRun = -1;
        int endRun = -1;
        String compareText = null;

        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            String runText = run.getText(run.getTextPosition());

            if (runText == null) {
                continue;
            }

            String trimText = runText.trim();

            if (trimText.length() == 0) {
                continue;
            }

            // 查看每个词素的开始字符，通过左尖括号找到开始词素，并开始记录
            // 注意：如若在找到结束词素之前出现了另一个开始词素，则整个匹配处理会重置。
            if (EL_LEFT.equals(trimText.substring(0, 1))) {
                beginRun = i;
                compareText = "";
            }

            // 已找到开始标记的情况下，查看每个词素的结束字符，找到结束标记右尖括号
            if (beginRun >= 0 && EL_RIGHT.equals(trimText.substring(trimText.length() - 1))) {
                endRun = i;
            }

            // 已找到开始标记的情况下，拼接每个词素
            if (beginRun >= 0) {
                compareText += runText;
            }

            // 当拼接的词素与需要替换的参数名完全一致时，开始替换
            // 将开始词素替换为参数值，清空随后的词素，直至结束词素
            if (paramName.equals(compareText == null ? null : compareText.trim())) {

                runs.get(beginRun).setText(compareText.replace(paramName, ""), 0);

                while (endRun > beginRun) {
                    runs.get(++beginRun).setText("", 0);
                }

                //加载编号
//                BigInteger numbering = new SpecialUtil().getNumbering(document);

                //添加文本
                for (String string : stringList) {

                    XWPFParagraph insertNewParagraph = document.insertNewParagraph(paragraph.getCTP().newCursor());

                    if (numbering != null) {
                        insertNewParagraph.setNumID(numbering);
                        insertNewParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(4));
                    }

                    XWPFRun position = insertNewParagraph.createRun();
                    position.setText(string);
                    position.setBold(false);
                    position.setFontFamily("宋体");
                    position.setFontSize(12);
                    if (isHighlight) {
                        setHighlight(position);
                    }
                }
            }
        }
    }


    private void replaceText(XWPFParagraph paragraph, String paramName, String replaceText, boolean isImage, boolean isHighlight) throws InvalidFormatException, IOException {


        // 拆解段落中的词素并循环
        List<XWPFRun> runs = paragraph.getRuns();
        int beginRun = -1;
        int endRun = -1;
        String compareText = null;

//        System.out.println("---------------------------------------");

        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            String runText = run.getText(run.getTextPosition());

            if (runText == null) {
                continue;
            }

            String trimText = runText.trim();

            if (trimText.length() == 0) {
                continue;
            }

            //处理不带格式的纯文本替换，如果有直接替换
            if (!isImage) {

                String string = runText;
                Matcher matcher = Pattern.compile(paramName).matcher(string);
                Integer begin = 0;
                Integer end = 0;
                while (matcher.find()) {

                    String tag = matcher.group();

                    if (string.trim().length() == tag.length()) {
                        break;
                    }

                    end = string.indexOf(tag);
                    String ind = "";
                    for (int n = 0; n < tag.length(); n++) {
                        ind += " ";
                    }
                    string = string.replaceFirst(tag, ind);
                    String text = string.substring(begin, end);
                    if (text != null && !"".equals(text)) {
                        XWPFRun contentRun = paragraph.insertNewRun(i);
                        contentRun.setText(text);
                        contentRun.setFontSize(12);
                    }
                    begin = end + tag.length();
                    XWPFRun tagRun = paragraph.insertNewRun(i);
                    tagRun.setText(replaceText);
                    tagRun.setFontSize(12);

                    if (isHighlight) {
                        setHighlight(tagRun);
                    }

                    //清空
                    run.setText("", 0);
                }
                if (begin > 0) {
                    if (begin < string.length()) {
                        String text = string.substring(begin, string.length());
                        if (!"".equals(text) && text != null) {
                            XWPFRun contentRun = paragraph.insertNewRun(i);
                            contentRun.setText(text);
                            contentRun.setFontSize(12);
                        }
                    }
                }

            }

            // 查看每个词素的开始字符，通过左尖括号找到开始词素，并开始记录
            // 注意：如若在找到结束词素之前出现了另一个开始词素，则整个匹配处理会重置。
            if (EL_LEFT.equals(trimText.substring(0, 1))) {
                beginRun = i;
                compareText = "";
            }

            // 已找到开始标记的情况下，查看每个词素的结束字符，找到结束标记右尖括号
            if (beginRun >= 0 && EL_RIGHT.equals(trimText.substring(trimText.length() - 1))) {
                endRun = i;
            }

            // 已找到开始标记的情况下，拼接每个词素
            if (beginRun >= 0) {
                compareText += runText;
            }

//            System.out.println("runText:" + runText);
//            System.out.println("paramName:" + paramName);
//            System.out.println("compareText:" + compareText);
//            System.out.println("replaceText:" + replaceText);

            // 当拼接的词素与需要替换的参数名完全一致时，开始替换
            // 将开始词素替换为参数值，清空随后的词素，直至结束词素
            if (paramName.equals(compareText == null ? null : compareText.trim())) {

                if (isImage) {

                    if (replaceText == null || replaceText.length() == 0) {
                        continue;
                    }


                    byte[] intByte;
                    if (replaceText.toUpperCase().startsWith("HTTP")) {
                        intByte = FileUtil.getHttpFileUrl(replaceText);
                    } else {
                        InputStream dis = new FileInputStream(replaceText);
                        ByteArrayOutputStream fos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int length;
                        //开始填充数据
                        while ((length = dis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                        intByte = fos.toByteArray();
                        dis.close();
                        fos.close();
                    }

                    ByteArrayInputStream bff = new ByteArrayInputStream(intByte);
                    BufferedImage bufferedImage = ImageIO.read(bff);
                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();
                    bff.close();

                    ByteArrayInputStream bss = new ByteArrayInputStream(intByte);
                    insertPicture(document, bss, runs.get(i), width, height);
                    bss.close();

//                    System.out.println("1-写入:" + compareText.replace(paramName, replaceText));

                    runs.get(beginRun).setText(compareText.replace(paramName, ""), 0);


                } else {

//                    System.out.println("2-写入:" + compareText.replace(paramName, replaceText));

                    runs.get(beginRun).setText(compareText.replace(paramName, replaceText), 0);

                    if (isHighlight) {
                        setHighlight(runs.get(beginRun));
                    }


                }

                while (endRun > beginRun) {
                    runs.get(++beginRun).setText("", 0);
                }


            }

//            System.out.println("---------------------------------------");

        }
    }

//    /**
//     * @Description: 得到XWPFRun的CTRPr
//     */
//    private CTRPr getRunCTRPr(XWPFParagraph p, XWPFRun pRun) {
//        CTRPr pRpr = null;
//        if (pRun.getCTR() != null) {
//            pRpr = pRun.getCTR().getRPr();
//            if (pRpr == null) {
//                pRpr = pRun.getCTR().addNewRPr();
//            }
//        } else {
//            pRpr = p.getCTP().addNewR().addNewRPr();
//        }
//        return pRpr;
//    }
//
//    private void setHighlight(XWPFParagraph paragraph,XWPFRun run){
//        CTRPr pRpr = getRunCTRPr(paragraph, run);
//        // 设置突出显示文本
//            CTHighlight hightLight = pRpr.isSetHighlight() ? pRpr
//                    .getHighlight() : pRpr.addNewHighlight();
//            hightLight.setVal(STHighlightColor.Enum.forInt(9));
//    }

    private void setHighlight(XWPFRun run) {

        run.setColor("0000FF");
//        run.setBold(true);
        run.setFontFamily("微软雅黑");
    }

    /**
     * insert Picture
     *
     * @param document document
     * @param run      run
     * @param width    width
     * @param height   height
     * @throws InvalidFormatException InvalidFormatException
     */
    private void insertPicture(XWPFDocument document, InputStream inputStream, XWPFRun run, int width, int height) throws InvalidFormatException, FileNotFoundException {
        document.addPictureData(inputStream, XWPFDocument.PICTURE_TYPE_PNG);

        CTR ctr = run.getCTR();
        CTDrawing ctDrawing = ctr.addNewDrawing();

        int id = document.getAllPictures().size() - 1;
        final int EMU = 9525;
        width *= EMU;
        height *= EMU;
        String blipId = document.getAllPictures().get(id).getPackageRelationship().getId();
        String picXml = getPicXml(blipId, width, height);
        XmlToken xmlToken = null;
        try {
            xmlToken = XmlToken.Factory.parse(picXml);
        } catch (XmlException xe) {
            xe.printStackTrace();
        }

        CTInline inline = ctDrawing.addNewInline();
        inline.set(xmlToken);
        inline.setDistT(0);
        inline.setDistB(0);
        inline.setDistL(0);
        inline.setDistR(0);

        CTPositiveSize2D extent = inline.addNewExtent();
        extent.setCx(width);
        extent.setCy(height);

        CTNonVisualDrawingProps docPr = inline.addNewDocPr();
        docPr.setId(id);
        docPr.setName("IMG_" + id);
        docPr.setDescr("IMG_" + id);

//        CTAnchor ctAnchor = ctDrawing.addNewAnchor();
//        ctAnchor.setLayoutInCell(true);
//        ctAnchor.setAllowOverlap(true);
//        ctAnchor.setLocked(false);
//        ctAnchor.setBehindDoc(false);
//        ctAnchor.set(xmlToken);
//        ctAnchor.setDistT(0);
//        ctAnchor.setDistB(0);
//        ctAnchor.setDistL(114300);
//        ctAnchor.setDistR(114300);
//        ctAnchor.setRelativeHeight(251658240);
//        ctAnchor.setSimplePos2(false);
//
//
//        CTPoint2D ctPoint2D = CTPoint2D.Factory.newInstance();
//        ctPoint2D.setX(0);
//        ctPoint2D.setY(0);
//        ctAnchor.setSimplePos(ctPoint2D);
//
//        CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties = ctAnchor.addNewCNvGraphicFramePr();
//
//        CTOfficeArtExtensionList ctOfficeArtExtensionList = CTOfficeArtExtensionList.Factory.newInstance();
//
//        CTOfficeArtExtension ctOfficeArtExtension = ctOfficeArtExtensionList.addNewExt();
//
//        ctNonVisualGraphicFrameProperties.setExtLst(ctOfficeArtExtensionList);
//
//
//        CTPositiveSize2D extent = ctAnchor.addNewExtent();
//        extent.setCx(width);
//        extent.setCy(height);
//
//        CTNonVisualDrawingProps docPr = ctAnchor.addNewDocPr();
//        docPr.setId(id);
//        docPr.setName("IMG_" + id);
//        docPr.setDescr("IMG_" + id);
    }

    /**
     * get the xml of the picture
     *
     * @param blipId blipId
     * @param width  width
     * @param height height
     * @return 返回
     */
    private String getPicXml(String blipId, int width, int height) {
        return "" + "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">" + "   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
                + "      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" + "         <pic:nvPicPr>" + "            <pic:cNvPr id=\""
                + 0
                + "\" name=\"Generated\"/>"
                + "            <pic:cNvPicPr/>"
                + "         </pic:nvPicPr>"
                + "         <pic:blipFill>"
                + "            <a:blip r:embed=\""
                + blipId
                + "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>"
                + "            <a:stretch>"
                + "               <a:fillRect/>"
                + "            </a:stretch>"
                + "         </pic:blipFill>"
                + "         <pic:spPr>"
                + "            <a:xfrm>"
                + "               <a:off x=\"0\" y=\"0\"/>"
                + "               <a:ext cx=\""
                + width
                + "\" cy=\""
                + height
                + "\"/>"
                + "            </a:xfrm>"
                + "            <a:prstGeom prst=\"rect\">"
                + "               <a:avLst/>" + "            </a:prstGeom>" + "         </pic:spPr>" + "      </pic:pic>" + "   </a:graphicData>" + "</a:graphic>";
    }

    public List<String> getNoSNTable() {
        if (null == noSNTable) {
            noSNTable = new ArrayList<>();
        }
        return noSNTable;
    }

    public void setNoSNTable(List<String> noSNTable) {
        this.noSNTable = noSNTable;
    }
}
