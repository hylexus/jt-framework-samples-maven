package io.github.hylexus.jt.jt1078.samples.webflux.utils;

import io.github.hylexus.jt.jt1078.samples.webflux.model.dto.DemoVideoStreamSubscriberDto;
import io.github.hylexus.oaks.utils.Numbers;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class WebSocketUtils {

    public static DemoVideoStreamSubscriberDto createForReactiveSession(WebSocketSession session, UriTemplate uriTemplate) {
        final URI uri = session.getHandshakeInfo().getUri();
        final Map<String, String> values = uriTemplate.match(uri.getPath());
        final String sim = values.getOrDefault("sim", "018930946552");
        final short channel = Numbers.parseInteger(values.getOrDefault("channel", "3")).orElseThrow().shortValue();
        final String query = uri.getQuery();
        if (!StringUtils.hasText(query)) {
            return new DemoVideoStreamSubscriberDto().setSim(sim).setChannel(channel);
        }

        final String[] arrays = query.split("&");
        final Map<String, String> params = new HashMap<>();
        for (final String item : arrays) {
            final String[] split = item.split("=");
            if (split.length == 2) {
                params.put(split[0], split[1]);
            }
        }

        final int timeout = Numbers.parseInteger(params.get("timeout")).orElse(10);
        return new DemoVideoStreamSubscriberDto()
                .setSim(sim)
                .setChannel(channel)
                .setTimeout(timeout)
                .setAutoCloseJt1078SessionOnClientClosed(parseBoolean(params, "autoCloseJt1078SessionOnClientClosed", false))
                ;
    }

    public static boolean parseBoolean(Map<String, String> params, String key, boolean def) {
        return Optional.ofNullable(params.get(key)).map(it -> {
            try {
                return Boolean.parseBoolean(it);
            } catch (Exception e) {
                return def;
            }
        }).orElse(def);
    }
}
