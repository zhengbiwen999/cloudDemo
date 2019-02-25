package com.zbw.config;

import java.sql.Statement;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
        @Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
        @Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }) })
public class SqlHandler implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        final BoundSql boundSql = statementHandler.getBoundSql();

        Exception ex = null;
        try {
            Object obj = boundSql.getParameterObject();


            boundSql.setAdditionalParameter("ficityid",2);

            String sql = boundSql.getSql();

            System.out.printf("sql ==>"+sql);

            return invocation.proceed();

        } catch (Exception es) {
            ex = es;
            throw ex;
        } finally {
//            statsDClient.recordExecutionTime("sql.time", end, 1);

        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    @Override
    public void setProperties(Properties properties) {
    }
}
