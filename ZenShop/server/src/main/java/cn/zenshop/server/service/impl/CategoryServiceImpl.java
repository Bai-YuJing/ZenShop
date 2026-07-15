package cn.zenshop.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.model.entity.Category;
import cn.zenshop.server.mapper.CategoryMapper;
import cn.zenshop.server.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取所有分类
     * @return
     */
    @Override
    public List<Category> getCategory() {
            String key = RedisConstant.CATEGORY_KEY;
            // 尝试从redis中取数据
            String json = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isNotBlank(json)) {
                return JSONUtil.toList(json, Category.class);
            }
            // 没有数据 查数据库
            List<Category> categoryList = query().list();
            // 存入redis（整个列表序列化为一个JSON字符串）
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(categoryList),
                    RedisConstant.CATEGORY_KEY_TTL, TimeUnit.SECONDS);
            return categoryList;
    }
}
