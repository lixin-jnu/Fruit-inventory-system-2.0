package com.lixin.myssm.filters;

import com.lixin.myssm.util.StringUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

@WebFilter(urlPatterns = {"*.do"}, initParams = {@WebInitParam(name = "encoding", value = "UTF-8")})
public class CharacterEncodingFilter implements Filter {

    private String encoding = "UTF-8";//默认编码为UTF-8

    @Override
    public void init(FilterConfig filterConfig) {
        String encodingStr = filterConfig.getInitParameter("encoding");//读取配置文件中对编码的设置
        if (StringUtil.isNotEmpty(encodingStr)) {//如果配置文件中的编码为空则使用默认编码
            this.encoding = encodingStr;//不为空则赋值使用
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //拦截所有以.do结尾的请求,设置编码
        servletRequest.setCharacterEncoding(this.encoding);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

}