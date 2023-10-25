package cn.zeroeden.im.service.user.service.impl;

import cn.zeroeden.im.common.ResponseVO;
import cn.zeroeden.im.common.enums.DelFlagEnum;
import cn.zeroeden.im.common.enums.UserErrorCode;
import cn.zeroeden.im.common.exception.ApplicationException;
import cn.zeroeden.im.service.user.dao.ImUserDataEntity;
import cn.zeroeden.im.service.user.dao.mapper.ImUserDataMapper;
import cn.zeroeden.im.service.user.model.req.*;
import cn.zeroeden.im.service.user.model.resp.GetUserInfoResp;
import cn.zeroeden.im.service.user.model.resp.ImportUserResp;
import cn.zeroeden.im.service.user.service.ImUserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Zero
 * @time: 2023/10/24
 * @description:
 */

@Service
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
                }else{
                    errorIdList.add(e.getUserId());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorIdList.add(e.getUserId());
            }
        });
        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessIdList(successsIdList);
        resp.setErrorIdList(errorIdList);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO getUserSequence(GetUserSequenceReq req) {
//        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(req.getAppId() + ":" + Constants.RedisConstants.SeqPrefix + ":" + req.getUserId());
//        Long groupSeq = imGroupService.getUserGroupMaxSeq(req.getUserId(),req.getAppId());
//        map.put(Constants.SeqConstants.Group,groupSeq);
//        return ResponseVO.successResponse(map);
        return  null;
    }

    @Override
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper query = new QueryWrapper<>();
        query.eq("app_id",req.getAppId());
        query.eq("user_id",req.getUserId());
        query.eq("del_flag",DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity user = imUserDataMapper.selectOne(query);
        if(user == null){
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        ImUserDataEntity update = new ImUserDataEntity();
        BeanUtils.copyProperties(req,update);

        update.setAppId(null);
        update.setUserId(null);
        int update1 = imUserDataMapper.update(update, query);
        if(update1 == 1){

//            UserModifyPack pack = new UserModifyPack();
//            BeanUtils.copyProperties(req,pack);
//            messageProducer.sendToUser(req.getUserId(),req.getClientType(),req.getImei(),
//                    UserEventCommand.USER_MODIFY,pack,req.getAppId());
//
//            if(appConfig.isModifyUserAfterCallback()){
//                callbackService.callback(req.getAppId(),
//                        Constants.CallbackCommand.ModifyUserAfter,
//                        JSONObject.toJSONString(req));
//            }
            return ResponseVO.successResponse();
        }
        throw new ApplicationException(UserErrorCode.MODIFY_USER_ERROR);
    }

    @Override
    public ResponseVO getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("app_id",appId);
        objectQueryWrapper.eq("user_id",userId);
        objectQueryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity ImUserDataEntity = imUserDataMapper.selectOne(objectQueryWrapper);
        if(ImUserDataEntity == null){
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }

        return ResponseVO.successResponse(ImUserDataEntity);
    }

    @Override
    public ResponseVO getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id",req.getAppId());
        queryWrapper.in("user_id",req.getUserIds());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        List<ImUserDataEntity> userDataEntities = imUserDataMapper.selectList(queryWrapper);
        HashMap<String, ImUserDataEntity> map = new HashMap<>();

        for (ImUserDataEntity data:
                userDataEntities) {
            map.put(data.getUserId(),data);
        }

        List<String> failUser = new ArrayList<>();
        for (String uid:
                req.getUserIds()) {
            if(!map.containsKey(uid)){
                failUser.add(uid);
            }
        }

        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList();
        List<String> successId = new ArrayList();

        for (String userId:
                req.getUserId()) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("app_id",req.getAppId());
            wrapper.eq("user_id",userId);
            wrapper.eq("del_flag",DelFlagEnum.NORMAL.getCode());
            int update = 0;

            try {
                update =  imUserDataMapper.update(entity, wrapper);
                if(update > 0){
                    successId.add(userId);
                }else{
                    errorId.add(userId);
                }
            }catch (Exception e){
                errorId.add(userId);
            }
        }

        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessIdList(successId);
        resp.setErrorIdList(errorId);
        return ResponseVO.successResponse(resp);
    }
}
