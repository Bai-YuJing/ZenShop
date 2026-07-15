package cn.zenshop.server.controller.admin;

import cn.hutool.core.util.StrUtil;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.BusinessPageDto;
import cn.zenshop.model.dto.BusinessReviewDto;
import cn.zenshop.model.entity.BusinessReviewReason;
import cn.zenshop.model.entity.Category;
import cn.zenshop.model.vo.BusinessPageVo;
import cn.zenshop.model.vo.BusinessVo;
import cn.zenshop.server.service.BusinessReviewReasonService;
import cn.zenshop.server.service.BusinessService;
import cn.zenshop.server.service.CategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Struct;
import java.util.List;

@RestController("adminBusinessController")
@RequestMapping("/admin/business")
@Slf4j
public class BusinessController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private BusinessReviewReasonService businessReviewReasonService;

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
     * 分页查询
     * @param pageDto
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public Result<IPage<BusinessPageVo>> businessPage(@RequestBody BusinessPageDto pageDto){
        return Result.success(businessService.businessPage( new Page<BusinessVo>(pageDto.getPage(), pageDto.getSize()),pageDto));
    }
    /**
     * 根据id查询商家信息
     * @param id
     * @return
     */
    @GetMapping("/get")
    @Operation(summary = "根据id获取商家信息")
    public Result<BusinessVo> getById(@RequestParam String id){
        return Result.success(businessService.getBusinessById(id));
    }

    /**
     * 更新状态
     * @param status
     * @param id
     * @return
     */
    @PutMapping("/updateStatus/{id}")
    @Operation(summary = "更新状态")
    public Result<Void> updateStatus(@RequestParam Integer status, @PathVariable String id, @RequestParam(required = false) Integer reason) {
        if (reason == null) {
            businessService.updateStatus(id, status);
        } else {
            businessService.updateStatus(id, status, reason);
        }
        return Result.success();
    }

    /**
     * 获取冻结/驳回原因
     * @param type
     * @param id
     * @return
     */
    @GetMapping("/getReason/{id}")
    @Operation(summary = "获取冻结/驳回原因")
    public Result<Integer> getReason(@RequestParam Integer type,@PathVariable String id){
        return Result.success(businessReviewReasonService.getReason(type,id));
    }

    /**
     * 审核通过 修改商家信息
     * @param id
     * @return
     */
    @PutMapping("/updateBusiness")
    @Operation(summary = "审核通过")
    public Result<Void> updateBusiness(String id){
        businessService.updateBusinessInfo(id);
        return Result.success();
    }
    @GetMapping("/getUpdateInfo")
    @Operation(summary = "获取修改信息")
    public Result<BusinessVo> getUpdateInfo(String id){
        return Result.success(businessService.getUpdateInfo(id));
    }
}
