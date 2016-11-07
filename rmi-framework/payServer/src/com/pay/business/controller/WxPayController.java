/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.business.controller   
 * 文件名：WxPayController.java   
 * 版本信息：   
 * 创建日期：2016年10月24日-下午5:42:49
 */
package com.pay.business.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.business.EnumState;
import com.pay.business.bean.Orders;
import com.pay.business.dao.OrdersMapper;
import com.pay.business.response.OrderPayResponse;
import com.pay.core.CodeEnum;
import com.pay.core.PayCore;
import com.pay.core.util.PayResponseUtil;
import com.pay.core.wechat.util.Signature;
import com.pay.core.wechat.util.WechatSubmit;
import com.pay.system.util.ResponseUtil;
import com.utils.DateUtil;
import com.utils.LoggerUtil;
import com.utils.StringUtil;
import com.utils.UrlConnectUtil;

/**   
 * 类名：WxPayController   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月24日 下午5:42:49   
 * 修改备注：   
 * @version 1.0.0
 */

@Controller
public class WxPayController {

	@Resource
	private OrdersMapper ordersMapper;

	private static Map<String, HttpServletRequest> requestMap = new ConcurrentHashMap<String, HttpServletRequest>();

	/**
	 * 生成支付二维码
	 * @param request
	 * @param response
	 * @param model
	 * @return    
	 * @return String   
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/pay/wechat/qrcode.do")
	public String qrcode(HttpServletRequest request, HttpServletResponse response, Model model) {
		String code_url = request.getParameter("code_url");
		String prepay_id = request.getParameter("prepay_id");
		String out_trade_no = request.getParameter("out_trade_no");

		model.addAttribute("code_url", code_url);
		model.addAttribute("prepay_id", prepay_id);
		model.addAttribute("out_trade_no", out_trade_no);

		return "/we_chat_qrcode";
	}

	@RequestMapping("/pay/wechat/payment/notify_url.do")
	public void wxPayBack(HttpServletRequest request, HttpServletResponse response) throws Exception {

		InputStream wxStream = request.getInputStream();

		String result = "";

		BufferedReader reader = new BufferedReader(new InputStreamReader(wxStream));

		String buffer;
		while ((buffer = reader.readLine()) != null) {
			result = result + buffer;
		}

		LoggerUtil.info(this.getClass(), "微信支付回调信息：" + result);

		//获取微信回调信息
		Map<String, Object> resultMap = WechatSubmit.getMapFromXML(result);

		String tradeNo = resultMap.get("out_trade_no").toString();
		Orders orders = ordersMapper.selectByNo(tradeNo);
		if (Signature.checkIsSignValidFromResponseString(result)) {
			//签名验证成功
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("return_code", "SUCCESS");
			retMap.put("return_msg", "OK");
			ResponseUtil.out(response, WechatSubmit.buildXmlString(retMap));

			if (WechatSubmit.checkResponseState(resultMap)) {

				orders.setCodeCreateTime(System.currentTimeMillis());
				orders.setExpireTime(30);
				orders.setVerifyCode(PayCore.generateVerifyCode());
				orders.setState(EnumState.PAYED.getCode());
				ordersMapper.updateByPrimaryKeySelective(orders);

				//异步回调
				OrderPayResponse payResponse = new OrderPayResponse();
				payResponse.setCode(CodeEnum.SUCCESS.getCode());
				payResponse.setLimit_pay(orders.getLimitPay());
				payResponse.setSubject(orders.getSubject());
				payResponse.setTotal_fee(orders.getTotalFee());
				String queryString = PayResponseUtil.getResponseQuery(payResponse);
				//异步通知商户
				String notify_url = orders.getFreezeNotifyUrl();
				UrlConnectUtil.doUrl(notify_url + "?" + queryString);

				//通知监听器，跳转到商户页面
				HttpServletRequest synRequest = requestMap.get(tradeNo);
				if (synRequest != null) {
					synchronized (synRequest) {
						synRequest.setAttribute("state", EnumState.PAYED.getCode());
						requestMap.put("tradeNo", synRequest);
						synRequest.notifyAll();
					}
				}
				return;
			}
			else {
				//异步回调
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
		else {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("return_code", "SUCCESS");
			retMap.put("return_msg", "sign error");
			ResponseUtil.out(response, WechatSubmit.buildXmlString(retMap));

			//异步回调
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

	/**
	 * 支付结果监听，如果支付，页面则跳转到商户设置的页面
	 * @param request
	 * @return    
	 * @return String   
	 * @throws Exception 
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/pay/wechat/payMonitor.do")
	@ResponseBody
	public String payMonitor(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tradeNo = request.getParameter("out_trade_no");

		if (!StringUtil.isEmpty(tradeNo)) {

			if (requestMap.containsKey(tradeNo)) {
				HttpServletRequest synRequest = requestMap.get(tradeNo);
				synchronized (synRequest) {
					synRequest.setAttribute("state", EnumState.WAIT_PAY.getCode());
					requestMap.put("tradeNo", synRequest);
					synRequest.notifyAll();
					requestMap.remove(tradeNo);
				}
			}

			requestMap.put(tradeNo, request);
			LoggerUtil.info(this.getClass(), "等待支付通知");

			HttpServletRequest synRequest = requestMap.get(tradeNo);
			synchronized (synRequest) {
				while (requestMap.get(tradeNo).getAttribute("state") == null) {
					System.out.println("状态:" + requestMap.get(tradeNo).getAttribute("state"));
					try {
						request.wait();
					}
					catch (InterruptedException e) {
						LoggerUtil.error(this.getClass(), "pay monitor wait exception, " + e.getMessage());
					}
				}
			}

			LoggerUtil.info(this.getClass(), "支付状态：" + request.getAttribute("state"));
			Orders payOrders = ordersMapper.selectByNo(tradeNo);

			OrderPayResponse payResponse = new OrderPayResponse();
			payResponse.setCode(CodeEnum.SUCCESS.getCode());
			payResponse.setLimit_pay(payOrders.getLimitPay());
			payResponse.setNotify_time(DateUtil.getDefaultUtil().dateLongToString(System.currentTimeMillis()));
			payResponse.setOut_trade_no(tradeNo);
			payResponse.setSubject(payOrders.getSubject());
			payResponse.setTotal_fee(payOrders.getTotalFee());
			String queryString = PayResponseUtil.getResponseQuery(payResponse);
			//重定向到商户页面
			String notify_url = payOrders.getFreezeReturnUrl();
			LoggerUtil.info(this.getClass(), "已通知，即将跳转");
			return notify_url + "?" + queryString;
		}

		return "";
	}

}
