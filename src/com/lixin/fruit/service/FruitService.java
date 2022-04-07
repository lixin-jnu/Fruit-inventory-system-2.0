package com.lixin.fruit.service;

import com.lixin.fruit.pojo.Fruit;

import java.util.List;

public interface FruitService {

    //获取指定页码上的库存列表信息,每页显示5条ok
    List<Fruit> getFruitList(String keyword, Integer pageNo);

    //根据fid获取特定的水果库存信息ok
    Fruit getFruitByFid(Integer fid);

    //修改指定的库存记录ok
    void updateFruit(Fruit fruit);

    //根据fid删除指定的库存记录ok
    void delFruit(Integer fid);

    //添加新库存记录ok
    void addFruit(Fruit fruit);

    //查询库存总记录条数ok
    int getPageCount(String keyword);

}