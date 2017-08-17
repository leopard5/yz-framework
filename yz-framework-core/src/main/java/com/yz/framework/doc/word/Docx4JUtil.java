package com.yz.framework.doc.word;

import com.yz.framework.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.org.apache.poi.util.IOUtils;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.soap.Text;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Docx4JUtil {

    private static final String IMAGE_ = "IMAGE_";
    private static final String TABLE_ON_SN_ = "TABLE_ON_SN_";
    private static final String TABLE_OFF_SN_ = "TABLE_OFF_SN_";
    private static final String EL_LEFT = "<";
    private static final String EL_RIGHT = ">";

    /**
     * 搜索文本对象
     *
     * @param obj      obj
     * @param toSearch toSearch
     * @return 返回
     */
    private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<>();
        if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

        if (obj.getClass().equals(toSearch))
            result.add(obj);
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }

        }
        return result;
    }

    /**
     * 添加表格
     *
     * @param reviewtable  reviewtable
     * @param templateRow  templateRow
     * @param replacements replacements
     */
    private static void addRowToTable(Tbl reviewtable, Tr templateRow, Map<String, String> replacements) {
        Tr workingRow = XmlUtils.deepCopy(templateRow);
        List<?> textElements = getAllElementFromObject(workingRow, Text.class);
        for (Object object : textElements) {
            Text text = (Text) object;
            String replacementValue = replacements.get(text.getValue());
            if (replacementValue != null)
                text.setValue(replacementValue);
        }

        reviewtable.getContent().add(workingRow);
    }

    /**
     * Docx4j拥有一个由字节数组创建图片部件的工具方法, 随后将其添加到给定的包中. 为了能将图片添加
     * 到一个段落中, 我们需要将图片转换成内联对象. 这也有一个方法, 方法需要文件名提示, 替换文本,
     * 两个id标识符和一个是嵌入还是链接到的指示作为参数.
     * 一个id用于文档中绘图对象不可见的属性, 另一个id用于图片本身不可见的绘制属性. 最后我们将内联
     * 对象添加到段落中并将段落添加到包的主文档部件.
     *
     * @param wordMLPackage 要添加图片的包
     * @param bytes         图片对应的字节数组
     * @throws Exception 不幸的createImageInline方法抛出一个异常(没有更多具体的异常类型)
     */

    public static void addImageToPackage(WordprocessingMLPackage wordMLPackage,
                                         byte[] bytes) throws Exception {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

        int docPrId = 1;
        int cNvPrId = 2;
        Inline inline = imagePart.createImageInline("Filename hint", "Alternative text", docPrId, cNvPrId, false);

        P paragraph = addInlineImageToParagraph(inline);

        wordMLPackage.getMainDocumentPart().addObject(paragraph);
    }

    /**
     * 创建一个对象工厂并用它创建一个段落和一个可运行块R.
     * 然后将可运行块添加到段落中. 接下来创建一个图画并将其添加到可运行块R中. 最后我们将内联
     * 对象添加到图画中并返回段落对象.
     *
     * @param inline 包含图片的内联对象.
     * @return 包含图片的段落
     */
    private static P addInlineImageToParagraph(Inline inline) {
        // 添加内联对象到一个段落中
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        paragraph.getContent().add(run);
        Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return paragraph;
    }

    /**
     * 将图片从文件对象转换成字节数组.
     *
     * @param file 将要转换的文件
     * @return 包含图片字节数据的字节数组
     */
    public static byte[] convertImageToByteArray(File file)
            throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        // 不能使用long类型创建数组, 需要用int类型.
        if (length > Integer.MAX_VALUE) {
            System.out.println("File too large!!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确认所有的字节都没读取
        if (offset < bytes.length) {
            System.out.println("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    /**
     * 合并文件
     *
     * @param streams streams
     * @return 返回
     * @throws Docx4JException Docx4JException
     * @throws IOException     IOException
     */
    public byte[] mergeDocx(final List<byte[]> streams) throws Docx4JException, IOException {

        WordprocessingMLPackage target = null;
//        File generated = File.createTempFile("generated", ".docx");

        String chunkId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        for (byte[] is : streams) {
            if (is != null) {
                if (target == null) {

                    // Copy first (master) document
//                    FileOutputStream os = new FileOutputStream(generated);
//                    os.write(is);
//                    os.flush();
//                    os.close();
//                    tempOs.write(is);

//                    FileInputStream in = new FileInputStream(generated);
//                    target = WordprocessingMLPackage.load(new ByteArrayInputStream(in));
//                    in.close();

                    target = WordprocessingMLPackage.load(new ByteArrayInputStream(is));
                } else {
                    // Attach the others (Alternative input parts)

                    insertDocx(target.getMainDocumentPart(), is, chunkId);
//                    insertDocx(target.getMainDocumentPart(), is, chunkId++);
                }
            }
        }

        if (target != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] outByte;

            target.save(os);
            outByte = os.toByteArray();
            os.flush();
            os.close();

//            boolean isDelete = generated.delete();
//
//            System.out.println(generated.getCanonicalPath() + ":" + isDelete);

            return outByte;

        } else {
            return null;
        }
    }

    public byte[] mergeDocx(byte[] inByte, String templatePath) throws Exception {

        byte[] lastByte = null;
        if (templatePath.toUpperCase().startsWith("HTTP")) {
            lastByte = FileUtil.getHttpFileUrl(templatePath);
        } else {
            lastByte = FileUtil.toByteArray(templatePath);
        }

        return mergeDocx(inByte, lastByte);
    }

    public byte[] mergeDocx(byte[] inByte, byte[] outByte) throws Exception {
//        Docx4JUtil docx4JUtil = new Docx4JUtil();
        List<byte[]> list = new ArrayList<>();
        list.add(inByte);
        list.add(outByte);
        return mergeDocx(list);
    }

    /**
     * 插入文档
     *
     * @param main    main
     * @param bytes   bytes
     * @param chunkId chunkId
     */
    private void insertDocx(MainDocumentPart main, byte[] bytes, String chunkId) {
        try {
            AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/part" + chunkId + ".docx"));
//            afiPart.setContentType(new ContentType(CONTENT_TYPE));
            afiPart.setBinaryData(bytes);
            Relationship altChunkRel = main.addTargetPart(afiPart);
            CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
            chunk.setId(altChunkRel.getId());
            main.addObject(chunk);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * docx文档转换为PDF
     *
     * @throws Exception 可能为Docx4JException, IOException等
     */
    public byte[] convertDocxToPDF(byte[] intByte) throws Exception {

        byte[] outByte;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(intByte);
        try {
            WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(in);
            //           Mapper fontMapper = new BestMatchingMapper();
            Mapper fontMapper = new IdentityPlusMapper();
            fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
            fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
            fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
            mlPackage.setFontMapper(fontMapper);

            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setWmlPackage(mlPackage);
            Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
            outByte = os.toByteArray();

            os.flush();
            os.close();
            in.close();

            return outByte;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os);
        }

        return null;
    }

    /**
     * 把docx转成html
     *
     * @param docxFilePath docxFilePath
     * @param htmlPath     htmlPath
     * @throws Exception Exception
     */
    public void convertDocxToHtml(String docxFilePath, String htmlPath) throws Exception {

        WordprocessingMLPackage wordMLPackage = Docx4J.load(new File(docxFilePath));

        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        String imageFilePath = htmlPath.substring(0, htmlPath.lastIndexOf("/") + 1) + "/images";
        htmlSettings.setImageDirPath(imageFilePath);
        htmlSettings.setImageTargetUri("images");
        htmlSettings.setWmlPackage(wordMLPackage);

        String userCSS = "html, body, div, span, h1, h2, h3, h4, h5, h6, p, a, img,  ol, ul, li, table, caption, tbody, tfoot, thead, tr, th, td " +
                "{ margin: 0; padding: 0; border: 0;}" +
                "body {line-height: 1;} ";

        htmlSettings.setUserCSS(userCSS);

        OutputStream os;

        os = new FileOutputStream(htmlPath);

        Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML", true);

        Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

    }

    /**
     * 模板替换主入口
     *
     * @param templatePath templatePath
     * @param map          map
     * @return 返回
     * @throws InvalidFormatException InvalidFormatException
     * @throws IOException            IOException
     * @throws Docx4JException        Docx4JException
     */
    public byte[] generateWordFromTemplate(String templatePath, Map<String, Object> map) throws InvalidFormatException, IOException, Docx4JException {
        byte[] out;

        WordprocessingMLPackage template = getTemplate(templatePath);

        for (Map.Entry<String, Object> entry : map.entrySet()) {

            String paramName = EL_LEFT + entry.getKey() + EL_RIGHT;

            replacePlaceholder(template, paramName, String.valueOf(entry.getValue()));

        }

        out = writeDocxToStream(template);
        return out;

    }

    /**
     * 加载模板文件
     *
     * @param templatePath templatePath
     * @return 返回
     * @throws Docx4JException Docx4JException
     * @throws IOException     IOException
     */
    private WordprocessingMLPackage getTemplate(String templatePath) throws Docx4JException, IOException {
        WordprocessingMLPackage template;
        if (templatePath.toUpperCase().startsWith("HTTP")) {
            template = WordprocessingMLPackage.load(new ByteArrayInputStream(FileUtil.getHttpFileUrl(templatePath)));
        } else {
            template = WordprocessingMLPackage.load(new FileInputStream(new File(templatePath)));
        }
        return template;
    }

    /**
     * 输出模板文件
     *
     * @param template template
     * @return 返回
     * @throws IOException     IOException
     * @throws Docx4JException Docx4JException
     */
    private byte[] writeDocxToStream(WordprocessingMLPackage template) throws IOException, Docx4JException {
        byte[] out;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        template.save(os);
        out = os.toByteArray();
        os.flush();
        os.close();
        return out;
    }

    /**
     * 全局替换
     *
     * @param template    template
     * @param paramName   paramName
     * @param placeholder placeholder
     */
    private void replacePlaceholder(WordprocessingMLPackage template, String paramName, String placeholder) {

        List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

        int beginIndex = 0;
        int contentIndex = 0;
        int endIndex = 0;
        String compareText = "";
        for (int i = 0; i < texts.size(); i++) {
            Object text = texts.get(i);
            Text textElement = (Text) text;

            if (textElement.getValue().contains(EL_LEFT)) {
                beginIndex = i;
                contentIndex = 0;
                endIndex = 0;
//                System.out.println("开始:" + textElement.getValue());
            }
            if (beginIndex > 0) {
                contentIndex = i;
                compareText += textElement.getValue();
            }
            if (beginIndex > 0 && textElement.getValue().contains(EL_RIGHT)) {
                endIndex = i;
//                System.out.println("结尾:" + textElement.getValue());
            }

            if (beginIndex > 0 && contentIndex > 0 && endIndex > 0) {

                if (beginIndex == contentIndex && contentIndex == endIndex) {

                    System.out.println("一行:" + textElement.getValue());

                    String value = "";
                    value = ((Text) texts.get(contentIndex)).getValue();
                    ((Text) texts.get(contentIndex)).setValue(value.replace(EL_LEFT, ""));

                    value = ((Text) texts.get(contentIndex)).getValue();
                    ((Text) texts.get(contentIndex)).setValue(value.replace(EL_RIGHT, ""));

                } else {
                    System.out.println("拼接:" + compareText);

                    if (paramName.equals(compareText.trim())) {
                        ((Text) texts.get(beginIndex)).setValue("");
                        ((Text) texts.get(contentIndex)).setValue("");
                        ((Text) texts.get(endIndex)).setValue("");
                    }
                }

//                System.out.println(compareText);
                beginIndex = 0;
                contentIndex = 0;
                endIndex = 0;
                compareText = "";
            }
        }
    }

//    public static void main(String[] args) throws Exception {
//
//        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
//
//        File file = new File("src/main/resources/iProfsLogo.png");
//        byte[] bytes = convertImageToByteArray(file);
//        addImageToPackage(wordMLPackage, bytes);
//
//        wordMLPackage.save(new java.io.File("src/main/files/HelloWord7.docx"));
//
//    }

    /**
     * 文本替换添加行
     *
     * @param placeholder placeholder
     * @param textToAdd   textToAdd
     * @param template    template
     * @param addTo       addTo
     */
    public void replaceParagraph(String placeholder, String textToAdd, WordprocessingMLPackage template, ContentAccessor addTo) {
        // 1. get the paragraph
        List<Object> paragraphs = getAllElementFromObject(template.getMainDocumentPart(), P.class);

        P toReplace = null;
        for (Object p : paragraphs) {
            List<Object> texts = getAllElementFromObject(p, Text.class);
            for (Object t : texts) {
                Text content = (Text) t;
                if (content.getValue().equals(placeholder)) {
                    toReplace = (P) p;
                    break;
                }
            }
        }

        // we now have the paragraph that contains our placeholder: toReplace
        // 2. split into seperate lines
        String as[] = StringUtils.splitPreserveAllTokens(textToAdd, '\n');

        for (String ptext : as) {
            // 3. copy the found paragraph to keep styling correct
            P copy = XmlUtils.deepCopy(toReplace);

            // replace the text elements from the copy
            List<?> texts = getAllElementFromObject(copy, Text.class);
            if (texts.size() > 0) {
                Text textToReplace = (Text) texts.get(0);
                textToReplace.setValue(ptext);
            }

            // add the paragraph to the document
            addTo.getContent().add(copy);
        }

        // 4. remove the original one
        if (null != toReplace) {
            ((ContentAccessor) toReplace.getParent()).getContent().remove(toReplace);
        }

    }

    /**
     * 表格替换
     *
     * @param placeholders placeholders
     * @param textToAdd    textToAdd
     * @param template     template
     * @throws Docx4JException Docx4JException
     * @throws JAXBException   JAXBException
     */
    public void replaceTable(String[] placeholders, List<Map<String, String>> textToAdd,
                             WordprocessingMLPackage template) throws Docx4JException, JAXBException {
        List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

        // 1. find the table
        Tbl tempTable = getTemplateTable(tables, placeholders[0]);
        if (tempTable == null) {
            return;
        }
        List<Object> rows = getAllElementFromObject(tempTable, Tr.class);

        // first row is header, second row is content
        if (rows.size() == 2) {
            // this is our template row
            Tr templateRow = (Tr) rows.get(1);

            for (Map<String, String> replacements : textToAdd) {
                // 2 and 3 are done in this method
                addRowToTable(tempTable, templateRow, replacements);
            }

            // 4. remove the template row
            tempTable.getContent().remove(templateRow);
        }
    }

    /**
     * 获取模板表格
     *
     * @param tables      tables
     * @param templateKey templateKey
     * @return 返回
     * @throws Docx4JException Docx4JException
     * @throws JAXBException   JAXBException
     */
    private Tbl getTemplateTable(List<Object> tables, String templateKey) throws Docx4JException, JAXBException {
        for (Object tbl : tables) {
            List<?> textElements = getAllElementFromObject(tbl, Text.class);
            for (Object text : textElements) {
                Text textElement = (Text) text;
                if (textElement.getValue() != null && textElement.getValue().equals(templateKey))
                    return (Tbl) tbl;
            }
        }
        return null;
    }

}
