package com.yz.framework.doc.word;

import com.yz.framework.util.FileUtil;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialUtil {

    XWPFDocument document = null;

    public XWPFDocument getGenerateFirst(String templatePath) throws Exception {

        if (templatePath.toUpperCase().startsWith("HTTP")) {
            document = new XWPFDocument(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            document = new XWPFDocument(POIXMLDocument.openPackage(templatePath));
        }
        return getGenerateFirst();
    }

    public XWPFDocument getGenerateFirst(byte[] inByte) throws Exception {
        document = new XWPFDocument(new ByteArrayInputStream(inByte));
        return getGenerateFirst();
    }

    public XWPFDocument getGenerateFirst(XWPFDocument templateDocument) throws Exception {
        document = templateDocument;
        return getGenerateFirst();
    }

    private XWPFDocument getGenerateFirst() throws Exception {
        XWPFParagraph firstParagraph = document.createParagraph();
        setParagraphAlignInfo(firstParagraph, ParagraphAlignment.CENTER, null);
        XWPFRun firstRun = firstParagraph.createRun();
        firstRun.setText("第一部分 专用条款");
        firstRun.setFontSize(12);
        firstRun.setFontFamily("微软雅黑");
        firstRun.setBold(true);

        return document;
    }

    public XWPFDocument getGenerateLast(String templatePath) throws Exception {
        if (templatePath.toUpperCase().startsWith("HTTP")) {
            document = new XWPFDocument(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            document = new XWPFDocument(POIXMLDocument.openPackage(templatePath));
        }
        return getGenerateLast();
    }

    public XWPFDocument getGenerateLast(byte[] inByte) throws Exception {
        document = new XWPFDocument(new ByteArrayInputStream(inByte));
        return getGenerateLast();
    }

    public XWPFDocument getGenerateLast(XWPFDocument templateDocument) throws Exception {
        document = templateDocument;
        return getGenerateLast();
    }

    private XWPFDocument getGenerateLast() throws Exception {
        XWPFParagraph lastParagraph = document.createParagraph();
        setParagraphAlignInfo(lastParagraph, ParagraphAlignment.LEFT, null);
        XWPFRun lastRun = lastParagraph.createRun();
        lastRun.setText("（以下无正文）");
        lastRun.setFontSize(12);
        lastRun.addBreak(BreakType.PAGE);
        return document;
    }

    public XWPFDocument getPageBreaks(byte[] inByte) throws Exception {
        document = new XWPFDocument(new ByteArrayInputStream(inByte));
        return getPageBreaks();
    }

    public XWPFDocument getPageBreaks(XWPFDocument templateDocument) throws Exception {
        document = templateDocument;
        return getPageBreaks();
    }

    private XWPFDocument getPageBreaks() throws Exception {
        XWPFParagraph lastParagraph = document.createParagraph();
        setParagraphAlignInfo(lastParagraph, ParagraphAlignment.LEFT, null);
        XWPFRun lastRun = lastParagraph.createRun();
        lastRun.addBreak(BreakType.PAGE);
        return document;
    }

    public XWPFDocument getGenerateTitle(String templatePath, BigInteger numbering, String title) throws Exception {
        if (templatePath.toUpperCase().startsWith("HTTP")) {
            document = new XWPFDocument(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            document = new XWPFDocument(POIXMLDocument.openPackage(templatePath));
        }
        return getGenerateTitle(numbering, title);
    }

    public XWPFDocument getGenerateTitle(byte[] inByte, BigInteger numbering, String title) throws Exception {
        document = new XWPFDocument(new ByteArrayInputStream(inByte));
        return getGenerateTitle(numbering, title);
    }

    public XWPFDocument getGenerateTitle(XWPFDocument templateDocument, BigInteger numbering, String title) throws Exception {
        document = templateDocument;
        return getGenerateTitle(numbering, title);
    }

    private XWPFDocument getGenerateTitle(BigInteger numbering, String title) throws Exception {

        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setNumID(numbering);
        titleParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(0));
        setParagraphAlignInfo(titleParagraph, ParagraphAlignment.LEFT, null);
        setParagraphSpacingInfo(titleParagraph, true, null, null, "50", "50", true, "276", STLineSpacingRule.AUTO);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setFontSize(12);
        titleRun.setFontFamily("微软雅黑");
        titleRun.setBold(true);

        return document;
    }

    public XWPFDocument getGenerateContent(String templatePath, BigInteger numbering, String content, Boolean isNumber) throws Exception {
        if (templatePath.toUpperCase().startsWith("HTTP")) {
            document = new XWPFDocument(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            document = new XWPFDocument(POIXMLDocument.openPackage(templatePath));
        }
        return getGenerateContent(numbering, content, isNumber);
    }

    public XWPFDocument getGenerateContent(byte[] inByte, BigInteger numbering, String content, Boolean isNumber) throws Exception {
        document = new XWPFDocument(new ByteArrayInputStream(inByte));
        return getGenerateContent(numbering, content, isNumber);
    }

    public XWPFDocument getGenerateContent(XWPFDocument templateDocument, BigInteger numbering, String content, Boolean isNumber) throws Exception {
        document = templateDocument;
        return getGenerateContent(numbering, content, isNumber);
    }

    private XWPFDocument getGenerateContent(BigInteger numbering, String content, Boolean isNumber) {
        String[] stringList = content.split("\\n");

        if (stringList != null) {
            for (int i = 0; i < stringList.length; i++) {
                String string = stringList[i];

                XWPFParagraph contentParagraph;
                if (isNumber) {
                    contentParagraph = document.createParagraph();
                    contentParagraph.setNumID(numbering);
                    if (i == 0) {
                        contentParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(1));
                    } else {
                        if (string.contains("<SERIAL_NUMBER>")) {
                            string = string.replaceFirst("<SERIAL_NUMBER>", "");
                            contentParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(2));
                        } else {
                            contentParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(4));
                        }
                    }
                } else {
                    if (!string.contains("<PERIOD_CONTENT>")) {
                        continue;
                    }
                    contentParagraph = document.createParagraph();
                    contentParagraph.setNumID(numbering);
                    contentParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(4));
                }
                string = string.replaceFirst("<PERIOD_CONTENT>", "");

                setParagraphAlignInfo(contentParagraph, ParagraphAlignment.LEFT, null);
                setParagraphSpacingInfo(contentParagraph, true, null, null, "50", "50", true, "276", STLineSpacingRule.AUTO);

                Matcher matcher = Pattern.compile("\\s*<.*?>\\s*").matcher(string);
                Integer begin = 0;
                Integer end = 0;
                boolean isTrue = true;

                while (matcher.find()) {
                    isTrue = false;

                    String tag = matcher.group();
                    end = string.indexOf(tag);

                    String ind = "";
                    for (int n = 0; n < tag.length(); n++) {
                        ind += " ";
                    }

                    string = string.replaceFirst(tag, ind);

                    String text = string.substring(begin, end);

                    if (text != null && !"".equals(text)) {
                        XWPFRun contentRun = contentParagraph.createRun();
                        contentRun.setText(text);
                        contentRun.setFontSize(12);
                    }

                    begin = end + tag.length();
                    XWPFRun tagRun = contentParagraph.createRun();
                    tagRun.setText(tag);
                    tagRun.setFontSize(12);
                    CTUnderline ctUnderline = tagRun.getCTR().getRPr().addNewU();
                    ctUnderline.setVal(STUnderline.Enum.forInt(1));
                    ctUnderline.setColor("000000");

                }
                if (begin > 0) {
                    if (begin < string.length()) {
                        String text = string.substring(begin, string.length());
                        if (text != null && !"".equals(text)) {
                            XWPFRun contentRun = contentParagraph.createRun();
                            contentRun.setText(text);
                            contentRun.setFontSize(12);
                        }
                    }

                }

                if (isTrue) {

                    XWPFRun contentRun = contentParagraph.createRun();
                    contentRun.setText(string);
                    contentRun.setFontSize(12);
                }
            }
        }
        return document;
    }

    public XWPFDocument getGenerateAttachment(String templatePath, BigInteger numbering, String title, byte[] attachment, Boolean isAttachment) throws Exception {
        if (templatePath.toUpperCase().startsWith("HTTP")) {
            document = new XWPFDocument(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            document = new XWPFDocument(POIXMLDocument.openPackage(templatePath));
        }
        return getGenerateAttachment(numbering, title, attachment, isAttachment);
    }

    public XWPFDocument getGenerateAttachment(byte[] inByte, BigInteger numbering, String title, byte[] attachment, Boolean isAttachment) throws Exception {
        document = new XWPFDocument(new ByteArrayInputStream(inByte));
        return getGenerateAttachment(numbering, title, attachment, isAttachment);
    }

    public XWPFDocument getGenerateAttachment(XWPFDocument templateDocument, BigInteger numbering, String title, byte[] attachment, Boolean isAttachment) throws Exception {
        document = templateDocument;
        return getGenerateAttachment(numbering, title, attachment, isAttachment);
    }

    private XWPFDocument getGenerateAttachment(BigInteger numbering, String contract, byte[] attachment, Boolean isAttachment) throws Exception {

        //加载合并附件
        if (attachment != null) {

            if (contract != null && !"".equals(contract)) {

                XWPFParagraph contentParagraph = document.createParagraph();
                contentParagraph.setNumID(numbering);
                setParagraphSpacingInfo(contentParagraph, true, null, null, "50", "50", true, "276", STLineSpacingRule.AUTO);
                XWPFRun contentRun = contentParagraph.createRun();
                contentRun.setText(contract);
                contentRun.setFontSize(12);

                if (isAttachment != null && isAttachment) {
                    contentParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(0));
                    setParagraphAlignInfo(contentParagraph, ParagraphAlignment.CENTER, null);
                    contentRun.setFontFamily("微软雅黑");
                    contentRun.setBold(true);
                } else {
                    contentParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(1));
                    setParagraphAlignInfo(contentParagraph, ParagraphAlignment.LEFT, null);
                }

            }

            //合并附件
            Docx4JUtil docx4JUtil = new Docx4JUtil();
            byte[] bytes = docx4JUtil.mergeDocx(getDocument(document), attachment);
            document = new XWPFDocument(new ByteArrayInputStream(bytes));

        }

        return document;
    }

    public byte[] getDocument(XWPFDocument document) throws Exception {
        byte[] outByte;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        document.write(os);
        outByte = os.toByteArray();
        os.flush();
        os.close();

        return outByte;
    }

    public XWPFDocument getDocument(byte[] inByte) throws Exception {
        return new XWPFDocument(new ByteArrayInputStream(inByte));
    }

    public BigInteger getNumbering(XWPFDocument document) {
        SpecialUtil poiWordUtil = new SpecialUtil();
        BigInteger numbering = poiWordUtil.addNumbering(document);
        return numbering;
    }

    /**
     * @Description: 设置段落对齐
     */
    private void setParagraphAlignInfo(XWPFParagraph p,
                                       ParagraphAlignment pAlign, TextAlignment valign) {
        if (pAlign != null) {
            p.setAlignment(pAlign);
        }
        if (valign != null) {
            p.setVerticalAlignment(valign);
        }
    }

    /**
     * @Description: 设置段落间距信息, 一行=100 一磅=20
     */
    private void setParagraphSpacingInfo(XWPFParagraph p, boolean isSpace,
                                         String before, String after, String beforeLines, String afterLines,
                                         boolean isLine, String line, STLineSpacingRule.Enum lineValue) {
        CTPPr pPPr = getParagraphCTPPr(p);
        CTSpacing pSpacing = pPPr.getSpacing() != null ? pPPr.getSpacing() : pPPr.addNewSpacing();
        if (isSpace) {
            // 段前磅数
            if (before != null) {
                pSpacing.setBefore(new BigInteger(before));
            }
            // 段后磅数
            if (after != null) {
                pSpacing.setAfter(new BigInteger(after));
            }
            // 段前行数
            if (beforeLines != null) {
                pSpacing.setBeforeLines(new BigInteger(beforeLines));
            }
            // 段后行数
            if (afterLines != null) {
                pSpacing.setAfterLines(new BigInteger(afterLines));
            }
        }
        // 间距
        if (isLine) {
            if (line != null) {
                pSpacing.setLine(new BigInteger(line));
            }
            if (lineValue != null) {
                pSpacing.setLineRule(lineValue);
            }
        }
    }

    /**
     * @Description: 得到段落CTPPr
     */
    private CTPPr getParagraphCTPPr(XWPFParagraph p) {
        CTPPr pPPr = null;
        if (p.getCTP() != null) {
            if (p.getCTP().getPPr() != null) {
                pPPr = p.getCTP().getPPr();
            } else {
                pPPr = p.getCTP().addNewPPr();
            }
        }
        return pPPr;
    }

    /**
     * 创建编号
     *
     * @param doc
     * @return
     */
    private BigInteger addNumbering(XWPFDocument doc) {


        XWPFNumbering numbering = doc.createNumbering();
        // generate numbering style from XML
        CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();

        CTLvl[] cTLvl = new CTLvl[5];


        cTLvl[0] = addCTLvl(0, setCTRPr("微软雅黑", 24, true), "第%1条", 0, 975, 975);
        cTLvl[1] = addCTLvl(1, setCTRPr("Times New Roman", 24, false), "%1.%2", 0, 680, 680);
        cTLvl[2] = addCTLvl(2, setCTRPr("Times New Roman", 24, false), "   (%3)", 0, 630, 630);
        cTLvl[3] = addCTLvl(3, setCTRPr("Times New Roman", 24, false), "   %4.", 0, 630, 630);
        cTLvl[4] = addCTLvl(4, setCTRPr("Times New Roman", 24, false), "", 0, 680, 680);


        abstractNum.setLvlArray(cTLvl);
        //abstractNum.setLvlArray(0,addCTLvl(0,"%1.", 720, 360));
        //abstractNum.setLvlArray(1,addCTLvl(1,"%2.", 1440,360));

        XWPFAbstractNum abs = new XWPFAbstractNum(abstractNum, numbering);

        // find available id in document
        BigInteger id = BigInteger.valueOf(0);
        boolean found = false;
        while (!found) {
            Object o = numbering.getAbstractNum(id);
            found = (o == null);
            if (!found) id = id.add(BigInteger.ONE);
        }
        // assign id
        abs.getAbstractNum().setAbstractNumId(id);
        // add to numbering, should get back same id
        id = numbering.addAbstractNum(abs);
        // add to num list, result is numid
        return doc.getNumbering().addNum(id);
    }


    /**
     * 创建编号级别
     *
     * @param ilvl
     * @param ctrpr
     * @param levelText
     * @param left
     * @param hanging
     * @return
     */
    private CTLvl addCTLvl(int ilvl, CTRPr ctrpr, String levelText, int right, int left, int hanging) {
        CTLvl cTLvl = CTLvl.Factory.newInstance();

        //设置级别
        cTLvl.setIlvl(BigInteger.valueOf(ilvl));

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(1));

        cTLvl.setStart(indentNumber);

        //设置Fomat
        CTNumFmt ctNumFmt = CTNumFmt.Factory.newInstance();
        ctNumFmt.setVal(STNumberFormat.Enum.forInt(1));
        cTLvl.setNumFmt(ctNumFmt);

        //设置级别文本
        CTLevelText ctLevelText = CTLevelText.Factory.newInstance();
        ctLevelText.setVal(levelText);
        cTLvl.setLvlText(ctLevelText);

        CTJc ctJc = CTJc.Factory.newInstance();
        ctJc.setVal(STJc.Enum.forInt(1));
        cTLvl.setLvlJc(ctJc);

        CTPPr ctpPr = CTPPr.Factory.newInstance();

        CTInd ctInd = CTInd.Factory.newInstance();
        ctInd.setLeft(BigInteger.valueOf(left));
        ctInd.setHanging(BigInteger.valueOf(hanging));
        ctInd.setRight(BigInteger.valueOf(right));
        ctpPr.setInd(ctInd);


        cTLvl.setPPr(ctpPr);

        cTLvl.setRPr(ctrpr);

        return cTLvl;
    }

    /**
     * 设置文本
     *
     * @param fontFamily
     * @param fontSize
     * @param bold
     * @return
     */
    private CTRPr setCTRPr(String fontFamily, int fontSize, boolean bold) {
        CTRPr ctrpr = CTRPr.Factory.newInstance();

        CTFonts ctfonts = CTFonts.Factory.newInstance();
        ctfonts.setAscii(fontFamily);
        ctfonts.setHAnsi(fontFamily);
        ctfonts.setCs(fontFamily);
        ctfonts.setEastAsia(fontFamily);

        ctrpr.setRFonts(ctfonts);

        CTHpsMeasure cTHpsMeasure = CTHpsMeasure.Factory.newInstance();
        cTHpsMeasure.setVal(BigInteger.valueOf(fontSize));
        ctrpr.setSz(cTHpsMeasure);
        ctrpr.setSzCs(cTHpsMeasure);

        CTOnOff cTOnOff = CTOnOff.Factory.newInstance();
        cTOnOff.setVal(STOnOff.Enum.forInt(bold == true ? 1 : 2));

        ctrpr.setB(cTOnOff);
        ctrpr.setBCs(cTOnOff);

        return ctrpr;
    }

}
