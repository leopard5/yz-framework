package com.yz.framework.unique;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Description:
 * 42位的时间前缀+10位的节点标识+12位的sequence避免并发的数字（12位不够用时强制得到新的时间前缀）
 * 对系统时间的依赖性非常强，需要关闭ntp的时间同步功能，或者当检测到ntp时间调整后，拒绝分配id。
 * <p>
 * <p>
 * 最好的使用方式是,单独部署成服务,有需要的可以通过服务调用.如果不想通过服务调用,那么需要唯一ID的业务在一个机器只部署一个进程,并且
 * 使用单例模式.workId,如果IP在同一个网段内部,可以使用IP的最后一位来表示.要生成全局的唯一ID,限制较多,在设计的时候需要特别注意.
 */

public class IdBuilder {
    private final static Logger logger = LoggerFactory.getLogger(IdBuilder.class);

    private final long workerId;
    private final long snsEpoch = 1330328109047L;//起始标记点，作为基准
    private long sequence = 0L;//0，并发控制
    private final long workerIdBits = 10L;//workid的范围为：0-1023
    private final long maxWorkerId = -1L ^ -1L << this.workerIdBits;//1023,1111111111,10位
    private final long sequenceBits = 12L;//sequence值控制在0-4095

    private final long workerIdShift = this.sequenceBits;//12
    private final long timestampLeftShift = this.sequenceBits + this.workerIdBits;//22
    private final long sequenceMask = -1L ^ -1L << this.sequenceBits;//4095,111111111111,12位

    private long lastTimestamp = -1L;

    public IdBuilder(long workerId) {
        super();
        if (workerId > this.maxWorkerId || workerId < 0) {//workid < 1024[10位：2的10次方]
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() throws Exception {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) {//如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环)，下次再使用时sequence是新值
            this.sequence = this.sequence + 1 & this.sequenceMask;
            if (this.sequence == 0) {
                timestamp = this.tilNextMillis(this.lastTimestamp);//重新生成timestamp
            }
        } else {
            this.sequence = 0;
        }
        if (timestamp < this.lastTimestamp) {
            logger.error(String.format("Clock moved backwards.Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
            throw new Exception(String.format("Clock moved backwards.Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
        }

        this.lastTimestamp = timestamp;
        //生成的timestamp
        return timestamp - this.snsEpoch << this.timestampLeftShift | this.workerId << this.workerIdShift | this.sequence;
    }

    /**
     * 保证返回的毫秒数在参数之后
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 获得系统当前毫秒数
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
        IdBuilder iw = new IdBuilder(1);
        System.out.println("maxWorkerId:" + iw.maxWorkerId);
        System.out.println("sequenceMask:" + iw.sequenceMask);
        long begin = new Date().getTime();
        for (int i = 0; i < 4094; i++) {
            System.out.println(iw.nextId());
        }
        long end = new Date().getTime();
        System.out.println((end - begin) / 1000);
    }
}
