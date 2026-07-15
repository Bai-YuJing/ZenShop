package cn.zenshop.server.controller.Business;

import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.ProductCategoryDto;
import cn.zenshop.model.dto.ProductDto;
import cn.zenshop.model.dto.ProductPageDto;
import cn.zenshop.model.dto.ProductRatingDto;
import cn.zenshop.model.vo.BusinessRatingVo;
import cn.zenshop.model.vo.ProductCategoryVo;
import cn.zenshop.model.vo.ProductPageVo;
import cn.zenshop.server.service.BusinessRatingService;
import cn.zenshop.server.service.CommonService;
import cn.zenshop.server.service.ProductCategoryService;
import cn.zenshop.server.service.ProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/business/shop/product")
@Slf4j
@Tag(name = "店铺商品相关接口")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private BusinessRatingService businessRatingService;

    @PostMapping("/addProduct")
    @Operation(summary = "添加商品")
    public Result<Void> addProduct(@Valid @RequestBody ProductDto productDto){
        productService.addProduct(productDto);
        return Result.success();
    }
    /**
     * 上传商品图片
     * @param file
     * @return
     */
    @PostMapping("/img")
    @Operation(summary = "上传商品图片")
    public Result<String> logo(MultipartFile file){
        String filePath= CommonConstant.PRODUCT_IMG+ThreadLocalContext.get()+"/"+LocalDate.now()+"/";
        return commonService.uploadAvatar(file,filePath);
    }

    /**
     * 商品分页查询
     * @param pageDto
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "商品分页查询")
    public Result<IPage<ProductPageVo>> getProductList(@RequestBody ProductPageDto pageDto){
        pageDto.setBusinessId(String.valueOf(ThreadLocalContext.get()));
        return Result.success(productService.getProductList(new Page<ProductPageVo>(pageDto.getPage(),pageDto.getSize()),pageDto));
    }

    /**
     * 更新商品信息
     * @param productDto
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "更新商品信息")
    public Result<Void> updateProduct(@RequestBody ProductDto productDto){
        productService.updateProduct(productDto);
        return Result.success();
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/updateStatus/{id}")
    public Result<Void> deleteById(@PathVariable Long id,@RequestParam Integer status){
        productService.updateStatus(id,status);
        return Result.success();
    }

    @PostMapping("/ratings")
    @Operation(summary = "获取商品评价（支持按评分/时间排序）")
    public Result<IPage<BusinessRatingVo>> getProductRatings(@RequestBody ProductRatingDto dto) {
        return Result.success(businessRatingService.getProductRatings(dto));
    }


}
