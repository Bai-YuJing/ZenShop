package cn.zenshop.server.controller.user;

import cn.hutool.core.bean.BeanUtil;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.ProductPageDto;
import cn.zenshop.model.dto.UserInfoDto;
import cn.zenshop.model.dto.UserUpdatePwdDto;
import cn.zenshop.model.entity.Category;
import cn.zenshop.model.entity.User;
import cn.zenshop.model.vo.ProductPageVo;
import cn.zenshop.model.vo.UserInfoVo;
import cn.zenshop.server.service.CategoryService;
import cn.zenshop.server.service.ProductService;
import cn.zenshop.server.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user/index")
@Slf4j
@Tag(name = "用户端-用户首页相关接口")
public class UserIndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("/category")
    @Operation(summary = "获取分类")
    public Result<List<Category>> category(){
        return Result.success(categoryService.getCategory());
    }
    @GetMapping("/top10")
    @Operation(summary = "获取销量TOP10商品")
    public Result<IPage<ProductPageVo>> top10(){
        ProductPageDto pageDto = new ProductPageDto();
        pageDto.setSize(10);
        pageDto.setPage(0);
        pageDto.setSalesSort("desc");
        IPage<ProductPageVo> result = productService.userGetProductList(new Page<ProductPageVo>(pageDto.getPage(),pageDto.getSize()),pageDto);
        return Result.success(result);
    }

}
