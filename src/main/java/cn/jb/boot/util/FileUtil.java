package cn.jb.boot.util;


import cn.jb.boot.framework.exception.CavException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static cn.jb.boot.framework.com.Constants.SPOT;

/**
 * 文件操作封装
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-04-30 下午 02:55
 **/
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 文件格式和 Extension 对应关系
     **/
    private static final Map<String, String> MIME_TYPE = new HashMap<>();

    /**
     * TIKA
     **/
    private static final TikaConfig CONFIG = TikaConfig.getDefaultConfig();
    private static final Tika TIKA = new Tika(CONFIG);

    /**
     * 下载指定资源
     *
     * @param response HttpServletResponse
     * @param fileName 文件名
     * @param bs       输出的字节
     * @param consumer 执行方法
     */
    public static void download(HttpServletResponse response, String fileName, byte[] bs, Consumer<BufferedOutputStream> consumer) {
        setResponse(response, fileName, bs);
        try (OutputStream os = response.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(os)) {
            if (bs != null) {
                bos.write(bs);
            }
            if (consumer != null) {
                consumer.accept(bos);
            }
        } catch (Exception e) {
            log.error("下载资源错误。", e);
            throw new CavException("下载资源错误");
        }
    }

    public static MultipartFile getFile(HttpServletRequest request) {
        MultipartHttpServletRequest mrequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = mrequest.getFiles("uploadFiles");
        if (CollectionUtils.isEmpty(files)) {
            throw new CavException("上传文件为空!!!");
        }
        return files.get(0);
    }

    /**
     * 设置 response
     *
     * @param response response
     * @param fileName 文件名
     * @param bs       要输出的流
     */
    public static void setResponse(HttpServletResponse response, String fileName, byte[] bs) {
        String charset = StandardCharsets.UTF_8.name();
        response.reset();
        response.setCharacterEncoding(charset);
        response.setHeader(HttpHeaders.CONTENT_ENCODING, charset);
        response.setContentType(fileType(fileName));
        if (bs != null) {
            response.setIntHeader(HttpHeaders.CONTENT_LENGTH, bs.length);
        }
        try {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileName, charset));
        } catch (Exception ignored) {
        }
    }

    /**
     * 将MultipartFile 转为文件
     * <p>执行使用file的方法
     * <p>最后需要删除临时文件 或者应用退出后自动删除
     *
     * @param multipartFile MultipartFile
     * @return File
     */
    public static File multipartFileToFile(MultipartFile multipartFile) {
        if (multipartFile == null) {
            return null;
        }
        File file;
        try {
            file = File.createTempFile("multipart_file", multipartFile.getOriginalFilename());
            file.deleteOnExit();
            FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    /**
     * 将文件转为 MultipartFile
     *
     * @param file 文件
     * @return MultipartFile
     */
    public static MultipartFile multipartFile(File file) {
        if (file == null) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return multipartFile(fis, file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CavException("BIZ000100028");
        }
    }

    /**
     * 将文件流转为 MultipartFile
     *
     * @param is       文件里
     * @param fileName 文件名
     * @return MultipartFile
     */
    public static MultipartFile multipartFile(InputStream is, String fileName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem fileItem = factory.createItem(fileName, fileType(fileName), true, fileName);
        try (OutputStream os = fileItem.getOutputStream()) {
            writeBuffer(is, os);
            return new CommonsMultipartFile(fileItem);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CavException("BIZ000100028");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 缓存写流
     *
     * @param is 输入流
     * @param os 输出流
     * @throws IOException 异常
     */
    public static void writeBuffer(InputStream is, OutputStream os) throws IOException {
        int bytesRead;
        int cacheLength = 8192;
        byte[] buffer = new byte[cacheLength];
        while ((bytesRead = is.read(buffer, 0, cacheLength)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
    }

    /**
     * 获取文件类型 不读取流的方式 直接通过文件名称
     *
     * @param fileName 文件名称
     * @return 文件类型
     */
    public static String fileType(String fileName) {
        Optional<MediaType> optional = MediaTypeFactory.getMediaType(fileName);
        return optional.orElse(MediaType.TEXT_PLAIN).toString();
    }

    /**
     * 判断上传文件类型
     *
     * @param file   文件
     * @param arrays 其他的文件后缀
     * @return 文件类型
     */
    public static boolean judgeFile(MultipartFile file, String... arrays) {
        return judgeFile(multipartFileToFile(file), arrays);
    }

    /**
     * 获取文件类型 流未关闭
     *
     * @param file 文件
     * @return 文件类型
     */
    public static String fileType(File file) {
        if (file == null || file.isDirectory()) {
            return null;
        }
        String fileType;
        try {
            fileType = TIKA.detect(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (MIME_TYPE.isEmpty()) {
            initMimeType();
        }
        return fileType;
    }


    /**
     * 通过文件流判断 文件格式和后缀是否一致
     *
     * @param file   文件
     * @param arrays 其他的文件后缀
     * @return 是否一致
     */
    public static boolean judgeFile(File file, String... arrays) {
        if (ArrayUtils.isEmpty(arrays)) {
            return false;
        }
        String fileType = fileType(file);
        String extension = MIME_TYPE.get(fileType);
        if (StringUtils.isBlank(extension)) {
            log.error("无法找到对应的文件类型，文件类型为：{}", fileType);
            return false;
        }
        for (String ext : arrays) {
            if (!StringUtils.startsWith(ext, SPOT)) {
                ext = SPOT + ext;
            }
            if (StringUtils.equalsIgnoreCase(extension, ext)) {
                return true;
            }
        }
        log.error("解析出来的文件格式为：{}，要判断的文件格式为：{}", extension, arrays);
        return false;
    }


    /**
     * 初始化 MimeType 的map
     */
    private static void initMimeType() {
        MimeTypes types = CONFIG.getMimeRepository();
        Object o = PojoUtil.getProperty(types, "types");
        Map<org.apache.tika.mime.MediaType, MimeType> map = PojoUtil.cast(o);
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<org.apache.tika.mime.MediaType, MimeType> e : map.entrySet()) {
                MIME_TYPE.put(e.getValue().toString(), e.getValue().getExtension());
            }
        }
    }
}