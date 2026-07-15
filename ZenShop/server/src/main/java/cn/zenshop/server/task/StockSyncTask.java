package cn.zenshop.server.task;

import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.model.entity.Product;
import cn.zenshop.server.mapper.ProductMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Component
public class StockSyncTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 每5分钟同步一次Redis库存到数据库
     */
    @Scheduled(fixedRate = 300_000)
    public void syncStock() {
        log.info("开始同步库存...");
        Set<String> stockKeys = stringRedisTemplate.keys(RedisConstant.BUSINESS_PRODUCT_STOCK_KEY + "*");
        if (stockKeys == null || stockKeys.isEmpty()) {
            log.info("无库存数据需要同步");
            return;
        }

        int count = 0;
        for (String key : stockKeys) {
            try {
                // key格式: ZenShop:business:product:stock:{businessId}:{productId}
                String[] parts = key.split(":");
                Long productId = Long.valueOf(parts[parts.length - 1]);
                Integer stock = Integer.valueOf(stringRedisTemplate.opsForValue().get(key));

                productMapper.update(null, new LambdaUpdateWrapper<Product>()
                        .eq(Product::getId, productId)
                        .set(Product::getStock, stock)
                        .set(Product::getUpdatedTime, LocalDateTime.now()));
                count++;
            } catch (Exception e) {
                log.warn("同步库存失败, key={}", key, e);
            }
        }
        log.info("库存同步完成, 共更新{}条", count);
    }

    /**
     * 每5分钟同步一次Redis销量到数据库
     */
    @Scheduled(fixedRate = 300_000)
    public void syncSales() {
        log.info("开始同步销量...");
        Set<String> salesKeys = stringRedisTemplate.keys(RedisConstant.BUSINESS_PRODUCT_SALES_KEY + "*");
        if (salesKeys == null || salesKeys.isEmpty()) {
            log.info("无销量数据需要同步");
            return;
        }

        int count = 0;
        for (String key : salesKeys) {
            try {
                // key格式: ZenShop:business:product:sales:{businessId}:{productId}
                String[] parts = key.split(":");
                Long productId = Long.valueOf(parts[parts.length - 1]);
                Integer sales = Integer.valueOf(stringRedisTemplate.opsForValue().get(key));

                productMapper.update(null, new LambdaUpdateWrapper<Product>()
                        .eq(Product::getId, productId)
                        .set(Product::getSales, sales)
                        .set(Product::getUpdatedTime, LocalDateTime.now()));
                count++;
            } catch (Exception e) {
                log.warn("同步销量失败, key={}", key, e);
            }
        }
        log.info("销量同步完成, 共更新{}条", count);
    }
}
