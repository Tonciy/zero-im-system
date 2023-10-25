package cn.zeroeden.im.service.user.model.req;

import cn.zeroeden.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class UserId extends RequestBase {

    @NotNull
    private String userId;

}
