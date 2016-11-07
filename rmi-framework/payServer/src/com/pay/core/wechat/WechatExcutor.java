/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core.wechat   
 * 文件名：WechatExcutor.java   
 * 版本信息：   
 * 创建日期：2016年10月24日-下午3:21:17
 */
package com.pay.core.wechat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.pay.business.EnumState;
import com.pay.business.bean.Orders;
import com.pay.business.dao.OrdersMapper;
import com.pay.business.request.LedgerRequest;
import com.pay.business.request.OrderPayRequest;
import com.pay.business.request.RefundRequest;
import com.pay.business.response.LedgerResponse;
import com.pay.business.response.OrderPayResponse;
import com.pay.business.response.RefundResponse;
import com.pay.core.BaseBean;
import com.pay.core.CodeEnum;
import com.pay.core.PayExcutor;
import com.pay.core.util.PayResponseUtil;
import com.pay.core.wechat.config.WechatConfig;
import com.pay.core.wechat.util.WechatSubmit;
import com.pay.system.util.ResponseUtil;
import com.pay.system.util.SpringBeanUtil;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;
import com.utils.StringUtil;
import com.utils.UrlConnectUtil;

/**   
 * 类名：WechatExcutor   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月24日 下午3:21:17   
 * 修改备注：   
 * @version 1.0.0
 */
public class WechatExcutor implements PayExcutor {

	private OrdersMapper ordersMapper = SpringBeanUtil.getBean(OrdersMapper.class);

	private final static String payNotifyUrl = WechatConfig.notify_host + "/pay/wechat/payment/notify_url.do";

	private final static String payReturnUrl = WechatConfig.notify_host + "/pay/wechat/payment/return_url.do";

	private final static String refundNotifyUrl = WechatConfig.notify_host + "/pay/wechat/refund/notify_url.do";

	@Override
	public void payment(BaseBean bean, HttpServletResponse response) throws Exception {
		OrderPayRequest payRequest = (OrderPayRequest) bean;

		String tradeNo = payRequest.getOut_trade_no();
		Orders order = ordersMapper.selectByNo(tradeNo);
		if (order != null) {
			OrderPayResponse ledgerResponse = new OrderPayResponse();
			ledgerResponse.setCode(CodeEnum.FAIL.getCode());
			ledgerResponse.setErr_msg("该订单号已存在，请重新输入");
			String result = PayResponseUtil.getResponseJson(ledgerResponse);
			result = StringUtil.base64ToString(result, "utf-8");
			ResponseUtil.out(response, result);
			return;
		}

		//保存订单信息
		Orders orders = new Orders();
		orders.setOutTradeNo(payRequest.getOut_trade_no());
		orders.setOutRequestNo(payRequest.getOut_request_no());
		orders.setLimitPay(payRequest.getLimit_pay());
		orders.setTotalFee(payRequest.getTotal_fee());
		orders.setBody(payRequest.getBody());
		orders.setCreateTime(System.currentTimeMillis());
		orders.setSubject(payRequest.getSubject());
		orders.setFreezeNotifyUrl(payRequest.getNotify_url());
		orders.setSellerUserId(payRequest.getSeller_user_id());
		orders.setState(EnumState.WAIT_PAY.getCode());
		orders.setFreezeReturnUrl(payRequest.getReturn_url());
		ordersMapper.insertSelective(orders);

		//组装请求信息,请求微信
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("appid", WechatConfig.appid);
		sParaTemp.put("mch_id", WechatConfig.mchid);
		sParaTemp.put("nonce_str", WechatSubmit.getNonceStr(32));
		sParaTemp.put("notify_url", payNotifyUrl);
		sParaTemp.put("body", payRequest.getSubject());
		sParaTemp.put("out_trade_no", payRequest.getOut_trade_no());
		BigDecimal totalFee = payRequest.getTotal_fee().multiply(new BigDecimal("100"));
		int fee = totalFee.intValue();
		sParaTemp.put("total_fee", fee + "");
		sParaTemp.put("spbill_create_ip", WechatSubmit.getIpAddr());
		sParaTemp.put("trade_type", WechatConfig.trade_type);

		String result = WechatSubmit.doUrl(response, sParaTemp);

		Map<String, Object> xmlMap = WechatSubmit.getMapFromXML(result);

		LoggerUtil.info(this.getClass(), "微信返回json：" + JsonUtil.objToJson(xmlMap));

		response.sendRedirect("/pay/wechat/qrcode.do?code_url=" + xmlMap.get("code_url") +
				"&prepay_id=" + xmlMap.get("prepay_id") + "&out_trade_no=" + payRequest.getOut_trade_no());
	}

	@Override
	public void refund(BaseBean bean, HttpServletResponse response) throws Exception {
		RefundRequest refundRequest = (RefundRequest) bean;

		String out_trade_no = refundRequest.getOut_trade_no();
		Orders orders = ordersMapper.selectByNo(out_trade_no);
		LoggerUtil.info(this.getClass(), "退款订单信息：" + JsonUtil.objToJson(orders));
		if (orders == null) {
			RefundResponse refundResponse = new RefundResponse();
			refundResponse.setCode(CodeEnum.FAIL.getCode());
			refundResponse.setErr_msg("该笔订单不存在");
			String result = PayResponseUtil.getResponseJson(refundResponse);
			ResponseUtil.out(response, result);
			return;
		}
		if (orders.getState().equals(EnumState.REFUNDED.getCode())) {
			RefundResponse refundResponse = new RefundResponse();
			refundResponse.setOut_trade_no(orders.getOutTradeNo());
			refundResponse.setTotal_fee(orders.getTotalFee());
			refundResponse.setCode(CodeEnum.FAIL.getCode());
			refundResponse.setErr_msg("该笔订单已退款，请勿重复操作");
			String result = PayResponseUtil.getResponseJson(refundResponse);
			ResponseUtil.out(response, result);
			return;
		}
		orders.setRefundFee(orders.getTotalFee());
		orders.setRefundNotifyUrl(refundRequest.getNotify_url());
		ordersMapper.updateByPrimaryKeySelective(orders);

		//组装请求信息,请求微信
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("appid", WechatConfig.appid);
		sParaTemp.put("mch_id", WechatConfig.mchid);
		sParaTemp.put("nonce_str", WechatSubmit.getNonceStr(32));
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("out_refund_no", out_trade_no + System.currentTimeMillis());
		BigDecimal totalFee = orders.getTotalFee().multiply(new BigDecimal("100"));
		int fee = totalFee.intValue();
		sParaTemp.put("total_fee", fee + "");
		sParaTemp.put("refund_fee", fee + "");
		sParaTemp.put("refund_fee_type", "CNY");
		sParaTemp.put("op_user_id", WechatConfig.mchid);

		String result = WechatSubmit.doHttpsUrl(response, sParaTemp);

		Map<String, Object> xmlMap = WechatSubmit.getMapFromXML(result);
		LoggerUtil.info(this.getClass(), "微信返回json：" + JsonUtil.objToJson(xmlMap));

		RefundResponse refundResponse = new RefundResponse();
		refundResponse.setOut_trade_no(out_trade_no);
		refundResponse.setTotal_fee(orders.getTotalFee());
		if (WechatSubmit.checkResponseState(xmlMap)) {
			refundResponse.setCode(CodeEnum.SUCCESS.getCode());

			//更改订单状态
			orders.setState(EnumState.REFUNDED.getCode());
			ordersMapper.updateByPrimaryKeySelective(orders);
		}
		else {
			refundResponse.setCode(CodeEnum.FAIL.getCode());
			refundResponse.setErr_msg("退款失败");
		}

		String responseResult = PayResponseUtil.getResponseJson(refundResponse);
		//同步返回数据
		ResponseUtil.out(response, responseResult);
		//异步通知商户
		String queryString = PayResponseUtil.getResponseQuery(refundResponse);
		String notify_url = orders.getRefundNotifyUrl();
		UrlConnectUtil.doUrl(notify_url + "?" + queryString);
		return;
	}

	@Override
	public void prorate(BaseBean bean, HttpServletResponse response) throws Exception {
		LedgerRequest ledgerRequest = (LedgerRequest) bean;

		String out_trade_no = ledgerRequest.getOut_trade_no();
		Orders orders = ordersMapper.selectByNo(out_trade_no);
		if (orders == null) {
			LedgerResponse ledgerResponse = new LedgerResponse();
			ledgerResponse.setCode(CodeEnum.FAIL.getCode());
			ledgerResponse.setErr_msg("该笔订单不存在");
			String result = PayResponseUtil.getResponseJson(ledgerResponse);
			ResponseUtil.out(response, result);
			return;
		}
		orders.setPayNotifyUrl(ledgerRequest.getNotify_url());
		ordersMapper.updateByPrimaryKeySelective(orders);

		if (orders.getState().equals(EnumState.REFUNDED.getCode())) {
			LedgerResponse ledgerResponse = new LedgerResponse();
			ledgerResponse.setCode(CodeEnum.FAIL.getCode());
			ledgerResponse.setErr_msg("该笔订单已退款，无法分账");
			String result = PayResponseUtil.getResponseJson(ledgerResponse);
			ResponseUtil.out(response, result);
			return;
		}

		if (!orders.getState().equals(EnumState.VERIFYED.getCode())) {
			LedgerResponse ledgerResponse = new LedgerResponse();
			ledgerResponse.setCode(CodeEnum.FAIL.getCode());
			ledgerResponse.setErr_msg("请先核销该订单才能分账");
			String result = PayResponseUtil.getResponseJson(ledgerResponse);
			ResponseUtil.out(response, result);
			return;
		}

		if (!orders.getState().equals(EnumState.REFUNDED.getCode())) {
			LedgerResponse ledgerResponse = new LedgerResponse();
			ledgerResponse.setCode(CodeEnum.FAIL.getCode());
			ledgerResponse.setErr_msg("该笔订单已完成分账，请勿重复操作");
			String result = PayResponseUtil.getResponseJson(ledgerResponse);
			ResponseUtil.out(response, result);
			return;
		}

	}

}
