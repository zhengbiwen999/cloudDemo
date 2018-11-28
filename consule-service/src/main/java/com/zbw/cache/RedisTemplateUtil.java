package com.zbw.cache;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Setter
public class RedisTemplateUtil implements CacheUtil {

    private final Logger logger = LoggerFactory.getLogger(RedisTemplateUtil.class);
    private StringRedisTemplate stringRedisTemplate;
    private RetryTemplate retryTemplate;

    /**
     * 此方法,当存在大量数据时,慎用
     *
     * @param key
     * @return
     */
    @Override
    public Map<String, String> hGetAll(String key) {
        Map<String, String> result = retryTemplate.execute(
                (RetryContext context) -> (Map) stringRedisTemplate.opsForHash().entries(key),
                context -> {
                    logger.error("REDIS ERROR: hKeys =>  key:" + key
                            + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                    return stringRedisTemplate.opsForHash().entries(key);
                }
        );
        return result;
    }

    @Override
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public Long ttl(String key) {
        return stringRedisTemplate.boundSetOps(key).getExpire();
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return stringRedisTemplate.boundZSetOps(key).range(start, end);
    }

    @Override
    public Long zcard(String key) {
        return stringRedisTemplate.boundZSetOps(key).zCard();
    }

    @Override
    public Long zunion(String dstkey, List<String> otherKeys) {
        stringRedisTemplate.boundZSetOps(dstkey).unionAndStore(otherKeys, dstkey);
        return Long.parseLong(stringRedisTemplate.boundValueOps(dstkey).get());
    }

    @Override
    public void expire(String key, int seconds) {
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void zadd(String key, double score, String member) {
        stringRedisTemplate.opsForZSet().add(key, member, score);
    }

    /**
     * 此方法不建议在生产上使用
     *
     * @param key
     * @return
     */
    @Override
    public Set<Object> hKeys(String key) {
        Set<Object> result = retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForHash().keys(key);
                },
                context -> {
                    logger.error("REDIS ERROR: hKeys =>  key:" + key
                            + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                    return stringRedisTemplate.opsForHash().keys(key);
                }
        );
        return result;
    }

    @Override
    public List<Object> hValues(String key) {
        List<Object> result = retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForHash().values(key);
                },
                context -> {
                    logger.error("REDIS ERROR: hKeys =>  key:" + key
                            + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                    return stringRedisTemplate.opsForHash().values(key);
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

//    @Override
//    public void unlock(final String key) {
//        delete(lockPrefix + key);
//    }

    @Override
    public void unlock(final String key, final String value) {
        String v = get(lockPrefix + key);
        if (v != null && v.equals(value)) {
            delete(lockPrefix + key);
        }
    }

    @Override
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    @Override
    public void set(String key, String value) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.opsForValue().set(key, value);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: set =>  key:" + key
                                    + ",value:" + value + " , retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForValue().set(key, value);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public void set(String key, Object value) {
        set(key,  JSONObject.toJSON(value));
    }

    @Override
    public String get(String key) {
        String result = retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForValue().get(key);
                },
                context -> {
                    logger.error("REDIS ERROR: get =>  key:" + key
                            + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                    return stringRedisTemplate.opsForValue().get(key);
                }
        );
        return result;
    }

    @Override
    public void set(String key, String value, long expiredSeconds) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.opsForValue().set(key, value,
                            expiredSeconds <= 0 ? defaultExpiredSeconds : expiredSeconds, TimeUnit.SECONDS);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: set =>  key:" + key
                                    + ",value:" + value + " , expiredSeconds:" + expiredSeconds + ", retrying at "
                                    + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForValue().set(key, value,
                            expiredSeconds <= 0 ? defaultExpiredSeconds : expiredSeconds, TimeUnit.SECONDS);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public void set(String key, Object value, long expiredSeconds) {
        set(key, JSONObject.toJSON(value), expiredSeconds);
    }

    @Override
    public void delete(String key) {
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.delete(key);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: delete =>  key:" + key
                                    + " , retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.delete(key);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public void deletePattern(String key) {
        Set<String> keys = keys(key);
        if (!keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    @Override
    public void deleteArr(String... keys) {
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.delete(Arrays.asList(keys));
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: deleteArr , retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.delete(Arrays.asList(keys));
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public Long incr(String key) {
        Long result = retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForValue().increment(key, NumberUtils.LONG_ONE);
                },
                context -> {
                    logger.error("REDIS ERROR: incr =>  key:" + key
                            + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                    return stringRedisTemplate.opsForValue().increment(key, NumberUtils.LONG_ONE);
                }
        );
        return result;
    }

    @Override
    public Long incr(String key, long value) {
        Long result = retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForValue().increment(key, value);
                },
                context -> {
                    logger.error("REDIS ERROR: incr =>  key:" + key
                            + ", value:" + value + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                    return stringRedisTemplate.opsForValue().increment(key, value);
                }
        );
        return result;
    }

    @Override
    public List<String> lRange(String key, long start, long end) {
        List<String> result = retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForList().range(key, start, end);
                },
                context -> {
                    logger.error("REDIS ERROR: lRange =>  key:" + key
                            + ",start:" + start + " , end:" + end + ", retrying at " + context.getRetryCount()
                            + " times", context.getLastThrowable());
                    return stringRedisTemplate.opsForList().range(key, start, end);
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
                (RetryContext context) -> {
                    return _hMget(key, fields);
                },
                context -> {
                    logger.error("REDIS ERROR: hMget =>  key:" + key
                            + ", retrying at " + context.getRetryCount()
                            + " times", context.getLastThrowable());
                    return _hMget(key, fields);
                }
        );
        return result;
    }

    private List<String> _hMget(final String key, final String... fields) {
        return stringRedisTemplate.execute((RedisCallback<List<String>>) redisConnection -> {
            RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
            byte[] btKey = serializer.serialize(key);
            List<String> resultList = new ArrayList<String>();
            if (redisConnection.exists(btKey)) {
                List<byte[]> list = new ArrayList<>(fields.length);
                for (String field : fields) {
                    list.add(serializer.serialize(field));
                }
                List<byte[]> btList = redisConnection.hMGet(btKey, list.toArray(new byte[list.size()][]));
                resultList.addAll(btList.stream().map(serializer::deserialize).collect(Collectors.toList()));
            }
            return resultList;
        });
    }

    @Override
    public List<Object> hMget(String key, List<String> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return Lists.newArrayList();
        }

        List<Object> result = retryTemplate.execute(
                (RetryContext context) -> {
                    List<Object> objFields = Lists.transform(fields, (Function<String, Object>) s -> s);

                    return stringRedisTemplate.opsForHash().multiGet(key, objFields);
                },
                context -> {
                    logger.error("REDIS ERROR: hMget =>  key:" + key
                            + ", retrying at " + context.getRetryCount()
                            + " times", context.getLastThrowable());
                    List<Object> objFields = Lists.transform(fields, (Function<String, Object>) s -> s);

                    return stringRedisTemplate.opsForHash().multiGet(key, objFields);
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
                (RetryContext context) -> {
                    return _hMgetMap(key, fields);
                },
                context -> {
                    logger.error("REDIS ERROR: hMgetMap =>  key:" + key
                            + ", retrying at " + context.getRetryCount()
                            + " times", context.getLastThrowable());
                    return _hMgetMap(key, fields);
                }
        );
        return result;
    }

    private Map<String, String> _hMgetMap(final String key, final String... fields) {
        return stringRedisTemplate.execute((RedisCallback<Map<String, String>>) redisConnection -> {
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
        });
    }

    @Override
    public void sSet(String key, String value, long expiredSeconds) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.opsForSet().add(key, value);
                    stringRedisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: sSet =>  key:" + key
                                    + ",value:" + value + ",expiredSeconds:" + expiredSeconds + ",retrying at "
                                    + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForSet().add(key, value);
                    stringRedisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public String hGet(String key, String hashKey) {
        String result = retryTemplate.execute(
                (RetryContext context) -> {
                    return StringUtil.getValue(stringRedisTemplate.opsForHash().get(key, hashKey));
                },
                context -> {
                    logger.error("REDIS ERROR: hGet =>  key:" + key
                            + ", retrying at " + context.getRetryCount() + " times", context.getLastThrowable());
                    return StringUtil.getValue(stringRedisTemplate.opsForHash().get(key, hashKey));
                }
        );
        return result;
    }

    @Override
    public byte hAdd(String key, String hashKey, String value) {
        byte result = retryTemplate.execute(
                (RetryContext context) -> {
                    byte t = stringRedisTemplate.opsForHash().hasKey(key, hashKey) ? (byte) 0 : (byte) 1;
                    stringRedisTemplate.opsForHash().put(key, hashKey, value);
                    return t;
                },
                context -> {
                    logger.error("REDIS ERROR: hAdd =>  key:" + key
                            + ",hashKey:" + hashKey + " , value:" + value + ", retrying at " + context.getRetryCount()
                            + " times", context.getLastThrowable());
                    byte t = stringRedisTemplate.opsForHash().hasKey(key, hashKey) ? (byte) 0 : (byte) 1;
                    stringRedisTemplate.opsForHash().put(key, hashKey, value);
                    return t;
                }
        );
        return result;
    }

    @Override
    public void lPush(String key, String value) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.opsForList().leftPush(key, value);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: lPush =>  key:" + key
                                    + ",value:" + value + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForList().leftPush(key, value);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public Long lSize(String key) {
        if (StringUtils.isBlank(key)) {
            return 0L;
        }
        return retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForList().size(key);
                },
                context -> {
                    logger.error("REDIS ERROR: listSize =>  key:" + key
                                    + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    return stringRedisTemplate.opsForList().size(key);
                }
        );
    }

    @Override
    public String lPop(String key) {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }
        return retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForList().leftPop(key);
                },
                context -> {
                    logger.error("REDIS ERROR: lPop =>  key:" + key
                                    + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    return stringRedisTemplate.opsForList().leftPop(key);
                }
        );
    }

    @Override
    public String lPop(String key, long timeout, TimeUnit unit) {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }
        return retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForList().leftPop(key, timeout, unit);
                },
                context -> {
                    logger.error("REDIS ERROR: lPop =>  key:" + key
                                    + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    return stringRedisTemplate.opsForList().leftPop(key);
                }
        );
    }


    @Override
    public void rPush(String key, String value) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.opsForList().rightPush(key, value);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: rPush =>  key:" + key
                                    + ",value:" + value + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForList().rightPush(key, value);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public void rPushAll(String key, List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.opsForList().rightPushAll(key, list);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: rPushAll =>  key:" + key
                                    + ",list:" + list + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForList().rightPushAll(key, list);
                    return StringUtils.EMPTY;
                });
    }

    @Override
    public String rPop(String key) {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }
        return retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForList().rightPop(key);
                },
                context -> {
                    logger.error("REDIS ERROR: rPop =>  key:" + key
                                    + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    return stringRedisTemplate.opsForList().rightPop(key);
                }
        );
    }

    @Override
    public String rPop(String key, long timeout, TimeUnit unit) {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }
        return retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForList().rightPop(key, timeout, unit);
                },
                context -> {
                    logger.error("REDIS ERROR: rPop =>  key:" + key
                                    + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    return stringRedisTemplate.opsForList().rightPop(key);
                }
        );
    }

    @Override
    public void sRem(String key, String... member) {
        Object[] obj = member;
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.opsForSet().remove(key, obj);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: sRem =>  key:" + key
                                    + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForSet().remove(key, obj);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public void sSet(String key, String value) {
        if (value == null) {
            return;
        }
        retryTemplate.execute(
                (RetryContext context) -> {
                    stringRedisTemplate.opsForSet().add(key, value);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: sSet =>  key:" + key
                                    + ",value:" + value + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForSet().add(key, value);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public void hAdd(String key, String hashKey, String value, long overtime) {
        retryTemplate.execute(
                (RetryContext content) -> {
                    stringRedisTemplate.opsForHash().put(key, hashKey, value);
                    stringRedisTemplate.expire(key, overtime, TimeUnit.SECONDS);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: hAdd =>  key:" + key
                                    + ",value:" + value + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForHash().put(key, hashKey, value);
                    stringRedisTemplate.expire(key, overtime, TimeUnit.SECONDS);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public void hAddAll(String key, Map<String, String> map) {
        retryTemplate.execute(
                (RetryContext content) -> {
                    stringRedisTemplate.opsForHash().putAll(key, map);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: hAdd =>  key:" + key
                                    + ",value:" + JSONObject.toJSON(map) + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForHash().putAll(key, map);
                    return StringUtils.EMPTY;
                }
        );
    }

    @Override
    public void hAddAll(String key, Map<String, String> map, long overtime) {
        retryTemplate.execute(
                (RetryContext content) -> {
                    stringRedisTemplate.opsForHash().putAll(key, map);
                    stringRedisTemplate.expire(key, overtime, TimeUnit.SECONDS);
                    return StringUtils.EMPTY;
                },
                context -> {
                    logger.error("REDIS ERROR: hAdd =>  key:" + key
                                    + ",value:" +  JSONObject.toJSON(map) + ",retrying at " + context.getRetryCount() + " times",
                            context.getLastThrowable());
                    stringRedisTemplate.opsForHash().putAll(key, map);
                    stringRedisTemplate.expire(key, overtime, TimeUnit.SECONDS);
                    return StringUtils.EMPTY;
                }
        );
    }


    @Override
    public Set<String> sMember(String key) {
        Set<String> result = retryTemplate.execute(
                (RetryContext context) -> {
                    return stringRedisTemplate.opsForSet().members(key);
                },
                context -> {
                    logger.error("REDIS ERROR: sMember =>  key:" + key
                            + ",retrying at " + context.getRetryCount()
                            + " times", context.getLastThrowable());
                    return stringRedisTemplate.opsForSet().members(key);
                }
        );
        return result;
    }
}
