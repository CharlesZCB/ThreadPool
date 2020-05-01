package com.cn;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 多线程下 资源抢占 和 CPU利用率的问题
 */
public class MyThread {
    private  int ticket = 4000;//初始化 容量值
    Lock lock = new ReentrantLock();
    public   void   consume(){
        this.lock.lock();//加锁
        System.out.println(Thread.currentThread().getName() + ":" + ticket);
        this.ticket = --ticket;//业务操作
        System.out.println(Thread.currentThread().getName() + ":" + ticket);
        this.lock.unlock();//解锁
    }
}
class  Test1{
    public static void main(String[] args) {
        System.err.println(Thread.currentThread().getName());
        MyThread myThread = new MyThread();
        ExecutorService executorService = Executors.newFixedThreadPool(5);//创建固定数量的线程池
        Thread th = new Thread(new Runnable() {  //线程中需要做的任务（需要调用的接口）
            @Override
            public void run() {
                myThread.consume();
            }
        });
        for (int i= 0;i< 4000 ; i++){  //循环执行多线程 完成业务逻辑
            try {
               /* Thread.sleep(10);*/  //加上 睡眠时间 可以有效的提高CPU的利用率（如果循环次数很多的话，减少不必要的线程开销）
                //当然如果没有那么多的 循环次数 不加上我觉得也可以啊<_<
                executorService.execute(th);  //调用线程池的执行方法
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();//关闭线程池
    }
}