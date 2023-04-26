package me.zhengjie.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author y
 * @create 2018-01-19 0:13
 * @desc
 **/
public class ExcelReaderUtil {
    //excel2003扩展名
    public static final String EXCEL03_EXTENSION = ".xls";
    //excel2007扩展名
    public static final String EXCEL07_EXTENSION = ".xlsx";

//    public static void sendRows(String filePath, String sheetName, int sheetIndex, int curRow, List<String> cellList) {
//        StringBuffer oneLineSb = new StringBuffer();
//        oneLineSb.append(filePath);
//        oneLineSb.append("--");
//        oneLineSb.append("sheet" + sheetIndex);
//        oneLineSb.append("::" + sheetName);//加上sheet名
//        oneLineSb.append("--");
//        oneLineSb.append("row" + curRow);
//        oneLineSb.append("::");
//        for (String cell : cellList) {
//            oneLineSb.append(cell.trim());
//            oneLineSb.append("|");
//        }
//        String oneLine = oneLineSb.toString();
//        if (oneLine.endsWith("|")) {
//            oneLine = oneLine.substring(0, oneLine.lastIndexOf("|"));
//        }// 去除最后一个分隔符
//
//        System.out.println(oneLine);
//    }

    public static List<Object> sendRowData(Object obj) {
        List<Object> list = new ArrayList<>();
        list.add(obj);
        return list;
    }

    public static List<Object>  readExcel(MultipartFile file, List<String> keys, Class pojoClass) {
        ExcelXlsxReader excelXlsxReader = new ExcelXlsxReader();
        List<Object> results = new ArrayList<>();
        try {
            results = excelXlsxReader.process(file.getInputStream(), keys, pojoClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static void copyToTemp(File file,String tmpDir) throws Exception{
        FileInputStream fis=new FileInputStream(file);
        File file1=new File(tmpDir);
        if (file1.exists()){
            file1.delete();
        }
        FileOutputStream fos=new FileOutputStream(tmpDir);
        byte[] b=new byte[1024];
        int n=0;
        while ((n=fis.read(b))!=-1){
            fos.write(b,0,n);
        }
        fis.close();
        fos.close();
    }

    public static void main(String[] args) throws Exception {
        String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\Test_0_10_H_20180125_Cto_Process_1834.xlsx";
        /*String path="/test/m05177new";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\1010filesCollection5000100";*/
        //H_20180111_Base_Date(4)_0420.xlsx
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\REWORK\\H_20180105_Cto_REWORK_1600.xlsx";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\ASS_ITEM\\H_20180116_ASS_ITEM_1915.xlsx";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\Cto_Process\\H_20180116_Cto_Process_2000.xlsx";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\Cto_Ship\\H_20180105_Cto_Ship_0005.xlsx";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\Po_sn\\H_20180105_Po_sn_1020.xlsx";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\Cto_CODE\\H_20180109_Cto_CODE_1640.xlsx";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\ASS_PRODUCT\\H_20171226_ASS_PRODUCT_0430 - 副本.xlsx";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\MAC\\H_20180108_MAC_2130.xlsx";*/
        /*String path = "C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\Base_Data3\\H_20180106_Base_Data3_1520 - 副本.xlsx";*/
        /*String path="C:\\Users\\y15079\\Desktop\\shenjiangnan\\TestSample\\H_20171226_ASS_PRODUCT_0430.xls";*/

        /*copyToTemp(file2,"/home/test/tmp.xlsx");*/

        /*ExcelReaderUtil.readExcel(file2.getAbsolutePath(),"/home/test/tmp.xlsx");*/
//        ExcelReaderUtil.readExcel(path);
        /*readXlsx(file2.getAbsolutePath());*/

    }
}
