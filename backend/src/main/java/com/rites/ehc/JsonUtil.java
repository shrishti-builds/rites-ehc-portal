package com.rites.ehc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static String unescape(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    public static String insertBeforeEnd(String json, String fragment) {
        int index = json.lastIndexOf('}');
        if (index < 0) {
            return "{}";
        }
        return json.substring(0, index) + fragment + json.substring(index);
    }

    public static String replaceOrAddField(String json, String field, String value) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.replaceFirst(Matcher.quoteReplacement("\"" + field + "\":\"" + escape(value) + "\""));
        }
        return insertBeforeEnd(json, ",\"" + field + "\":\"" + escape(value) + "\"");
    }

    public static java.util.Optional<String> jsonValue(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? java.util.Optional.of(unescape(matcher.group(1))) : java.util.Optional.empty();
    }
}
