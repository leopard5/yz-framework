package com.yz.framework.dfs;

import org.csource.common.IniFileReader;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class FdfsClient {
    private static Logger logger = LoggerFactory.getLogger(FdfsClient.class);
    private static TrackerClient tracker;
    private static String urlPrefix;

    static {
        try {
            String classPath = FdfsClient.class.getClassLoader().getResource("").getPath();
            String configPath = classPath + "fdfs_client.conf";
            ClientGlobal.init(configPath);
            IniFileReader iniReader = new IniFileReader(configPath);
            urlPrefix = iniReader.getStrValue("http.url.prefix") != null ? iniReader.getStrValue("http.url.prefix") : "";
            logger.info("network_timeout={}ms", ClientGlobal.g_network_timeout);
            logger.info("charset={}");
            tracker = new TrackerClient();
        } catch (Exception e) {
            logger.error("FDFS初始化异常", e);
        }
    }

    private static NameValuePair[] createMetaData(Map<String, String> map) {
        NameValuePair[] nameValuePairs = new NameValuePair[map.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            nameValuePairs[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }
        return nameValuePairs;
    }

    /**
     * 上传本地文件到fdfs,它是通过流的形式上传,所以可以用于大文件的上传.
     *
     * @param local_filename 本地文件
     * @param file_ext_name  文件扩展名
     * @param metaData       文件元数据,可以为null
     */
    public String upload(String local_filename, String file_ext_name, Map<String, String> metaData) {
        String fileId = null;
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            trackerServer = tracker.getConnection();
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            if (metaData != null && metaData.size() > 0) {
                fileId = client.upload_file1(local_filename, file_ext_name, createMetaData(metaData));
            } else {
                fileId = client.upload_file1(local_filename, file_ext_name, null);
            }

            logger.info("upload success. file id is: {}", fileId);
        } catch (Exception e) {
            logger.error("FDFS上传异常", e);
        } finally {
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
                if (storageServer != null) {
                    storageServer.close();
                }
            } catch (IOException e) {
                logger.error("Tracker Server Connection关闭异常", e);
            }
        }
        return urlPrefix + fileId;
    }

    /**
     * 上传byte流到fastdfs,由于byte要一次全部加载在内存,所以只适用于小文件.
     *
     * @param file_bytes    文件byte数组
     * @param file_ext_name 文件扩展名
     * @param metaData      文件元数据,可以为null
     */
    public String upload(byte[] file_bytes, String file_ext_name, Map<String, String> metaData) {
        String fileId = null;
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            trackerServer = tracker.getConnection();
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            if (metaData != null && metaData.size() > 0) {
                fileId = client.upload_file1(file_bytes, file_ext_name, createMetaData(metaData));
            } else {
                fileId = client.upload_file1(file_bytes, file_ext_name, null);
            }
            logger.info("upload success. file id is: {}", fileId);
        } catch (Exception e) {
            logger.error("FDFS上传异常", e);
        } finally {
            //此处不关闭storage server的原因是它的API已经帮我们进行了关闭
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                logger.error("Tracker Server Connection关闭异常", e);
            }
        }
        return urlPrefix + fileId;
    }

    /**
     * 流式上传,适合前端到后端不需要落盘的上传.
     *
     * @param file_size     文件长度
     * @param file_ext_name 扩展名
     * @param inputStream   输入流
     * @param metaData      文件元数据,可以为null
     */
    public String upload(long file_size, String file_ext_name, InputStream inputStream, Map<String, String> metaData) {
        String fileId = null;
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            trackerServer = tracker.getConnection();
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);


            if (metaData != null && metaData.size() > 0) {
                fileId = client.upload_file1(null, file_size, new UploadStream(inputStream, file_size), file_ext_name, createMetaData(metaData));
            } else {
                fileId = client.upload_file1(null, file_size, new UploadStream(inputStream, file_size), file_ext_name, null);
            }
            logger.info("upload success. file id is: {}", fileId);
        } catch (Exception e) {
            logger.error("FDFS上传异常", e);
        } finally {
            //此处不关闭storage server的原因是它的API已经帮我们进行了关闭
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                logger.error("Tracker Server Connection关闭异常", e);
            }
        }
        return urlPrefix + fileId;
    }

    /**
     * 下载文件
     *
     * @param fileId 不带URL前缀的文件ID
     */
    public byte[] download(String fileId) {
        byte[] fileByte = null;
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            trackerServer = tracker.getConnection();
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            fileByte = client.download_file1(fileId);
            logger.info("download success. file id is: {}", fileId);
        } catch (Exception e) {
            logger.error("FDFS下载异常", e);
        } finally {
            //此处不关闭storage server的原因是它的API已经帮我们进行了关闭
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                logger.error("Tracker Server Connection关闭异常", e);
            }
        }
        return fileByte;
    }

    /**
     * 下载文件,输出到指定的OutputStream
     *
     * @param fileId       不带URL前缀的文件ID
     * @param outputStream 指定的输出流
     */
    public void download(String fileId, OutputStream outputStream) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            trackerServer = tracker.getConnection();
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            client.download_file1(fileId, new DownloadStream(outputStream));
            logger.info("download success. file id is: {}", fileId);
        } catch (Exception e) {
            logger.error("FDFS下载异常", e);
        } finally {
            //此处不关闭storage server的原因是它的API已经帮我们进行了关闭
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                logger.error("Tracker Server Connection关闭异常", e);
            }
        }
    }
}
