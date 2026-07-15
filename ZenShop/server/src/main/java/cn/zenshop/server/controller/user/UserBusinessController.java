package cn.zenshop.server.controller.user;

import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.BusinessPageDto;
import cn.zenshop.model.entity.Category;
import cn.zenshop.model.vo.BusinessDetailVo;
import cn.zenshop.model.vo.BusinessPageVo;
import cn.zenshop.model.vo.BusinessVo;
import cn.zenshop.server.service.BusinessService;
import cn.zenshop.server.service.CategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/business")
@Slf4j
@Tag(name = "用户端-查询商家相关接口")
public class UserBusinessController {
    @Autowired
    private BusinessService businessService;

    /**
     * 根据id获取商家详情（含商品分类）
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "根据id获取商家详情")
    public Result<BusinessDetailVo> getBusinessDetail(@PathVariable String id){
        return Result.success(businessService.getBusinessDetail(id));
    }

    /**
     * 分页查询
     * @param pageDto
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public Result<IPage<BusinessPageVo>> businessPage(@RequestBody BusinessPageDto pageDto){
        return Result.success(businessService.userGetBusinessPage( new Page<BusinessVo>(pageDto.getPage(), pageDto.getSize()),pageDto));
    }
}
