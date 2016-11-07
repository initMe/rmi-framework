package com.pay.business.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pay.business.bean.Orders;

@Repository
public interface OrdersMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Orders record);

	int insertSelective(Orders record);

	Orders selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Orders record);

	int updateByPrimaryKey(Orders record);

	Orders selectByNo(String outTradeNo);

	List<Orders> getOrders();
}