package com.lixin.fruit.service.impl;

import com.lixin.fruit.dao.FruitDAO;
import com.lixin.fruit.pojo.Fruit;
import com.lixin.fruit.service.FruitService;

import java.util.List;

public class FruitServiceImpl implements FruitService {

    private FruitDAO fruitDAO = null;//反射->依赖注入->实现和数据访问层的解耦

    @Override
    public List<Fruit> getFruitList(String keyword, Integer pageNo) {
        return fruitDAO.getFruitList(keyword, pageNo);
    }

    @Override
    public Fruit getFruitByFid(Integer fid) {
        return fruitDAO.getFruitByFid(fid);
    }

    @Override
    public void updateFruit(Fruit fruit) {
        fruitDAO.updateFruit(fruit);
    }

    @Override
    public void delFruit(Integer fid) {
        fruitDAO.delFruit(fid);
    }

    @Override
    public void addFruit(Fruit fruit) {
        fruitDAO.addFruit(fruit);
        //测试事务管理
        //fruitDAO.getFruitByFid(0);
    }

    @Override
    public int getPageCount(String keyword) {
        return (fruitDAO.getFruitCount(keyword) + 4) / 5;
    }

}