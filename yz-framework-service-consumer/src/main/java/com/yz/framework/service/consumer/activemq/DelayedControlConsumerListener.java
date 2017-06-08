package com.yz.framework.service.consumer.activemq;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.activemq.MessageListenerBase;
import com.yz.framework.service.consumer.common.ExecuteTask;

public class DelayedControlConsumerListener extends MessageListenerBase {
	public static final Logger LOGGER = Logger.getLogger(DelayedControlConsumerListener.class);
	
	private static final String DELAY_CONTROL_KEY = "queue:control:delay";   //单位秒
	
    private StringRedisTemplate redisTemplate;
    private ExecuteTask executeTask;
    
	public StringRedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public ExecuteTask getExecuteTask() {
		return executeTask;
	}

	public void setExecuteTask(ExecuteTask executeTask) {
		this.executeTask = executeTask;
	}

	@Override
	protected void processMessage(JobMessage jobMessage) {
		try {
		// redis中设置开关项  控制库存更新延迟时间
		if (redisTemplate.hasKey(DELAY_CONTROL_KEY)) {
			Integer delayInteger = Integer.valueOf(redisTemplate.boundValueOps(DELAY_CONTROL_KEY).get());
				if (delayInteger > 0) {
					// 如果redis中设置了延时  则将接收到的消息重新放回队列  并且设置延迟时间
					jobMessage.setDelaySeconds(delayInteger);
					executeTask.getServiceProducer().produce(jobMessage, "delayedControlQueue");
				}else {
					executeTask.doWork(jobMessage, "delayedControlQueue");
				}
			}else {
				executeTask.doWork(jobMessage, "delayedControlQueue");
			}
		} catch (Throwable e) {
			LOGGER.error("processMessage", "消息延时回写队列发生未知错误", JSON.toJSONString(jobMessage), e);
		}
	}


}
