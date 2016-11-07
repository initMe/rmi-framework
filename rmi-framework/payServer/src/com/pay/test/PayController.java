/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.test   
 * 文件名：PayController.java   
 * 版本信息：   
 * 创建日期：2016年10月25日-上午2:04:29
 */
package com.pay.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pay.api.PayClient;
import com.pay.api.bean.Orders;
import com.pay.api.request.GetCodeRequest;
import com.pay.api.request.LedgerRequest;
import com.pay.api.request.OrderPayRequest;
import com.pay.api.request.RefundRequest;
import com.pay.api.request.VerifyCodeRequest;
import com.pay.api.response.GetCodeResponse;
import com.pay.api.response.LedgerResponse;
import com.pay.api.response.RefundResponse;
import com.pay.api.response.VerifyCodeResponse;
import com.pay.api.util.SubmitUtil;
import com.utils.ConfigUtil;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;
import com.utils.StringUtil;
import com.utils.UrlConnectUtil;

/**   
 * 类名：TestController   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月16日 下午10:13:28   
 * 修改备注：   
 * @version 1.0.0
 */
@Controller
public class PayController {

	/** 从配置文件读取商户的合作账户id **/
	private static String appid = ConfigUtil.getInstance().getStringValue("merchant_partner");

	/** 从配置文件读取商户的安全校验码 **/
	private static String key = ConfigUtil.getInstance().getStringValue("merchant_key");

	/** 从配置文件读取支付接口地址 **/
	private static String gateurl = ConfigUtil.getInstance().getStringValue("pay_gateway");

	/** 支付网关 **/
	private static String gateway = gateurl + "/pay/gateway.do";

	/** 从配置文件读取异步返回地址 **/
	private static String notify_host = ConfigUtil.getInstance().getStringValue("notify_host");

	/** 实例化支付客户端 **/
	private static PayClient client = PayClient.getClient(gateway, appid, key);

	/**
	 * 返回支付页面
	 */
	@RequestMapping("/index.do")
	public String index(Model model) {
		model.addAttribute("notify_host", notify_host);
		model.addAttribute("gateway", gateway);
		return "/index";
	}

	/**
	 * 执行支付请求
	 * @param response 响应参数对象
	 * @param request 支付请求参数
	 * @throws Exception    
	 * @return void   
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/payment.do")
	public void freezeAndPay(HttpServletResponse response, OrderPayRequest request, Model model) throws Exception {
		LoggerUtil.info(this.getClass(), "支付请求提交参数:" + JsonUtil.objToJson(request));

		/** 支付冻结请求将会重定向到支付平台，无返回值  **/
		//OrderPayResponse orderPayResponse = client.excute(request);
		String payUrl = client.getPaymentUrl(request);

		response.sendRedirect(payUrl);

		//		String message = "";
		//		if (orderPayResponse.getCode().equals("200")) {
		//			message = "支付成功，返回结果如下:</br></br>";
		//		}
		//		else {
		//			message = "支付失败，返回结果如下:</br></br>";
		//		}
		//		message = message + JsonUtil.objToJson(orderPayResponse);
		//
		//		model.addAttribute("message", message);
		//		return "/result";

	}

	/**
	 * 支付接口同步回调入口
	 * @param request 请求对象
	 * @param response 返回对象
	 * @param model   model
	 * @since  1.0.0
	 */
	@RequestMapping("/payment/callback.do")
	public String callback(HttpServletRequest request, HttpServletResponse response, Model model) throws InterruptedException {
		LoggerUtil.info(this.getClass(), "冻结支付同步返回参数:" + request.getQueryString());

		Map<String, String> retMap = getPostInfo(request);
		String code = retMap.get("code");
		String message = "";
		if (code.equals("200")) {
			message = "支付成功，返回结果如下:</br></br>" + JsonUtil.objToJson(retMap);
		}
		else if (code.equals("300")) {
			message = "支付失败，返回结果如下:</br></br>" + JsonUtil.objToJson(retMap);
		}

		model.addAttribute("message", message);
		return "/result";
	}

	/**
	 * 支付异步回调入口
	 * @param request 请求对象
	 * @param response 响应对象
	 * @param model   model
	 * @since  1.0.0
	 */
	@RequestMapping("/payment/notify.do")
	public void notify(HttpServletRequest request, HttpServletResponse response) {
		LoggerUtil.info(this.getClass(), "支付异步通知参数:" + request.getQueryString());
		//执行本地业务逻辑
	}

	/**
	 * 返回获取核销码页面
	 */
	@RequestMapping("/codeget.do")
	public String codeget(Model model) {
		model.addAttribute("gateway", gateway);
		return "/codeget";
	}

	/**
	 * 获取核销码
	 * @param response 响应对象
	 * @param request 获取核销码参数封装类
	 * @param model model  
	 * @since  1.0.0
	 */
	@RequestMapping("/regetCode.do")
	public String regetCode(HttpServletResponse response, GetCodeRequest request, Model model) throws Exception {
		LoggerUtil.info(this.getClass(), "获取核销码请求提交参数:" + JsonUtil.objToJson(request));

		GetCodeResponse getCodeResBean = client.excute(request);
		String message = "";
		if (getCodeResBean.getCode().equals("200")) {
			message = "获取核销码成功，返回结果如下:</br></br>";
		}
		else {
			message = "获取核销码失败，返回结果如下:</br></br>";
		}
		message = message + JsonUtil.objToJson(getCodeResBean);

		model.addAttribute("message", message);
		return "/result";
	}

	/**
	 * 返回支付分账页面
	 */
	@RequestMapping("/confirm.do")
	public String confirm(Model model) {
		model.addAttribute("notify_host", notify_host);
		model.addAttribute("gateway", gateway);
		return "/confirm";
	}

	/**
	 * 分账支付请求
	 * @param response 响应对象
	 * @param request 分账支付请求参数封装对象
	 * @param model model   
	 * @since  1.0.0
	 */
	@RequestMapping("/prorate.do")
	public String prorate(HttpServletResponse response, LedgerRequest request, Model model) throws Exception {
		LoggerUtil.info(this.getClass(), "分账请求提交参数:" + JsonUtil.objToJson(request));

		LedgerResponse ledgerResponse = client.excute(request);
		String message = "";
		if (ledgerResponse.getCode().equals("200")) {
			message = "分账成功，返回结果如下:</br></br>";
		}
		else {
			message = "分账失败，返回结果如下:</br></br>";
		}
		message = message + JsonUtil.objToJson(ledgerResponse);

		model.addAttribute("message", message);
		return "/result";
	}

	/**
	 * 分账支付异步回调入口
	 * @param request
	 * @param response    
	 * @return void   
	 * @exception    
	 * @since  1.0.0
	 */
	@RequestMapping("/confirm/notify.do")
	public void confirmNotify(HttpServletRequest request, HttpServletResponse response) {
		LoggerUtil.info(this.getClass(), "分账异步通知参数:" + request.getQueryString());
		//执行本地业务逻辑
	}

	/**
	 * 返回订单信息列表页面
	 */
	@RequestMapping("/list.do")
	public String list(Model model) {
		//String ordersJson = UrlConnectUtil.SendHttpsPOST(gateurl + "/pay/getOrders.do");
		String ordersJson = UrlConnectUtil.doUrl(gateurl + "/pay/getOrders.do");
		ordersJson = StringUtil.base64ToString(ordersJson, "utf-8");
		LoggerUtil.info(this.getClass(), "获取订单信息：" + ordersJson);

		List<Orders> list = new ArrayList<Orders>();
		list = JSONObject.parseArray(ordersJson, Orders.class);
		model.addAttribute("orders", list);

		return "/list";
	}

	/**
	 * 返回退款页面
	 */
	@RequestMapping("/refund.do")
	public String refund(Model model) {
		model.addAttribute("notify_host", notify_host);
		model.addAttribute("gateway", gateway);
		return "/refund";
	}

	@RequestMapping("/refundApply.do")
	public String refund(HttpServletResponse response, RefundRequest request, Model model) throws Exception {
		LoggerUtil.info(this.getClass(), "退款请求提交参数:" + JsonUtil.objToJson(request));
		RefundResponse refundResponse = client.excute(request);
		String message = "";
		if (refundResponse.getCode().equals("200")) {
			message = "退款成功，返回结果如下:</br></br>";
		}
		else {
			message = "退款失败，返回结果如下:</br></br>";
		}
		message = message + JsonUtil.objToJson(refundResponse);

		model.addAttribute("message", message);
		return "/result";
	}

	@RequestMapping("/refund/notify.do")
	public void refundNotify(HttpServletRequest request, HttpServletResponse response) {
		LoggerUtil.info(this.getClass(), "退款异步通知参数:" + request.getQueryString());
	}

	/**
	 * 返回核销页面
	 */
	@RequestMapping("/verifyCode.do")
	public String verifycode(Model model) {
		model.addAttribute("gateway", gateway);
		return "/verifycode";
	}

	@RequestMapping("/verify.do")
	public String verifyCode(HttpServletResponse response, VerifyCodeRequest request, Model model) throws Exception {
		LoggerUtil.info(this.getClass(), "核销请求提交参数:" + JsonUtil.objToJson(request));
		VerifyCodeResponse verifyCodeResponse = client.excute(request);
		String message = "";
		if (verifyCodeResponse.getCode().equals("200")) {
			message = "核销成功，返回结果如下:</br></br>";
		}
		else {
			message = "核销失败，返回结果如下:</br></br>";
		}
		message = message + JsonUtil.objToJson(verifyCodeResponse);

		model.addAttribute("message", message);
		return "/result";
	}

	/**
	 * 获取请求参数签名
	 */
	@RequestMapping("/getSign.do")
	@ResponseBody
	public String getSign(HttpServletRequest request) {
		Map<String, String> paraMap = getPostInfo(request);
		String sPara = SubmitUtil.buildRequestString(paraMap, key);

		return sPara;
	}

	/**
	 * 获取请求对象集合
	 */
	private Map<String, String> getPostInfo(HttpServletRequest request) {
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();

		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}

		return params;
	}

}
