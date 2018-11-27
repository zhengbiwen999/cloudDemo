package com.zbw.utils;


import cn.mwee.utils.json.fastjson.FastJsonUtil;
import cn.mwee.utils.log4j2.MwLogger;
import cn.mwee.utils.string.StringUtil;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangjunming on 4/2/16.
 */
@Setter
public class RedisTemplateUtil implements CacheUtil {

    private static final FastJsonUtil jsonUtil = new FastJsonUtil();
//    private final MwLogger logger = new MwLogger(RedisTemplateUtil.class);
    private StringRedisTemplate stringRedisTemplate;
    private RetryTemplate retryTemplate;

    /**
     * 此方法,当存在大量数据时,慎用
     *
     * @param key
     * @return
     */
    @Override
    public Map<String, String> hGetAll(final String key) {
        Map<String, String> result = retryTemplate.execute(
                new RetryCallback<Map, RuntimeException>() {
                    @Override
                    public Map doWithRetry(RetryContext context) throws RuntimeException {
                        return (Map) stringRedisTemplate.opsForHash().entries(key);
                    }
                },
                new RecoveryCallback<Map>() {
                    @Override
                    public Map recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hKeys =>  key:" + key
//                                + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                        return stringRedisTemplate.opsForHash().entries(key);
                    }
                }
        );
        return result;
    }

    /**
     * 此方法不建议在生产上使用
     *
     * @param key
     * @return
     */
    @Override
    public Set<Object> hKeys(final String key) {
        Set<Object> result = retryTemplate.execute(
                new RetryCallback<Set<Object>, RuntimeException>() {
                    @Override
                    public Set<Object> doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForHash().keys(key);
                    }
                },
                new RecoveryCallback<Set<Object>>() {
                    @Override
                    public Set<Object> recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hKeys =>  key:" + key
//                                + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                        return stringRedisTemplate.opsForHash().keys(key);
                    }
                }
        );
        return result;
    }

    @Override
    public List<Object> hValues(final String key) {
        List<Object> result = retryTemplate.execute(
                new RetryCallback<List<Object>, RuntimeException>() {
                    @Override
                    public List<Object> doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForHash().values(key);
                    }
                },
                new RecoveryCallback<List<Object>>() {
                    @Override
                    public List<Object> recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hKeys =>  key:" + key
//                                + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                        return stringRedisTemplate.opsForHash().values(key);
                    }
                }
        );
        return result;
    }


    @Override
    public boolean lock(final String key, final String value, final long expiredSeconds) {

        final String lockKey = lockPrefix + key;

        boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, value);
        if (result) {
            set(lockKey, value, expiredSeconds);
        }

        return result;

    }

    @Override
    public void unlock(final String key) {
        delete(lockPrefix + key);
    }

    @Override
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    @Override
    public void set(final String key, final String value) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.opsForValue().set(key, value);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: set =>  key:" + key
//                                        + ",value:" + value + " , retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForValue().set(key, value);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public String get(final String key) {
        String result = retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForValue().get(key);
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: get =>  key:" + key
//                                + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                        return stringRedisTemplate.opsForValue().get(key);
                    }
                }
        );
        return result;
    }

    @Override
    public void set(final String key, final String value, final long expiredSeconds) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.opsForValue().set(key, value,
                                expiredSeconds <= 0 ? defaultExpiredSeconds : expiredSeconds, TimeUnit.SECONDS);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: set =>  key:" + key
//                                        + ",value:" + value + " , expiredSeconds:" + expiredSeconds + ", retrying at "
//                                        + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForValue().set(key, value,
                                expiredSeconds <= 0 ? defaultExpiredSeconds : expiredSeconds, TimeUnit.SECONDS);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public void delete(final String key) {
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.delete(key);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: delete =>  key:" + key
//                                        + " , retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.delete(key);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public void deletePattern(final String key) {
        Set<String> keys = keys(key);
        if (!keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    @Override
    public void deleteArr(final String... keys) {
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.delete(Arrays.asList(keys));
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: deleteArr , retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
//                        stringRedisTemplate.delete(Arrays.asList(keys));
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public Long incr(final String key) {
        Long result = retryTemplate.execute(
                new RetryCallback<Long, RuntimeException>() {
                    @Override
                    public Long doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForValue().increment(key, NumberUtils.LONG_ONE);
                    }
                },
                new RecoveryCallback<Long>() {
                    @Override
                    public Long recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: incr =>  key:" + key
//                                + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                        return stringRedisTemplate.opsForValue().increment(key, NumberUtils.LONG_ONE);
                    }
                }
        );
        return result;
    }

    @Override
    public Long incr(final String key, final long value) {
        Long result = retryTemplate.execute(
                new RetryCallback<Long, RuntimeException>() {
                    @Override
                    public Long doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForValue().increment(key, value);
                    }
                },
                new RecoveryCallback<Long>() {
                    @Override
                    public Long recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: incr =>  key:" + key
//                                + ", value:" + value + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                        return stringRedisTemplate.opsForValue().increment(key, value);
                    }
                }
        );
        return result;
    }

    @Override
    public List<String> lRange(final String key, final long start, final long end) {
        List<String> result = retryTemplate.execute(
                new RetryCallback<List<String>, RuntimeException>() {
                    @Override
                    public List<String> doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForList().range(key, start, end);
                    }
                },
                new RecoveryCallback<List<String>>() {
                    @Override
                    public List<String> recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: lRange =>  key:" + key
//                                + ",start:" + start + " , end:" + end + ", retrying at " + context.getRetryCount()
//                                + " times", context.getLastThrowable());
                        return stringRedisTemplate.opsForList().range(key, start, end);
                    }
                }
        );
        return result;
    }

    @Override
    public List<String> hMget(final String key, final String... fields) {
        if (fields == null || fields.length == 0) {
            return Lists.newArrayList();
        }

        List<String> result = retryTemplate.execute(
                new RetryCallback<List<String>, RuntimeException>() {
                    @Override
                    public List<String> doWithRetry(RetryContext context) throws RuntimeException {
                        return RedisTemplateUtil.this._hMget(key, fields);
                    }
                },
                new RecoveryCallback<List<String>>() {
                    @Override
                    public List<String> recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hMget =>  key:" + key
//                                + ", retrying at " + context.getRetryCount()
//                                + " times", context.getLastThrowable());
                        return RedisTemplateUtil.this._hMget(key, fields);
                    }
                }
        );
        return result;
    }

    private List<String> _hMget(final String key, final String... fields) {
        return stringRedisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                byte[] btKey = serializer.serialize(key);
                List<String> resultList = new ArrayList<>();
                if (redisConnection.exists(btKey)) {
                    List<byte[]> list = new ArrayList<>(fields.length);
                    for (String field : fields) {
                        list.add(serializer.serialize(field));
                    }
                    List<byte[]> btList = redisConnection.hMGet(btKey, list.toArray(new byte[list.size()][]));

                    for (byte[] bt : btList) {
                        resultList.add(serializer.deserialize(bt));
                    }

                }
                return resultList;
            }
        });
    }

    @Override
    public List<Object> hMget(final String key, final List<String> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return Lists.newArrayList();
        }

        List<Object> result = retryTemplate.execute(
                new RetryCallback<List<Object>, RuntimeException>() {
                    @Override
                    public List<Object> doWithRetry(RetryContext context) throws RuntimeException {
                        List<Object> objFields = Lists.transform(fields, new Function<String, Object>() {
                            @Override
                            public Object apply(String s) {
                                return s;
                            }
                        });

                        return stringRedisTemplate.opsForHash().multiGet(key, objFields);
                    }
                },
                new RecoveryCallback<List<Object>>() {
                    @Override
                    public List<Object> recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hMget =>  key:" + key
//                                + ", retrying at " + context.getRetryCount()
//                                + " times", context.getLastThrowable());
                        List<Object> objFields = Lists.transform(fields, new Function<String, Object>() {
                            @Override
                            public Object apply(String s) {
                                return s;
                            }
                        });

                        return stringRedisTemplate.opsForHash().multiGet(key, objFields);
                    }
                }
        );
        return result;
    }

    @Override
    public Map<String, String> hMgetMap(final String key, final String... fields) {
        if (fields.length == 0) {
            return Maps.newHashMap();
        }

        Map<String, String> result = retryTemplate.execute(
                new RetryCallback<Map<String, String>, RuntimeException>() {
                    @Override
                    public Map<String, String> doWithRetry(RetryContext context) throws RuntimeException {
                        return RedisTemplateUtil.this._hMgetMap(key, fields);
                    }
                },
                new RecoveryCallback<Map<String, String>>() {
                    @Override
                    public Map<String, String> recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hMgetMap =>  key:" + key
//                                + ", retrying at " + context.getRetryCount()
//                                + " times", context.getLastThrowable());
                        return RedisTemplateUtil.this._hMgetMap(key, fields);
                    }
                }
        );
        return result;
    }

    private Map<String, String> _hMgetMap(final String key, final String... fields) {
        return stringRedisTemplate.execute(new RedisCallback<Map<String, String>>() {
            @Override
            public Map<String, String> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
                byte[] btKey = serializer.serialize(key);
                Map<String, String> resultMap = null;
                if (redisConnection.exists(btKey)) {
                    List<byte[]> list = new ArrayList<>(fields.length);
                    for (String field : fields) {
                        list.add(serializer.serialize(field));
                    }
                    List<byte[]> btList = redisConnection.hMGet(btKey, list.toArray(new byte[list.size()][]));
                    resultMap = new HashedMap<>();
                    for (int i = 0, len = btList.size(); i < len; i++) {
                        resultMap.put(fields[i], serializer.deserialize(btList.get(i)));
                    }
                }
                return resultMap;
            }
        });
    }

    @Override
    public void sSet(final String key, final String value, final long expiredSeconds) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.opsForSet().add(key, value);
                        stringRedisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: sSet =>  key:" + key
//                                        + ",value:" + value + ",expiredSeconds:" + expiredSeconds + ",retrying at "
//                                        + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForSet().add(key, value);
                        stringRedisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public String hGet(final String key, final String hashKey) {
        String result = retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        return StringUtil.getValue(stringRedisTemplate.opsForHash().get(key, hashKey));
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hGet =>  key:" + key
//                                + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                        return StringUtil.getValue(stringRedisTemplate.opsForHash().get(key, hashKey));
                    }
                }
        );
        return result;
    }

    @Override
    public byte hAdd(final String key, final String hashKey, final String value) {
        byte result = retryTemplate.execute(
                new RetryCallback<Byte, RuntimeException>() {
                    @Override
                    public Byte doWithRetry(RetryContext context) throws RuntimeException {
                        byte t = stringRedisTemplate.opsForHash().hasKey(key, hashKey) ? (byte) 0 : (byte) 1;
                        stringRedisTemplate.opsForHash().put(key, hashKey, value);
                        return t;
                    }
                },
                new RecoveryCallback<Byte>() {
                    @Override
                    public Byte recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hAdd =>  key:" + key
//                                + ",hashKey:" + hashKey + " , value:" + value + ", retrying at " + context.getRetryCount()
//                                + " times", context.getLastThrowable());
                        byte t = stringRedisTemplate.opsForHash().hasKey(key, hashKey) ? (byte) 0 : (byte) 1;
                        stringRedisTemplate.opsForHash().put(key, hashKey, value);
                        return t;
                    }
                }
        );
        return result;
    }

    @Override
    public void lPush(final String key, final String value) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.opsForList().leftPush(key, value);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: lPush =>  key:" + key
//                                        + ",value:" + value + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForList().leftPush(key, value);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public String lPop(final String key) {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }
        return retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForList().leftPop(key);
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: lPop =>  key:" + key
//                                        + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        return stringRedisTemplate.opsForList().leftPop(key);
                    }
                }
        );
    }


    @Override
    public void rPush(final String key, final String value) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.opsForList().rightPush(key, value);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: rPush =>  key:" + key
//                                        + ",value:" + value + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForList().rightPush(key, value);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public String rPop(final String key) {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }
        return retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForList().rightPop(key);
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: rPop =>  key:" + key
//                                        + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        return stringRedisTemplate.opsForList().rightPop(key);
                    }
                }
        );
    }

    @Override
    public void sRem(final String key, final String... member) {
        final Object[] obj = member;
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.opsForSet().remove(key, obj);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: sRem =>  key:" + key
//                                        + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForSet().remove(key, obj);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public void sSet(final String key, final String value) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext context) throws RuntimeException {
                        stringRedisTemplate.opsForSet().add(key, value);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: sSet =>  key:" + key
//                                        + ",value:" + value + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForSet().add(key, value);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public void hAdd(final String key, final String hashKey, final String value, final long overtime) {
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext content) throws RuntimeException {
                        stringRedisTemplate.opsForHash().put(key, hashKey, value);
                        stringRedisTemplate.expire(key, overtime, TimeUnit.SECONDS);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hAdd =>  key:" + key
//                                        + ",value:" + value + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForHash().put(key, hashKey, value);
                        stringRedisTemplate.expire(key, overtime, TimeUnit.SECONDS);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public void hAddAll(final String key, final Map<String, String> map) {
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext content) throws RuntimeException {
                        stringRedisTemplate.opsForHash().putAll(key, map);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hAdd =>  key:" + key
//                                        + ",value:" + jsonUtil.toJson(map) + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForHash().putAll(key, map);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }

    @Override
    public void hAddAll(final String key, final Map<String, String> map, final long overtime) {
        retryTemplate.execute(
                new RetryCallback<String, RuntimeException>() {
                    @Override
                    public String doWithRetry(RetryContext content) throws RuntimeException {
                        stringRedisTemplate.opsForHash().putAll(key, map);
                        stringRedisTemplate.expire(key, overtime, TimeUnit.SECONDS);
                        return StringUtils.EMPTY;
                    }
                },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: hAdd =>  key:" + key
//                                        + ",value:" + jsonUtil.toJson(map) + ",retrying at " + context.getRetryCount() + " times",
//                                context.getLastThrowable());
                        stringRedisTemplate.opsForHash().putAll(key, map);
                        stringRedisTemplate.expire(key, overtime, TimeUnit.SECONDS);
                        return StringUtils.EMPTY;
                    }
                }
        );
    }


    @Override
    public Set<String> sMember(final String key) {
        Set<String> result = retryTemplate.execute(
                new RetryCallback<Set<String>, RuntimeException>() {
                    @Override
                    public Set<String> doWithRetry(RetryContext context) throws RuntimeException {
                        return stringRedisTemplate.opsForSet().members(key);
                    }
                },
                new RecoveryCallback<Set<String>>() {
                    @Override
                    public Set<String> recover(RetryContext context) throws Exception {
//                        logger.error("REDIS ERROR: sMember =>  key:" + key
//                                + ",retrying at " + context.getRetryCount()
//                                + " times", context.getLastThrowable());
                        return stringRedisTemplate.opsForSet().members(key);
                    }
                }
        );
        return result;
    }

    @Override
    public Long ttl(String key) {
        return stringRedisTemplate.boundSetOps(key).getExpire();
    }

    @Override
    public void expire(String key, int seconds) {
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }
}
