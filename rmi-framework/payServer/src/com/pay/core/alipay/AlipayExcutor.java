/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core.alipay   
 * 文件名：AlipayExcutor.java   
 * 版本信息：   
 * 创建日期：2016年10月15日-下午6:49:07
 */
package com.pay.core.alipay;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.pay.api.ServiceEnum;
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
import com.pay.core.alipay.config.AlipayConfig;
import com.pay.core.alipay.util.AlipaySubmit;
import com.pay.core.util.PayResponseUtil;
import com.pay.system.util.ResponseUtil;
import com.pay.system.util.SpringBeanUtil;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;
import com.utils.StringUtil;

/**   
 * 类名：AlipayExcutor   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月15日 下午6:49:07   
 * 修改备注：   
 * @version 1.0.0
 */
public class AlipayExcutor implements PayExcutor {

	private OrdersMapper ordersMapper = SpringBeanUtil.getBean(OrdersMapper.class);

	private final static String payNotifyUrl = AlipayConfig.notify_host + "/pay/payment/notify_url.do";

	private final static String payReturnUrl = AlipayConfig.notify_host + "/pay/payment/return_url.do";

	private final static String refundNotifyUrl = AlipayConfig.notify_host + "/pay/refund/notify_url.do";

	private final static String ledgerNotifyUrl = AlipayConfig.notify_host + "/pay/ledger/notify_url.do";

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

		//组装请求信息,请求支付宝
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.fund.auth.create.freeze.apply");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("notify_url", payNotifyUrl);
		sParaTemp.put("return_url", payReturnUrl);
		sParaTemp.put("out_order_no", payRequest.getOut_trade_no());
		sParaTemp.put("out_request_no", payRequest.getOut_request_no());
		sParaTemp.put("product_code", AlipayConfig.auth_freeze_product_code);
		sParaTemp.put("scene_code", AlipayConfig.scene_code);
		sParaTemp.put("order_title", payRequest.getSubject());
		sParaTemp.put("amount", payRequest.getTotal_fee().toString());
		sParaTemp.put("pay_mode", AlipayConfig.pay_mode);
		sParaTemp.put("payee_logon_id", payRequest.getSeller_user_id());

		LoggerUtil.info(this.getClass(), "资金预授权冻结请求支付宝参数:" + JsonUtil.objToJson(sParaTemp));
		//建立请求
		AlipaySubmit.sendRedirect(response, sParaTemp);
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
		if (orders.getState().equals(EnumState.PAYED.getCode())) {
			RefundResponse refundResponse = new RefundResponse();
			refundResponse.setOut_trade_no(orders.getOutTradeNo());
			refundResponse.setTotal_fee(orders.getTotalFee());
			refundResponse.setCode(CodeEnum.FAIL.getCode());
			refundResponse.setErr_msg("该笔订单已分账，无法退款");
			String result = PayResponseUtil.getResponseJson(refundResponse);
			ResponseUtil.out(response, result);
			return;
		}
		orders.setRefundFee(orders.getTotalFee());
		orders.setRefundNotifyUrl(refundRequest.getNotify_url());
		ordersMapper.updateByPrimaryKeySelective(orders);

		String out_request_no = orders.getOutRequestNo();
		String auth_no = orders.getAuthNo();

		//组装请求信息,请求支付宝
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.fund.auth.unfreeze");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("notify_url", refundNotifyUrl);
		sParaTemp.put("auth_no", auth_no);
		sParaTemp.put("out_request_no", out_request_no);
		sParaTemp.put("amount", orders.getTotalFee().toString());
		sParaTemp.put("remark", "refund--" + orders.getTotalFee().toString());

		LoggerUtil.info(this.getClass(), "解冻退款请求支付宝参数:" + JsonUtil.objToJson(sParaTemp));

		//建立请求
		String xmlString = AlipaySubmit.doUrl(response, sParaTemp);
		RefundResponse refundResponse = new RefundResponse();
		refundResponse.setOut_trade_no(out_trade_no);
		refundResponse.setTotal_fee(orders.getRefundFee());
		if (AlipaySubmit.isSuccess(xmlString, ServiceEnum.REFUND.getMethod())) {
			refundResponse.setCode(CodeEnum.SUCCESS.getCode());
		}
		else {
			refundResponse.setCode(CodeEnum.FAIL.getCode());
			refundResponse.setErr_msg("退款失败");
		}

		String result = PayResponseUtil.getResponseJson(refundResponse);
		ResponseUtil.out(response, result);
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

		if (orders.getState().equals(EnumState.PAYED.getCode())) {
			LedgerResponse ledgerResponse = new LedgerResponse();
			ledgerResponse.setCode(CodeEnum.FAIL.getCode());
			ledgerResponse.setErr_msg("该笔订单已完成分账，请勿重复操作");
			String result = PayResponseUtil.getResponseJson(ledgerResponse);
			ResponseUtil.out(response, result);
			return;
		}

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

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.acquire.createandpay");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("notify_url", ledgerNotifyUrl);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", orders.getSubject());
		sParaTemp.put("product_code", AlipayConfig.ledger_product_code);
		sParaTemp.put("total_fee", orders.getTotalFee().toString());
		sParaTemp.put("buyer_id", orders.getPayerUserId());
		sParaTemp.put("seller_email", orders.getSellerUserId());
		sParaTemp.put("auth_no", orders.getAuthNo());
		if (!StringUtil.isEmpty(ledgerRequest.getLedger_params())) {
			sParaTemp.put("royalty_type", "ROYALTY");
			sParaTemp.put("royalty_parameters", ledgerRequest.getLedger_params());
		}

		LoggerUtil.info(this.getClass(), "分账请求支付宝参数:" + JsonUtil.objToJson(sParaTemp));

		//建立请求
		String xmlString = AlipaySubmit.doUrl(response, sParaTemp);
		LedgerResponse ledgerResponse = new LedgerResponse();
		ledgerResponse.setOut_trade_no(out_trade_no);
		ledgerResponse.setTotal_fee(orders.getTotalFee());
		if (AlipaySubmit.isSuccess(xmlString, ServiceEnum.PRORATE.getMethod())) {
			ledgerResponse.setCode(CodeEnum.SUCCESS.getCode());
		}
		else {
			ledgerResponse.setCode(CodeEnum.FAIL.getCode());
			ledgerResponse.setErr_msg("分账失败");
		}

		String result = PayResponseUtil.getResponseJson(ledgerResponse);
		ResponseUtil.out(response, result);
		return;
	}

}
