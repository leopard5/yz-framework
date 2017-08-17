package com.yz.framework.pdf;

import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.connection.ConnectionSetupException;
import com.sun.star.connection.NoConnectException;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.yz.framework.pdf.server.OOoServer;

/**
 * A bootstrap connector which establishes a connection to an OOo server.
 * <p>
 * Most of the source code in this class has been taken from the Java class
 * "Bootstrap.java" (Revision: 1.15) from the UDK projekt (Uno Software Develop-
 * ment Kit) from OpenOffice.org (http://udk.openoffice.org/). The source code
 * is available for example through a browser based online version control
 * access at http://udk.openoffice.org/source/browse/udk/. The Java class
 * "Bootstrap.java" is there available at
 * http://udk.openoffice.org/source/browse/udk/javaunohelper/com/sun/star/comp/helper/Bootstrap.java?view=markup
 * <p>
 * The idea to develop this BootstrapConnector comes from the blog "Getting
 * started with the OpenOffice.org API part III : starting OpenOffice.org with
 * jars not in the OOo install dir by Wouter van Reeven"
 * (http://technology.amis.nl/blog/?p=1284) and from various posts in the
 * "(Unofficial) OpenOffice.org Forum" at http://www.oooforum.org/ and the
 * "OpenOffice.org Community Forum" at http://user.services.openoffice.org/
 * complaining about "no office executable found!".
 */
public class BootstrapConnector {

    /**
     * The OOo server.
     */
    private OOoServer oooServer;

    /**
     * The connection string which has ben used to establish the connection.
     */
    private String oooConnectionString;

    /**
     * Constructs a bootstrap connector which uses the folder of the OOo
     * installation containing the soffice executable.
     *
     * @param oooExecFolder The folder of the OOo installation containing the soffice executable
     */
    BootstrapConnector(String oooExecFolder) {

        this.oooServer = new OOoServer(oooExecFolder);
        this.oooConnectionString = null;
    }

    /**
     * Constructs a bootstrap connector which connects to the specified
     * OOo server.
     *
     * @param oooServer The OOo server
     */
    BootstrapConnector(OOoServer oooServer) {

        this.oooServer = oooServer;
        this.oooConnectionString = null;
    }

    /**
     * Bootstraps a connection to an OOo server in the specified soffice
     * executable folder of the OOo installation using the specified accept
     * option and connection string and returns a component context for using
     * the connection to the OOo server.
     * <p>
     * The accept option and the connection string should match in connection
     * type and pipe name or host and port to get a connection.
     *
     * @param oooExecFolder       The folder of the OOo installation containing the soffice executable
     * @param oooAcceptOption     The accept option
     * @param oooConnectionString The connection string
     * @return The component context
     */
    public static XComponentContext bootstrap(String oooExecFolder, String oooAcceptOption, String oooConnectionString) throws BootstrapException {

        BootstrapConnector bootstrapConnector = new BootstrapConnector(oooExecFolder);
        return bootstrapConnector.connect(oooAcceptOption, oooConnectionString);
    }

    /**
     * Connects to an OOo server using the specified accept option and
     * connection string and returns a component context for using the
     * connection to the OOo server.
     * <p>
     * The accept option and the connection string should match to get a
     * connection. OOo provides to different types of connections:
     * 1) The socket connection
     * 2) The named pipe connection
     * <p>
     * To create a socket connection a host and port must be provided.
     * For example using the host "localhost" and the port "8100" the
     * accept option and connection string looks like this:
     * - accept option    : -accept=socket,host=localhost,port=8100;urp;
     * - connection string: uno:socket,host=localhost,port=8100;urp;StarOffice.ComponentContext
     * <p>
     * To create a named pipe a pipe name must be provided. For example using
     * the pipe name "oooPipe" the accept option and connection string looks
     * like this:
     * - accept option    : -accept=pipe,name=oooPipe;urp;
     * - connection string: uno:pipe,name=oooPipe;urp;StarOffice.ComponentContext
     *
     * @param oooAcceptOption     The accept option
     * @param oooConnectionString The connection string
     * @return The component context
     */
    XComponentContext connect(String oooAcceptOption, String oooConnectionString) throws BootstrapException {

        this.oooConnectionString = oooConnectionString;

        XComponentContext xContext;
        try {
            // get local context
            XComponentContext xLocalContext = getLocalContext();

            oooServer.start(oooAcceptOption);

            // initial service manager
            XMultiComponentFactory xLocalServiceManager = xLocalContext.getServiceManager();
            if (xLocalServiceManager == null)
                throw new BootstrapException("no initial service manager!");

            // create a URL resolver
            XUnoUrlResolver xUrlResolver = UnoUrlResolver.create(xLocalContext);

            // wait until office is started
            for (int i = 0; ; ++i) {
                try {
                    xContext = getRemoteContext(xUrlResolver);
                    break;
                } catch (NoConnectException ex) {
                    // Wait 500 ms, then try to connect again, but do not wait
                    // longer than 5 min (= 600 * 500 ms) total:
                    if (i == 600) {
                        throw new BootstrapException(ex.toString());
                    }
                    Thread.sleep(500);
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new BootstrapException(e);
        }
        return xContext;
    }

    /**
     * Disconnects from an OOo server using the connection string from the
     * previous connect.
     * <p>
     * If there has been no previous connect, the disconnects does nothing.
     * <p>
     * If there has been a previous connect, disconnect tries to terminate
     * the OOo server and kills the OOo server process the connect started.
     */
    public void disconnect() {

        if (oooConnectionString == null)
            return;

        // call office to terminate itself
        try {
            // get local context
            XComponentContext xLocalContext = getLocalContext();

            // create a URL resolver
            XUnoUrlResolver xUrlResolver = UnoUrlResolver.create(xLocalContext);

            // get remote context
            XComponentContext xRemoteContext = getRemoteContext(xUrlResolver);

            // get desktop to terminate office
            Object desktop = xRemoteContext.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", xRemoteContext);
            XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
            xDesktop.terminate();
        } catch (Exception e) {
            // Bad luck, unable to terminate office
            e.printStackTrace();
        }

        oooServer.kill();
        oooConnectionString = null;
    }

    /**
     * Create default local component context.
     *
     * @return The default local component context
     */
    private XComponentContext getLocalContext() throws Exception {

        return Bootstrap.createInitialComponentContext(null);
    }

    /**
     * Try to connect to office.
     *
     * @return The remote component context
     */
    private XComponentContext getRemoteContext(XUnoUrlResolver xUrlResolver) throws BootstrapException, ConnectionSetupException, IllegalArgumentException, NoConnectException {

        Object context = null;
        try {
            context = xUrlResolver.resolve(oooConnectionString);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        XComponentContext xContext = UnoRuntime.queryInterface(XComponentContext.class, context);
        if (xContext == null) {
            throw new BootstrapException("no component context!");
        }
        return xContext;
    }
}