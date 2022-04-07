package com.lixin.myssm.filters;

import com.lixin.myssm.trans.TransactionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.sql.SQLException;

@WebFilter("*.do")
public class OpenSessionInViewFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        /*
         * 一个业务方法中的多个DAO方法应该作为一个整体,要不然都成功,要不然都失败;
         * 这里为了能够在catch部分捕获到某个DAO操作异常,从而使数据库回滚该业务之前的DAO操作;
         * 需要使其内部的所有方法外抛异常;
         * 如果内部try-catch处理掉就没办法catch到异常从而回滚事务;
         */
        try {
            //开启事务
            TransactionManager.beginTrans();
            filterChain.doFilter(servletRequest, servletResponse);
            //提交事务
            TransactionManager.commit();
        } catch (ServletException | IOException | SQLException e) {
            e.printStackTrace();
            try {
                //回滚事务
                TransactionManager.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
    }

}