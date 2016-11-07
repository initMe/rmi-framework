
package com.bean.db;

import java.lang.reflect.Field;

import com.bean.BaseBean;
import com.exception.DataBaseException;
import com.exception.Error;

public class Page extends BaseBean{
    private static final long serialVersionUID = 1L;

    private int showCount = 6; // 每页显示记录数
    private int totalPage; // 总页数
    private int totalResult; // 总记录数
    private int currentPage; // 当前页
    private int currentResult; // 当前记录起始索引
    private boolean entityOrField; // true:需要分页的地方，传入的参数就是Page实体；false:需要分页的地方，传入的参数所代表的实体拥有Page属性
    private String pageStr; // 最终页面显示的底部翻页导航，详细见：getPageStr();
    private String mobileStr; // 最终页面显示的底部翻页导航(手机版)，详细见：getMobileStr();
    private String params = "";

    /** 是否需要分页拼接(true:pageStr和mobileStr的geter返回为拼接值，false：返回值为null) */
    private boolean isNeedPageStr = true;
    
    public String getParams(){
        return params;
    }
    
    /** 不需要自动拼接 pageStr和mobileStr 的值*/
    public void notNeedPageStr() {
    	isNeedPageStr = false;
    }
    
    @SuppressWarnings("unchecked")
    public void setParams(Object param){
        try{
            Class clazz = param.getClass();
            Field[] fs = clazz.getDeclaredFields();
            for (Field f : fs){
                f.setAccessible(true);
                Object value = f.get(param);
                if (value instanceof Page){
                    continue;
                }
                if (value != null && !value.toString().trim().equals("") && !value.toString().trim().equals("0")){
                    params += "&" + f.getName() + "=" + value.toString();
                }
            }
        }
        catch (Exception e){
            throw new DataBaseException(Error.database_error, e);
        }
    }

    public int getTotalPage(){
        if (totalResult % showCount == 0)
            totalPage = totalResult / showCount;
        else
            totalPage = totalResult / showCount + 1;
        return totalPage;
    }

    public void setTotalPage(int totalPage){
        this.totalPage = totalPage;
    }

    public int getTotalResult(){
        return totalResult;
    }

    public void setTotalResult(int totalResult){
        this.totalResult = totalResult;
    }

    public int getCurrentPage(){
        if (currentPage > getTotalPage())
            currentPage = getTotalPage();
        if (currentPage <= 0)
        	currentPage = 1;
        return currentPage;
    }

    public void setCurrentPage(int currentPage){
        this.currentPage = currentPage;
    }

    public String getMobileStr() {
    	if(!isNeedPageStr) {
    		mobileStr = null;
    		return mobileStr;
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append("<input id='moreButtId' type='button' value='下一页' onclick='nextPage("+(getCurrentPage()+1)+")' />\n");
    	sb.append("<script type=\"text/javascript\">\n");
        sb.append("function nextPage(page){");
        sb.append(" if(true && document.forms[0]){\n");
        sb.append("     var url = document.forms[0].getAttribute(\"action\");\n");
        sb.append("     if(url.indexOf('?')>-1){url += \"&" + (entityOrField ? "currentPage" : "page.currentPage")
                + "=\";}\n");
        sb.append("     else{url += \"?" + (entityOrField ? "currentPage" : "page.currentPage") + "=\";}\n");
        sb.append("     document.forms[0].action = url+page;\n");
        sb.append("     document.forms[0].submit();\n");
        sb.append(" }else{\n");
        sb.append("     var url = document.location+'';\n");
        sb.append("     if(url.indexOf('?')>-1){\n");
        sb.append("         if(url.indexOf('currentPage')>-1){\n");
        sb.append("             var reg = /currentPage=\\d*/g;\n");
        sb.append("             url = url.replace(reg,'currentPage=');\n");
        sb.append("         }else{\n");
        sb.append("             url += \"&" + (entityOrField ? "currentPage" : "page.currentPage") + "=\";\n");
        sb.append("         }\n");
        sb.append("     }else{url += \"?" + (entityOrField ? "currentPage" : "page.currentPage") + "=\";}\n");
        sb.append("     url = url.replace(\"" + params + "\", \"\");\n");
        sb.append("     document.location = url + page + \"" + params + "\";\n");
        sb.append(" }\n");
        sb.append("}\n");
        sb.append("</script>\n");
        mobileStr = sb.toString();
        return mobileStr;
    }
    
    public String getPageStr() {
    	if(!isNeedPageStr) {
    		pageStr = null;
    		return pageStr;
    	}
        StringBuffer sb = new StringBuffer();
        if (totalResult > 0) {
            sb.append(" <font face=\"隶书\" size=-1><ul>\n");
            if (currentPage == 1) {
                sb.append(" <a class=\"first\">首页</a>\n");
                sb.append(" <a class=\"ajaxPrev\">上页</a>\n");
            }
            else {
                sb.append(" <a href=\"javascript:void(0);\" class=\"first\" onclick=\"nextPage(1)\">首页</a>\n");
                sb.append(" <a href=\"javascript:void(0);\" class=\"ajaxPrev\" onclick=\"nextPage(" + (currentPage - 1) + ")\">上页</a>\n");
            }
            int showTag = 5; // 分页标签显示数量
            int startTag = 1;
            if (currentPage > showTag) {
                startTag = currentPage - 1;
            }
            int endTag = startTag + showTag - 1;
            for (int i = startTag; i <= totalPage && i <= endTag; i++) {
                if (currentPage == i)
                    sb.append("<a class=\"pageNum cur\">" + i + "</a>\n");
                else
                    sb.append("<a href=\"javascript:void(0);\" class=\"pageNum\" onclick=\"nextPage(" + i + ")\">" + i + "</a>\n");
            }
            if (currentPage == totalPage) {
                sb.append("  <a class=\"ajaxNext\">下页</li></a>\n");
                sb.append(" <a class=\"last\">尾页</li></a>\n");
            }
            else {
                sb.append(" <a href=\"javascript:void(0);\" class=\"ajaxNext\" onclick=\"nextPage(" + (currentPage + 1) + ")\">下页</a>\n");
                sb.append(" <a href=\"javascript:void(0);\" class=\"last\"  onclick=\"nextPage(" + totalPage + ")\">尾页</a>\n");
            }
            sb.append(" &nbsp;|&nbsp;第" + currentPage + "页</li>\n");
            sb.append(" &nbsp;共" + totalPage + "页</li>\n");
            sb.append("</ul>\n");
            sb.append("</font>\n");
            sb.append("<script type=\"text/javascript\">\n");
            sb.append("function nextPage(page){");
            sb.append(" if(true && document.forms[0]){\n");
            sb.append("     var url = document.forms[0].getAttribute(\"action\");\n");
            sb.append("     if(url.indexOf('?')>-1){url += \"&" + (entityOrField ? "currentPage" : "page.currentPage")
                    + "=\";}\n");
            sb.append("     else{url += \"?" + (entityOrField ? "currentPage" : "page.currentPage") + "=\";}\n");
            sb.append("     document.forms[0].action = url+page;\n");
            sb.append("     document.forms[0].submit();\n");
            sb.append(" }else{\n");
            sb.append("     var url = document.location+'';\n");
            sb.append("     if(url.indexOf('?')>-1){\n");
            sb.append("         if(url.indexOf('currentPage')>-1){\n");
            sb.append("             var reg = /currentPage=\\d*/g;\n");
            sb.append("             url = url.replace(reg,'currentPage=');\n");
            sb.append("         }else{\n");
            sb.append("             url += \"&" + (entityOrField ? "currentPage" : "page.currentPage") + "=\";\n");
            sb.append("         }\n");
            sb.append("     }else{url += \"?" + (entityOrField ? "currentPage" : "page.currentPage") + "=\";}\n");
            sb.append("     url = url.replace(\"" + params + "\", \"\");\n");
            sb.append("     document.location = url + page + \"" + params + "\";\n");
            sb.append(" }\n");
            sb.append("}\n");
            sb.append("</script>\n");
        }
        pageStr = sb.toString();
        return pageStr;
    }

    public void setMobileStr(String mobileStr) {
		this.mobileStr = mobileStr;
	}

	public void setPageStr(String pageStr){
        this.pageStr = pageStr;
    }

    public int getShowCount(){
        return showCount;
    }

    public void setShowCount(int showCount){
        this.showCount = showCount;
    }

    public int getCurrentResult(){
        currentResult = (getCurrentPage() - 1) * getShowCount();
        if (currentResult < 0)
            currentResult = 0;
        return currentResult;
    }

    public void setCurrentResult(int currentResult){
        this.currentResult = currentResult;
    }

    public boolean isEntityOrField(){
        return entityOrField;
    }

    public void setEntityOrField(boolean entityOrField){
        this.entityOrField = entityOrField;
    }

}
