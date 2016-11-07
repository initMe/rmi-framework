package com.pay.core;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.RandomUtils;

import com.pay.business.BaseRequest;
import com.pay.business.BaseResponse;
import com.pay.business.EnumState;
import com.pay.business.bean.Orders;
import com.pay.business.dao.OrdersMapper;
import com.pay.business.request.GetCodeRequest;
import com.pay.business.request.OrderPayRequest;
import com.pay.business.request.VerifyCodeRequest;
import com.pay.business.response.GetCodeResponse;
import com.pay.business.response.VerifyCodeResponse;
import com.pay.core.util.PayResponseUtil;
import com.pay.system.util.ResponseUtil;
import com.pay.system.util.SpringBeanUtil;
import com.utils.StringUtil;

public class PayCore {

	private static OrdersMapper ordersMapper = SpringBeanUtil.getBean(OrdersMapper.class);

	private static PayCore instance = null;

	private static PayExcutor payExcutor;

	private static BaseRequest request;

	private static ServiceEnum serviceEnum;

	private PayCore() throws Exception {

	}

	public void doService(HttpServletResponse response) throws Exception {
		try {
			request.check();
		}
		catch (Exception e) {
			//参数有误，返回信息illeagalparam
			BaseResponse resBean = new BaseResponse();
			resBean.setCode(CodeEnum.ILLEGAL_PARAM.getCode());
			resBean.setErr_msg(CodeEnum.ILLEGAL_PARAM.getRemark());
			String result = PayResponseUtil.getResponseJson(resBean);
			result = StringUtil.base64ToString(result, "utf-8");
			ResponseUtil.out(response, result);
			return;
		}

		if (serviceEnum.isPay()) {
			Method method = payExcutor.getClass().getDeclaredMethod(serviceEnum.getMethod(),
					BaseBean.class, HttpServletResponse.class);
			method.invoke(payExcutor, request, response);
		}
		else {
			if (serviceEnum.getMethod().equals(ServiceEnum.REVERIFYCODE.getMethod())) {
				this.reVerificationCode(request, response);
			}
			else if (serviceEnum.getMethod().equals(ServiceEnum.VERIFYCODE.getMethod())) {
				this.verifyCode(request, response);
			}
		}
	}

	public static PayCore getInstance(BaseBean resBean) throws Exception {
		request = (BaseRequest) resBean;
		serviceEnum = ServiceEnum.getByServiceName(request.getService());
		if (serviceEnum.isPay()) {
			String tradeNo = request.getOut_trade_no();
			String limitPay = "";
			if (resBean.getClass().equals(OrderPayRequest.class)) {
				OrderPayRequest payRequest = (OrderPayRequest) resBean;
				limitPay = payRequest.getLimit_pay();
			}
			else {
				Orders order = ordersMapper.selectByNo(tradeNo);
				if (order != null) {
					limitPay = order.getLimitPay();
				}
				else {
					//订单不存在,先设置为支付宝
					limitPay = PayEnum.ALIPAY.getCode();
				}
			}
			payExcutor = (PayExcutor) Class.forName(PayEnum.getPkg(limitPay)).newInstance();
		}
		if (instance == null) {
			instance = new PayCore();
		}

		return instance;
	}

	public void reVerificationCode(BaseBean bean, HttpServletResponse response) throws Exception {
		GetCodeRequest request = (GetCodeRequest) bean;

		String tradeNo = request.getOut_trade_no();
		Orders order = ordersMapper.selectByNo(tradeNo);

		if (order == null) {
			GetCodeResponse codeResponse = new GetCodeResponse();
			codeResponse.setCode(CodeEnum.FAIL.getCode());
			codeResponse.setErr_msg("该笔订单不存在");
			String result = PayResponseUtil.getResponseJson(codeResponse);
			ResponseUtil.out(response, result);
			return;
		}

		if (StringUtil.isEmpty(order.getVerifyCode())) {
			GetCodeResponse codeResponse = new GetCodeResponse();
			codeResponse.setCode(CodeEnum.FAIL.getCode());
			codeResponse.setErr_msg("该笔订单核销码为空");
			String result = PayResponseUtil.getResponseJson(codeResponse);
			ResponseUtil.out(response, result);
			return;
		}

		GetCodeResponse codeResponse = new GetCodeResponse();
		codeResponse.setCode(CodeEnum.SUCCESS.getCode());
		codeResponse.setOut_trade_no(tradeNo);

		Long codeCreated = order.getCodeCreateTime();
		int expire = order.getExpireTime();
		Long expired = (long) (expire * 60 * 1000);
		if (System.currentTimeMillis() - codeCreated > expired) {
			String code = generateVerifyCode();
			order.setVerifyCode(code);
			order.setCodeCreateTime(System.currentTimeMillis());
			ordersMapper.updateByPrimaryKeySelective(order);

			codeResponse.setVerify_code(code);
		}

		codeResponse.setVerify_code(order.getVerifyCode());
		codeResponse.setExpire_time(order.getExpireTime());

		String result = PayResponseUtil.getResponseJson(codeResponse);
		ResponseUtil.out(response, result);
	}

	public void verifyCode(BaseBean bean, HttpServletResponse response) throws Exception {
		VerifyCodeRequest verifyRequest = (VerifyCodeRequest) bean;

		String tradeNo = verifyRequest.getOut_trade_no();
		Orders order = ordersMapper.selectByNo(tradeNo);

		if (order == null || StringUtil.isEmpty(order.getVerifyCode())) {
			VerifyCodeResponse verifyResponse = new VerifyCodeResponse();
			verifyResponse.setErr_msg("该笔订单不存在");
			verifyResponse.setCode(CodeEnum.FAIL.getCode());
			String result = PayResponseUtil.getResponseJson(verifyResponse);
			ResponseUtil.out(response, result);
			return;
		}
		String code = order.getVerifyCode();

		VerifyCodeResponse verifyResponse = new VerifyCodeResponse();
		verifyResponse.setOut_trade_no(tradeNo);
		verifyResponse.setSubject(order.getSubject());
		verifyResponse.setTotal_fee(order.getTotalFee());

		String checkState = "";
		if (order.getState().equals(EnumState.VERIFYED.getCode())) {
			checkState = "该订单已核销，请勿重复操作";
		}
		if (!StringUtil.isEmpty(checkState)) {
			verifyResponse.setErr_msg("核销失败");
			verifyResponse.setCode(CodeEnum.FAIL.getCode());
			String result = PayResponseUtil.getResponseJson(verifyResponse);
			ResponseUtil.out(response, result);
			return;
		}

		if (verifyRequest.getVerify_code().equals(code)) {
			order.setState(EnumState.VERIFYED.getCode());
			ordersMapper.updateByPrimaryKeySelective(order);
			verifyResponse.setCode(CodeEnum.SUCCESS.getCode());
		}
		else {
			verifyResponse.setErr_msg("核销失败");
			verifyResponse.setCode(CodeEnum.FAIL.getCode());
		}

		String result = PayResponseUtil.getResponseJson(verifyResponse);
		ResponseUtil.out(response, result);
	}

	/**
	 * 生成10位字母加数字随机字符串
	 * @return    
	 * @return String   
	 * @exception    
	 * @since  1.0.0
	 */
	public static String generateVerifyCode() {
		String seed = "0123456789abcdefghijklmnopqrstuvwxyz";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			int pos = RandomUtils.nextInt(36);
			sb.append(seed.charAt(pos));
		}
		return sb.toString();
	}

}
