package com.example;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class RuleParser {

    public static List<String> formatListByType(String type,List<String> list) {
      if(type.equals("string") || type.equals("date")) {
        return list.stream().map(s -> "\"" + s + "\"").collect(Collectors.toList());
      }
      return list;
    }
    
    public static void main(String[] args) throws JsonSyntaxException, java.text.ParseException{
        String json = """
           [
                  {
                    "name": "paymentMode",
                    "operator": "eq",
                    "valueType": "string",
                    "values": ["COD"]
                  },
                  {
                    "name": "weight",
                    "operator": "gt",
                    "valueType": "number",
                    "values": ["10"]
                  },
                  {
                    "name": "zoneWise",
                    "operator": "in",
                    "valueType": "string",
                    "values": ["A","B"]
                  },
                  {
                    "name": "orderValue",
                    "operator": "le",
                    "valueType": "number",
                    "values": ["10"]
                  },
                  {
                    "name": "toPickupPincodes",
                    "operator": "in",
                    "valueType": "number",
                    "values": ["101101","248001"]
                  },
                  {
                    "name": "fromPickupPincodes",
                    "operator": "in",
                    "valueType": "string",
                    "values": ["101101","248001"]
                  },
                  {
                      "name": "fromCity",
                      "operator": "in",
                      "valueType": "string",
                      "values": ["Delhi","Mumbai"]
                  },
                  {
                      "name": "toCity",
                      "operator": "in",
                      "valueType": "string",
                      "values": ["Delhi","Mumbai"]
                  },
                  {
                    "name": "awbAssignedTime",
                    "operator": "range",
                    "valueType": "date",
                    "values": ["2024-03-02","2024-04-02"]
                  },
                  {
                    "name": "orderDate",
                    "operator": "gt",
                    "valueType": "date",
                    "values": ["2024-03-02"]
                  }
                ]
                """;
        Gson gson = new Gson();
        Type rulesListType = new TypeToken<List<RuleProperty>>() {}.getType();
        List<RuleProperty> rules = gson.fromJson(json, rulesListType);
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
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        context.setVariable("paymentMode", "COD");
        context.setVariable("weight", 12);
        context.setVariable("zoneWise", "A");
        context.setVariable("orderValue", 10);
        context.setVariable("toPickupPincodes", 101101);
        context.setVariable("fromPickupPincodes", "101101");
        context.setVariable("fromCity", "Delhi");
        context.setVariable("toCity", "Delhi");
        context.setVariable("awbAssignedTime", dateFormat.parse("2024-03-12"));
        context.setVariable("orderDate", dateFormat.parse("2024-03-12"));
        context.setVariable("dateFormatter", dateFormat);
        
        boolean rulePassed = parser.parseExpression(ruleSPELExpression).getValue(context, Boolean.class);
        // Evaluate an expression comparing a string with a constant
        System.out.println(rulePassed);
    }
}