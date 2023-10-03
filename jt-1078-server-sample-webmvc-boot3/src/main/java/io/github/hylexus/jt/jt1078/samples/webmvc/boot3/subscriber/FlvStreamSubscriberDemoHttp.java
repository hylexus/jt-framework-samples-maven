package io.github.hylexus.jt.jt1078.samples.webmvc.boot3.subscriber;

import io.github.hylexus.jt.jt1078.samples.webmvc.boot3.model.dto.DemoVideoStreamSubscriberDto;
import io.github.hylexus.jt.jt1078.samples.webmvc.boot3.model.values.MyJt1078SessionCloseReason;
import io.github.hylexus.jt.jt1078.spec.Jt1078Publisher;
import io.github.hylexus.jt.jt1078.spec.Jt1078SessionManager;
import io.github.hylexus.jt.jt1078.spec.exception.Jt1078SessionDestroyException;
import io.github.hylexus.jt.jt1078.support.codec.Jt1078ChannelCollector;
import io.github.hylexus.jt.utils.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Controller
@RequestMapping("/jt1078/subscription/http/flv")
public class FlvStreamSubscriberDemoHttp {

    public static final Scheduler BOUNDED_ELASTIC = Schedulers.newBoundedElastic(8, 128, "HttpDemo");
    private final Jt1078SessionManager sessionManager;
    private final Jt1078Publisher publisher;

    public FlvStreamSubscriberDemoHttp(Jt1078SessionManager sessionManager, Jt1078Publisher publisher) {
        this.sessionManager = sessionManager;
        this.publisher = publisher;
    }

    @RequestMapping(value = "/{sim}/{channel}")
    public ResponseBodyEmitter handle(
            // HttpServletResponse response,
            DemoVideoStreamSubscriberDto params) {

        // response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        final ResponseBodyEmitter sseEmitter = new ResponseBodyEmitter(0L);
        this.subscribeFlvStream(params, sseEmitter);
        return sseEmitter;
    }

    private void subscribeFlvStream(DemoVideoStreamSubscriberDto params, ResponseBodyEmitter sseEmitter) {
        final int timeout = params.getTimeout();
        this.publisher.subscribe(Jt1078ChannelCollector.H264_TO_FLV_COLLECTOR, params.getSim(), params.getChannel(), Duration.ofSeconds(timeout))
                .publishOn(BOUNDED_ELASTIC)
                .onErrorComplete(Jt1078SessionDestroyException.class)
                .onErrorComplete(TimeoutException.class)
                .doOnError(Jt1078SessionDestroyException.class, e -> {
                    log.error("取消订阅(Session销毁)");
                })
                .doOnError(TimeoutException.class, e -> {
                    log.error("取消订阅(超时, {} 秒)", timeout);
                })
                .doOnError(Throwable.class, e -> {
                    log.error(e.getMessage(), e);
                    // 异常结束
                    sseEmitter.completeWithError(e);
                })
                .doOnNext(subscription -> {
                    final byte[] payload = subscription.payload();
                    log.info("Http outbound {}", FormatUtils.toHexString(payload));
                })
                .doFinally(signalType -> {
                    log.info("Http outbound complete with signal: {}", signalType);
                    if (params.isAutoCloseJt1078SessionOnClientClosed()) {
                        this.sessionManager.removeBySimAndChannelAndThenClose(params.getSim(), params.getChannel(), MyJt1078SessionCloseReason.CLOSED_BY_WEB_SOCKET);
                        log.info("Jt1078SessionClosed By HttpStream: {}", params);
                    }
                    // 正常结束
                    sseEmitter.complete();
                }).subscribe(subscription -> {
                    final byte[] payload = subscription.payload();
                    try {
                        // 异常结束
                        log.info("send: {}", FormatUtils.toHexString(payload));
                        sseEmitter.send(payload, MediaType.APPLICATION_OCTET_STREAM);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                });
    }

}
