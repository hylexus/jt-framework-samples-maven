package io.github.hylexus.jt.jt1078.samples.webmvc.boot3.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DemoVideoStreamSubscriberDto {

    // 终端手机号
    private String sim;
    // 逻辑通道号
    private short channel;
    // 超时时间(秒)
    // 超过 timeout 秒之后依然没有收到终端的数据 ==> 自动关闭当前订阅(websocket)
    private int timeout = 10;
    // websocket/http 断开时自动关闭 1078 服务端和终端的连接?
    private boolean autoCloseJt1078SessionOnClientClosed = true;
}
