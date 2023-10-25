package cn.zeroeden.im.service.user.controller;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.service.user.model.req.GetUserInfoReq;
import cn.zeroeden.im.service.user.model.req.ModifyUserInfoReq;
import cn.zeroeden.im.service.user.model.req.UserId;
import cn.zeroeden.im.service.user.service.ImUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: Zero
 * @time: 2023/10/25
 * @description:
 */


@RestController
@RequestMapping("v1/user/data")
public class ImUserDataController {

    @Resource
    private ImUserService imUserService;

    /**
     * 获取多个用户数据
     */
    @PostMapping("/getUserInfo")
    public ResponseVO getUserInfo(@RequestBody GetUserInfoReq req, Integer appId){//@Validated
        req.setAppId(appId);
        return imUserService.getUserInfo(req);
    }



    @PostMapping("/getSingleUserInfo")
    public ResponseVO getSingleUserInfo(@RequestBody @Validated UserId req, Integer appId){
        req.setAppId(appId);
        return imUserService.getSingleUserInfo(req.getUserId(),req.getAppId());
    }

    @PostMapping("/modifyUserInfo")
    public ResponseVO modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req, Integer appId){
        req.setAppId(appId);
        return imUserService.modifyUserInfo(req);
    }
}
