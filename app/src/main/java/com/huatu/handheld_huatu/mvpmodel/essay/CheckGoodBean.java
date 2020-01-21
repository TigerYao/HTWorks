package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;


public class CheckGoodBean implements Serializable {


    /**
     * id : 4
     * inventory : 10
     * name : 套题批改5次
     * num : 55
     * price : 200
     * salesNum : 0
     * type : 1
     */

    public int id;
    public int inventory;//库存
    public String name;
    public int num;
    public int unit;//商品对应批改次数
    public int price;
    public float doublePrice;
    public int  activityPrice;
    public float  doubleActivityPrice;
    public int salesNum;
    public int type;//商品类型
    public int userSetCount;
    public int selectedUserSetCount;
    public boolean isSelected;
    public long expireDate;//有效期 新增字段
    public long expireFlag;//	0无过期时间1有过期时间
    public int correctMode;//新增字段--批改类型（1智能批改2 人工批改）

//    activityPrice	活动价格（单元：分）	number	@mock=$order(73304,73304)
//    correctMode	新增字段--批改类型（1智能批改2 人工批改）	number	@mock=$order(88687,88687)
//    expireDate	有效期	number	@mock=$order(12657,12657)
//    id	ID	number	@mock=$order(4,4)
//    inventory	库存	number	@mock=$order(10,10)
//    name	商品名称	string	@mock=$order('套题批改5次','套题批改5次')
//    price	价格（单位：分）	number	@mock=$order(200,200)
//    type	商品类型	number	@mock=$order(1,1)
//    unit	商品对应批改次数	number	@mock=$order(55,55)

    public int getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(int activityPrice) {
        this.activityPrice = activityPrice;
    }



    public float getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSalesNum() {
        return salesNum;
    }

    public void setSalesNum(int salesNum) {
        this.salesNum = salesNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
