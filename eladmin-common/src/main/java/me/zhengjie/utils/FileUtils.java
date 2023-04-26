package me.zhengjie.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

//文件上传的工具类
@Slf4j
public class FileUtils extends FileUtil {
    //Excel导出
    public static void exportExcel(List<?> list, Class<?> pojoClass ,String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(),pojoClass,list);
        if (workbook != null);
        downLoadExcel(fileName, response, workbook);
    }
    public static void exportExcel(ExportParams exportParams,List<?> list, Class<?> pojoClass ,String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        if (workbook != null);
        downLoadExcel(fileName, response, workbook);
    }
    public static void exportExcel(Workbook workbook , String fileName, HttpServletResponse response) {
        if (workbook != null);
        downLoadExcel(fileName, response, workbook);
    }
    //下载Excel
    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
    //导入Excel
    public static <T> List<T> importExcel(MultipartFile multipartFile, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcelBySax(multipartFile.getInputStream(), pojoClass, params);
        }catch (NoSuchElementException e){
            throw new RuntimeException("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return list;
    }

    //导入Excel
    public static <T> List<T> importExcel(MultipartFile multipartFile, Integer startSheetIndex, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setSheetNum(startSheetIndex);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcelBySax(multipartFile.getInputStream(), pojoClass, params);
        }catch (NoSuchElementException e){
            throw new RuntimeException("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return list;
    }

    public static List<Map<String, Object>> importMapExcel(MultipartFile multipartFile){
        ImportParams params = new ImportParams();
//        params.setDataHanlder(new MapImportHanlder());
        List<Map<String, Object>> list = null;
        try {
            list = ExcelImportUtil.importExcel(multipartFile.getInputStream(), Map.class, params);
        }catch (NoSuchElementException e){
            throw new BadRequestException("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return list;
    }

    public static List<Map<String, Object>> importMapExcelCsv(InputStream ins){
        List<Map<String, Object>> list = null;
        try {
            list = importExcelCsv(ins);
        }catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return list;
    }

    private static List<Map<String, Object>> importExcelCsv(InputStream ins) throws Exception{
        InputStreamReader reader=new InputStreamReader(ins);
        BufferedReader bufferedReader=new BufferedReader(reader);
        String line;
        String[]titles=null;
        List<Map<String,Object>>mapList=new ArrayList<>();
        while ((line=bufferedReader.readLine())!=null){
            String[]data=line.split(",");
            if (titles==null){
                titles=new String[data.length];
                for (int j = 0; j < data.length; j++) {
                    titles[j]=data[j];
                }
            }else {
                Map<String,Object>map=new LinkedHashMap<>();
                for (int j = 0; j < data.length; j++) {
                    if (data[j].matches("-?\\d+\\.?/?\\d*%?")){
                        map.put(titles[j],new BigDecimal(data[j]));
                    }else {
                        map.put(titles[j],data[j]);
                    }
                }
                mapList.add(map);
            }

        }
        return mapList;
    }

    public static String saveFile(MultipartFile multipartFile, String path, boolean withRealName) {
        String filename = multipartFile.getOriginalFilename();
        String filenamePrefix=filename.substring(0,filename.lastIndexOf('.')-1);
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (withRealName) {
            filename = path + "/" + filename;
        }else {
            filename = path + "/" + filenamePrefix+"_"+ UUID.randomUUID().toString() + suffix;
        }
        File file = new File(filename);
        if (isNotEmpty(file))
            del(file);
        try {
            writeFromStream(multipartFile.getInputStream(), file);
            return filename;
        } catch (IOException e) {
            log.error("文件上传出错:{}", e.getMessage());
            throw new BadRequestException("文件上传出错");
        }
    }

    public static void downLoadFile(String filePath, HttpServletRequest request, HttpServletResponse response) {
        File file = new File(filePath);
        if (file != null && file.isFile()) {
            response.setHeader("Accept-Ranges", "bytes");
            String fn = file.getName();
            response.setHeader("Content-disposition", "attachment; " + encodeFileName(request, fn));
            response.setContentType("application/octet-stream");
            if (StringUtil.isBlank(request.getHeader("Range"))) {
                normalDownLoad(file, response);
            } else {
                rangeDownLoad(file, request, response);
            }
        }else {
            log.error("下载文件路径不存在：{}", filePath);
            throw new BadRequestException("下载文件路径不存在");
        }
    }

    private static void rangeDownLoad(File file, HttpServletRequest request, HttpServletResponse response) {
        Long[] range = {null, null};
        processRange(range, request, file);

        String contentLength = String.valueOf(range[1].longValue() - range[0].longValue() + 1);
        response.setHeader("Content-Length", contentLength);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);	// status = 206

        // Content-Range: bytes 0-499/10000
        StringBuilder contentRange = new StringBuilder("bytes ").append(String.valueOf(range[0])).append("-").append(String.valueOf(range[1])).append("/").append(String.valueOf(file.length()));
        response.setHeader("Content-Range", contentRange.toString());

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            long start = range[0];
            long end = range[1];
            inputStream = new BufferedInputStream(new FileInputStream(file));
            if (inputStream.skip(start) != start)
                throw new RuntimeException("File skip error");
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            long position = start;
            for (int len; position <= end && (len = inputStream.read(buffer)) != -1;) {
                if (position + len <= end) {
                    outputStream.write(buffer, 0, len);
                    position += len;
                } else {
                    for (int i=0; i<len && position <= end; i++) {
                        outputStream.write(buffer[i]);
                        position++;
                    }
                }
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null)
                try {inputStream.close();} catch (IOException e) {log.error(e.getMessage(), e);}
            if (outputStream != null)
                try {outputStream.close();} catch (IOException e) {log.error(e.getMessage(), e);}
        }
    }


    private static void normalDownLoad(File file, HttpServletResponse response) {
        response.setHeader("Content-Length", String.valueOf(file.length()));
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null)
                try {inputStream.close();} catch (IOException e) {log.error(e.getMessage(), e);}
            if (outputStream != null)
                try {outputStream.close();} catch (IOException e) {log.error(e.getMessage(), e);}
        }
    }


    private static String encodeFileName(HttpServletRequest request, String fileName) {
        String userAgent = request.getHeader("User-Agent");
        try {
            String encodedFileName = URLEncoder.encode(fileName, "UTF8");
            // 如果没有UA，则默认使用IE的方式进行编码
            if (userAgent == null) {
                return "filename=\"" + encodedFileName + "\"";
            }

            userAgent = userAgent.toLowerCase();
            // IE浏览器，只能采用URLEncoder编码
            if (userAgent.indexOf("msie") != -1) {
                return "filename=\"" + encodedFileName + "\"";
            }

            // Opera浏览器只能采用filename*
            if (userAgent.indexOf("opera") != -1) {
                return "filename*=UTF-8''" + encodedFileName;
            }

            // Safari浏览器，只能采用ISO编码的中文输出,Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
            if (userAgent.indexOf("safari") != -1 || userAgent.indexOf("applewebkit") != -1 || userAgent.indexOf("chrome") != -1) {
                return "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
            }

            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            if (userAgent.indexOf("mozilla") != -1) {
                return "filename*=UTF-8''" + encodedFileName;
            }

            return "filename=\"" + encodedFileName + "\"";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processRange(Long[] range, HttpServletRequest request, File file) {
        String rangeStr = request.getHeader("Range");
        int index = rangeStr.indexOf(',');
        if (index != -1)
            rangeStr = rangeStr.substring(0, index);
        rangeStr = rangeStr.replace("bytes=", "");

        String[] arr = rangeStr.split("-", 2);
        if (arr.length < 2)
            throw new RuntimeException("Range error");

        long fileLength = file.length();
        for (int i=0; i<range.length; i++) {
            if (StringUtil.isNotBlank(arr[i])) {
                range[i] = Long.parseLong(arr[i].trim());
                if (range[i] >= fileLength)
                    range[i] = fileLength - 1;
            }
        }

        // Range format like: 9500-
        if (range[0] != null && range[1] == null) {
            range[1] = fileLength - 1;
        }
        // Range format like: -500
        else if (range[0] == null && range[1] != null) {
            range[0] = fileLength - range[1];
            range[1] = fileLength - 1;
        }

        // check final range
        if (range[0] == null || range[1] == null || range[0].longValue() > range[1].longValue())
            throw new RuntimeException("Range error");
    }

    public static String getSuffix(File file) {
        if (file==null)
            return null;
        if (file.isDirectory()){
            return null;
        }if (file.getName().lastIndexOf('.')==-1){
            return null;
        }
        return file.getName().substring(file.getName().lastIndexOf('.'));
    }
}
