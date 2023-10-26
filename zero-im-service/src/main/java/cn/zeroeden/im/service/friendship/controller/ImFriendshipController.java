package cn.zeroeden.im.service.friendship.controller;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.service.friendship.model.req.*;
import cn.zeroeden.im.service.friendship.service.ImFriendshipService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: Zero
 * @time: 2023/10/26
 * @description:
 */


@RestController
@RequestMapping("/v1/friendship")
public class ImFriendshipController {

    @Resource
    private ImFriendshipService imFriendshipService;

    @PostMapping("/importFriendShip")
    public ResponseVO importFriendShip(@RequestBody @Validated ImporFriendShipReq imporFriendShipReq,
                                       Integer appId) {
        imporFriendShipReq.setAppId(appId);
        return imFriendshipService.importFriendShip(imporFriendShipReq);
    }

    @PostMapping("/addFriend")
    public ResponseVO addFriend(@RequestBody @Validated AddFriendReq addFriendReq,
                                Integer appId) {
        addFriendReq.setAppId(appId);
        return imFriendshipService.addFriend(addFriendReq);
    }

    @PostMapping("/updateFriend")
    public ResponseVO updateFriend(@RequestBody @Validated UpdateFriendReq updateFriendReq,
                                   Integer appId) {
        updateFriendReq.setAppId(appId);
        return imFriendshipService.updateFriend(updateFriendReq);
    }

    @PostMapping("/deleteFriend")
    public ResponseVO deleteFriend(@RequestBody @Validated DeleteFriendReq deleteFriendReq,
                                   Integer appId) {
        deleteFriendReq.setAppId(appId);
        return imFriendshipService.deleteFriend(deleteFriendReq);
    }

    @PostMapping("/deleteAllFriend")
    public ResponseVO deleteAllFriend(@RequestBody @Validated DeleteFriendReq deleteFriendReq,
                                      Integer appId) {
        deleteFriendReq.setAppId(appId);
        return imFriendshipService.deleteAllFriend(deleteFriendReq);
    }

    @PostMapping("/getAllFriend")
    public ResponseVO getAllFriendship(@RequestBody @Validated GetAllFriendShipReq req,
                                       Integer appId) {
        req.setAppId(appId);
        return imFriendshipService.getAllFriendship(req);
    }

    @PostMapping("/getRelation")
    public ResponseVO getRelation(@RequestBody @Validated GetRelationReq req,
                                  Integer appId) {
        req.setAppId(appId);
        return imFriendshipService.getRelation(req);
    }

    @PostMapping("/checkFriendship")
    public ResponseVO checkFriendship(@RequestBody @Validated CheckFriendShipReq req,
                                  Integer appId) {
        req.setAppId(appId);
        return imFriendshipService.checkFriendship(req);
    }
}
