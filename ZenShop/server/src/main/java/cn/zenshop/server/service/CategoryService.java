package cn.zenshop.server.service;

import cn.zenshop.model.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getCategory();
}
