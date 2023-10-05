package io.github.hylexus.jt.jt1078.samples.webmvc.boot3.config;

import io.github.hylexus.jt.jt1078.samples.webmvc.boot3.subscriber.FlvStreamSubscriberDemoWebSocket;
import io.github.hylexus.jt.jt1078.spec.Jt1078Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final Jt1078Publisher jt1078Publisher;

    public WebSocketConfig(Jt1078Publisher jt1078Publisher) {
        this.jt1078Publisher = jt1078Publisher;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketSubscriberDemoFlv(), FlvStreamSubscriberDemoWebSocket.PATH_PATTERN)
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler webSocketSubscriberDemoFlv() {
        return new FlvStreamSubscriberDemoWebSocket(this.jt1078Publisher);
    }

}
