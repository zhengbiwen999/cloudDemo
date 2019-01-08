package com.zbw.utils;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface MWMapper<T> extends Mapper<T>, MySqlMapper<T> {


}
