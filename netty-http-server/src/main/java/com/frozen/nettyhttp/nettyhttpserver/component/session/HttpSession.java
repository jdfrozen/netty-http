package com.frozen.nettyhttp.nettyhttpserver.component.session;

/**
 * @Auther: Frozen
 * @Date: 2019/1/1 21:27
 * @Description: HttpSession接口
 */
public interface HttpSession {
    public static final String SESSIONID = "FrozenSessionId";
    public Object getAttribute(String name);
    public void setAttribute(String name,Object value);
    public void removeAttribute(String name);
    public String getId();
    public void invalidate();
}
