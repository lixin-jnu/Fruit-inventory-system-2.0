package com.lixin.fruit.dao.impl;

import com.lixin.fruit.dao.FruitDAO;
import com.lixin.fruit.pojo.Fruit;
import com.lixin.myssm.basedao.BaseDAO;

import java.util.List;

public class FruitDAOImpl extends BaseDAO<Fruit> implements FruitDAO {

    //ok
    @Override
    public List<Fruit> getFruitList(String keyword, Integer pageNo) {
        return super.executeQuery("select * from fruit where fname like ? limit ?, 5", "%" + keyword + "%", (pageNo - 1) * 5);
    }

    //ok
    @Override
    public Fruit getFruitByFid(Integer fid) {
        return super.load("select * from fruit where fid = ?", fid);
    }

    //ok
    @Override
    public void updateFruit(Fruit fruit) {
        String sql = "update fruit set fname = ?, price = ?, fcount = ?, remark = ? where fid = ?";
        super.executeUpdate(sql, fruit.getFname(), fruit.getPrice(), fruit.getFcount(), fruit.getRemark(), fruit.getFid());
    }

    //ok
    @Override
    public void delFruit(Integer fid) {
        super.executeUpdate("delete from fruit where fid = ?", fid);
    }

    //ok
    @Override
    public void addFruit(Fruit fruit) {
        String sql = "insert into fruit values(0, ?, ?, ?, ?)";
        super.executeUpdate(sql, fruit.getFname(), fruit.getPrice(), fruit.getFcount(), fruit.getRemark());
    }

    //ok
    @Override
    public int getFruitCount(String keyword) {
        return ((Long) super.executeComplexQuery("select count(*) from fruit where fname like ?", "%" + keyword + "%")[0]).intValue();
    }

}