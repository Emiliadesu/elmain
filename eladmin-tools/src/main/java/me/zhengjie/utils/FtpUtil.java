package me.zhengjie.utils;
import me.zhengjie.exception.BadRequestException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.*;
import java.nio.charset.StandardCharsets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FtpUtil {

    private static Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    // 服务器IP地址
    private static String ip = "10.0.2.101";
    // 用户名
    private static String username = "flftp";
    // 密码
    private static String password = "fuli@123";
    // 端口号
    private static int port = 21; // 修改ftp端口
    // ftp客户端
    //private static FTPClient ftpClient;


    public static void main(String[] args) throws Exception {
        FTPClient ftpClient=connect(ip,port);
        ftpClient.disconnect();
    }

    /**
     * 测试是否能连接
     * @return 返回真则能连接
     */
    public static FTPClient connect(String ip,int port) {
        try {
            FTPClient ftpClient = new FTPClient();
            //ftp初始化的一些参数
            ftpClient.connect(ip, port);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("UTF-8");
            if (ftpClient.login(username, password)) {
                logger.info("连接ftp成功");
                return ftpClient;
            } else {
                logger.error("连接ftp失败，可能用户名或密码错误!");
                try {
                    disconnect(ftpClient);
                } catch (Exception e) {
                    logger.error("ftp关闭失败！", e);
                }
                throw new BadRequestException("ftp连接失败，可能用户名密码错误");
            }
        } catch (IOException e) {
            logger.error("连接失败，可能ip或端口错误", e);
            throw new BadRequestException("ftp地址和端口有误");
        }
    }

    /**
     * 上传
     *
     * @param //workingPath 服务器的工作目
     * @param localFile 本地要上传的文件
     * @param fileName    设置上传之后的文件名
     * @return
     */
    public static boolean upload(File localFile, String fileName) throws FileNotFoundException {
        //2 检查工作目录是否存在
        if (localFile.exists()){
            throw new BadRequestException("文件已存在");
        }
        InputStream fileInputStream = new FileInputStream(localFile);
        return upload(fileInputStream,fileName,"");
    }
    public static boolean upload(InputStream fileInputStream, String fileName,String uploadPath){
        boolean flag = false;
        BufferedInputStream bufIns=new BufferedInputStream(fileInputStream);
        //1 测试连接
        FTPClient ftpClient = connect(ip, port);
        if (ftpClient!=null) {
            try {
                if (!exists(ftpClient,uploadPath)){
                    //不存在目录，创建
                    forceMkdir(ftpClient,uploadPath);
                    ftpClient.changeWorkingDirectory(uploadPath);//改变工作目录
                }
                // Use passive mode to pass firewalls.
                ftpClient.enterLocalPassiveMode();

                ftpClient.setRemoteVerificationEnabled(false);
                //设置为被动模式
                ftpClient.enterLocalPassiveMode();
                //设置上传文件的类型为二进制类型
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                //传输文件为流的形式
//                ftpClient.setFileTransferMode(ftpClient.STREAM_TRANSFER_MODE);



                // 3 检查是否上传成功
                if (storeFile(new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1), bufIns,ftpClient)) {
                    flag = true;
                    disconnect(ftpClient);
                }
            } catch (Exception e) {
                logger.error("工作目录不存在", e);
                disconnect(ftpClient);
            }
        }
        return flag;
    }

    /**
     * 上传文件
     *
     * @param saveName        ftp上要保存的文件名
     * @param fileInputStream 要上传的文件流
     * @return
     */
    public static boolean storeFile(String saveName, InputStream fileInputStream,FTPClient ftpClient) {
        boolean flag = false;
        try {
            ftpClient.setRemoteVerificationEnabled(false);
            if (ftpClient.storeFile(saveName, fileInputStream)) {
                flag = true;
                logger.info("{}文件上传成功", saveName);
            }
        } catch (IOException e) {
            logger.error("{}文件上传失败", saveName, e);
        }
        return flag;
    }
    /**
     * @MethodName: forceMkdir
     * @Description: 创建文件夹
     * @Param: [remotePath]
     * @Return: boolean
     * @Author: zheng
     * @Date: 2019/12/10
     **/
    public static boolean forceMkdir(FTPClient ftpClient,String remotePath) throws Exception {
        boolean res = false;
        if (ftpClient!=null) {
            res = ftpClient.makeDirectory(remotePath);
        }
        return res;
    }

    /**
     * @MethodName: exists
     * @Description: 判断路径是否存在
     * @Param: [remotePath]
     * @Return: boolean
     * @Author: zheng
     * @Date: 2019/12/10
     **/
    public static boolean exists(FTPClient ftpClient,String remotePath) {
        boolean isExists = true;
        try {
            if (ftpClient!=null) {
                isExists = ftpClient.changeWorkingDirectory(remotePath);
            }
        } catch (Exception e) {
            isExists = false;
        }
        return isExists;
    }

    /**
     * 断开连接
     *
     * @param ftpClient
     * @throws Exception
     */
    public static void disconnect(FTPClient ftpClient) {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
                logger.info("已关闭连接");
            } catch (IOException e) {
                logger.error("没有关闭连接", e);
            }
        }
    }

    /** * 下载文件 *
     * @param fileName 文件名称 *
     * @param localPath 下载后的文件路径 *
     * @return */
    public static boolean downloadFile(String fileName, String localPath,String FTPpath) throws Exception{
        boolean flag = false;
        OutputStream os=null;
        FTPClient ftpClient=connect(ip, port);
        try {
            System.out.println("开始下载文件");
            //切换ftp目录
//            ftpClient.changeWorkingDirectory(pathName);
            ftpClient.setRemoteVerificationEnabled(false);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for(FTPFile file : ftpFiles){
                if(fileName.equalsIgnoreCase(file.getName())){
                    File localFile = new File(localPath + "/" + file.getName());
                    os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftpClient.logout();
            flag = true;
            System.out.println("下载文件成功");
        } catch (Exception e) {
            System.out.println("下载文件失败");
            e.printStackTrace();
        } finally{
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }


}
