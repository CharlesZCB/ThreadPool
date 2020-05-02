package com.cn;

import com.mysql.jdbc.StringUtils;
import com.util.Conn;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程  测试文件上传
 */
public class Fileup {
    private int number = 1000;
    Lock lock = new ReentrantLock();

    static void copyFile (String src, String dec) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        //为了提高效率，设置缓存数组！（读取的字节数据会暂存放到该字节数组中）
        byte[] buffer = new byte[1024];
        int temp = 0;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dec);
            //边读边写
            //temp指的是本次读取的真实长度，temp等于-1时表示读取结束
            while ((temp = fis.read(buffer)) != -1) {
                /*将缓存数组中的数据写入文件中，注意：写入的是读取的真实长度；
                 *如果使用fos.write(buffer)方法，那么写入的长度将会是1024，即缓存
                 *数组的长度*/
                fos.write(buffer, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //两个流需要分别关闭
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  void uploadfile(String path,int num){
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(int i = 0 ;i < num ; i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String name = UUID.randomUUID().toString();
                        String savePath = "D:\\a\\image\\a";
                        File file = new File(savePath);
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        //创建拷贝文件
                        copyFile(path,savePath+"\\"+UUID.randomUUID().toString()+".jpg");
                        lock.lock();
                        number = -- number;
                        lock.unlock();
                        System.out.println(Thread.currentThread().getName() + ":" + savePath + ":" + number);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            executorService.execute(thread);
        }
        executorService.shutdown();
    }

    //链接url下载图片
    private static void downloadPicture(String urlList) {
        URL url = null;
        int imageNumber = 0;
        try {
            url = new URL("http:" + urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());;
            String savePath = "D:\\a\\image\\a";
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdir();
            }
            String imageName =  savePath + "\\" + UUID.randomUUID().toString() +".jpg";
            FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            byte[] context=output.toByteArray();
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //网络图片下载
    public void downNetPic(){
        Conn conn = new Conn();
        List<String > list = conn.conn();
        //多线程操作下载网络图片
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            for(String string : list){
                if (StringUtils.isEmptyOrWhitespaceOnly(string))
                    continue;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            downloadPicture(string);
                            System.out.println(Thread.currentThread().getName()+ ":" + string);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                executorService.execute(thread);
            }
        }finally {
            executorService.shutdown();
        }
    }

    public static void main(String[] args) {
            Fileup fileup = new Fileup();
    //        fileup.uploadfile("C:\\Users\\ThinkPad\\Pictures\\Camera Roll\\1.jpg",100);
        fileup.downNetPic();
    }
}
