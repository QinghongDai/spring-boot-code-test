package com.example.springboot.service;

import com.example.springboot.model.Battery;
import com.example.springboot.model.BatteryQuery;
import com.example.springboot.model.BatteryResults;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import com.alibaba.fastjson.JSON;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.DoubleStream;

@Service
public class BatteryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryService.class);
    /**
     * refresh time
     */
    private static final Integer EXPIRE_SENCOND = 60 * 60;
    /**
     * memory data capacity
     */
    private static final ConcurrentHashMap<String, String> DATA_MAP = new ConcurrentHashMap<String, String>();

    /**
     * key
     */
    public static final String DATA_LIST = "data_list";

    /**
     * get data
     */
    public Map<String, String> getDataMap() {
        return DATA_MAP;
    }

    /**
     * Spring Bean: executed after created
     */
    @PostConstruct
    public void initMethod() {
        ExecutorService pool = new ThreadPoolExecutor(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1),
                new BasicThreadFactory.Builder().namingPattern("data-map-pool-%d").daemon(true).build(),
                new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new Task());
        pool.shutdown();
    }

    /**
     * init method，load data to map
     */
    private void initDataMap() {

    }

    /**
     * insert data
     */
    public void insertData(Battery battery) {
        List<Battery> originalData = getList(DATA_LIST, Battery.class);
        if (null != originalData) {
            originalData.add(battery);
            setDataMap(DATA_LIST, originalData);
        } else {
            List list = new ArrayList<>(1);
            list.add(battery);
            setDataMap(DATA_LIST, list);
        }

    }

    /**
     * query data
     */
    public BatteryResults queryData(BatteryQuery query) {
        BatteryResults res = new BatteryResults();
        List<Battery> resData = new ArrayList<>();
        List<Battery> resAllData = getList(DATA_LIST, Battery.class);

        if (null == resData){
            res.setAvgCapacity((double) 0);
            res.setBatteries(null);
            res.setTotal(0L);
        } else {
            resAllData.forEach(temp -> {
                if (temp.getPostcode() <= query.getPostcodeTo()
                        && temp.getPostcode() >= query.getPostcodeFrom()) {
                    resData.add(temp);
                }
            });

            res.setTotal(resData.stream().count());
            res.setAvgCapacity(resData.stream().flatMapToDouble((Battery) -> DoubleStream.of(Battery.getWattCapacity())).summaryStatistics().getAverage());
            resData.stream().sorted(Comparator.comparing(Battery -> Battery.getName()));
            res.setBatteries(resData);
        }
        return res;
    }

    /**
     * get map object
     */
    public static <T> T getObject(String key, Class<T> clazz) {
        return json2Object(DATA_MAP.get(key), clazz);
    }

    public static <T> List<T> getList(String key, Class<T> clazz) {
        return json2List(DATA_MAP.get(key), clazz);
    }

    private <T> void setDataMap(String key, T value) {
        DATA_MAP.put(key, object2Json(value));
    }

    private <T> void setDataMap(String key, List<T> value) {
        DATA_MAP.put(key, list2Json(value));
    }

    /**
     * json to Object
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            LOGGER.error("{} to JSON fail", json);
        }
        return null;
    }

    /**
     * object to json
     */
    public static <T> String object2Json(T object){
        String json = JSON.toJSONString(object);
        return json;
    }

    /**
     * list转json
     */
    public static <T> String list2Json(List<T> list) {
        String jsons = JSON.toJSONString(list);
        return jsons;
    }

    /**
     * get List by key
     */
    public static <T> List<T> json2List(String json, Class<T> clazz) {
        List<T> list = JSON.parseArray(json, clazz);
        return list;
    }

    /**
     * init data and refresh data
     */
    class Task implements Runnable {
        @Override
        public void run() {
            while (true) {
                initDataMap();
                LOGGER.info("###################init data#######################");
                try {
                    Thread.sleep(EXPIRE_SENCOND * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
