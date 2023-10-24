package cn.zeroeden.im.service.user.model.resp;

import lombok.Data;

import java.util.List;

/**
 * @author: Zero
 * @time: 2023/10/24
 * @description:
 */

@Data
public class ImportUserResp {
    private List<String> successIdList;
    private List<String> errorIdList;

}
