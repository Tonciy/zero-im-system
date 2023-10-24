package cn.zeroeden.im.service.user.service;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.service.user.model.req.ImportUserReq;

public interface ImUserService {

    public ResponseVO importUser(ImportUserReq req);
}
