package cn.zenshop.server.controller.user;

import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.ProductPageDto;
import cn.zenshop.model.dto.ProductRatingDto;
import cn.zenshop.model.vo.BusinessRatingVo;
import cn.zenshop.model.vo.ProductPageVo;
import cn.zenshop.model.vo.ProductResVo;
import cn.zenshop.server.service.BusinessRatingService;
import cn.zenshop.server.service.ProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/product")
@Slf4j
@Tag(name = "用户端-商品相关接口")
public class UserProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private BusinessRatingService businessRatingService;

    @GetMapping("/unrated")
    @Operation(summary = "获取用户未评价商品列表")
    public Result<List<ProductPageVo>> getUnratedProducts(@RequestParam String businessId){
        Long userId = ThreadLocalContext.get();
        return Result.success(productService.getUnratedProducts(businessId, userId));
    }

    @GetMapping("/listByBusiness")
    @Operation(summary = "根据商家ID和分类查询商品")
    public Result<IPage<ProductPageVo>> getProductListByBusiness(
            @RequestParam String businessId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "15") Integer size) {
        ProductPageDto dto = new ProductPageDto();
        dto.setBusinessId(businessId);
        dto.setCategoryId(categoryId);
        return Result.success(productService.getProductList(new Page<ProductPageVo>(page, size), dto));
    }

    @PostMapping("/page")
    @Operation(summary = "商品分页查询")
    public Result<IPage<ProductPageVo>> getProductList(@RequestBody ProductPageDto pageDto){
        pageDto.setBusinessId(String.valueOf(ThreadLocalContext.get()));
        return Result.success(productService.userGetProductList(new Page<ProductPageVo>(pageDto.getPage(),pageDto.getSize()),pageDto));
    }

    @GetMapping("/product")
    @Operation(summary = "根据id查询商品")
    public Result<ProductResVo> getProductById(@RequestParam Integer id){

        return Result.success(productService.getProductById(id));
    }

    @GetMapping("/ratings")
    @Operation(summary = "获取商品评价（按时间倒序）")
    public Result<IPage<BusinessRatingVo>> getProductRatings(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        ProductRatingDto dto = new ProductRatingDto();
        dto.setProductId(productId);
        dto.setPage(page);
        dto.setSize(size);
        dto.setSortBy("time");
        dto.setSortOrder("DESC");
        return Result.success(businessRatingService.getProductRatings(dto));
    }
}
