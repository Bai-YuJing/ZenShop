package cn.zenshop.server.controller.admin;

import cn.hutool.core.util.StrUtil;
import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.AdminDto;
import cn.zenshop.model.dto.AdminLoginDto;
import cn.zenshop.model.dto.AdminPageDto;
import cn.zenshop.model.vo.AdminPageVo;
import cn.zenshop.model.vo.AdminVo;
import cn.zenshop.model.vo.DailyUserStatsVo;
import cn.zenshop.model.vo.BusinessStatsVo;
import cn.zenshop.server.interceptor.SuperAdmin;
import cn.zenshop.server.service.AdminService;
import cn.zenshop.server.service.CommonService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.DeleteExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 *管理员相关接口
 */
@Slf4j
@Tag(name = "管理员相关接口")
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private CommonService commonService;
    /**
     * 管理员登录
     * @param adminLoginDto
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<String> adminLogin(@RequestBody AdminLoginDto adminLoginDto){
        if (StrUtil.hasBlank(adminLoginDto.getUsername(), adminLoginDto.getPassword())){
            throw new BaseException(MessageConstant.NAME_OR_PWD_NULL);
        }
        if (StrUtil.hasBlank(adminLoginDto.getCaptcha())){
            throw new BaseException(MessageConstant.CAPTCHA_NULL);
        }
        
        return Result.success(adminService.adminLogin(adminLoginDto));
    }

    /**
     * 获取验证码
     * @return
     */
    @GetMapping("/login/getCaptcha")
    @Operation(summary = "获取验证码")
    public Result<Map<String, String>> getCaptcha(){
        return Result.success(adminService.getCaptcha());
    }
    /**
     * 添加管理员
     */
    @SuperAdmin
    @PostMapping("/addAdminUser")
    @Operation(summary = "添加管理员")
    public Result<Void> addAdminUser(@Valid @RequestBody AdminDto adminDto){
        adminService.addAdminUser(adminDto);
        return Result.success();
    }

    /**
     * 更新管理员信息
     * @param adminDto
     * @return
     */

    @PutMapping("/update")
    @Operation(summary = "更新管理员信息")
    public Result<Void> updateAdmin(@RequestBody AdminDto adminDto){
        adminService.adminUpdate(adminDto);
        return Result.success();
    }

    /**
     * 根据id获取管理员信息
     * @param id
     * @return
     */
    @SuperAdmin
    @GetMapping("/getone")
    @Operation(summary = "根据id获取管理员信息")
    public Result<AdminVo> getAdminById(@RequestParam Integer id){
        if (!(id!=null && id>0)){
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        return Result.success(adminService.getAdminById(id));
    }

    /**
     * 更新管理员状态
     * @param status
     * @param id
     * @return
     */
    @SuperAdmin
    @PatchMapping("/update/{status}")
    @Operation(summary = "更新管理员状态")
    private Result<Void> updateStatus(@PathVariable Integer status,@RequestParam Integer id){
       if (status==null || id==null){
           throw new BaseException(MessageConstant.UN_KNOW_ERROR);
       }
        if (!(status == 0 || status == 1)) {
            throw new BaseException(MessageConstant.UN_KNOW_STATUS);
        }
        adminService.adminUpdate(status,id);
        return Result.success();
    }

    /**
     * 删除管理员
     * @param id
     * @return
     */
    @SuperAdmin
    @DeleteMapping("/delete")
    @Operation(summary = "删除管理员")
    public Result<Void> deleteAdminById(@RequestParam Integer id){
        if (id == null){
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        adminService.deleteAdminById(id);
        return Result.success();
    }

    /**
     * 分页查询
     * @param adminPageDto
     * @return
     */
    @SuperAdmin
    @GetMapping("/list")
    @Operation(summary = "分页查询")
    public Result<IPage<AdminPageVo>> adminList(AdminPageDto adminPageDto){
        return Result.success(adminService.adminList(adminPageDto));
    }
    /**
     * 获取当前登录用户
     */
    @GetMapping("/getNowUser")
    @Operation(summary = "获取当前登录用户")
    public Result<AdminVo> getNowUser(){
        Long id = ThreadLocalContext.get();
        log.info("当前登陆的用户{}",id);
        if (id==null){
            throw new BaseException(MessageConstant.NOT_LOGIN);
        }
        AdminVo vo = adminService.getAdminById(Math.toIntExact(id));
        return Result.success(vo);
    }

    /**
     * 下线
     * @param token
     * @return
     */
    @GetMapping("/logout")
    @Operation(summary = "下线")
    public Result<Void> logout(@RequestHeader("token") String token){
        adminService.logout(token);
        return Result.success();
    }
    @PostMapping("/uploadAvatar")
    @Operation(summary = "上传头像")
    public Result<String> uploadAvatar(MultipartFile file){
        String filePath= CommonConstant.ADMIN_AVATAR_PATH;
      return commonService.uploadAvatar(file,filePath);
    }

    @GetMapping("/dashboard/user")
    @Operation(summary = "获取每日用户统计")
    public Result<List<DailyUserStatsVo>> getDailyUserStats(@RequestParam(defaultValue = "7d") String range) {
        return Result.success(adminService.getDailyUserStats(range));
    }

    @GetMapping("/dashboard/business")
    @Operation(summary = "获取每日商家统计")
    public Result<BusinessStatsVo> getDailyBusinessStats(@RequestParam(defaultValue = "7d") String range) {
        return Result.success(adminService.getDailyBusinessStats(range));
    }
}
