package io.github.hylexus.jt808.samples.annotation.config;

import io.github.hylexus.jt.data.msg.BuiltinJt808MsgType;
import io.github.hylexus.jt.data.msg.MsgType;
import io.github.hylexus.jt808.boot.config.Jt808ServerConfigurationSupport;
import io.github.hylexus.jt808.converter.MsgTypeParser;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * @author hylexus
 */
@Configuration
public class Jt808Configuration extends Jt808ServerConfigurationSupport {

    // [[必须配置]] -- 自定义消息类型解析器
    @Override
    public MsgTypeParser supplyMsgTypeParser() {
        return msgType -> {
            Optional<MsgType> type = Jt808MsgType.CLIENT_AUTH.parseFromInt(msgType);
            return type.isPresent()
                    ? type
                    // 自定义解析器无法解析,使用内置解析器
                    : BuiltinJt808MsgType.CLIENT_AUTH.parseFromInt(msgType);
        };
    }

}
