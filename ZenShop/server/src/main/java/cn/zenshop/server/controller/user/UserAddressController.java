package cn.zenshop.server.controller.user;

import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.UserAddressDto;
import cn.zenshop.model.vo.UserAddressListVo;
import cn.zenshop.server.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/address")
@Slf4j
@Tag(name = "用户端-地址相关接口")
public class UserAddressController {
    @Autowired
    private UserAddressService userAddressService;

    /**
     * 添加地址
     * @param userAddressDto
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "添加地址")
    public Result<Void> addAddress(@RequestBody UserAddressDto userAddressDto){
        userAddressService.addAddress(userAddressDto);
        return Result.success();
    }

    /**
     * 获取所有地址
     * @return
     */
    @GetMapping("/getAddress")
    @Operation(summary = "获取所有地址")
    public Result<UserAddressListVo> getAddress(){
        return Result.success(userAddressService.getAddressByUserId());
    }

    /**
     * 修改地址
     * @param userAddressDto
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改地址")
    public Result<Void> updateAddress(@RequestBody UserAddressDto userAddressDto){
        userAddressService.updateAddress(userAddressDto);
        return Result.success();
    }

    /**
     * 删除地址
     * @param id 地址
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除地址")
    public Result<Void> deleteAddress(@PathVariable Integer id){
        userAddressService.deleteAddress(id);
        return Result.success();
    }

    /**
     * 设为默认地址
     * @param id 地址ID
     * @return
     */
    @PutMapping("/default/{id}")
    @Operation(summary = "设为默认地址")
    public Result<Void> setDefaultAddress(@PathVariable Integer id){
        userAddressService.setDefaultAddress(id);
        return Result.success();
    }
}
