package com.imooc.spring.sql;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用来解析数据表account.sql文件中的sql语句用于初始化/批量操作数据库
 * 读取文件的每一行，作如下处理：
 * "--"开头，跳过这一样，不做处理；
 * "!--"开头，将这一行内容做为Pair的key，新建一个Pair；
 * 其他行如果不为空，则将每一行内容放入Deque<Pair>最后一个Pair对象的lines属性中；
 *
 * 最后将Deque<Pair>中的内容转为Map<String, Object>,存放为键值对（key-lines拼接为一条字符串）,放入MapPropertySource中
 * Created by lizhen on 2018/9/14.
 */
public class SqlPropertySourceFactory implements PropertySourceFactory {

    private static final String KEY_LEADING = "-- !";

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        Deque<Pair> queries = new LinkedList<>();

        new BufferedReader(resource.getReader()).lines().forEach(line -> {
            if (line.startsWith(KEY_LEADING)) {
                queries.addLast(new Pair(line.replaceFirst(KEY_LEADING, "")));
            } else if (line.startsWith("--")) {
                //skip comment line
            } else if (!line.trim().isEmpty()) {
                Optional.ofNullable(queries.getLast()).ifPresent(pair -> pair.lines.add(line));
            }
        });

        Map<String, Object> sqlMap = queries.stream()
                .filter(pair -> !pair.lines.isEmpty())
                .collect(Collectors.toMap(pair -> pair.key,
                        pair -> String.join(System.lineSeparator(), pair.lines),
                        (r, pair) -> r, LinkedHashMap::new));

        System.out.println("Configured SQL statements:");
        sqlMap.forEach((s, o) -> System.out.println(s + "=" + o));

        return new MapPropertySource(resource.toString(), sqlMap);
    }

    private static class Pair {
        private String key;
        private List<String> lines = new LinkedList<>();

        Pair(String key) {
            this.key = key;
        }
    }
}
