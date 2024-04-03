package com.example;

import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RuleParser {
    
    private static List<String> formatListByType(String type,List<String> list) {
      if(type.equals("string") || type.equals("date")) {
        return list.stream().map(s -> "\"" + s + "\"").collect(Collectors.toList());
      }
      return list;
    }
    
    public static String prepareSPELExpression(List<RuleProperty> rules) throws JsonSyntaxException, java.text.ParseException {
        List<String> conditions = new ArrayList<>();
        for(RuleProperty rp: rules) {
            String propertyName = rp.getName();
            List<String> typeFormattedList = formatListByType(rp.getValueType(),rp.getValues());
            switch (rp.getOperator()) {
                case "range":
                    String from = typeFormattedList.get(0);
                    String to = typeFormattedList.get(1);
                    if("date".equals(rp.getValueType())) {
                      conditions.add("#"+propertyName+ ".after(#dateFormatter.parse("+from+")) and #"+propertyName+".before(#dateFormatter.parse("+to+"))");
                    } else {
                      conditions.add("#"+propertyName+ " gt "+from+" and #"+ propertyName + " lt "+to);
                    }
                    break;
                case "in":
                    conditions.add("T(com.example.StringUtils).equalsAny(#"+ propertyName +","+ String.join(",", typeFormattedList) +")");
                    break;
                case "eq","gt","ge","lt","le":
                    String val = typeFormattedList.get(0);
                    if(rp.getValueType().equals("date")) {
                      if(rp.getOperator().equals("lt")) {
                        conditions.add("#"+propertyName+ ".before(#dateFormatter.parse("+val+"))");
                      } else if(rp.getOperator().equals("gt")) {
                        conditions.add("#"+propertyName+ ".after(#dateFormatter.parse("+val+"))");
                      }
                    } else {
                      conditions.add("#"+propertyName+" "+ rp.getOperator() +" "+val);
                    }
                    break;
                default:
                    break;
            }
        }
        String ruleSPELExpression = String.join(" and ", conditions);
        System.out.println("spel expr:"+ruleSPELExpression);
        return ruleSPELExpression;
    }
}