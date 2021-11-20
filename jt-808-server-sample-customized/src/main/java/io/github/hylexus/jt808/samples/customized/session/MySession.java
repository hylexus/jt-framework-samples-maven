package io.github.hylexus.jt808.samples.customized.session;

import io.github.hylexus.jt808.session.Session;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author hylexus
 */
@Getter
@Setter
@ToString
public class MySession extends Session {
    private String someField;
}
