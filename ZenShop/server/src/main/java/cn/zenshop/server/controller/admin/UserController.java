package cn.zenshop.server.controller.admin;

import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.UserPageDto;
import cn.zenshop.model.vo.UserPageVo;
import cn.zenshop.model.vo.UserInfoVo;
import cn.zenshop.model.vo.UserRatingVo;
import cn.zenshop.model.vo.userAvatarVo;
import cn.zenshop.server.service.AdminService;
import cn.zenshop.server.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "用户相关接口")
@RestController("adminUserController")
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    // ===== 头像审核 =====

    @GetMapping("/pendingAvatar")
    @Operation(summary = "获取待审核头像")
    public Result<List<userAvatarVo>> getPendingAvatar() {
        return Result.success(adminService.getPendingAvatarList());
    }

    @PutMapping("/avatar/{id}")
    @Operation(summary = "审核头像")
    public Result<Void> avatar(@PathVariable Long id, @RequestParam Integer status) {
        adminService.avatar(id, status);
        return Result.success();
    }

    // ===== 用户管理 =====

    @GetMapping("/page")
    @Operation(summary = "分页查询用户列表")
    public Result<IPage<UserPageVo>> getUserPage(UserPageDto dto) {
        return Result.success(userService.adminGetUserPage(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户详情")
    public Result<UserInfoVo> getUserDetail(@PathVariable Long id) {
        return Result.success(userService.adminGetUserDetail(id));
    }

    @PatchMapping("/status/{id}")
    @Operation(summary = "启用/禁用用户")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.adminUpdateUserStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.adminDeleteUser(id);
        return Result.success();
    }

    @GetMapping("/ratings/{userId}")
    @Operation(summary = "查看用户评价列表")
    public Result<List<UserRatingVo>> getUserRatings(@PathVariable Long userId) {
        return Result.success(userService.adminGetUserRatings(userId));
    }
}
