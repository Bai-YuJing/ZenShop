package cn.zenshop.server.controller.Business;

import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.ProductCategoryDto;
import cn.zenshop.model.vo.ProductCategoryVo;
import cn.zenshop.server.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business/shop/category")
@Slf4j
@Tag(name = "店铺分类相关接口")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 添加商品分类
     * @param dto
     * @return
     */
    @PostMapping("/addCategory")
    @Operation(summary = "添加商品分类")
    public Result<Void> addCategory(@RequestBody ProductCategoryDto dto){
        Long l = ThreadLocalContext.get();
        productCategoryService.addCategory(String.valueOf(l),dto);
        return Result.success();
    }

    @GetMapping("/getAll")
    @Operation(summary = "获取分类")
    public Result<List<ProductCategoryVo>> getAll(@RequestParam(required = false) String name){
        Long l = ThreadLocalContext.get();
        return Result.success(productCategoryService.getAll(String.valueOf(l),name));
    }
    /**
     * 添加商品分类
     * @param dto
     * @return
     */
    @PutMapping("/updateCategory")
    @Operation(summary = "更新分类")
    public Result<Void> updateCategory(@RequestBody ProductCategoryDto dto){
        productCategoryService.updateCategory(dto);
        return Result.success();
    }

    /**
     * 修改状态
     * @param id
     * @return
     */
    @PutMapping("/updateStatus/{id}")
    @Operation(summary = "修改状态")
    public Result<Void> deleteById(@PathVariable Long id,@RequestParam Integer status){
        productCategoryService.updateStatus(id,status);
        return Result.success();
    }

}
