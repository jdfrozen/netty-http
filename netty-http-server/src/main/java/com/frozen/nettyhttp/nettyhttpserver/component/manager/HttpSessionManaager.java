package com.frozen.nettyhttp.nettyhttpserver.component.manager;

import com.frozen.nettyhttp.nettyhttpserver.component.session.DefaultHttpSession;
import com.frozen.nettyhttp.nettyhttpserver.component.session.HttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: Frozen
 * @Date: 2019/1/1 21:44
 * @Description:
 */
public class HttpSessionManaager {
    private static final Map<String,HttpSession> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 每当有用户连接的时候就需要创建一个SessionId的数据内容
     * @return
     */
    public static String createSession(){
        HttpSession session = new DefaultHttpSession();
        String sessionId = session.getId();
        SESSION_MAP.put(sessionId,session);
        return sessionId;
    }

    /**
     * 判断当前的SessionId是否存在于集合之中
     * @param sessionId
     * @return
     */
    public static boolean isExists(String sessionId){
        if(SESSION_MAP.containsKey(sessionId)){
            HttpSession session = SESSION_MAP.get(sessionId);
            if(session.getId()==null){// 该Session已经被销毁了
                SESSION_MAP.remove(sessionId);
                return false;
            }
            return true;
        }else {
            return false;
        }
    }
    public static void invalidate(String sessionId) {
        SESSION_MAP.remove(sessionId) ;
    }
    public static HttpSession getSession(String sessionId) {
        return SESSION_MAP.get(sessionId) ;
    }
}
