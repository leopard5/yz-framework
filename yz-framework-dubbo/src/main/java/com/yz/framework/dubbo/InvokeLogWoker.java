/**
 * yz-gc-service 
 * InvokeLogWoker.java 
 * com.yz.gc.service.dubbo 
 * TODO  
 * @author yazhong.qi
 * @date   2015年10月26日 下午1:40:43 
 * @version   1.0
 */

package com.yz.framework.dubbo;

import com.alibaba.dubbo.rpc.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yz.framework.bizlogging.BizLogger;
import com.yz.framework.logging.Logger;

/**
 * ClassName:InvokeLogWoker <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年10月26日 下午1:40:43 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class InvokeLogWoker implements Runnable {

	// private static final int SUCESS_STATUS = 10000;
	private static final int SYS_ERROR = 10001;

	private Object[] arguments;
	private String api;
	private String serviceName;
	private String serverIP;
	private String from;
	private String invokerIP;
	private BizLogger bizLogger;
	private long requestTime;
	private long returnTime;
	private Result result;
	private Throwable exception;

	public InvokeLogWoker(
			final Object[] arguments,
			final String api,
			final String serviceName,
			final String serverIP,
			final String from,
			final String invokerIP,
			final long requestTime,
			final long returnTime,
			final Result result,
			final Throwable ex,
			final BizLogger bizLogger

	)
	{
		this.arguments = arguments;
		this.api = api;
		this.serviceName = serviceName;
		this.serverIP = serverIP;
		this.from = from;
		this.invokerIP = invokerIP;
		this.requestTime = requestTime;
		this.returnTime = returnTime;
		this.exception = ex;
		this.result = result;
		this.bizLogger = bizLogger;
	}

	private static final Logger LOGGER = Logger.getLogger(InvokeLogWoker.class);

	@Override
	public void run() {
		try {
			String message = JSON.toJSONString(arguments);
			int status = -1;
			String returnMessage = null;
			String returnData = null;
			if (result == null) {
				if (exception != null) {
					returnMessage = LOGGER.getDetailMessage(exception);
				}
				else {
					returnMessage = "没有异常，没有返回值，不可能，请检查代码";
				}
			}
			else {
				Object bizResult = result.getValue();
				if (bizResult == null) {
					Throwable bizException = result.getException();
					if (bizException != null) {
						returnMessage = bizException.toString();
						returnData = LOGGER.getDetailMessage(bizException);
					}
				}
				else {
					JSONObject message2 = (JSONObject) JSON.toJSON(bizResult);
					status = message2.getIntValue("status");
					returnMessage = message2.getString("message");
					if (status == SYS_ERROR) {
						if (exception != null) {
							returnData = LOGGER.getDetailMessage(exception);
						}
					}
				}
			}
			bizLogger.logInvoke(api, message, from, "", requestTime, returnTime, status,
					returnMessage, returnData, invokerIP, serverIP, serviceName);

		} catch (Exception e) {
			LOGGER.error("run", "记录日志错误", e);
		}

	}

}
