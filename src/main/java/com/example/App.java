package com.example;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Hello world!
 *
 */
public class App 
{
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static void main( String[] args ) throws JsonSyntaxException, ParseException
    {
        String rulesJson = """
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
        List<RuleProperty> rules = gson.fromJson(rulesJson, rulesListType);
        String ruleSPELExpression = RuleParser.prepareSPELExpression(rules);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
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
