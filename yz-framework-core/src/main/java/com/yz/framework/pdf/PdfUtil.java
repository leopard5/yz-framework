package com.yz.framework.pdf;

import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.CloseVetoException;

/**
 * @author ABeam GDC
 */
public class PdfUtil {

    /**
     * convertType
     */
    private static final String convertType = "writer_pdf_Export";
    /**
     * OpenOffice/LibreOffice Home
     * Win64:
     * OpenOffice:
     * C:/Program Files (x86)/OpenOffice 4/program/
     * LibreOffice:
     * C:/Program Files (x86)/LibreOffice 5/program/
     * Unix/Linux:
     * LibreOffice:
     * /usr/xxx/xxxx
     */
    private String oooExeFolder = "";

    //private static Logger logger = LoggerFactory.getLogger(PdfUtil.class);

    /**
     * creIns4WOpenOffice
     *
     * @return instance for Windows OpenOffice
     */
    public static PdfUtil creIns4WOpenOffice() {
        PdfUtil inst = new PdfUtil();
        inst.oooExeFolder = "C:/Program Files (x86)/OpenOffice 4/program/";
        return inst;
    }

    /**
     * creIns4WLibreOffice
     *
     * @return instance for Windows LibreOffice
     */
    public static PdfUtil creIns4WLibreOffice() {
        PdfUtil inst = new PdfUtil();
        inst.oooExeFolder = "C:/Program Files (x86)/LibreOffice 5/program/";
        return inst;
    }

    /**
     * creIns4ULibreOffice
     *
     * @return instance for Unix/Linux LibreOffice
     */
    public static PdfUtil creIns4ULibreOffice() {
        PdfUtil inst = new PdfUtil();
        inst.oooExeFolder = "/usr/xxx/xxx";// todo Linux path
        return inst;
    }

    /**
     * creIns4MsOffice
     *
     * @return 返回
     */
    public static PdfUtil creIns4MsOffice() {
        return new PdfUtil();
    }

    /**
     * generatePDFUsingMsWord
     *
     * @param iFileFullPath iFileFullPath
     * @param oFileFullPath oFileFullPath
     * @return 返回
     */
    public boolean generatePDFUsingMsWord(String iFileFullPath, String oFileFullPath) {
        //logger.debug("cgeneratePDFUsingMsWord ...");

        final String cmdpath = "C:\\PdfConverterForOffice14.exe \"%s\" \"%s\"";

        String cmd = String.format(cmdpath, iFileFullPath, oFileFullPath);

        boolean result = false;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();

            int extVal = process.exitValue();

            //logger.debug("cgeneratePDFUsingMsWord ...SUCCESS");
//logger.debug("cgeneratePDFUsingMsWord ...FAILED");
            result = extVal == 0;
        } catch (InterruptedException e) {
            //logger.debug("cgeneratePDFUsingMsWord ...FAILED" + e);
        } catch (java.io.IOException e) {
            //logger.debug("cgeneratePDFUsingMsWord ...FAILED" + e);
        }

        return result;
    }

    /**
     * generatePDF
     *
     * @param iFileFullPath iFileFullPath
     * @param oFileFullPath oFileFullPath
     * @return 返回
     */
    public boolean generatePDF(String iFileFullPath, String oFileFullPath) {
        try {
            XComponentContext context = createContext();
            //logger.debug("connected to a running office ...");

            XComponentLoader compLoader = createLoader(context);
            //logger.debug("loader created ...");

            Object doc = loadDocument(compLoader, iFileFullPath);
            //logger.debug("document loaded ...");

            convertDocument(doc, oFileFullPath, convertType);
            //logger.debug("document converted ...");

            closeDocument(doc);
            //logger.debug("document closed ...");

        } catch (CloseVetoException e) {
            // closeDocument
            //logger.info("Method closeDocument throws exception.");

        } catch (IOException e) {
            // loadDocument
            //logger.info("Method loadDocument throws exception.");

        } catch (IllegalArgumentException e) {
            // convertDocument loadDocument
            //logger.info("Method convertDocument/loadDocument throws exception.");

        } catch (BootstrapException e) {
            // createContext
            //logger.info("Method createContext throws exception.");

        } catch (Exception e) {
            // createLoader
            //logger.info("Method createLoader throws exception.");

        }
        return false;

    }

    /**
     * createContext
     *
     * @return 返回
     * @throws BootstrapException BootstrapException
     */
    private XComponentContext createContext() throws BootstrapException {
        // get the remote office component context
        // return Bootstrap.bootstrap();
        return BootstrapSocketConnector.bootstrap(oooExeFolder);
    }

    /**
     * createLoader
     *
     * @param xContext xContext
     * @return 返回
     * @throws Exception Exception
     */
    private XComponentLoader createLoader(XComponentContext xContext) throws Exception {
        // get the remote office service manager
        com.sun.star.lang.XMultiComponentFactory xMCF = xContext.getServiceManager();
        Object oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);
        return UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);
    }

    /**
     * loadDocument
     *
     * @param compLoader    compLoader
     * @param inputFilePath inputFilePath
     * @return 返回
     * @throws IOException              IOException
     * @throws IllegalArgumentException IllegalArgumentException
     */
    private Object loadDocument(XComponentLoader compLoader, String inputFilePath) throws IOException, IllegalArgumentException {
        // Composing the URL by replacing all backslashes
        String sUrl = "file:///" + inputFilePath.replace('\\', '/');

        // Loading the wanted document
        com.sun.star.beans.PropertyValue propertyValues[] = new com.sun.star.beans.PropertyValue[1];
        propertyValues[0] = new com.sun.star.beans.PropertyValue();
        propertyValues[0].Name = "Hidden";
        propertyValues[0].Value = Boolean.TRUE;

        Object oDocToStore = null;
        try {
            oDocToStore = compLoader.loadComponentFromURL(sUrl, "_blank", 0, propertyValues);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return oDocToStore;
    }

    /**
     * convertDocument
     *
     * @param doc            doc
     * @param outputFilePath outputFilePath
     * @param convertType    convertType
     * @throws IOException IOException
     */
    private void convertDocument(Object doc, String outputFilePath, String convertType) throws IOException {
        // Getting an object that will offer a simple way to store a document to a URL.
        com.sun.star.frame.XStorable xStorable = UnoRuntime.queryInterface(com.sun.star.frame.XStorable.class, doc);

        // Preparing properties for converting the document
        com.sun.star.beans.PropertyValue[] propertyValues = new com.sun.star.beans.PropertyValue[2];

        // Setting the flag for overwriting
        propertyValues[0] = new com.sun.star.beans.PropertyValue();
        propertyValues[0].Name = "Overwrite";
        propertyValues[0].Value = Boolean.TRUE;
        // Setting the filter name
        propertyValues[1] = new com.sun.star.beans.PropertyValue();
        propertyValues[1].Name = "FilterName";
        propertyValues[1].Value = convertType;

        // Storing and converting the document
        String outputUrl = "file:///" + outputFilePath.replace('\\', '/');
        xStorable.storeToURL(outputUrl, propertyValues);

    }

    /**
     * closeDocument
     *
     * @param doc doc
     * @throws CloseVetoException CloseVetoException
     */
    private void closeDocument(Object doc) throws CloseVetoException {
        // Closing the converted document. Use XCloseable.clsoe if the interface is supported, otherwise use XComponent.dispose
        com.sun.star.util.XCloseable xCloseable = UnoRuntime.queryInterface(com.sun.star.util.XCloseable.class, doc);

        if (xCloseable != null) {
            xCloseable.close(false);
        } else {
            com.sun.star.lang.XComponent xComp = UnoRuntime.queryInterface(com.sun.star.lang.XComponent.class, doc);
            xComp.dispose();
        }
    }
}
