/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.server   
 * 文件名：PayController.java   
 * 版本信息：   
 * 创建日期：2016年10月12日-上午10:04:29
 */
package com.pay.business.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pay.business.BaseResponse;
import com.pay.business.EnumState;
import com.pay.business.bean.Orders;
import com.pay.business.dao.OrdersMapper;
import com.pay.business.response.LedgerResponse;
import com.pay.business.response.OrderPayResponse;
import com.pay.business.response.RefundResponse;
import com.pay.core.BaseBean;
import com.pay.core.BeanFactory;
import com.pay.core.CodeEnum;
import com.pay.core.PayCore;
import com.pay.core.PayEnum;
import com.pay.core.config.MerchantConfig;
import com.pay.core.util.PayResponseUtil;
import com.pay.system.util.ResponseUtil;
import com.utils.DateUtil;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;
import com.utils.StringUtil;
import com.utils.UrlConnectUtil;

/**   
 * 类名：PayController   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月12日 上午10:04:29   
 * 修改备注：   
 * @version 1.0.0
 */
@Controller
public class PayController {

	@Resource
	private OrdersMapper ordersMapper;

	/**
	 * 支付总控制器，对外提供单个接口，由传入的service字段决定具体执行的业务
	 * @param request
	 * @param response
	 * @throws Exception    
	 * @return void   
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/pay/gateway.do")
	public void payGateway(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, String> paraMap = getPostInfo(request);
		if (paraMap.containsKey("appid")) {
			/**验证身份信息**/
			paraMap.remove("appid");
		}
		LoggerUtil.info(this.getClass(), "支付网关接收请求参数:" + JsonUtil.objToJson(paraMap));

		/**
		 * 验证参数合法性
		 */
		if (!PayResponseUtil.verifyMechant(paraMap)) {
			BaseResponse resBean = new BaseResponse();
			resBean.setCode(CodeEnum.ILLEGAL_SIGN.getCode());
			resBean.setErr_msg("illegal sign");
			String result = PayResponseUtil.getResponseJson(resBean);
			ResponseUtil.out(response, result);
			return;
		}

		String service = paraMap.get("service");
		if (StringUtil.isEmpty(service)) {
			BaseResponse resBean = new BaseResponse();
			resBean.setCode(CodeEnum.ILLEGAL_PARAM.getCode());
			resBean.setErr_msg("the parameter service is null");
			String result = PayResponseUtil.getResponseJson(resBean);
			ResponseUtil.out(response, result);
			return;
		}

		//实例化请求参数
		BaseBean payBean = BeanFactory.getInstance().getBeanByParams(paraMap);

		/**
		 * 执行业务逻辑
		 */
		PayCore payCore = PayCore.getInstance(payBean);
		payCore.doService(response);
	}

	/**
	 * 处理订单授权冻结异步返回结果
	 * @param request
	 * @param response    
	 * @return void   
	 * @throws Exception 
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/pay/payment/notify_url.do")
	public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, String> params = getPostInfo(request);
		LoggerUtil.info(this.getClass(), "授权冻结异步返回结果" + JsonUtil.objToJson(params));

		String tradeNo = params.get("out_order_no");
		Orders orders = ordersMapper.selectByNo(tradeNo);

		/**
		 * 签名校验
		 */
		if (PayResponseUtil.verifyThird(params, PayEnum.ALIPAY.getCode())) {
			ResponseUtil.out(response, "success"); //请不要修改或删除

			//验证成功
			String authNo = params.get("auth_no");

			String status = params.get("status");

			if (!status.equals("FAIL")) {
				//支付成功
				//更新本地订单信息
				if (orders != null) {
					orders.setAuthNo(authNo);
					orders.setCodeCreateTime(System.currentTimeMillis());
					orders.setExpireTime(30);
					orders.setVerifyCode(PayCore.generateVerifyCode());
					String payer_user_id = params.get("payer_user_id");
					orders.setPayerUserId(payer_user_id);
					orders.setState(EnumState.Freezed.getCode());
					ordersMapper.updateByPrimaryKeySelective(orders);
				}

				/**
				 * 异步通知商户支付结果
				 */
				OrderPayResponse payResponse = new OrderPayResponse();
				payResponse.setCode(CodeEnum.SUCCESS.getCode());
				payResponse.setLimit_pay(orders.getLimitPay());
				payResponse.setSubject(orders.getSubject());
				payResponse.setVerify_code(orders.getVerifyCode());
				payResponse.setExpire_time(orders.getExpireTime());
				payResponse.setTotal_fee(orders.getTotalFee());
				String queryString = PayResponseUtil.getResponseQuery(payResponse);
				//异步通知商户
				String notify_url = orders.getFreezeNotifyUrl();
				UrlConnectUtil.doUrl(notify_url + "?" + queryString);
				return;
			}
			else {
				OrderPayResponse payResponse = new OrderPayResponse();
				payResponse.setCode(CodeEnum.FAIL.getCode());
				payResponse.setErr_msg("pay failed");
				payResponse.setLimit_pay(orders.getLimitPay());
				payResponse.setSubject(orders.getSubject());
				payResponse.setTotal_fee(orders.getTotalFee());
				String queryString = PayResponseUtil.getResponseQuery(payResponse);
				//异步通知商户
				String notify_url = orders.getFreezeNotifyUrl();
				UrlConnectUtil.doUrl(notify_url + "?" + queryString);
				return;
			}

		}
		else {//验证失败
			OrderPayResponse payResponse = new OrderPayResponse();
			payResponse.setCode(CodeEnum.ILLEGAL_SIGN.getCode());
			payResponse.setErr_msg(CodeEnum.ILLEGAL_SIGN.getRemark());
			payResponse.setLimit_pay(orders.getLimitPay());
			payResponse.setSubject(orders.getSubject());
			payResponse.setTotal_fee(orders.getTotalFee());
			String queryString = PayResponseUtil.getResponseQuery(payResponse);
			//异步通知商户
			String notify_url = orders.getFreezeNotifyUrl();
			UrlConnectUtil.doUrl(notify_url + "?" + queryString);
			return;
		}
	}

	@RequestMapping("/pay/payment/return_url.do")
	public void payReturn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String is_success = request.getParameter("is_success");
		String status = request.getParameter("status");
		String out_order_no = request.getParameter("out_order_no");
		Orders order = ordersMapper.selectByNo(out_order_no);
		if (is_success.equals("T") && status.equals("SUCCESS") && order != null) {
			OrderPayResponse payResponse = new OrderPayResponse();
			payResponse.setCode(CodeEnum.SUCCESS.getCode());
			payResponse.setLimit_pay(order.getLimitPay());
			payResponse.setNotify_time(DateUtil.getDefaultUtil().dateLongToString(System.currentTimeMillis()));
			payResponse.setOut_trade_no(out_order_no);
			payResponse.setSubject(order.getSubject());
			payResponse.setTotal_fee(order.getTotalFee());
			String queryString = PayResponseUtil.getResponseQuery(payResponse);
			//重定向到商户页面
			String notify_url = order.getFreezeReturnUrl();
			response.sendRedirect(notify_url + "?" + queryString);
			return;
		}
		else {
			OrderPayResponse payResponse = new OrderPayResponse();
			payResponse.setCode(CodeEnum.FAIL.getCode());
			payResponse.setErr_msg("支付失败");
			payResponse.setLimit_pay(order.getLimitPay());
			payResponse.setNotify_time(DateUtil.getDefaultUtil().dateLongToString(System.currentTimeMillis()));
			payResponse.setOut_trade_no(out_order_no);
			payResponse.setSubject(order.getSubject());
			payResponse.setTotal_fee(order.getTotalFee());
			String queryString = PayResponseUtil.getResponseQuery(payResponse);
			//重定向到商户页面
			String notify_url = order.getFreezeReturnUrl();
			response.sendRedirect(notify_url + "?" + queryString);
			return;
		}
	}

	/**
	 * 处理退款异步返回结果
	 * @param request
	 * @param response    
	 * @return void   
	 * @throws Exception 
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/pay/refund/notify_url.do")
	public void refundNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, String> params = getPostInfo(request);
		LoggerUtil.info(this.getClass(), "退款异步返回结果" + JsonUtil.objToJson(params));

		String tradeNo = params.get("out_order_no");
		Orders orders = ordersMapper.selectByNo(tradeNo);

		/**
		 * 签名校验
		 */
		if (PayResponseUtil.verifyThird(params, PayEnum.ALIPAY.getCode())) {
			ResponseUtil.out(response, "success"); //请不要修改或删除

			String status = params.get("status");

			if (!status.equals("FAIL")) {
				if (orders != null) {
					orders.setState(EnumState.REFUNDED.getCode());
					ordersMapper.updateByPrimaryKeySelective(orders);
				}

				/**
				 * 异步通知商户支付结果
				 */
				RefundResponse refundResponse = new RefundResponse();
				refundResponse.setCode(CodeEnum.SUCCESS.getCode());
				refundResponse.setOut_trade_no(tradeNo);
				refundResponse.setTotal_fee(orders.getTotalFee());
				String queryString = PayResponseUtil.getResponseQuery(refundResponse);
				//异步通知商户
				String notify_url = orders.getRefundNotifyUrl();
				UrlConnectUtil.doUrl(notify_url + "?" + queryString);
				return;

			}
			else {
				RefundResponse refundResponse = new RefundResponse();
				refundResponse.setCode(CodeEnum.FAIL.getCode());
				refundResponse.setErr_msg("refund failed");
				refundResponse.setOut_trade_no(tradeNo);
				refundResponse.setTotal_fee(orders.getTotalFee());
				String queryString = PayResponseUtil.getResponseQuery(refundResponse);
				//异步通知商户
				String notify_url = orders.getRefundNotifyUrl();
				UrlConnectUtil.doUrl(notify_url + "?" + queryString);
				return;
			}

		}
		else {//验证失败
			RefundResponse refundResponse = new RefundResponse();
			refundResponse.setCode(CodeEnum.ILLEGAL_SIGN.getCode());
			refundResponse.setErr_msg(CodeEnum.ILLEGAL_SIGN.getRemark());
			refundResponse.setOut_trade_no(tradeNo);
			refundResponse.setTotal_fee(orders.getTotalFee());
			String queryString = PayResponseUtil.getResponseQuery(refundResponse);
			//异步通知商户
			String notify_url = orders.getRefundNotifyUrl();
			UrlConnectUtil.doUrl(notify_url + "?" + queryString);
			return;
		}

	}

	/**
	 * 处理分账返回结果
	 * @param request
	 * @param response    
	 * @return void   
	 * @throws Exception 
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/pay/ledger/notify_url.do")
	public void ledgerNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> params = getPostInfo(request);
		LoggerUtil.info(this.getClass(), "分账异步返回结果" + JsonUtil.objToJson(params));

		String tradeNo = params.get("out_trade_no");
		Orders orders = ordersMapper.selectByNo(tradeNo);

		/**
		 * 签名校验
		 */
		if (PayResponseUtil.verifyThird(params, PayEnum.ALIPAY.getCode())) {
			ResponseUtil.out(response, "success"); //请不要修改或删除

			if (orders != null) {
				orders.setState(EnumState.PAYED.getCode());
				ordersMapper.updateByPrimaryKeySelective(orders);
			}

			/**
			 * 异步通知商户支付结果
			 */
			LedgerResponse ledgerResponse = new LedgerResponse();
			ledgerResponse.setCode(CodeEnum.SUCCESS.getCode());
			ledgerResponse.setOut_trade_no(tradeNo);
			ledgerResponse.setTotal_fee(orders.getTotalFee());
			String queryString = PayResponseUtil.getResponseQuery(ledgerResponse);
			//异步通知商户
			String notify_url = orders.getPayNotifyUrl();
			UrlConnectUtil.doUrl(notify_url + "?" + queryString);
			return;
		}
		else {//验证失败
			LedgerResponse ledgerResponse = new LedgerResponse();
			ledgerResponse.setCode(CodeEnum.ILLEGAL_SIGN.getCode());
			ledgerResponse.setErr_msg(CodeEnum.ILLEGAL_SIGN.getRemark());
			ledgerResponse.setOut_trade_no(tradeNo);
			ledgerResponse.setTotal_fee(orders.getTotalFee());
			String queryString = PayResponseUtil.getResponseQuery(ledgerResponse);
			//异步通知商户
			String notify_url = orders.getPayNotifyUrl();
			UrlConnectUtil.doUrl(notify_url + "?" + queryString);
			return;
		}

	}

	private Map<String, String> getPostInfo(HttpServletRequest request) throws UnsupportedEncodingException {
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();

		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
				valueStr = URLDecoder.decode(valueStr, MerchantConfig.input_charset);
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}

		return params;
	}

	/**
	 * 获取订单信息
	 * @param model
	 * @return    
	 * @return String   
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/pay/getOrders.do")
	public void getOrders(HttpServletRequest request, HttpServletResponse response) {
		String tradeNo = request.getParameter("out_order_no");
		LoggerUtil.info(this.getClass(), "请求订单号：" + tradeNo);
		if (!StringUtil.isEmpty(tradeNo)) {
			Orders order = ordersMapper.selectByNo(tradeNo);
			if (order != null) {
				String orderList = JsonUtil.objToJson(order);
				orderList = StringUtil.toBase64(orderList, "utf-8");
				ResponseUtil.out(response, orderList);
				return;
			}
		}
		List<Orders> orders = ordersMapper.getOrders();
		LoggerUtil.info(this.getClass(), "获取订单信息成功:" + JsonUtil.objToJson(orders));
		String orderList = JsonUtil.objToJson(orders);
		orderList = StringUtil.toBase64(orderList, "utf-8");
		ResponseUtil.out(response, orderList);
	}

}
