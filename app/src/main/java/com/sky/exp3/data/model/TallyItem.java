package com.sky.exp3.data.model;

/**
 * TallyItem 是记账条目的数据模型类，
 * 用于封装一条账单记录的数据，包括ID、类型、金额、时间。
 */
public class TallyItem {

    private int id;             // 主键ID（数据库中唯一标识）
    private String type;        // 类型：收入或支出
    private double amount;      // 金额
    private String time;        // 记账时间（字符串格式）

    // 构造方法：用于创建一个账单条目对象
    public TallyItem(int id, String type, double amount, String time) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.time = time;
    }

    // Getter 方法：获取各属性值
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getTime() {
        return time;
    }

    // Setter 方法：设置各属性值
    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
