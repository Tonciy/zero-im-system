package cn.zeroeden.im.service.user.service;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.service.user.model.req.*;

public interface ImUserService {

    public ResponseVO importUser(ImportUserReq req);

    ResponseVO deleteUser(DeleteUserReq req);

    ResponseVO getUserSequence(GetUserSequenceReq req);

    ResponseVO getUserInfo(GetUserInfoReq req);

    ResponseVO getSingleUserInfo(String userId, Integer appId);

    ResponseVO modifyUserInfo(ModifyUserInfoReq req);
}
