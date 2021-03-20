package com.minzheng.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minzheng.blog.dao.ChatRecordDao;
import com.minzheng.blog.entity.ChatRecord;
import com.minzheng.blog.enums.FilePathEnum;
import com.minzheng.blog.utils.*;
import com.minzheng.blog.vo.VoiceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.minzheng.blog.enums.ChatTypeEnum.*;

/**
 * @author: yezhiqiu
 * @date: 2021-02-19
 **/

@ServerEndpoint(value = "/websocket", configurator = WebSocketServiceImpl.ChatConfigurator.class)
@Service
public class WebSocketServiceImpl {

    /**
     * 用户session
     */
    private Session session;

    /**
     * 用户session集合
     */
    private static CopyOnWriteArraySet<WebSocketServiceImpl> webSocketSet = new CopyOnWriteArraySet<>();


    private static ChatRecordDao chatRecordDao;

    @Autowired
    public void setChatRecordDao(ChatRecordDao chatRecordDao) {
        WebSocketServiceImpl.chatRecordDao = chatRecordDao;
    }


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) throws IOException {
        this.session = session;
        webSocketSet.add(this);
        // 更新在线人数
        updateOnlineCount();
        // 加载历史聊天记录
        List<ChatRecord> chatRecordList = chatRecordDao.selectList(new LambdaQueryWrapper<ChatRecord>()
                .ge(ChatRecord::getCreateTime, DateUtil.getBeforeHourTime(12)));
        Map<String, Object> recordMap = new HashMap<>(16);
        String ipAddr = endpointConfig.getUserProperties().get(ChatConfigurator.HEADER_NAME).toString();
        recordMap.put("chatRecordList", chatRecordList);
        recordMap.put("ipAddr", ipAddr);
        recordMap.put("ipSource", IpUtil.getIpSource(ipAddr));
        recordMap.put("type", HISTORY_RECORD.getType());
        synchronized (session) {
            session.getBasicRemote().sendText(JSON.toJSONString(recordMap));
        }
    }

    /**
     * 获取客户端真实ip
     */
    public static class ChatConfigurator extends ServerEndpointConfig.Configurator {
        public static String HEADER_NAME = "X-Real-IP";

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            String firstFoundHeader = request.getHeaders().get(HEADER_NAME.toLowerCase()).get(0);
            sec.getUserProperties().put(HEADER_NAME, firstFoundHeader);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() throws IOException {
        // 更新在线人数
        webSocketSet.remove(this);
        updateOnlineCount();
    }

    /**
     * 更新在线人数
     *
     * @throws IOException io异常
     */
    private void updateOnlineCount() throws IOException {
        Map<String, Object> countMap = new HashMap<>(16);
        countMap.put("count", webSocketSet.size());
        countMap.put("type", ONLINE_COUNT.getType());
        for (WebSocketServiceImpl webSocketService : webSocketSet) {
            synchronized (webSocketService.session) {
                webSocketService.session.getBasicRemote().sendText(JSON.toJSONString(countMap));
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Map data = JSON.parseObject(message, Map.class);
        switch (Objects.requireNonNull(getChatType((Integer) data.get("type")))) {
            case SEND_MESSAGE:
                // 发送消息
                ChatRecord chatRecord = JSON.parseObject(JSON.toJSONString(data), ChatRecord.class);
                chatRecordDao.insert(chatRecord);
                data.put("id", chatRecord.getId());
                for (WebSocketServiceImpl webSocketService : webSocketSet) {
                    synchronized (webSocketService.session) {
                        webSocketService.session.getBasicRemote().sendText(JSON.toJSONString(data));
                    }
                }
                break;
            case RECALL_MESSAGE:
                // 撤回消息
                Integer id = (Integer) data.get("id");
                // 删除记录
                deleteRecord(id);
                for (WebSocketServiceImpl webSocketService : webSocketSet) {
                    synchronized (webSocketService.session) {
                        webSocketService.session.getBasicRemote().sendText(message);
                    }
                }
                break;
            default:
                break;
        }

    }

    /**
     * 删除记录
     *
     * @param id ID
     */
    @Async
    public void deleteRecord(Integer id) {
        chatRecordDao.deleteById(id);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 发送语音
     *
     * @param voiceVO 语音路径
     */
    public void sendVoice(VoiceVO voiceVO) throws IOException {
        // 上传语音文件
        String content = OSSUtil.upload(voiceVO.getFile(), FilePathEnum.VOICE.getPath());
        voiceVO.setContent(content);
        // 保存记录
        ChatRecord chatRecord = BeanCopyUtil.copyObject(voiceVO, ChatRecord.class);
        chatRecordDao.insert(chatRecord);
        for (WebSocketServiceImpl webSocketService : webSocketSet) {
            synchronized (webSocketService.session) {
                webSocketService.session.getBasicRemote().sendText(JSON.toJSONString(chatRecord));
            }
        }
    }

}
