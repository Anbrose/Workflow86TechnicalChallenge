package com.company;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Main {

    private static final String INDENT = "    ";

    private static String addIndent(int num){
        StringBuilder indent = new StringBuilder();
        int offset = 0;
        while (offset < num){
            indent.append(INDENT);
            offset++;
        }
        return indent.toString();
    }

    private static String readFromFile(String filepath){
        StringBuilder content = new StringBuilder();

        try (FileInputStream fileInputStream = new FileInputStream(filepath);) {
            int ch;
            while ((ch = fileInputStream.read()) != -1){
                content.append((char) ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    private static Map<String, Object> createStudent(String name, boolean needSupport) {
        Map<String, Object> student = new HashMap<>();
        student.put("name", name);
        student.put("needSupport", needSupport);
        return student;
    }

    private static String mapToJson(Map<String, Object> map, int level) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        String indent = addIndent(level + 1);

        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            json.append(indent).append("\"").append(entry.getKey()).append("\": ");

            if (entry.getValue() instanceof Map) {
                json.append(mapToJson((Map<String, Object>) entry.getValue(), level + 1));
            } else if (entry.getValue() instanceof List) {
                json.append(listToJson((List<Object>) entry.getValue(), level + 1));
            } else if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }

            if (iterator.hasNext()) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append(addIndent(level)).append("}");
        return json.toString();
    }

    private static String listToJson(List<Object> list, int level) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        String indent = addIndent(level + 1);

        Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            json.append(indent);

            if (element instanceof Map) {
                json.append(mapToJson((Map<String, Object>) element, level + 1));
            } else if (element instanceof List) {
                json.append(listToJson((List<Object>) element, level + 1));
            } else if (element instanceof String) {
                json.append("\"").append(element).append("\"");
            } else {
                json.append(element);
            }

            if (iterator.hasNext()) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append(addIndent(level)).append("]");
        return json.toString();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the JSON file's path as an argument.");
            return;
        }

        String filePath = args[0];

        String json = readFromFile(filePath);
        RobustJSONParser parser = new RobustJSONParser(json);
        Map<String, Object> result = parser.parse();

        String jsonString = mapToJson(result, 0);

        try (FileWriter file = new FileWriter("output.json")) {
            file.write(jsonString);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
