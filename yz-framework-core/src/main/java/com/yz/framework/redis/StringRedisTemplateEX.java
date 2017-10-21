package com.yz.framework.redis;

import com.alibaba.fastjson.JSON;
import com.yz.framework.util.FileUtil;
import com.yz.framework.util.StringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StringRedisTemplateEX extends StringRedisTemplate {

    private static final RedisScript<String> SET_IF_ABSENT_SCRIPT =
            new DefaultRedisScript<String>("return redis.call('SET',KEYS[1],ARGV[1],'PX',ARGV[2],'NX')",
                    String.class);

    private static final RedisScript<String> DELETE_IF_EQUAL_SCRIPT =
            new DefaultRedisScript<String>("if redis.call('GET',KEYS[1])==ARGV[1] then redis.call('DEL',KEYS[1]) return 'OK' end return 'FALSE'",
                    String.class);

    private static final RedisScript<String> HASH_UPDATE_IF_EQUAL_SCRIPT =
            new DefaultRedisScript<String>(
                    "if redis.call('HGET',KEYS[1],ARGV[1])==ARGV[3] then redis.call('HSET',KEYS[1],ARGV[1],ARGV[2]) return 'OK' end return 'FALSE'",
                    String.class);

    private static final RedisScript<String> MULTI_SET_IF_ABSENT_SCRIPT;
    private static final RedisScript<String> MULTI_DEL_IF_EQUAL_SCRIPT;
    private static final RedisScript<String> STOCK_DECREASE_SCRIPT;
    private static final RedisScript<String> COUPON_STOCK_DECREASE_SCRIPT;
    private static final RedisScript<String> MULTI_VALUE_SET_SCRIPT;

    static {
        String multi_set_if_absent = null;
        String multi_del_if_equal = null;
        String stock_decrease = null;
        String coupon_stock_decrease = null;
        String multi_value_set = null;
        try {
            multi_set_if_absent = FileUtil.getResourceContent("lua/multi_set_if_absent.lua");
            multi_del_if_equal = FileUtil.getResourceContent("lua/multi_del_if_equal.lua");
            stock_decrease = FileUtil.getResourceContent("lua/stock_decrease.lua");
            coupon_stock_decrease =  FileUtil.getResourceContent("lua/coupon_stock_decrease.lua");
            multi_value_set = FileUtil.getResourceContent("lua/multi_value_set.lua");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        MULTI_SET_IF_ABSENT_SCRIPT = new DefaultRedisScript<String>(multi_set_if_absent, String.class);
        MULTI_DEL_IF_EQUAL_SCRIPT = new DefaultRedisScript<String>(multi_del_if_equal, String.class);
        STOCK_DECREASE_SCRIPT = new DefaultRedisScript<String>(stock_decrease, String.class);
        COUPON_STOCK_DECREASE_SCRIPT = new DefaultRedisScript<String>(coupon_stock_decrease, String.class);
        MULTI_VALUE_SET_SCRIPT  = new DefaultRedisScript<String>(multi_value_set, String.class);
    }

    private static final String SUCCESS_FLAG = "OK";

    public boolean setIfAbsent(final String key, final String value, final long timeOut) {
        return setIfAbsent(Collections.singletonList(key), value, String.valueOf(timeOut));
    }

    public boolean setIfAbsent(final List<String> keys, final String value, final String timeOut) {
        String result = null;

        if (keys.size() > 1) {
            result = this.execute(
                    MULTI_SET_IF_ABSENT_SCRIPT,
                    keys,
                    value,
                    timeOut);
        } else {
            result = this.execute(
                    SET_IF_ABSENT_SCRIPT,
                    keys,
                    value,
                    timeOut);
        }

        return result != null && result.equals(SUCCESS_FLAG);

    }

    public boolean deleteIfEqual(final String key, final String expectValue) {
        return deleteIfEqual(Collections.singletonList(key), expectValue);
    }

    public boolean hUpdateIfEqual(
            final String key,
            final String filed,
            final String value,
            final String expectValue) {
        return hUpdateIfEqual(Collections.singletonList(key), filed, value, expectValue);
    }

    private boolean hUpdateIfEqual(List<String> keys, String filed, String value, String expectValue) {

        String result = this.execute(
                HASH_UPDATE_IF_EQUAL_SCRIPT,
                keys,
                filed,
                value,
                expectValue);

        return result != null && result.equals(SUCCESS_FLAG);
    }

    public boolean deleteIfEqual(final List<String> keys, final String expectValue) {
        String result = null;
        if (keys.size() == 1) {
            result = this.execute(
                    DELETE_IF_EQUAL_SCRIPT,
                    keys,
                    expectValue);
        } else {
            result = this.execute(
                    MULTI_DEL_IF_EQUAL_SCRIPT,
                    keys,
                    expectValue);
        }
        return result != null && result.equals(SUCCESS_FLAG);
    }

    public <T> T getObject(final String key, final Class<T> clazz) {
        String json = opsForValue().get(key);
        return StringUtil.isBlank(json) ? null : JSON.parseObject(json, clazz);
    }

    public <T> List<T> getObjectList(final Collection<String> keys, final Class<T> clazz) {
        List<String> jsonList = opsForValue().multiGet(keys);
        List<T> list = new ArrayList<T>(jsonList.size());
        for (String json : jsonList) {
            if (StringUtil.isNotBlank(json)) {
                list.add(JSON.parseObject(json, clazz));
            }
        }
        return list;
    }

    /**
     * 扣减库存的lua脚本 保证事务一致性和原子性
     * @param keys
     * @param increment
     * @return
     * NOKEY => redis中没有库存标识的key
     * NO    => 库存不足
     * OK    => 成功扣减库存
     * null  => keys参数不正确 此方法的keys只需要传一个参数[库存的缓存key]
     */
    public String atomicDecrease(final List<String> keys, final String increment){
        String result = null;
        if (keys.size() == 1) {
            result = this.execute(
                    STOCK_DECREASE_SCRIPT,
                    keys,
                    increment);
        }else {
            return null;
        }
        return result;
    }

    /**
     * 优惠券扣减库存的lua脚本 保证事务一致性和原子性
     * @param keys
     * key1 优惠券渠道库存缓存key            mkt:coupon:123:channel:1:stock           11111
     * key2 优惠券渠道个人剩余领取数缓存key    mkt:coupon:123:user:123:remainingcount   11111
     * @param increment
     * @param perPersonLimit
     * @param ttl        秒
     * increment 必须小于等于 perPersonLimit
     * @return
     * NOKEY => redis中没有库存标识的key
     * NO    => 库存不足
     * OK    => 成功扣减库存
     * LIMIT => 单人领券数量用完
     * null  => keys参数不正确 此方法的keys只需要传一个参数[库存的缓存key]
     */
    public String atomicDecreaseForCoupon(final List<String> keys, final String increment, final String perPersonLimit, String ttl){
        if (Integer.parseInt(increment) > Integer.parseInt(perPersonLimit)){
            return null;
        }
        String result = null;
        if (keys.size() == 2) {
            result = this.execute(
                    COUPON_STOCK_DECREASE_SCRIPT,
                    keys,
                    increment, perPersonLimit, ttl);
        }
        return result;
    }

    /**
     *
     * @param keyValues  key|value example [a:b:d|value]
     * @param ttl        timeout : seconds
     * @param nxxx       NX XX
     * @return
     */
    public String multiValueSet(final List<String> keyValues, final String ttl, String nxxx) {
        if (keyValues == null || keyValues.isEmpty()) {
            return null;
        }
        String result = this.execute(
                MULTI_VALUE_SET_SCRIPT,
                keyValues,
                ttl,
                nxxx);
        return result;
    }
}
