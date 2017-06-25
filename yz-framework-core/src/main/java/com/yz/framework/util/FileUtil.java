package com.yz.framework.util;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.ByteBuffer;
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
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String tempString = null;
            StringBuilder sb = new StringBuilder();
            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString);
            }
            return sb.toString();

        } finally {
            if (reader != null) {
                reader.close();
                reader = null;
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
        FileOutputStream file = new FileOutputStream(fileName, true);
        DataOutputStream out = new DataOutputStream(file);
        out.writeBytes(data);
        out.flush();
        out.close();
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
     * @return boolean
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

        FileOutputStream fs = new FileOutputStream(f);
        fs.write(contentBytes);
        fs.flush();
        fs.close();
        result = true;
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
}
