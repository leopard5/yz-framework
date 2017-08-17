package com.yz.framework.util;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文本文件读写
 *
 * @author yazhong.qi
 */
public abstract class FileUtil {
    /**
     * 使用nio方式进行文件写入，适合大文件
     *
     * @param str
     * @param fileName
     * @throws IOException
     * @throws Exception
     */
    public static void writeUseNio(String fileName, String str)
            throws IOException {
        writeUseNio(fileName, str.getBytes());
    }

    /**
     * 使用nio方式进行文件写入，适合大文件
     *
     * @param data
     * @param fileName
     * @throws IOException
     */
    public static void writeUseNio(String fileName, byte[] data)
            throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
            FileChannel fileChannel = fileOutputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.wrap(data);
            fileChannel.write(buffer);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
                fileOutputStream = null;
            }
        }
    }

    /**
     * 使用nio方式进行文件写入，适合大文件
     *
     * @param data
     * @param fileName
     * @throws IOException
     */
    public static void writeUseNioWithLock(String fileName, byte[] data)
            throws IOException {
        synchronized (fileName.intern()) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(fileName);
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.lock();
                ByteBuffer buffer = ByteBuffer.wrap(data);
                fileChannel.write(buffer);
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    fileOutputStream = null;
                }
            }
        }
    }

    public static void writeUseNioWithLock(String fileName, String data)
            throws IOException {
        writeUseNioWithLock(fileName, data.getBytes());
    }

    /**
     * 直接用FileOutputStream进行文件写入
     *
     * @param fileName
     * @param data
     * @throws IOException
     */
    public static void writeUseBio(String fileName, byte[] data)
            throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(data);
            fos.flush();
        } finally {
            if (fos != null) {
                fos.close();
                fos = null;
            }
        }
    }

    /**
     * 直接用FileOutputStream进行文件写入
     *
     * @param str
     * @param fileName
     * @throws IOException
     */
    public static void writeUseBio(String fileName, String str)
            throws IOException {
        writeUseBio(fileName, str.getBytes());
    }

    public static void writeLong(String fileName, long l) throws IOException {
        writeUseBio(fileName, String.valueOf(l));
    }

    public static String readUseBio(String fileName) throws IOException {
        BufferedReader reader = null;
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
            reader = new BufferedReader(fr);
            String tempString = null;
            StringBuilder sb = new StringBuilder();
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString);
            }
            return sb.toString();

        } finally {
            if (fr != null) {
                fr.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static long readLong(String fileName) throws IOException {
        String str = readUseBio(fileName);
        if (str != null && str.length() > 0) {
            return Long.valueOf(str);
        }
        return 0;
    }

    public static String readStringUseNio(String fileName) throws IOException {
        byte[] data = readUseNio(fileName);
        return new String(data);
    }

    public static byte[] readUseNio(String fileName) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(fileName, "r");
            FileChannel fileChannel = raf.getChannel();
            int size = (int) fileChannel.size();
            ByteBuffer buffer = ByteBuffer.allocateDirect(size);
            fileChannel.read(buffer);
            byte[] dst = new byte[size];
            buffer.flip();
            buffer.get(dst);
            return dst;
        } finally {
            if (raf != null) {
                raf.close();
                raf = null;
            }
        }
    }

    public static byte[] readUseNioWithShareLock(String fileName)
            throws IOException {
        synchronized (fileName.intern()) {// 同一JVM不能对同一文件进行加锁
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(fileName, "r");
                FileChannel fileChannel = raf.getChannel();
                fileChannel.lock(0, Long.MAX_VALUE, true);
                int size = (int) fileChannel.size();
                ByteBuffer buffer = ByteBuffer.allocateDirect(size);
                fileChannel.read(buffer);
                byte[] dst = new byte[size];
                buffer.flip();
                buffer.get(dst);
                return dst;

            } finally {
                if (raf != null) {
                    raf.close();
                    raf = null;
                }
            }
        }
    }

    /**
     * 创建文件目录
     *
     * @param first
     * @param more
     * @throws IOException
     */
    public static void createDirectories(String first, String... more)
            throws IOException {
        createDirectories(Paths.get(first, more));
    }

    /**
     * 创建文件
     *
     * @param first
     * @param more
     * @throws IOException
     */
    public static void createFile(String first, String... more)
            throws IOException {
        Files.createFile(Paths.get(first, more));
    }

    /**
     * 创建文件目录
     *
     * @param path
     * @throws IOException
     */
    public static void createDirectories(Path path) throws IOException {
        if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectories(path);
        }
    }

    /**
     * 创建文件
     *
     * @param file
     * @throws IOException
     */
    public static void createFile(Path file) throws IOException {
        if (Files.notExists(file, LinkOption.NOFOLLOW_LINKS)) {
            Path directory = file.getParent();
            if (directory != null) {
                createDirectories(directory);
            }
            Files.createFile(file);
        }
    }

    /**
     * 根据class path读取资源内容
     *
     * @param classPath
     * @return
     * @throws IOException
     */
    public static String getResourceContent(String classPath)
            throws IOException {
        byte[] data = getClassPathResourceData(classPath);
        if (data != null) {
            return new String(data);
        }
        return null;
    }

    /**
     * 根据class path读取资源内容
     *
     * @param classPath
     * @return
     * @throws IOException
     */
    public static byte[] getClassPathResourceData(String classPath)
            throws IOException {
        InputStream inputStream = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource(
                    classPath);
            inputStream = classPathResource.getInputStream();
            int length = inputStream.available();
            byte[] data = new byte[length];
            inputStream.read(data, 0, length);
            return data;
        } finally {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
        }
    }

    /**
     * 向文件添加内容
     *
     * @param fileName
     * @param data
     * @throws IOException void
     * @throws
     */
    public static void appendToFile(String fileName, String data)
            throws IOException {
        FileOutputStream file = null;
        DataOutputStream out = null;
        try {
            file = new FileOutputStream(fileName, true);
            out = new DataOutputStream(file);

            out.writeBytes(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                file.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 二进制拷贝
     *
     * @param input
     * @param output
     * @throws IOException void
     */
    public static void binaryCopy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buf = new byte[1024];
        int n;
        while ((n = input.read(buf)) != -1) {
            output.write(buf, 0, n);
        }
    }

    /**
     * 删除目录
     *
     * @param dir
     * @return boolean
     * @throws
     */
    public static boolean delete(File dir) {
        if (!dir.exists()) {
            return true;
        }
        boolean success = true;

        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    if (!delete(files[i])) {
                        success = false;
                    }
                }
                if (!files[i].delete()) {
                    success = false;
                }
            }
        }
        if (!dir.delete()) {
            success = false;
        }
        return success;
    }

    /**
     * 删除文件
     *
     * @param dirname
     * @return boolean
     * @throws
     */
    public static boolean delete(String dirname) {
        return delete(new File(dirname));
    }

    /**
     * 输入流变String
     *
     * @param is
     * @return
     * @throws IOException String
     */
    public static String inputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * 写文件
     *
     * @param fileName      完整文件名(类似：/usr/a/b/c/d.txt)
     * @param contentBytes  文件内容的字节数组
     * @param autoCreateDir 目录不存在时，是否自动创建(多级)目录
     * @param autoOverwrite 目标文件存在时，是否自动覆盖
     * @return
     * @throws IOException
     */
    public static boolean write(String fileName, byte[] contentBytes, boolean autoCreateDir, boolean autoOverwrite)
            throws IOException {
        boolean result = false;
        if (autoCreateDir) {
            createDirs(fileName);
        }
        if (autoOverwrite) {
            delete(fileName);
        }
        File f = new File(fileName);
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(f);
            fs.write(contentBytes);
            fs.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                fs.close();
            }
        }
        return result;
    }

    /**
     * 创建(多级)目录
     *
     * @param filePath 完整的文件名(类似：/usr/a/b/c/d.xml)
     */
    public static void createDirs(String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

    }


    /**
     * 创建目录
     *
     * @param destDirName 目标目录名
     * @return 目录创建成功返回true，否则返回false
     */
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        // 创建单个目录
        return dir.mkdirs();
    }

    /**
     * 删除文件
     *
     * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
     *                        String
     */
    public static void delFile(String filePathAndName) {
        try {
            File myDelFile = new File(filePathAndName);
            if (!myDelFile.delete()) {
                System.out.println("删除文件失败");
            }

        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 读取到字节数组
     *
     * @param filePath //路径
     */
    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

    /**
     * 读取到字节数组1
     */
    public static byte[] toByteArray(String filePath) throws IOException {

        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }
        BufferedInputStream in = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length())) {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 读取到字节数组2
     */
    public static byte[] toByteArray2(String filePath) throws IOException {

        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
//            while ((channel.read(byteBuffer)) > 0) {
//                // do nothing
//                // System.out.println("reading");
//            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
     */
    public static byte[] toByteArray3(String filePath) throws IOException {

        FileChannel fc = null;
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(filePath, "r");
            fc = rf.getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size()).load();
            //System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                // System.out.println("remain");
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (rf != null) {
                    rf.close();
                }
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把字节数组写入文件
     */
    public static void writeByteArrToFile(byte[] bfile, String fileFullPath) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try {
            file = new File(fileFullPath);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取网络文件
     */
    public static byte[] getHttpFileUrl(String templateUrl) throws IOException {

        byte[] outByte;

        //打开网络输入流
        DataInputStream dis = new DataInputStream(new URL(templateUrl).openStream());
        ByteArrayOutputStream fos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;
        //开始填充数据
        while ((length = dis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }

        outByte = fos.toByteArray();

        dis.close();
        fos.close();
        return outByte;
    }

}
