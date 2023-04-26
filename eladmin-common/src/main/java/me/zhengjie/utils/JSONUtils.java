package me.zhengjie.utils;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import me.zhengjie.exception.BadRequestException;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONUtils {

    public static boolean isObj(JSONObject json, String key){
        if (json.getStr(key).indexOf("{")==0)
            return true;
        else if (json.getStr(key).indexOf("[")==0){
            return false;
        }
        throw new BadRequestException("非JSON字符串");
    }
    public static boolean isObj(com.alibaba.fastjson.JSONObject  json,String key){
        if (json.getString(key).indexOf("{")==0)
            return true;
        else if (json.getString(key).indexOf("[")==0){
            return false;
        }
        throw new BadRequestException("非JSON字符串");
    }

    public static String escape(String string) {
        final StringBuilder sb = new StringBuilder(string.length());
        for (int i = 0, length = string.length(); i < length; i++) {
            char c = string.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String toXml(Object object) throws JSONException {
        return toXml(object, null);
    }

    public static String toXml(Object object, String tagName) throws JSONException {
        StringBuilder sb = new StringBuilder();
        JSONArray ja;
        JSONObject jo;
        String key;
        Iterator<String> keys;
        String string;
        Object value;

        if (object instanceof JSONObject) {

            // Emit <tagName>
            if (tagName != null) {
                sb.append('<');
                sb.append(tagName);
                sb.append('>');
            }

            // Loop thru the keys.
            jo = (JSONObject) object;
            keys = jo.keySet().iterator();
            while (keys.hasNext()) {
                key = keys.next();
                value = jo.get(key);
                if (value == null) {
                    value = "";
                } else if (value.getClass().isArray()) {
                    value = new JSONArray(value);
                }
                string = value instanceof String ? (String) value : null;

                // Emit content in body
                if ("content".equals(key)) {
                    if (value instanceof JSONArray) {
                        ja = (JSONArray) value;
                        int i = 0;
                        for (Object val : ja) {
                            if (i > 0) {
                                sb.append('\n');
                            }
                            sb.append(escape(val.toString()));
                            i++;
                        }
                    } else {
                        sb.append(escape(value.toString()));
                    }

                    // Emit an array of similar keys

                } else if (value instanceof JSONArray) {
                    ja = (JSONArray) value;
                    for (Object val : ja) {
                        if (val instanceof JSONArray) {
                            sb.append('<');
                            sb.append(key);
                            sb.append('>');
                            sb.append(toXml(val));
                            sb.append("</");
                            sb.append(key);
                            sb.append('>');
                        } else {
                            sb.append(toXml(val, key));
                        }
                    }
                } else if ("".equals(value)) {
                    sb.append('<');
                    sb.append(key);
                    sb.append("/>");

                    // Emit a new tag <k>

                } else {
                    sb.append(toXml(value, key));
                }
            }
            if (tagName != null) {

                // Emit the </tagname> close tag
                sb.append("</");
                sb.append(tagName);
                sb.append('>');
            }
            return sb.toString();

        }

        if (object != null) {
            if (object.getClass().isArray()) {
                object = new JSONArray(object);
            }

            if (object instanceof JSONArray) {
                ja = (JSONArray) object;
                for (Object val : ja) {
                    // XML does not have good support for arrays. If an array
                    // appears in a place where XML is lacking, synthesize an
                    // <array> element.
                    sb.append(toXml(val, tagName == null ? "array" : tagName));
                }
                return sb.toString();
            }
        }

        string = (object == null) ? "null" : escape(object.toString());
        return (tagName == null) ? "\"" + string + "\"" : (string.length() == 0) ? "<" + tagName + "/>" : "<" + tagName + ">" + string + "</" + tagName + ">";

    }

    public static void sortJsonToMap(com.alibaba.fastjson.JSONObject oldJson, TreeMap<String,Object> newJson) {
        TreeSet<String> newKeySet = new TreeSet<>(oldJson.keySet());
        for (String key : newKeySet) {
            Object obj=oldJson.get(key);
            if (obj instanceof com.alibaba.fastjson.JSONObject){
                TreeMap<String,Object> newJson2=new TreeMap<>();
                sortJsonToMap(oldJson.getJSONObject(key),newJson2);
                newJson.put(key,newJson2);
            }else if (obj instanceof com.alibaba.fastjson.JSONArray){
                com.alibaba.fastjson.JSONArray newArray=new com.alibaba.fastjson.JSONArray();
                sortArray(oldJson.getJSONArray(key),newArray);
                newJson.put(key,newArray);
            }else {
                newJson.put(key,oldJson.get(key));
            }
        }
    }
    public static void sortArray(com.alibaba.fastjson.JSONArray oldArray,com.alibaba.fastjson.JSONArray newArray){
        for (int i = 0; i < oldArray.size(); i++) {
            Object obj=oldArray.get(i);
            if (obj instanceof com.alibaba.fastjson.JSONObject){
                TreeMap<String,Object> newJson=new TreeMap<>();
                sortJsonToMap(oldArray.getJSONObject(i),newJson);
                newArray.add(newJson);
            }else if (obj instanceof com.alibaba.fastjson.JSONArray){
                com.alibaba.fastjson.JSONArray newArray2=new com.alibaba.fastjson.JSONArray();
                sortArray(oldArray.getJSONArray(i),newArray);
                newArray.add(newArray2);
            }else {
                newArray.add(oldArray.get(i));
            }
        }
    }

    public static void main(String[] args) {
        String js="{\"lading_bill_push\":{\"cargo_lading_list\":[{\"quantity\":10,\"quality_grade\":1,\"bar_code\":\"FLTEST1234\",\"cargo_name\":\"宁波富立测试\",\"external_code\":\"FLTEST\",\"cargo_code\":\"7012212013648691500\"}],\"shop_id\":1111165433,\"warehouse_no\":\"FLBBC01\",\"vendor\":\"NBFL\",\"remark\":\"测试测试\",\"outbound_plan_no\":\"7012891433770238252\",\"outbound_date\":\"2021-09-2923:59:59\",\"outbound_type\":2},\"msg_type\":\"lading_bill_push\",\"logid\":\"20210928162329010225094075040699B4\"}";
        com.alibaba.fastjson.JSONObject object= JSON.parseObject(js);
        TreeMap<String,Object> map=new TreeMap<>();
        sortJsonToMap(object,map);
        System.out.println(JSON.toJSONString(map));
    }

    public static boolean isObj(String jsonStr) {
        return jsonStr != null && (jsonStr.indexOf("{") == 0 && jsonStr.lastIndexOf("}") == jsonStr.length() - 1);
    }

    public static boolean isXml(String xmlStr) {
        if (xmlStr==null) return false;
        xmlStr=StringUtil.removeEscape(xmlStr);
        return xmlStr.indexOf("<")==0&&xmlStr.lastIndexOf(">")==xmlStr.length()-1;
    }
}
