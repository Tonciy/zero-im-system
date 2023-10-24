package cn.zeroeden.im.service.user.service.impl;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.service.user.dao.mapper.ImUserDataMapper;
import cn.zeroeden.im.service.user.model.req.ImportUserReq;
import cn.zeroeden.im.service.user.model.resp.ImportUserResp;
import cn.zeroeden.im.service.user.service.ImUserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Zero
 * @time: 2023/10/24
 * @description:
 */

public class ImUserServiceImpl implements ImUserService {

    @Resource
    private ImUserDataMapper imUserDataMapper;

    @Override
    public ResponseVO importUser(ImportUserReq req) {
        if (req.getUserList().size() > 100) {
            //TODO 数量太多
        }

        List<String> successsIdList = new ArrayList<>();
        List<String> errorIdList = new ArrayList<>();


        req.getUserList().forEach(e -> {
            try {
                e.setAppId(req.getAppId());
                int count = imUserDataMapper.insert(e);
                if (count == 1) {
                    successsIdList.add(e.getUserId());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorIdList.add(e.getUserId());
            }
        });
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessIdList(successsIdList);
        resp.setErrorIdList(errorIdList);
        return null;
    }
}
