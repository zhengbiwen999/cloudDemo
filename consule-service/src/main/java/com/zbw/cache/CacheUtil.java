package com.zbw.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface CacheUtil {

    long defaultExpiredSeconds = 300;

    String lockPrefix = "lock:";

    boolean exists(String key);

    Long ttl(String key);

    Long zcard(String key);

    Set<String> zrange(String key, long start, long end);

    Long zunion(String dstkey, List<String> sets);

    Map<String, String> hGetAll(String key);

    void expire(String key, int seconds);

    void zadd(String key, double score, String member);

    Set<Object> hKeys(String key);

    List<Object> hValues(String key);

    boolean lock(final String key, final String value, final long expiredSeconds);

//    @Deprecated
//    void unlock(final String key);

    void unlock(final String key, final String value);

    Set<String> keys(String pattern);

    void set(String key, String value);

    void set(String key, Object value);

    String get(String key);

    //overtime 过期时间,单位为秒.
    void set(String key, String value, long expiredSeconds);

    /**
     * @param expiredSeconds 过期时间,单位为秒.
     */
    void set(String key, Object value, long expiredSeconds);

    void delete(String key);

    void deletePattern(String key);

    void deleteArr(String... keys);

    Set<String> sMember(String key);

    Long incr(String key);

    Long incr(String key, long value);

    List<String> lRange(String key, long start, long end);

    List<String> hMget(String key, String... fields);

    List<Object> hMget(String key, List<String> fields);

    Map<String, String> hMgetMap(String key, String... fields);

    void sSet(String key, String value, long overtime);

    String hGet(String key, String hashKey);

    /**
     * 对应php中的redis hadd方法
     *
     * @param key
     * @param hashKey
     * @param value
     * @return 如果key不存在，添加成功，返回1；如果key已存在，替换成功，返回0
     */
    byte hAdd(String key, String hashKey, String value);

    void lPush(String key, String value);

    /**
     * 返回list的size
     *
     * @param key
     * @return
     */
    Long lSize(String key);

    String lPop(String key);

    String lPop(String key, long timeout, TimeUnit unit);

    void rPush(String key, String value);

    void rPushAll(String key, List<String> list);

    String rPop(String key);

    String rPop(String key, long timeout, TimeUnit unit);

    void sRem(String key, String... member);

    void sSet(String key, String value);

    void hAdd(String key, String hashKey, String value, long overtime);

    void hAddAll(String key, Map<String, String> map);

    void hAddAll(String key, Map<String, String> map, long overtime);


}
