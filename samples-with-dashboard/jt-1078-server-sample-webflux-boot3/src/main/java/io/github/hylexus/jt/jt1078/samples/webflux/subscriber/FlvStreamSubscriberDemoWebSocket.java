package io.github.hylexus.jt.jt1078.samples.webflux.subscriber;

import io.github.hylexus.jt.jt1078.samples.webflux.model.dto.DemoVideoStreamSubscriberDto;
import io.github.hylexus.jt.jt1078.samples.webflux.model.values.MyJt1078SessionCloseReason;
import io.github.hylexus.jt.jt1078.samples.webflux.utils.WebSocketUtils;
import io.github.hylexus.jt.jt1078.spec.Jt1078Publisher;
import io.github.hylexus.jt.jt1078.spec.Jt1078SessionManager;
import io.github.hylexus.jt.jt1078.spec.exception.Jt1078SessionDestroyException;
import io.github.hylexus.jt.jt1078.support.codec.Jt1078ChannelCollector;
import io.github.hylexus.jt.utils.HexStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * 示例代码--通过 WebSocket(推荐) 协议播放 FLV 视频流
 */
@Slf4j
public class FlvStreamSubscriberDemoWebSocket implements WebSocketHandler {
    public static final Scheduler BOUNDED_ELASTIC = Schedulers.newBoundedElastic(8, 128, "WsDemo");
    public static final String PATH_PATTERN = "/jt1078/subscription/websocket/flv/{sim}/{channel}";
    private final Jt1078Publisher jt1078Publisher;
    private final UriTemplate uriTemplate;

    private final Jt1078SessionManager sessionManager;

    public FlvStreamSubscriberDemoWebSocket(Jt1078Publisher jt1078Publisher, Jt1078SessionManager sessionManager) {
        this.jt1078Publisher = jt1078Publisher;
        this.sessionManager = sessionManager;
        this.uriTemplate = new UriTemplate(PATH_PATTERN);
    }

    @NonNull
    @Override
    public Mono<Void> handle(@NonNull WebSocketSession session) {

        final DemoVideoStreamSubscriberDto params = WebSocketUtils.createForReactiveSession(session, this.uriTemplate);
        log.info("New FLV publisher created via WebSocket: {}", params);

        // websocket inbound
        final Mono<Void> input = session.receive()
                .doOnNext(message -> {
                    log.info("Receive webSocket msg, webSocketSessionId={}, payload={}", session.getId(), message.getPayloadAsText());
                })
                .then();

        // FLV 数据流
        // websocket outbound
        final Mono<Void> flvStream = this.subscribeFlv(session, params);

        return Mono.zip(input, flvStream).doFinally(signalType -> {
            if (params.isAutoCloseJt1078SessionOnClientClosed()) {
                this.sessionManager.removeBySimAndChannelAndThenClose(params.getSim(), params.getChannel(), MyJt1078SessionCloseReason.CLOSED_BY_WEB_SOCKET);
                log.info("Jt1078SessionClosed By WebSocket: {}", params);
            }
        }).then();
    }


    private Mono<Void> subscribeFlv(WebSocketSession session, DemoVideoStreamSubscriberDto params) {
        return this.jt1078Publisher
                .subscribe(Jt1078ChannelCollector.H264_TO_FLV_COLLECTOR, params.getSim(), params.getChannel(), Duration.ofSeconds(params.getTimeout()))
                .publishOn(BOUNDED_ELASTIC)
                .onErrorComplete(Jt1078SessionDestroyException.class)
                .onErrorComplete(TimeoutException.class)
                .doOnError(Jt1078SessionDestroyException.class, e -> {
                    log.error("取消订阅(Session销毁)");
                })
                .doOnError(TimeoutException.class, e -> {
                    log.error("取消订阅(超时, {} 秒)", params.getTimeout());
                })
                .doOnError(Throwable.class, e -> {
                    log.error(e.getMessage(), e);
                })
                .flatMap(subscription -> {
                    final byte[] data = subscription.payload();

                    log.info("FLV WebSocket outbound: {}", HexStringUtils.bytes2HexString(data));

                    final WebSocketMessage webSocketMessage = session.binaryMessage(factory -> factory.wrap(data));
                    return Mono.just(webSocketMessage);
                })
                .flatMap(it -> session.send(Mono.just(it)))
                .then();
    }
}
