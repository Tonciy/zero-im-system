package cn.zeroeden.im.service.user.controller;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.service.user.model.req.DeleteUserReq;
import cn.zeroeden.im.service.user.model.req.GetUserSequenceReq;
import cn.zeroeden.im.service.user.model.req.ImportUserReq;
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
@RequestMapping("v1/user")
public class ImUserController {

    @Resource
    private ImUserService imUserService;


    @PostMapping("/importUser")
    public ResponseVO importUser(@RequestBody ImportUserReq importUserReq,
                                 Integer appId){
        importUserReq.setAppId(appId);
        return imUserService.importUser(importUserReq);
    }

    @PostMapping("/deleteUser")
    public ResponseVO deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.deleteUser(req);
    }

    @PostMapping("/getUserSequence")
    public ResponseVO getUserSequence(@RequestBody @Validated
                                      GetUserSequenceReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.getUserSequence(req);
    }

}
