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
