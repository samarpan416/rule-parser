package com.example;

import java.util.List;

public class RuleProperty {
    private String name;    
    private String operator;
    private String valueType;
    private List<String> values;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public String getValueType() {
        return valueType;
    }
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    
    public List<String> getValues() {
        return values;
    }
    public void setValues(List<String> values) {
        this.values = values;
    }
    @Override
    public String toString() {
        return "RuleProperty [name=" + name + ", operator=" + operator + ", valueType=" + valueType + ", values=" + values + "]";
    }
    
}
