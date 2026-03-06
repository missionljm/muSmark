package com.mu.musmart.service.user.impl;


import cn.hutool.Hutool;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.dao.user.UserDao;
import com.mu.musmart.dao.user.UserInfoDao;
import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;
import com.mu.musmart.domain.entity.IpInfo;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.domain.entity.user.UserInfoDO;
import com.mu.musmart.enums.common.StatusEnum;
import com.mu.musmart.exception.ExceptionUtil;
import com.mu.musmart.mapper.CommonMapper;
import com.mu.musmart.service.help.UserSessionHelper;
import com.mu.musmart.service.user.UserService;
import com.mu.musmart.util.IpUtil;
import com.mu.musmart.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户Service
 *
 * @author lijinmu
 * @date 2025-07-20
 */
@Service("UserService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private UserSessionHelper userSessionHelper;

    @Autowired
    private CommonMapper commonMapper;



    @Override
    public BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String sessionId , String clientIp) {
        Long userId = userSessionHelper.getUserIdBySession(sessionId);
        UserDO userDo = userDao.getUserById(userId);
        UserInfoDO userInfo = userInfoDao.getUserInfoById(userId);
        BaseUserInfoDTO baseUserInfoDTO = new BaseUserInfoDTO();
        baseUserInfoDTO.setId(userDo.getId());
        baseUserInfoDTO.setUserAccount(userDo.getUserName());
        baseUserInfoDTO.setRegion(clientIp);
        baseUserInfoDTO.setEmail(userInfo.getEmail());
        baseUserInfoDTO.setPhone(userInfo.getPhone());
        baseUserInfoDTO.setPhoto(userInfo.getPhoto());
        baseUserInfoDTO.setAvatar(userInfo.getAvatar());
        baseUserInfoDTO.setUserName(userInfo.getUserName());
        baseUserInfoDTO.setIdCard(userInfo.getIdCard());
        return baseUserInfoDTO;
    }

    @Override
    public List<Map> getUserPermissions() {
        ReqInfoContext.ReqInfo reqInfo = ReqInfoContext.getReqInfo();
        return queryUserPermissionList(new ArrayList<>() , reqInfo.getUser().getId() , 0);
//        List<Map<String, Object>> userPermission = commonMapper.getUserPermission(reqInfo.getUserId());
    }

    @Override
    public List<Map> queryUserPermissionList(List<Map> userPermissionList ,Long userId , Integer level) {
        if (ObjectUtil.isNotEmpty(userId)){
            List<Map<String, Object>> userPermission = commonMapper.getUserPermission(userId , level);
            if (CollectionUtil.isEmpty(userPermission)){
                return userPermissionList;
            }
            userPermissionList.addAll(userPermission);
            for (Map<String, Object> map : userPermissionList){
                Long menuId = Long.valueOf(map.get("id").toString());
                map.remove("id");
                Map<String, Object> meta = new HashMap<>();
                meta.put("title" , map.get("title"));
                meta.put("icon" , map.get("icon"));
                map.put("meta" , meta);
                map.remove("title");
                map.remove("icon");
                this.queryUserPermissionTree(map , menuId , userId);
            }

            log.info("UserServiceImpl_queryUserPermissionList_userPermissionList:{}" , JSONUtil.toJsonStr(userPermissionList));
        }
        return userPermissionList;
    }

    /**
     * 递归查询用户权限树
     * @param paramsMap
     * @param parentId
     */
    public void queryUserPermissionTree( Map paramsMap , Long parentId , Long userId){
        List<Map<String, Object>> userPermission = commonMapper.getUserPermissionByParentsId(parentId , userId);
        if (CollectionUtil.isEmpty(userPermission)){
            return;
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> map : userPermission){
            Long menuId = Long.valueOf(map.get("id").toString());
            map.remove("id");
            Map<String, Object> meta = new HashMap<>();
            meta.put("title" , map.get("title"));
            meta.put("icon" , map.get("icon"));
            map.put("meta" , meta);
            map.remove("title");
            map.remove("icon");
            this.queryUserPermissionTree(map  , menuId , userId);
            resultList.add(map);
        }
        paramsMap.put("children" , resultList);
    }

    /**
     * 分页查询用户信息
     * @param params
     * @return
     */
    @Override
    public IPage<Map> getPageUserList(Map params) {
        //使用mybatis的分页方法
        Page<UserDO> pageUser = new Page<>(Long.valueOf(params.get("page").toString()), Long.valueOf(params.get("limit").toString()));
        //查询所有用户信息进行返回
        IPage<Map> page = userDao.getUserPageList(pageUser, params);
        return page;
    }

    @Override
    public String addUser(Map userMap) {
        UserDO userDO = new UserDO();
        UserInfoDO userInfoDO = new UserInfoDO();
        //生成雪花id
        Snowflake snowflake = new Snowflake(1, 1);
        long id = snowflake.nextId();
        userDO.setId(id);
        userDO.setUserName(userMap.get("phone").toString());
        userInfoDO.setUserId(id);
        userInfoDO.setUserName(userMap.get("userName").toString());
        userInfoDO.setPhone(userMap.get("phone").toString());
        userInfoDO.setEmail(userMap.get("email").toString());
        IpInfo ip = userInfoDO.getIp();
        String clientIp = userMap.get("ip").toString();
        if (clientIp != null && !Objects.equals(ip.getLatestIp(), clientIp)) {
            // ip不同，需要更新
            ip.setLatestIp(clientIp);
            ip.setLatestRegion(IpUtil.getLocationByIp(clientIp).toRegionStr());

            if (ip.getFirstIp() == null) {
                ip.setFirstIp(clientIp);
                ip.setFirstRegion(ip.getLatestRegion());
            }
        }
        if (userMap.containsKey("company")) {
            userInfoDO.setCompany(userMap.get("company").toString());
        }
        if (userMap.containsKey("profile")) {
            userInfoDO.setProfile(userMap.get("profile").toString());
        }
        if (userMap.containsKey("position")) {
            userInfoDO.setPosition(userMap.get("position").toString());
        }
        try {
            userDao.save(userDO);
            userInfoDao.save(userInfoDO);
            return "success";
        } catch (Exception e) {
            throw ExceptionUtil.of(StatusEnum.UNEXPECT_ERROR , e.getMessage());
        }
    }



    @Override
    public void updateUser(Map userMap) {
        UserInfoDO userInfoDO = new UserInfoDO();
        LambdaQueryWrapper<UserInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        if (!userMap.containsKey("id")){
            throw ExceptionUtil.of(StatusEnum.UNEXPECT_ERROR , "用户id不能为空");
        }
        queryWrapper.eq(UserInfoDO::getUserId , userMap.get("id"));
        userInfoDO = userInfoDao.getOne(queryWrapper);
        if (userMap.containsKey("company")){
            userInfoDO.setCompany(userMap.get("company").toString());
        }
        if (userMap.containsKey("profile")){
            userInfoDO.setProfile(userMap.get("profile").toString());
        }
        if (userMap.containsKey("position")){
            userInfoDO.setPosition(userMap.get("position").toString());
        }
        if (userMap.containsKey("avatar")){
            userInfoDO.setAvatar(userMap.get("avatar").toString());
        }
        if (userMap.containsKey("phone")){
            userInfoDO.setPhone(userMap.get("phone").toString());
        }
        userInfoDao.updateById(userInfoDO);
    }
}
