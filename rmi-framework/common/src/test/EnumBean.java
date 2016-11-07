package test;

import java.io.Serializable;
import com.exception.Error;

public class EnumBean implements Serializable {
	private static final long serialVersionUID = -64587738541612777L;
	private Error error;
	private Integer code;
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
}
