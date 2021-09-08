package com.itdom.snowflake;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 一句话的功能说明
 * <p>
 * 雪花算法的代码实现
 *
 * @author administer
 * @date 2021/9/8
 * @since 1.0.0
 */
public class IdWorker {
    //时间其实标记点，作为基准，一般取系统的最近时间(一旦确定不能改动)
    private final static long twepoch = 1631081298564L;
    //机器标识位数
    private final static long workIdBits = 5L;
    //数据中心标识位数
    private final static long datacenterIdBits = 5L;
    // 机器ID最大值
    private final static long maxWorkerId=-1L^(-1L<<workIdBits);
    //数据中心ID最大值
    private final static long maxDatacenterId=-1L^(-1L<<datacenterIdBits);
    //毫秒内自增
    private final static long sequenceBits=12L;
    //机器ID偏左移12位
    private final static long workerIdShift=sequenceBits;
    //数据中心左移17位
    private final static long getDatacenterIdShift = sequenceBits+workIdBits;
    //时间毫秒左移22位
    private final static long timestampLeftShift = sequenceBits+workIdBits+datacenterIdBits;
    private final static long sequenceMask = -1L^(-1L<<sequenceBits);
    //上次生产ID时间戳
    private static long lastTimestamp = -1L;
    //0,并发控制
    private long sequence=0L;
    private final long workerId;
    private final long datacenterId;

    public IdWorker() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getMaxWorkId(datacenterId,maxWorkerId);
    }

    public IdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized  long nextId(){
        long timestamp = timeGen();
        if (timestamp<lastTimestamp){
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果是同一时间生产的，则进行毫秒内序列
        if (lastTimestamp==timestamp){
            sequence = (sequence+1)&sequenceMask;
            //毫秒内移除
            if (sequence==0){
                //阻塞到下一个毫秒，获取得到新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }else {
            sequence=0L;
        }
        //上次生成ID的时间戳
        lastTimestamp = timestamp;
        return ((timestamp-twepoch)<<timestampLeftShift)|
                (datacenterId<<getDatacenterIdShift)|
                (workerId<<workerIdShift)|
                sequence;
    }
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static long getMaxWorkId(long datacenterId, long maxWorkerId){
        StringBuffer mpid = new StringBuffer();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()){
            /**
             * 获取JVM的进程ID
             */
            mpid.append(name.split("@")[0]);
        }
        /**
         * MAC+PID的hashcode获取16个地位
         */
        return (mpid.toString().hashCode()&0xffff)%(maxWorkerId+1);
    }

    protected static long getDatacenterId(long maxDatacenterId){
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network==null){
                id=1L;

            }else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF&(long)mac[mac.length-1])|(0x0000FF00&(((long)mac[mac.length-2])<<8))) >>6;
                id=id%(maxDatacenterId+1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }


    public static void main(String[] args) throws InterruptedException, UnknownHostException, SocketException {
//
//        IdWorker idWorker = new IdWorker();
//        for (int i = 0; i < 10000; i++) {
//            long id = idWorker.nextId();
//            System.out.println(id);
//        }

        System.out.println(0x000000FF);
        System.out.println(0xffff);
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());

        long id = 0L;
        InetAddress ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);

        byte[] mac = network.getHardwareAddress();
        id = ((0x000000FF&(long)mac[mac.length-1])|(0x0000FF00&(((long)mac[mac.length-2])<<8))) >>6;
        id=id%(maxDatacenterId+1);
        System.out.println(id);

    }


}
