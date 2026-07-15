package cn.zenshop.server.controller.Business;

import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.BusinessLoginDto;
import cn.zenshop.model.dto.BusinessRegisterDto;
import cn.zenshop.model.entity.Category;
import cn.zenshop.server.service.BusinessService;
import cn.zenshop.server.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business/business")
@Slf4j
@Tag(name = "商家端-商家相关接口")
public class BusinessController {
    @Autowired
    private BusinessService businessService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @GetMapping("/getCaptcha")
    @Operation(summary = "获取验证码")
    public Result<Void> getCaptcha(@RequestParam String phone){
        if (phone == null || !phone.matches(CommonConstant.PHONE_REGEX)) {
            throw new BaseException(MessageConstant.PHONE_NUMBER_ERROR);
        }
        businessService.getCaptcha(phone);
        return  Result.success();
    }

    /**
     * 获取分类
     * @return
     */
    @GetMapping("/getCategory")
    @Operation(summary = "获取分类")
    public Result<List<Category>> getCategory(){
        return Result.success(categoryService.getCategory());
    }

    /**
     * 注册
     * @param businessRegisterDto
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result<String> register(@Valid @RequestBody BusinessRegisterDto businessRegisterDto){
        return Result.success(businessService.register(businessRegisterDto));
    }

    /**
     * 登录
     * @param businessLoginDto
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<String> businessLogin(@RequestBody BusinessLoginDto businessLoginDto){
        return Result.success(businessService.businessLogin(businessLoginDto));
    }


}
