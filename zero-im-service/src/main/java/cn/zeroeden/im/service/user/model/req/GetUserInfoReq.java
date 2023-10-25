package cn.zeroeden.im.service.user.model.req;

import cn.zeroeden.im.common.model.RequestBase;
import lombok.Data;

import java.util.List;


@Data
public class GetUserInfoReq extends RequestBase {

    private List<String> userIds;


}
