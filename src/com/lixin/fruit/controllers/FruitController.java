package com.lixin.fruit.controllers;

import com.lixin.fruit.pojo.Fruit;
import com.lixin.fruit.service.FruitService;
import com.lixin.myssm.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class FruitController {

    private FruitService fruitService = null;//反射->依赖注入->实现和业务层的解耦

    //index
    protected String index(String oper, String keyword, Integer pageNo, HttpServletRequest request) {
        //获得HttpSession对象:在会话期间有效
        HttpSession session = request.getSession();
        //页码初始化为1(第一次请求index没有页码)
        if (pageNo == null) {
            pageNo = 1;
        }
        if (StringUtil.isNotEmpty(oper) && oper.equals("search")) {
            //如果查询的关键字为空
            if (StringUtil.isEmpty(keyword)) {
                keyword = "";
            }
            session.setAttribute("keyword", keyword);
        } else {
            //如果不是点击的查询按钮,那么查询就是基于session中保存的现有的keyword进行查询,而且这个key会一直存在
            Object o = session.getAttribute("keyword");
            if (o != null) {
                keyword = (String) o;
            } else {
                //第一次访问
                keyword = "";//不存在也要赋值为null,否则查询的时候会失败->%null%
            }
        }
        //查询pageNo页码下keyword关键字下的数据
        session.setAttribute("pageNo", pageNo);
        List<Fruit> fruitList = fruitService.getFruitList(keyword, pageNo);
        //保存到Session作用域
        session.setAttribute("fruitList", fruitList);
        int pageCount = fruitService.getPageCount(keyword);
        session.setAttribute("pageCount", pageCount);
        return "main";
    }

    //add
    public String add(String fname, Integer price, Integer fcount, String remark) {
        fruitService.addFruit(new Fruit(0, fname, price, fcount, remark));
        return "redirect:fruit.do";
    }

    //edit
    public String edit(HttpServletRequest request, Integer fid) {
        if (fid != null) {
            Fruit fruit = fruitService.getFruitByFid(fid);
            request.setAttribute("fruit", fruit);
            return "edit";
        }
        return "error";
    }

    //del
    public String del(Integer fid) {
        if (fid != null) {
            fruitService.delFruit(fid);
            return "redirect:fruit.do";
        }
        return "error";
    }

    //update
    public String update(Integer fid, String fname, Integer price, Integer fcount, String remark) {
        fruitService.updateFruit(new Fruit(fid, fname, price, fcount, remark));
        return "redirect:fruit.do";
    }

}