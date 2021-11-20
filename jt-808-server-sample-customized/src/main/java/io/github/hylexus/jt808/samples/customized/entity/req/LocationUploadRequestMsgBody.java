package io.github.hylexus.jt808.samples.customized.entity.req;

import io.github.hylexus.jt808.msg.RequestMsgBody;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author hylexus
 */
@Data
@Accessors(chain = true)
public class LocationUploadRequestMsgBody implements RequestMsgBody {
    private int warningFlag;

    private int status;

    private Double lat;

    private Double lng;

    private short height;

    private short speed;

    private Short direction;

    private String time;
}
