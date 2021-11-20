package io.github.hylexus.jt808.samples.mixedversion.handler;

import io.github.hylexus.jt.annotation.msg.handler.Jt808RequestMsgHandler;
import io.github.hylexus.jt.config.Jt808ProtocolVersion;
import io.github.hylexus.jt808.handler.AbstractMsgHandler;
import io.github.hylexus.jt808.msg.RequestMsgMetadata;
import io.github.hylexus.jt808.msg.RespMsgBody;
import io.github.hylexus.jt808.samples.mixedversion.entity.req.RegisterRequestMsgV2011;
import io.github.hylexus.jt808.samples.mixedversion.entity.resp.RegisterRespMsgV2011;
import io.github.hylexus.jt808.session.Jt808Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * @author hylexus
 */
@Slf4j
// 也可以将 MsgHandler 加入到 Spring 容器中
@Component
@Jt808RequestMsgHandler(msgType = 0x0100)
public class RegisterMsgV2011MsgHandler extends AbstractMsgHandler<RegisterRequestMsgV2011> {

    @Override
    public Set<Jt808ProtocolVersion> getSupportedProtocolVersions() {
        return Jt808ProtocolVersion.unmodifiableSetVersion2011();
    }

    @Override
    protected Optional<RespMsgBody> doProcess(RequestMsgMetadata metadata, RegisterRequestMsgV2011 msg, Jt808Session session) {
        final RegisterRespMsgV2011 registerRespMsgV2011 = new RegisterRespMsgV2011(
                metadata.getHeader().getFlowId(),
                (byte) 0,
                "admin-123456"
        );
        return Optional.of(registerRespMsgV2011);
    }
}
