package cn.zenshop.server.controller.user;

import cn.hutool.core.bean.BeanUtil;
import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.UserInfoDto;
import cn.zenshop.model.dto.UserUpdatePwdDto;
import cn.zenshop.model.entity.User;
import cn.zenshop.model.vo.UserInfoVo;
import cn.zenshop.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user/info")
@Slf4j
@Tag(name = "用户端-用户信息相关接口")
public class UserInfoController {
    @Autowired
    private UserService userService;

    /**
     *获取当前登录用户信息
     * @return
     */
    @GetMapping("/getUser")
    @Operation(summary = "获取当前登录用户信息")
    public Result<UserInfoVo> getUser(){
        Long l = ThreadLocalContext.get();
        User user=userService.getById(l);
        return Result.success(BeanUtil.copyProperties(user,UserInfoVo.class));
    }

    /**
     * 修改用户信息
     * @param userInfoDto
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改用户信息")
    public Result<Void> updateUserInfo(@RequestBody UserInfoDto userInfoDto){
        userService.updateUserInfo(userInfoDto);
        return Result.success();
    }

    /**
     * 修改密码
     * @param userUpdatePwdDto
     * @return
     */
    @PatchMapping("/updatePassword")
    @Operation(summary = "修改密码")
    public Result<Void> updatePassword(@RequestBody UserUpdatePwdDto userUpdatePwdDto){
        userService.updatePassword(userUpdatePwdDto);
        return Result.success();
    }

    /**
     * 上传头像
     * @param file
     * @return
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传logo")
    public Result<Void> avatar(MultipartFile file){
        userService.avatar(file);
        return Result.success();
    }
}
