package cn.zeroeden.im.service.friendship.service;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.service.friendship.model.req.*;

/**
 * @author: Zero
 * @time: 2023/10/26
 * @description:
 */

public interface ImFriendshipService {
    ResponseVO importFriendShip(ImporFriendShipReq imporFriendShipReq);

    ResponseVO addFriend(AddFriendReq req);

    ResponseVO updateFriend(UpdateFriendReq req);


    ResponseVO deleteFriend(DeleteFriendReq req);

    ResponseVO deleteAllFriend(DeleteFriendReq req);


    /**
     * 获取所有好友关系--包括删除的
     */
    ResponseVO getAllFriendship(GetAllFriendShipReq req);


    /**
     * 查询两个人之间的额关系链
     */
    ResponseVO getRelation(GetRelationReq req);

    ResponseVO checkFriendship(CheckFriendShipReq req);

     ResponseVO addBlack(AddFriendShipBlackReq req);

     ResponseVO deleteBlack(DeleteBlackReq req);

     ResponseVO checkBlack(CheckFriendShipReq req);

}
