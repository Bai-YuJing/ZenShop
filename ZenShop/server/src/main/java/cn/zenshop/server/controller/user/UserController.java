package cn.zenshop.server.controller.user;

import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.UserLoginDto;
import cn.zenshop.model.dto.UserRegisterDto;
import cn.zenshop.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Tag(name = "用户端-用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getCaptcha")
    @Operation(summary = "获取验证码")
    public Result<Void> getCaptcha(@RequestParam String phone) {
        if (phone == null || !phone.matches(CommonConstant.PHONE_REGEX)) {
            throw new BaseException(MessageConstant.PHONE_NUMBER_ERROR);
        }
        userService.getCaptcha(phone);
        return Result.success();
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result<String> register(@Valid @RequestBody UserRegisterDto dto) {
        return Result.success(userService.register(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<String> login(@RequestBody UserLoginDto dto) {
        return Result.success(userService.login(dto));
    }
}
