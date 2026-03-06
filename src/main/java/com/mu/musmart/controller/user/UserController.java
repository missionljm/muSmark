package com.mu.musmart.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.dao.user.UserDao;
import com.mu.musmart.domain.dto.user.BaseUserInfoDTO;
import com.mu.musmart.domain.entity.user.UserDO;
import com.mu.musmart.domain.vo.ResVo;
import com.mu.musmart.service.user.UserService;
import com.mu.musmart.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

import static com.mu.musmart.util.IpUtil.getClientIp;

@RestController
@RequestMapping("/getUser/api")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/getUserInfo")
    public ResVo<ReqInfoContext.ReqInfo> getUserInfo(){
        ReqInfoContext.ReqInfo reqInfo = ReqInfoContext.getReqInfo();
        return ResVo.ok(reqInfo);
    }

    @GetMapping("/getUserPermissions")
    public ResVo<List<Map>> getUserPermissions(){
        return ResVo.ok(userService.getUserPermissions());
    }

    @GetMapping("/pageQueryUserList")
    public ResVo<IPage<Map>> getUserPageList(@RequestParam(required = false) Map params){
        return ResVo.ok(userService.getPageUserList(params));
    }

    /**
     * 添加用户
     * @param userMap
     * @return
     */
    @PostMapping("/addUser")
    @Transactional(rollbackFor = Exception.class)
    public ResVo<String> addUser(@RequestBody Map userMap , HttpServletRequest request){
        String clientIp = getClientIp(request);
        userMap.put("ip" , clientIp);
        return ResVo.ok(userService.addUser(userMap));
    }

    /**
     * 修改用户
     * @param userMap
     * @return
     */
    @PostMapping("/addUser")
    @Transactional(rollbackFor = Exception.class)
    public ResVo<String> modifyUser(@RequestBody Map userMap){
        userService.updateUser(userMap);
        return ResVo.ok();
    }

//    @PostMapping("/uploadAvator")
//    public ResVo<String> modifyUser(@RequestPart MultipartFile file){
//        // 文件名
//        String fileName = file.getOriginalFilename();
//
//    }
}
