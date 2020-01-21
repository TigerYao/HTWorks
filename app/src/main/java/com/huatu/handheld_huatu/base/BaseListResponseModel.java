package com.huatu.handheld_huatu.base;

import java.io.Serializable;
import java.util.List;

public class BaseListResponseModel<T> implements Serializable {
	public int code;
	public String message;
	public List<T> data;

}
