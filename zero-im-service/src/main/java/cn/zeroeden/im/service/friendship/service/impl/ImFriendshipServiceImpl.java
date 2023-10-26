package cn.zeroeden.im.service.friendship.service.impl;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.common.enums.CheckFriendShipTypeEnum;
import cn.zeroeden.im.common.enums.FriendShipErrorCode;
import cn.zeroeden.im.common.enums.FriendShipStatusEnum;
import cn.zeroeden.im.common.exception.ApplicationException;
import cn.zeroeden.im.common.model.RequestBase;
import cn.zeroeden.im.service.friendship.dao.ImFriendShipEntity;
import cn.zeroeden.im.service.friendship.dao.mapper.ImFriendShipMapper;
import cn.zeroeden.im.service.friendship.model.req.*;
import cn.zeroeden.im.service.friendship.model.resp.CheckFriendShipResp;
import cn.zeroeden.im.service.friendship.model.resp.ImportFriendShipResp;
import cn.zeroeden.im.service.friendship.service.ImFriendshipService;
import cn.zeroeden.im.service.user.dao.ImUserDataEntity;
import cn.zeroeden.im.service.user.service.ImUserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Zero
 * @time: 2023/10/26
 * @description:
 */


@Service
public class ImFriendshipServiceImpl implements ImFriendshipService {

    @Resource
    private ImFriendShipMapper imFriendShipMapper;

    @Resource
    private ImUserService imUserService;



    @Override
    public ResponseVO importFriendShip(ImporFriendShipReq imporFriendShipReq) {
        if(imporFriendShipReq.getFriendItem().size() > 100){
            return ResponseVO.errorResponse(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }
        ImportFriendShipResp resp = new ImportFriendShipResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        for (ImporFriendShipReq.ImportFriendDto dto : imporFriendShipReq.getFriendItem()) {
            ImFriendShipEntity entity = new ImFriendShipEntity();
            BeanUtils.copyProperties(dto, entity);
            entity.setAppId(imporFriendShipReq.getAppId());
            entity.setFromId(imporFriendShipReq.getFromId());
            try {
                int insert = imFriendShipMapper.insert(entity);
                if(insert == 1){
                    successId.add(dto.getToId());
                }else{
                    errorId.add(dto.getToId());
                }
            }catch (Exception e){
                e.printStackTrace();
                errorId.add(dto.getToId());
            }
        }
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO addFriend(AddFriendReq req) {
        // 判断两个用户存不存在
        ResponseVO fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if(!fromInfo.isOk()){
            return fromInfo;
        }
        ResponseVO toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if(!toInfo.isOk()){
            return toInfo;
        }
        return this.doAddFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    @Override
    public ResponseVO updateFriend(UpdateFriendReq req) {
        // 判断两个用户存不存在
        ResponseVO fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if(!fromInfo.isOk()){
            return fromInfo;
        }
        ResponseVO toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if(!toInfo.isOk()){
            return toInfo;
        }

        return this.doUpdateFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    @Override
    public ResponseVO deleteAllFriend(DeleteFriendReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("status", FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(update, query);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteFriend(DeleteFriendReq req) {

        // 判断两个用户存不存在
        ResponseVO fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if(!fromInfo.isOk()){
            return fromInfo;
        }
        ResponseVO toInfo = imUserService.getSingleUserInfo(req.getToId(), req.getAppId());
        if(!toInfo.isOk()){
            return toInfo;
        }

        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());
        ImFriendShipEntity dbEntity = imFriendShipMapper.selectOne(query);
        if(dbEntity == null){
            // 两人之间不存在任何联系
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }else{
            if(dbEntity.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()){
                // 修改为删除状态
                ImFriendShipEntity entity = new ImFriendShipEntity();
                entity.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
                imFriendShipMapper.update(entity, query);

            }else{
                // 已经被删除了
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }
        }

        return ResponseVO.successResponse();
    }

    public ImFriendShipEntity selectOne(String from_id, String toId,Integer appId){
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("from_id", from_id);
        query.eq("to_id", appId);
        return  imFriendShipMapper.selectOne(query);
    }


    @Transactional
    public ResponseVO doUpdateFriend(String from_id, FriendDto dto, Integer appId){
        LambdaUpdateWrapper<ImFriendShipEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ImFriendShipEntity::getFromId, from_id);
        updateWrapper.eq(ImFriendShipEntity::getToId, dto.getToId());
        updateWrapper.eq(ImFriendShipEntity::getAppId, appId);
        updateWrapper.set(ImFriendShipEntity::getAddSource, dto.getAddSource());
        updateWrapper.set(ImFriendShipEntity::getExtra, dto.getExtra());
        updateWrapper.set(ImFriendShipEntity::getRemark, dto.getRemark());
        imFriendShipMapper.update(null, updateWrapper);
        return ResponseVO.successResponse();
    }




    @Transactional
    public ResponseVO doAddFriend(String from_id, FriendDto dto, Integer appId){
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("from_id", from_id);
        query.eq("to_id", dto.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        if(fromItem == null){
            // 走添加逻辑
            fromItem = new ImFriendShipEntity();
            BeanUtils.copyProperties(dto, fromItem);
            fromItem.setFromId(from_id);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if(insert != 1){
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        }else{
            // 如果存在则判断状态，如果已经添加，则提示已添加 如果是未添加，则修改状态
            if(fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()){
                // 已存在好友关系
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }else{
                // 数据库有记录但不是处于好友关系--更新状态
                ImFriendShipEntity update = new ImFriendShipEntity();
                if(StringUtils.isNoneBlank(dto.getAddSource())){
                    update.setAddSource(dto.getAddSource());
                }
                if(StringUtils.isNoneBlank(dto.getRemark())){
                    update.setRemark(dto.getRemark());
                }
                if(StringUtils.isNoneBlank(dto.getExtra())){
                    update.setExtra(dto.getExtra());
                }
                if(StringUtils.isNoneBlank(dto.getAddSource())){
                    update.setAddSource(dto.getAddSource());
                }

                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                int result = imFriendShipMapper.update(update, query);
                if(result != 1){
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getAllFriendship(GetAllFriendShipReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        return ResponseVO.successResponse(imFriendShipMapper.selectList(query));
    }

    @Override
    public ResponseVO getRelation(GetRelationReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());
        ImFriendShipEntity entity = imFriendShipMapper.selectOne(query);
        if(entity == null){
            // 不存在关系链
            return ResponseVO.errorResponse(FriendShipErrorCode.REPEATSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    @Override
    public ResponseVO checkFriendship(CheckFriendShipReq req) {

        Map<String, Integer> result = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));

        List<CheckFriendShipResp> resp = new ArrayList<>();
        if(req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()){
            resp =  imFriendShipMapper.checkFriendShip(req);
        }else{
            resp = imFriendShipMapper.checkFriendShipBoth(req);
        }
        Map<String, Integer> collect = resp.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));
        for (String s : result.keySet()) {
            if(!collect.containsKey(s)){
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setToId(s);
                checkFriendShipResp.setStatus(result.get(s));
                resp.add(checkFriendShipResp);
            }
        }
        return ResponseVO.successResponse(resp);
    }


    @Override
    public ResponseVO addBlack(AddFriendShipBlackReq req) {
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if(!fromInfo.isOk()){
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToId(), req.getAppId());
        if(!toInfo.isOk()){
            return toInfo;
        }
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id",req.getAppId());
        query.eq("from_id",req.getFromId());
        query.eq("to_id",req.getToId());

        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        Long seq = 0L;
        if(fromItem == null){
            //走添加逻辑。
//            seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.Friendship);

            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(req.getFromId());
            fromItem.setToId(req.getToId());
            fromItem.setFriendSequence(seq);
            fromItem.setAppId(req.getAppId());
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if(insert != 1){
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
//            writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.Friendship,seq);

        } else{
            //如果存在则判断状态，如果是拉黑，则提示已拉黑，如果是未拉黑，则修改状态
            if(fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()){
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
            }

            else {
//                seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.Friendship);

                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setFriendSequence(seq);
                update.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                int result = imFriendShipMapper.update(update, query);
                if(result != 1){
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR);
                }
//                writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.Friendship,seq);

            }
        }

//        AddFriendBlackPack addFriendBlackPack = new AddFriendBlackPack();
//        addFriendBlackPack.setFromId(req.getFromId());
//        addFriendBlackPack.setSequence(seq);
//        addFriendBlackPack.setToId(req.getToId());
//        //发送tcp通知
//        messageProducer.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
//                FriendshipEventCommand.FRIEND_BLACK_ADD, addFriendBlackPack, req.getAppId());
//
//        //之后回调
//        if (appConfig.isAddFriendShipBlackAfterCallback()){
//            AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
//            callbackDto.setFromId(req.getFromId());
//            callbackDto.setToId(req.getToId());
//            callbackService.beforeCallback(req.getAppId(),
//                    Constants.CallbackCommand.AddBlackAfter, JSONObject
//                            .toJSONString(callbackDto));
//        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteBlack(DeleteBlackReq req) {
        QueryWrapper queryFrom = new QueryWrapper<>()
                .eq("from_id", req.getFromId())
                .eq("app_id", req.getAppId())
                .eq("to_id", req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(queryFrom);
        if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }

//        long seq = redisSeq.doGetSeq(req.getAppId() + ":" + Constants.SeqConstants.Friendship);

        ImFriendShipEntity update = new ImFriendShipEntity();
//        update.setFriendSequence(seq);
        update.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        int update1 = imFriendShipMapper.update(update, queryFrom);
        if(update1 == 1){
//            writeUserSeq.writeUserSeq(req.getAppId(),req.getFromId(),Constants.SeqConstants.Friendship,seq);
//            DeleteBlackPack deleteFriendPack = new DeleteBlackPack();
//            deleteFriendPack.setFromId(req.getFromId());
//            deleteFriendPack.setSequence(seq);
//            deleteFriendPack.setToId(req.getToId());
//            messageProducer.sendToUser(req.getFromId(), req.getClientType(), req.getImei(), FriendshipEventCommand.FRIEND_BLACK_DELETE,
//                    deleteFriendPack, req.getAppId());
//
//            //之后回调
//            if (appConfig.isAddFriendShipBlackAfterCallback()){
//                AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
//                callbackDto.setFromId(req.getFromId());
//                callbackDto.setToId(req.getToId());
//                callbackService.beforeCallback(req.getAppId(),
//                        Constants.CallbackCommand.DeleteBlack, JSONObject
//                                .toJSONString(callbackDto));
//            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO checkBlack(CheckFriendShipReq req) {
        Map<String, Integer> toIdMap
                = req.getToIds().stream().collect(Collectors
                .toMap(Function.identity(), s -> 0));
        List<CheckFriendShipResp> result = new ArrayList<>();
        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            result = imFriendShipMapper.checkFriendShipBlack(req);
        } else {
            result = imFriendShipMapper.checkFriendShipBlackBoth(req);
        }

        Map<String, Integer> collect = result.stream()
                .collect(Collectors
                        .toMap(CheckFriendShipResp::getToId,
                                CheckFriendShipResp::getStatus));
        for (String toId:
                toIdMap.keySet()) {
            if(!collect.containsKey(toId)){
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setStatus(toIdMap.get(toId));
                result.add(checkFriendShipResp);
            }
        }

        return ResponseVO.successResponse(result);
    }
}

