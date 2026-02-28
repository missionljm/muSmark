package com.mu.musmart.service.user.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.dao.user.UserDao;
import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.mapper.CommonMapper;
import com.mu.musmart.service.help.UserSessionHelper;
import com.mu.musmart.service.user.UserService;
import com.mu.musmart.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private UserSessionHelper userSessionHelper;

    @Autowired
    private CommonMapper commonMapper;



    @Override
    public BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String sessionId , String clientIp) {
        Long userId = userSessionHelper.getUserIdBySession(sessionId);
        UserDO userDo = userDao.getUserById(userId);
        BaseUserInfoDTO baseUserInfoDTO = new BaseUserInfoDTO();
        baseUserInfoDTO.setId(userDo.getId());
        baseUserInfoDTO.setUserName(userDo.getUserName());
        baseUserInfoDTO.setRegion(clientIp);
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
                this.queryUserPermissionTree(map , menuId);
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
    public void queryUserPermissionTree( Map paramsMap , Long parentId){
        List<Map<String, Object>> userPermission = commonMapper.getUserPermissionByParentsId(parentId);
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
            this.queryUserPermissionTree(map  , menuId);
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
    public IPage<UserDO> getPageUserList(Map params) {
        //使用mybatis的分页方法
        Page<UserDO> pageUser = new Page<>(Long.valueOf(params.get("page").toString()), Long.valueOf(params.get("limit").toString()));
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted" , 0);
        //查询所有用户信息进行返回
        IPage<UserDO> page = userDao.page(pageUser, queryWrapper);
        return page;
    }
}
