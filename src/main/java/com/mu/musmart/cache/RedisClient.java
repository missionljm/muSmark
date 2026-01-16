package com.mu.musmart.cache;

import com.google.common.collect.Maps;
import com.mu.musmart.util.JsonUtil;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author missionLjm
 * @date 2023/2/7
 */
public class RedisClient {
    private static final Charset CODE = StandardCharsets.UTF_8;
    private static final String KEY_PREFIX = "muSmart_";
    private static final Logger log = LoggerFactory.getLogger(RedisClient.class);
    private static RedissonClient redissonClient;

    public static void register(RedissonClient redissonClient) {
        log.info("redis init");
        RedisClient.redissonClient = redissonClient;
    }

    public static boolean isRedissonAvailable() {
        return redissonClient != null;
    }

    public static void nullCheck(Object... args) {
        for (Object obj : args) {
            if (obj == null) {
                throw new IllegalArgumentException("redis argument can not be null!");
            }
        }
    }

    /**
     * 技术派的缓存值序列化处理
     *
     * @param val
     * @param <T>
     * @return
     */
    public static <T> String serialize(T val) {
        if (val instanceof String) {
            return (String) val;
        } else {
            return JsonUtil.toStr(val);
        }
    }

    /**
     * 生成技术派的缓存key
     *
     * @param key
     * @return
     */
    public static String generateKey(String key) {
        nullCheck(key);
        return KEY_PREFIX + key;
    }

    /**
     * 返回key的有效期
     *
     * @param key
     * @return
     */
    public static Long ttl(String key) {
        if (redissonClient == null) {
            return null;
        }
        RBucket<String> bucket = redissonClient.getBucket(generateKey(key), StringCodec.INSTANCE);
        return bucket.remainTimeToLive();
    }

    /**
     * 查询缓存
     *
     * @param key
     * @return
     */
    public static String getStr(String key) {
        if (redissonClient == null) {
            return null; // RedissonClient未初始化，返回null
        }
        RBucket<String> bucket = redissonClient.getBucket(generateKey(key), StringCodec.INSTANCE);
        return bucket.get();
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    public static void setStr(String key, String value) {
        if (redissonClient == null) {
            // RedissonClient未初始化，跳过操作
            return;
        }
        RBucket<String> bucket = redissonClient.getBucket(generateKey(key), StringCodec.INSTANCE);
        bucket.set(value);
    }

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    public static void del(String key) {
        if (redissonClient == null) {
            // RedissonClient未初始化，跳过操作
            return;
        }
        RBucket<String> bucket = redissonClient.getBucket(generateKey(key), StringCodec.INSTANCE);
        bucket.delete();
    }

    /**
     * 设置缓存有效期
     *
     * @param key
     * @param expire 有效期，s为单位
     */
    public static void expire(String key, Long expire) {
        if (redissonClient == null) {
            return;
        }
        RBucket<String> bucket = redissonClient.getBucket(generateKey(key), StringCodec.INSTANCE);
        bucket.expire(java.time.Duration.ofSeconds(expire));
    }

    /**
     * 带过期时间的缓存写入
     *
     * @param key
     * @param value
     * @param expire s为单位
     * @return
     */
    public static Boolean setStrWithExpire(String key, String value, Long expire) {
        if (redissonClient == null) {
            return false;
        }
        RBucket<String> bucket = redissonClient.getBucket(generateKey(key), StringCodec.INSTANCE);
        return bucket.trySet(value, expire, TimeUnit.SECONDS);
    }

    public static <T> Map<String, T> hGetAll(String key, Class<T> clz) {
        if (redissonClient == null) {
            return Collections.emptyMap();
        }
        RMap<String, String> map = redissonClient.getMap(generateKey(key), StringCodec.INSTANCE);
        Map<String, String> entries = map.readAllMap();
        if (entries == null) {
            return Collections.emptyMap();
        }

        Map<String, T> result = Maps.newHashMapWithExpectedSize(entries.size());
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            result.put(entry.getKey(), toObj(entry.getValue(), clz));
        }
        return result;
    }

    public static <T> T hGet(String key, String field, Class<T> clz) {
        if (redissonClient == null) {
            return null;
        }
        RMap<String, String> map = redissonClient.getMap(generateKey(key), StringCodec.INSTANCE);
        String value = map.get(field);
        return toObj(value, clz);
    }

    /**
     * 自增
     *
     * @param key
     * @param field
     * @param cnt
     * @return
     */
    public static Long hIncr(String key, String field, Integer cnt) {
        if (redissonClient == null) {
            return 0L;
        }
        RMap<String, String> map = redissonClient.getMap(generateKey(key), StringCodec.INSTANCE);
        Long value = Long.valueOf(map.addAndGet(field, cnt));
        return value;
    }

    public static <T> Boolean hDel(String key, String field) {
        if (redissonClient == null) {
            return false;
        }
        RMap<String, String> map = redissonClient.getMap(generateKey(key), StringCodec.INSTANCE);
        return map.remove(field) != null;
    }

    public static <T> Boolean hSet(String key, String field, T ans) {
        if (redissonClient == null) {
            return false;
        }
        RMap<String, String> map = redissonClient.getMap(generateKey(key), StringCodec.INSTANCE);
        String value = serialize(ans);
        map.put(field, value);
        return true;
    }

    public static <T> void hMSet(String key, Map<String, T> fields) {
        if (redissonClient == null) {
            return;
        }
        RMap<String, String> map = redissonClient.getMap(generateKey(key), StringCodec.INSTANCE);
        Map<String, String> stringMap = new HashMap<>();
        for (Map.Entry<String, T> entry : fields.entrySet()) {
            stringMap.put(entry.getKey(), serialize(entry.getValue()));
        }
        map.putAll(stringMap);
    }

    public static <T> Map<String, T> hMGet(String key, final List<String> fields, Class<T> clz) {
        if (redissonClient == null) {
            return Collections.emptyMap();
        }
        RMap<String, String> map = redissonClient.getMap(generateKey(key), StringCodec.INSTANCE);
        Set<String> fieldSet = new HashSet<>(fields);
        Map<String, String> allValues = map.getAll(fieldSet);
        Map<String, T> result = Maps.newHashMapWithExpectedSize(fields.size());
        for (String field : fields) {
            result.put(field, toObj(allValues.get(field), clz));
        }
        return result;
    }

    /**
     * 判断value是否再set中
     *
     * @param key
     * @param value
     * @return
     */
    public static <T> Boolean sIsMember(String key, T value) {
        if (redissonClient == null) {
            return false;
        }
        RSet<String> set = redissonClient.getSet(generateKey(key), StringCodec.INSTANCE);
        String valueStr = serialize(value);
        return set.contains(valueStr);
    }

    /**
     * 获取set中的所有内容
     *
     * @param key
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> Set<T> sGetAll(String key, Class<T> clz) {
        if (redissonClient == null) {
            return Collections.emptySet();
        }
        RSet<String> set = redissonClient.getSet(generateKey(key), StringCodec.INSTANCE);
        Set<String> stringSet = set.readAll();
        if (stringSet == null) {
            return Collections.emptySet();
        }
        return stringSet.stream().map(s -> toObj(s, clz)).collect(Collectors.toSet());
    }

    /**
     * 往set中添加内容
     *
     * @param key
     * @param val
     * @param <T>
     * @return
     */
    public static <T> boolean sPut(String key, T val) {
        if (redissonClient == null) {
            return false;
        }
        RSet<String> set = redissonClient.getSet(generateKey(key), StringCodec.INSTANCE);
        String value = serialize(val);
        return set.add(value);
    }

    /**
     * 移除set中的内容
     *
     * @param key
     * @param val
     * @param <T>
     */
    public static <T> void sDel(String key, T val) {
        if (redissonClient == null) {
            return;
        }
        RSet<String> set = redissonClient.getSet(generateKey(key), StringCodec.INSTANCE);
        String value = serialize(val);
        set.remove(value);
    }

    /**
     * 分数更新
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public static Double zIncrBy(String key, String value, Integer score) {
        if (redissonClient == null) {
            return 0.0;
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        return sortedSet.addScore(value, score);
    }

    public static Double zScore(String key, String value) {
        if (redissonClient == null) {
            return 0.0;
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        return sortedSet.getScore(value);
    }

    public static Integer zRank(String key, String value) {
        if (redissonClient == null) {
            return -1;
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        Integer rank = sortedSet.rank(value);
        return rank != null ? rank : -1;
    }

    /**
     * 找出排名靠前的n个
     *
     * @param key
     * @param n
     * @return
     */
    public static List<org.apache.commons.lang3.tuple.ImmutablePair<String, Double>> zTopNScore(String key, int n) {
        if (redissonClient == null) {
            return Collections.emptyList();
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        Collection<org.redisson.client.protocol.ScoredEntry<String>> entries = sortedSet.entryRange(-n, -1);
        if (entries == null) {
            return Collections.emptyList();
        }
        return entries.stream()
                .map(entry -> org.apache.commons.lang3.tuple.ImmutablePair.of(entry.getValue(), entry.getScore()))
                .sorted((o1, o2) -> Double.compare(o2.getRight(), o1.getRight())).collect(Collectors.toList());
    }

    public static <T> Long lPush(String key, T val) {
        if (redissonClient == null) {
            return 0L;
        }
        RList<String> list = redissonClient.getList(generateKey(key), StringCodec.INSTANCE);
        String value = serialize(val);
        list.add(0, value); // 在开头添加元素
        return (long) list.size(); // 返回列表长度
    }

    public static <T> Long rPush(String key, T val) {
        if (redissonClient == null) {
            return 0L;
        }
        RList<String> list = redissonClient.getList(generateKey(key), StringCodec.INSTANCE);
        String value = serialize(val);
        list.add(value); // 在末尾添加元素
        return (long) list.size(); // 返回列表长度
    }

    public static <T> List<T> lRange(String key, int start, int size, Class<T> clz) {
        if (redissonClient == null) {
            return new ArrayList<>();
        }
        RList<String> list = redissonClient.getList(generateKey(key), StringCodec.INSTANCE);
        List<String> stringList = list.range(start, start + size - 1);
        if (stringList == null) {
            return new ArrayList<>();
        }
        return stringList.stream().map(k -> toObj(k, clz)).collect(Collectors.toList());
    }

    public static void lTrim(String key, int start, int size) {
        if (redissonClient == null) {
            return;
        }
        RList<String> list = redissonClient.getList(generateKey(key), StringCodec.INSTANCE);
        list.trim(start, start + size - 1);
    }

    // ZSet 相关操作 - 用于延迟队列
    public static <T> Boolean zAdd(String key, T value, double score) {
        if (redissonClient == null) {
            return false; // RedissonClient未初始化，返回false
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        String valueStr = serialize(value);
        return sortedSet.add(score, valueStr);
    }

    public static <T> Set<T> zRangeByScore(String key, double min, double max, Class<T> clz) {
        if (redissonClient == null) {
            return Collections.emptySet();
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        Collection<String> values = sortedSet.valueRange(min, true, max, true);
        if (values == null) {
            return Collections.emptySet();
        }
        return values.stream().map(s -> toObj(s, clz)).collect(Collectors.toSet());
    }

    public static <T> Set<T> zRangeWithScores(String key, long start, long end, Class<T> clz) {
        if (redissonClient == null) {
            return Collections.emptySet();
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        Collection<org.redisson.client.protocol.ScoredEntry<String>> entries = sortedSet.entryRange((int)start, (int)end);
        if (entries == null) {
            return Collections.emptySet();
        }
        return entries.stream().map(entry -> toObj(entry.getValue(), clz)).collect(Collectors.toSet());
    }

    public static Long zRem(String key, String... values) {
        if (redissonClient == null) {
            return 0L; // RedissonClient未初始化，返回0
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        int count = 0;
        for (String value : values) {
            if (sortedSet.remove(value)) {
                count++;
            }
        }
        return (long) count;
    }

    public static Set<String> zRangeByScoreForString(String key, double min, double max) {
        if (redissonClient == null) {
            return Collections.emptySet(); // RedissonClient未初始化，返回空集合
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        Collection<String> values = sortedSet.valueRange(min, true, max, true);
        if (values == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(values);
    }

    public static Long zCard(String key) {
        if (redissonClient == null) {
            return 0L;
        }
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(generateKey(key), StringCodec.INSTANCE);
        return (long) sortedSet.size();
    }

    private static <T> T toObj(String ans, Class<T> clz) {
        if (ans == null) {
            return null;
        }

        if (clz == String.class) {
            return (T) ans;
        }

        return JsonUtil.toObj(ans, clz);
    }
}