package cn.zeroeden.im.service.user.model.req;

import cn.zeroeden.im.common.model.RequestBase;
import cn.zeroeden.im.service.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @author: Zero
 * @time: 2023/10/24
 * @description: 导入用户
 */

@Data
public class ImportUserReq extends RequestBase {

    private List<ImUserDataEntity> userList;
}
