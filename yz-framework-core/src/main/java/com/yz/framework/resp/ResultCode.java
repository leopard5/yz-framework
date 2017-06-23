package com.yz.framework.resp;

public enum ResultCode {
	// 操作成功标识
	SUCCESS("200", "Success"),
    // 系统异常标识
	SYS_ERROR("500", "服务器异常，请稍后再试！"),

    USER_NOT_EXIST("-401", "用户不存在"),
    NO_PERMISSION("-402",  "没有此操作权限"),
    NO_LOGGED_IN("-403",  "用户未登录"),
    
    EXTERNAL_SYSTEM_ERROR("1000",  "调用外部系统[%s]出错"),
    
	//----------------------------------------
	// 通用异常 
	// 区间：10001-10050
	//----------------------------------------
    COMMON_PARAM_NULL("10001",    "必要参数%s为空！"),
    COMMON_PARAM_INVALID("10002", "参数%s无效！"),
    CONTAINS_ILLEGAL_CHARACTERS("10003", "%s含有非法字符"),
    DATA_NOT_EXIST("10004", "%s不存在"),
    NO_PERMISSION_OPERATE("10005",  "没有%s的操作权限"),
    CAN_NOT_BE_OPERATE("10006",  "%s不能%s"),
    FIELD_NOT_ALLOWED_EMPTY("10010", "%s不能为空"),
	FIELD_VALUE_MUST_LARGE_THAN("10011", "%s必须大于%s"),
	FIELD_VALUE_MUST_LESS_THAN("10012", "%s必须小于%s"),
	DATA_EXIST("10013", "%s已存在"),
	FIELD_NOT_EMAIL("10013", "%s格式错误"),
	FIELD_NOT_MOBILE("10014", "%s格式错误"),
	FIELD_NOT_ID_NUM("10015", "%s格式错误"),
	FIELD_NOT_DATE("10016", "%s格式错误"),
	FIELD_LENGTH_MUST_LESS("10017", "%s必须小于%d"),
	FIELD_LENGTH_MUST_MORE("10018", "%s必须大于%d"),
	FIELD_LENGTH_MUST_BETWEEN("10019", "%s长度必须大于%d和小于%d"),
	FILED_NOT_IN_ENUM_VALUES("10020", "不支持的%s"),
	FIELD_NOT_BOTH_BE_EMPTY("10021", "%s不能同时为空"),
	MUST_BE_INTEGER_MULTIPLE("10022", "%s必须是%s的整数倍"),
	UPLOAD_PICTURES_ERROR("10023", "上传图片出错，%s"),
	
	FIELD_VALUE_MUST_GE_THAN("10023", "%s必须大于等于%s"),
	FIELD_VALUE_MUST_LE_THAN("10024", "%s必须小于等于%s"),
	FIELD_VALUE_MUST_DONE("10025", "%s还未%s"),
	
	TIME_MUST_LARGE_THAN("10030", "%s必须晚于%s"),
	TIME_VALUE_MUST_LESS_THAN("10031", "%s必须早于%s"),
	INVALID_CHANGE_STATUS("10032","%s更新状态非法"),
	FETCH_INFO_ERROR("10033", "获取%s信息出错"),
	LENGTH_MUST_LESS("10034", "%s必须小于%s"),
	LENGTH_MUST_MORE("10035", "%s必须大于%s"),
	NO_DATA_FOUND("10036","暂无数据"),
	PARAM_REPEAT("10037", "%s有重复数据"),
	STATUS_ERROR("10038", "%s状态只有是%s才能%s"),
	AUDIT_STATUS_ERROR("10039", "%s审核状态只有是%s才能%s"),

	STATUS_SAVED_OFFLINE_ERROR("10040", "店铺活动状态是已保存、已下线状态才能%s"),
	STATUS_SAVED_ERROR("10041", "红星活动状态只有是已保存状态才能%s"),
	STATUS_INITIAL_ERROR("10042", "活动审核状态只有是初始状态才能%s"),


	COMMON_PARAM_MUST_SELECTED("10043","%s必选"),
	OBJECT_INVALID("10044", "%s已失效"),
	COMMON_MESSAGE("10045", "%s"),

	//----------------------------------------
	// api调用异常 
	// 区间：10051-10099
	//----------------------------------------
	API_INVLID_SIGN("10100", "签名错误"),
	API_INVLID_FROM("10101", "没有权限访问"),
	API_INVLID_DATA("10102", "请求数据错误"),
	API_NOT_SUPPORT_METHOD("10103", "只接受Post请求"),
	API_INVLID_DATA_FORMAT("10104", "数据格式不符合要求"),
	API_INVLID_API("10105", "api【%s】不存在"),
	
	//****************************************
	// 业务异常 
	// 区间：10100-10999
	//****************************************
	//----------------------------------------



	// a异常
	// 区间：10200-10299
	//----------------------------------------


    //----------------------------------------
	// b异常
	// 区间：10300-10399
	//----------------------------------------


	//----------------------------------------
	// c异常
	// 区间：10400-10499
	//----------------------------------------


	//----------------------------------------
	// d异常
	// 区间：10500-10599
	//----------------------------------------



	;



    public String code;
    public String message;
    
    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
