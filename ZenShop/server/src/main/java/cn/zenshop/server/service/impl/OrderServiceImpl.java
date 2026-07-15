package cn.zenshop.server.service.impl;

import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.model.dto.OrderDto;
import cn.zenshop.model.dto.OrderPageDto;
import cn.zenshop.model.dto.PayDto;
import cn.zenshop.model.dto.RatingDto;
import cn.zenshop.model.entity.Business;
import cn.zenshop.model.entity.BusinessRating;
import cn.zenshop.model.entity.Coupon;
import cn.zenshop.model.entity.CouponDiscount;
import cn.zenshop.model.entity.CouponFullReduction;
import cn.zenshop.model.entity.Order;
import cn.zenshop.model.entity.OrderDetail;
import cn.zenshop.model.entity.OrderCancel;
import cn.zenshop.model.entity.Product;
import cn.zenshop.model.entity.ProductImage;
import cn.zenshop.model.entity.UserCoupon;
import cn.zenshop.model.vo.BusinessOrderVo;
import cn.zenshop.model.vo.OrderCancelVo;
import cn.zenshop.model.vo.UnreviewedOrderVo;
import cn.zenshop.model.vo.OrderCreateVo;
import cn.zenshop.model.vo.OrderPreviewItemVo;
import cn.zenshop.model.vo.OrderPreviewVo;
import cn.zenshop.model.vo.OrderRatingVo;
import cn.zenshop.model.vo.OrderStatusCountVo;
import cn.zenshop.server.mapper.BusinessMapper;
import cn.zenshop.server.mapper.CouponDiscountMapper;
import cn.zenshop.server.mapper.CouponFullReductionMapper;
import cn.zenshop.server.mapper.CouponMapper;
import cn.zenshop.server.mapper.OrderCancelMapper;
import cn.zenshop.server.mapper.OrderDetailMapper;
import cn.zenshop.server.mapper.OrderMapper;
import cn.zenshop.server.mapper.ProductImageMapper;
import cn.zenshop.server.mapper.UserCouponMapper;
import cn.zenshop.server.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private cn.zenshop.server.mapper.ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponFullReductionMapper couponFullReductionMapper;

    @Autowired
    private CouponDiscountMapper couponDiscountMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private cn.zenshop.server.mapper.BusinessRatingMapper businessRatingMapper;

    @Autowired
    private OrderCancelMapper orderCancelMapper;

    /**
     * 生成订单编号：yyyyMMdd + 6位随机数
     */
    private String generateOrderNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return datePart + random;
    }

    /**
     * 从Redis获取商品库存，不存在则从数据库同步
     */
    private int getStock(String businessId, Long productId, Integer dbStock) {
        String stockKey = RedisConstant.BUSINESS_PRODUCT_STOCK_KEY + businessId + ":" + productId;
        String stockStr = stringRedisTemplate.opsForValue().get(stockKey);
        if (stockStr != null) {
            return Integer.parseInt(stockStr);
        }
        // Redis无缓存，写入数据库值
        stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(dbStock));
        return dbStock;
    }

    /**
     * 扣减Redis库存
     */
    private void deductStock(String businessId, Long productId, int quantity) {
        String stockKey = RedisConstant.BUSINESS_PRODUCT_STOCK_KEY + businessId + ":" + productId;
        Long remain = stringRedisTemplate.opsForValue().decrement(stockKey, quantity);
        if (remain != null && remain < 0) {
            // 超卖，回滚
            stringRedisTemplate.opsForValue().increment(stockKey, quantity);
            throw new BaseException(MessageConstant.PRODUCT_STOCK_NOT_ENOUGH);
        }
    }

    /**
     * 增加Redis销量
     */
    private void incrementSales(String businessId, Long productId, int quantity) {
        String salesKey = RedisConstant.BUSINESS_PRODUCT_SALES_KEY + businessId + ":" + productId;
        stringRedisTemplate.opsForValue().increment(salesKey, quantity);
    }

    @Override
    public List<OrderPreviewVo> preview(OrderDto dto) {
        List<Long> productIds = dto.getProductIds();
        List<Integer> quantities = dto.getQuantities();

        if (productIds == null || quantities == null || productIds.isEmpty() || quantities.isEmpty()) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (productIds.size() != quantities.size()) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        // 查询商品
        List<Product> products = productMapper.selectBatchIds(productIds);
        if (products.size() != productIds.size()) {
            throw new BaseException(MessageConstant.PRODUCT_NOT_FOUND);
        }

        // 批量查询商品图片（取首图）
        List<ProductImage> allImages = productImageMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductImage>()
                        .in(ProductImage::getProductId, productIds)
                        .orderByAsc(ProductImage::getId)
        );

        String businessId = null;
        List<OrderPreviewItemVo> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (int i = 0; i < productIds.size(); i++) {
            Long pid = productIds.get(i);
            Integer qty = quantities.get(i);

            Product product = products.stream()
                    .filter(p -> p.getId().equals(pid))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND));

            if (businessId == null) {
                businessId = product.getBusinessId();
            }

            // 取首图
            String image = allImages.stream()
                    .filter(img -> img.getProductId().equals(pid))
                    .findFirst()
                    .map(ProductImage::getUrl)
                    .orElse(null);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(qty));
            totalAmount = totalAmount.add(subtotal);

            // 校验库存
            int stock = getStock(businessId, pid, product.getStock());
            if (stock < qty) {
                throw new BaseException(product.getName() + " " + MessageConstant.PRODUCT_STOCK_NOT_ENOUGH);
            }

            OrderPreviewItemVo item = new OrderPreviewItemVo();
            item.setProductId(pid);
            item.setName(product.getName());
            item.setImage(image);
            item.setPrice(product.getPrice());
            item.setQuantity(qty);
            item.setStock(stock);
            item.setSubtotal(subtotal);
            items.add(item);
        }

        // 查询商家信息
        Business business = businessMapper.selectById(businessId);

        OrderPreviewVo vo = new OrderPreviewVo();
        vo.setBusinessId(businessId);
        vo.setBusinessName(business != null ? business.getName() : "未知商家");
        vo.setLogo(business != null ? business.getLogo() : null);
        vo.setItems(items);
        vo.setTotalAmount(totalAmount);

        return List.of(vo);
    }

    @Override
    @Transactional
    public OrderCreateVo createOrder(OrderDto dto) {
        // 1. 校验参数
        List<Long> productIds = dto.getProductIds();
        List<Integer> quantities = dto.getQuantities();
        Integer userAddressId = dto.getUserAddressId();
        Long couponId = dto.getCouponId();

        if (productIds == null || quantities == null || productIds.isEmpty() || quantities.isEmpty()) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (productIds.size() != quantities.size()) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (userAddressId == null) {
            throw new BaseException(MessageConstant.ADDRESS_NULL);
        }

        Long userId = ThreadLocalContext.get();
        if (userId == null) {
            throw new BaseException(MessageConstant.NOT_LOGIN);
        }

        // 2. 批量查询商品
        List<Product> products = productMapper.selectBatchIds(productIds);
        if (products.size() != productIds.size()) {
            throw new BaseException(MessageConstant.PRODUCT_NOT_FOUND);
        }

        // 3. 校验商品状态并计算总金额
        String businessId = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();

        for (int i = 0; i < productIds.size(); i++) {
            Long pid = productIds.get(i);
            Integer qty = quantities.get(i);

            Product product = products.stream()
                    .filter(p -> p.getId().equals(pid))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(MessageConstant.PRODUCT_NOT_FOUND));

            if (product.getStatus() != CommonConstant.ON_SALE) {
                throw new BaseException(product.getName() + MessageConstant.PRODUCT_NOT_ON_SALE);
            }

            if (businessId == null) {
                businessId = product.getBusinessId();
            } else if (!businessId.equals(product.getBusinessId())) {
                throw new BaseException("包含不同商家的商品，请分开下单");
            }

            int stock = getStock(businessId, pid, product.getStock());
            if (stock < qty) {
                throw new BaseException(product.getName() + " " + MessageConstant.PRODUCT_STOCK_NOT_ENOUGH);
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(qty));
            totalAmount = totalAmount.add(subtotal);

            OrderDetail detail = new OrderDetail();
            detail.setProductId(pid);
            detail.setPrice(product.getPrice());
            detail.setQuantity(qty);
            details.add(detail);
        }

        // 3.5 校验商家是否关店
        Business business = businessMapper.selectById(businessId);
        if (business == null || business.getStatus().getValue() != 2) {
            throw new BaseException("商家已关闭，无法下单");
        }

        // 4. 计算优惠券折扣
        BigDecimal actualAmount = totalAmount;
        if (couponId != null) {
            // 校验用户是否拥有该优惠券且未使用
            UserCoupon userCoupon = userCouponMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserCoupon>()
                            .eq(UserCoupon::getUserId, userId)
                            .eq(UserCoupon::getCouponId, couponId)
                            .eq(UserCoupon::getStatus, 0));
            if (userCoupon == null) {
                throw new BaseException("优惠券不存在或已使用");
            }

            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon == null || coupon.getStatus() != 1) {
                throw new BaseException("优惠券已失效");
            }

            if (coupon.getType() == 1) {
                // 满减券
                CouponFullReduction fr = couponFullReductionMapper.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CouponFullReduction>()
                                .eq(CouponFullReduction::getCouponId, couponId));
                if (fr != null && totalAmount.compareTo(fr.getMinAmount()) >= 0) {
                    actualAmount = totalAmount.subtract(fr.getDiscountAmount());
                    if (actualAmount.compareTo(BigDecimal.ZERO) < 0) {
                        actualAmount = BigDecimal.ZERO;
                    }
                }
            } else if (coupon.getType() == 2) {
                // 折扣券
                CouponDiscount disc = couponDiscountMapper.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CouponDiscount>()
                                .eq(CouponDiscount::getCouponId, couponId));
                if (disc != null) {
                    BigDecimal discount = totalAmount.multiply(
                            BigDecimal.ONE.subtract(disc.getDiscountRate()));
                    if (disc.getMaxDiscountAmount() != null
                            && discount.compareTo(disc.getMaxDiscountAmount()) > 0) {
                        discount = disc.getMaxDiscountAmount();
                    }
                    actualAmount = totalAmount.subtract(discount);
                }
            }

            // 标记优惠券为已使用
            userCoupon.setStatus(1);
            userCoupon.setOrderId(null); // 暂存，后面设置
            userCouponMapper.updateById(userCoupon);
        }

        // 5. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setBusinessId(Long.valueOf(businessId));
        order.setUserAddressId(userAddressId);
        order.setTotalAmount(totalAmount);
        order.setActualAmount(actualAmount);
        order.setStatus(0);
        orderMapper.insert(order);

        // 6. 保存订单详情
        Long orderId = order.getId();
        for (OrderDetail detail : details) {
            detail.setOrderId(orderId);
            orderDetailMapper.insert(detail);
        }

        // 7. 更新优惠券关联订单ID
        if (couponId != null) {
            userCouponMapper.update(
                    null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UserCoupon>()
                            .eq(UserCoupon::getUserId, userId)
                            .eq(UserCoupon::getCouponId, couponId)
                            .set(UserCoupon::getOrderId, orderId)
                            .set(UserCoupon::getUsedTime, LocalDateTime.now()));
        }

        // 8. 扣减Redis库存
        for (int i = 0; i < productIds.size(); i++) {
            deductStock(businessId, productIds.get(i), quantities.get(i));
        }

        // 9. 增加Redis销量
        for (int i = 0; i < productIds.size(); i++) {
            incrementSales(businessId, productIds.get(i), quantities.get(i));
        }

        // 10. 更新Redis缓存（top10和商品信息过期，下次查询自动重建）
        stringRedisTemplate.delete(RedisConstant.TOP_TEN + LocalDate.now());
        for (Long pid : productIds) {
            stringRedisTemplate.delete(RedisConstant.PRODUCT_INFO + pid);
        }

        // 11. 清除购物车中已下单的商品
        String cartKey = RedisConstant.USER_CART_KEY + userId;
        for (Long pid : productIds) {
            stringRedisTemplate.opsForHash().delete(cartKey, String.valueOf(pid));
        }

        log.info("创建订单成功: orderId={}, orderNo={}", orderId, order.getOrderNo());

        OrderCreateVo vo = new OrderCreateVo();
        vo.setOrderId(orderId);
        vo.setActualAmount(actualAmount);
        return vo;
    }

    @Override
    @Transactional
    public void pay(PayDto dto) {
        Long userId = ThreadLocalContext.get();
        if (dto.getOrderId() == null || dto.getPaymentMethod() == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (dto.getPaymentMethod() < 1 || dto.getPaymentMethod() > 2) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != 0) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }

        order.setStatus(1);
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("支付成功: orderId={}, paymentMethod={}", dto.getOrderId(), dto.getPaymentMethod());
    }

    @Override
    public IPage<BusinessOrderVo> getUserOrders(OrderPageDto pageDto) {
        Long userId = ThreadLocalContext.get();
        java.util.List<Integer> statusList = pageDto.getStatus() != null
                ? java.util.List.of(pageDto.getStatus()) : null;

        Long total = orderMapper.countUserOrders(pageDto, userId, statusList);
        long offset = (long) Math.max(0, pageDto.getPage() - 1) * pageDto.getSize();
        long limit = pageDto.getSize();
        java.util.List<Long> orderIds = orderMapper.selectUserOrderIds(pageDto, userId, statusList, offset, limit);

        java.util.List<BusinessOrderVo> records = new java.util.ArrayList<>();
        if (orderIds != null && !orderIds.isEmpty()) {
            // 1. 查询订单
            java.util.Map<Long, Order> orderMap = orderMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                            .in(Order::getId, orderIds)
                            .eq(Order::getIsDelete, 1))
                    .stream().collect(java.util.stream.Collectors.toMap(Order::getId, o -> o));

            // 2. 查询订单商品明细
            java.util.List<OrderDetail> details = orderDetailMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderDetail>()
                            .in(OrderDetail::getOrderId, orderIds));

            // 3. 商品ID去重
            java.util.List<Long> productIds = details.stream()
                    .map(OrderDetail::getProductId)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());

            // 4. 查询商品信息
            java.util.Map<Long, Product> productMap = productIds.isEmpty()
                    ? new java.util.HashMap<>()
                    : productMapper.selectBatchIds(productIds).stream()
                            .collect(java.util.stream.Collectors.toMap(Product::getId, p -> p));

            // 5. 查询商品图片（首图）
            java.util.List<ProductImage> images = productIds.isEmpty()
                    ? new java.util.ArrayList<>()
                    : productImageMapper.selectList(
                            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductImage>()
                                    .in(ProductImage::getProductId, productIds)
                                    .select(ProductImage::getProductId, ProductImage::getUrl)
                                    .orderByAsc(ProductImage::getId));
            java.util.Map<Long, String> imageMap = images.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            ProductImage::getProductId,
                            ProductImage::getUrl,
                            (a, b) -> a));

            // 6. 按订单ID分组组装VO
            java.util.Map<Long, List<OrderDetail>> detailGroup = details.stream()
                    .collect(java.util.stream.Collectors.groupingBy(OrderDetail::getOrderId));

            for (Long oid : orderIds) {
                Order order = orderMap.get(oid);
                if (order == null) continue;

                BusinessOrderVo vo = new BusinessOrderVo();
                vo.setId(order.getId());
                vo.setOrderNo(order.getOrderNo());
                vo.setTotalAmount(order.getTotalAmount());
                vo.setActualAmount(order.getActualAmount());
                vo.setStatus(order.getStatus());
                vo.setCreatedTime(order.getCreatedTime());

                java.util.List<OrderDetail> itemDetails = detailGroup.getOrDefault(oid, new java.util.ArrayList<>());
                java.util.List<cn.zenshop.model.vo.BusinessOrderItemVo> items = new java.util.ArrayList<>();
                for (OrderDetail od : itemDetails) {
                    Product p = productMap.get(od.getProductId());
                    cn.zenshop.model.vo.BusinessOrderItemVo item = new cn.zenshop.model.vo.BusinessOrderItemVo();
                    item.setProductId(od.getProductId());
                    item.setProductName(p != null ? p.getName() : null);
                    item.setProductImage(imageMap.get(od.getProductId()));
                    item.setPrice(od.getPrice());
                    item.setQuantity(od.getQuantity());
                    items.add(item);
                }
                vo.setItems(items);
                records.add(vo);
            }
        }

        Page<BusinessOrderVo> page = new Page<>(pageDto.getPage(), pageDto.getSize(), total);
        page.setRecords(records);
        return page;
    }

    @Override
    public OrderStatusCountVo getOrderStatusCounts() {
        Long userId = ThreadLocalContext.get();
        return orderMapper.selectOrderStatusCounts(userId);
    }

    @Override
    public BusinessOrderVo getOrderDetail(Long orderId) {
        Long userId = ThreadLocalContext.get();
        java.util.List<BusinessOrderVo> list = orderMapper.selectOrderDetailById(orderId, userId);
        if (list == null || list.isEmpty()) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        return list.get(0);
    }

    @Override
    @Transactional
    public void deliver(Long orderId) {
        Long businessId = ThreadLocalContext.get();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getBusinessId().equals(businessId)) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != 1) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(2);
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("商家发货: orderId={}", orderId);
    }

    @Override
    @Transactional
    public void complete(Long orderId) {
        Long userId = ThreadLocalContext.get();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != 2) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(3);
        order.setReceivedTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("用户确认收货: orderId={}", orderId);
    }

    @Override
    public IPage<UnreviewedOrderVo> getUnreviewedOrders(OrderPageDto pageDto) {
        Long userId = ThreadLocalContext.get();
        Page<UnreviewedOrderVo> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        return orderMapper.selectUnreviewedOrders(page, userId);
    }

    @Override
    @Transactional
    public void rate(RatingDto dto) {
        Long userId = ThreadLocalContext.get();
        if (dto.getOrderId() == null || dto.getProductId() == null || dto.getScore() == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (dto.getScore() < 1 || dto.getScore() > 5) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        // 1. 校验订单
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != 3) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 2. 校验商品是否属于该订单
        Long count = orderDetailMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, dto.getOrderId())
                        .eq(OrderDetail::getProductId, dto.getProductId()));
        if (count == 0) {
            throw new BaseException(MessageConstant.PRODUCT_NOT_FOUND);
        }

        // 3. 校验是否已评价
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BusinessRating> ratingQuery =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BusinessRating>()
                        .eq(BusinessRating::getUserId, String.valueOf(userId))
                        .eq(BusinessRating::getProductId, String.valueOf(dto.getProductId()));
        Long existRating = businessRatingMapper.selectCount(ratingQuery);
        if (existRating > 0) {
            throw new BaseException("该商品已评价");
        }

        // 4. 获取 businessId
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new BaseException(MessageConstant.PRODUCT_NOT_FOUND);
        }

        // 5. 保存评价
        BusinessRating rating = new BusinessRating();
        rating.setBusinessId(product.getBusinessId());
        rating.setProductId(String.valueOf(dto.getProductId()));
        rating.setUserId(String.valueOf(userId));
        rating.setOrderNo(order.getOrderNo());
        rating.setScore(dto.getScore());
        rating.setContent(dto.getContent());
        businessRatingMapper.insert(rating);

        // 6. 清除商品详情缓存（评分/评价会变）
        stringRedisTemplate.delete(RedisConstant.PRODUCT_INFO + dto.getProductId());

        log.info("用户 {} 评价商品 {}，评分: {}", userId, dto.getProductId(), dto.getScore());
    }

    @Override
    public List<OrderRatingVo> getOrderRatings(Long orderId, Long productId) {
        Long userId = ThreadLocalContext.get();

        // 1. 校验订单归属
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 2. 查询订单详情
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderDetail> detailQuery =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderId);
        if (productId != null) {
            detailQuery.eq(OrderDetail::getProductId, productId);
        }
        List<OrderDetail> details = orderDetailMapper.selectList(detailQuery);

        if (details.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> productIds = details.stream()
                .map(OrderDetail::getProductId)
                .collect(Collectors.toList());

        // 3. 查询商品信息（名称、图片）
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 4. 查询商品首图
        List<ProductImage> images = productImageMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductImage>()
                        .in(ProductImage::getProductId, productIds)
                        .select(ProductImage::getProductId, ProductImage::getUrl)
                        .orderByAsc(ProductImage::getId));
        Map<Long, String> imageMap = images.stream()
                .collect(Collectors.toMap(
                        ProductImage::getProductId,
                        ProductImage::getUrl,
                        (a, b) -> a));

        // 5. 查询该用户对这些商品的评价
        List<BusinessRating> ratings = businessRatingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BusinessRating>()
                        .eq(BusinessRating::getUserId, String.valueOf(userId))
                        .in(BusinessRating::getProductId, productIds.stream().map(String::valueOf).collect(Collectors.toList())));
        Map<String, BusinessRating> ratingMap = ratings.stream()
                .collect(Collectors.toMap(BusinessRating::getProductId, r -> r));

        // 6. 组装VO
        List<OrderRatingVo> result = new ArrayList<>();
        for (OrderDetail detail : details) {
            Product product = productMap.get(detail.getProductId());
            BusinessRating rating = ratingMap.get(String.valueOf(detail.getProductId()));

            OrderRatingVo vo = new OrderRatingVo();
            vo.setProductId(detail.getProductId());
            vo.setProductName(product != null ? product.getName() : null);
            vo.setProductImage(imageMap.get(detail.getProductId()));
            if (rating != null) {
                vo.setScore(rating.getScore());
                vo.setContent(rating.getContent());
                vo.setCreatedTime(rating.getCreatedTime());
            }
            result.add(vo);
        }

        return result;
    }

    @Override
    @Transactional
    public String cancelOrder(Long orderId, Integer reasonType) {
        Long userId = ThreadLocalContext.get();
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (order.getStatus() == 0 || order.getStatus() == 1) {
            // 待付款或待发货，直接取消
            order.setStatus(4);
            orderMapper.updateById(order);
            // 记录取消记录
            OrderCancel cancel = new OrderCancel();
            cancel.setOrderId(orderId);
            cancel.setOrderNo(order.getOrderNo());
            cancel.setReasonType(reasonType);
            cancel.setStatus(1);
            orderCancelMapper.insert(cancel);
            log.info("用户直接取消订单: orderId={}", orderId);
            return "订单已取消";
        } else if (order.getStatus() == 2) {
            // 配送中，需商家审核
            order.setStatus(5);
            orderMapper.updateById(order);
            OrderCancel cancel = new OrderCancel();
            cancel.setOrderId(orderId);
            cancel.setOrderNo(order.getOrderNo());
            cancel.setReasonType(reasonType);
            cancel.setStatus(0);
            orderCancelMapper.insert(cancel);
            log.info("用户提交取消申请，待商家审核: orderId={}", orderId);
            return "订单配送中，取消申请已提交，待商家审核";
        } else {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    @Transactional
    public void reviewCancel(Long orderId, boolean approved, String reason) {
        Long businessId = ThreadLocalContext.get();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getBusinessId().equals(businessId)) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }

        OrderCancel cancel = orderCancelMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderCancel>()
                        .eq(OrderCancel::getOrderId, orderId));
        if (cancel == null || cancel.getStatus() != 0) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        if (approved) {
            order.setStatus(4);
            orderMapper.updateById(order);
            cancel.setStatus(1);
            log.info("商家同意取消订单: orderId={}", orderId);
        } else {
            order.setStatus(6);
            orderMapper.updateById(order);
            cancel.setStatus(2);
            cancel.setReason(reason);
            log.info("商家拒绝取消订单: orderId={}, reason={}", orderId, reason);
        }
        cancel.setUpdatedTime(LocalDateTime.now());
        orderCancelMapper.updateById(cancel);
    }

    @Override
    public IPage<OrderCancelVo> getCancelOrders(OrderPageDto pageDto) {
        Long businessId = ThreadLocalContext.get();
        Page<OrderCancelVo> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        IPage<OrderCancelVo> result = orderMapper.selectCancelOrders(page, businessId, pageDto.getOrderStatus());
        for (OrderCancelVo vo : result.getRecords()) {
            // status=2(拒绝)时显示商家拒绝原因，其他显示用户申请原因
            if (vo.getStatus() != null && vo.getStatus() == 2) {
                vo.setReason(vo.getReason() != null ? vo.getReason() : "");
            } else {
                vo.setReason(getReasonText(vo.getReasonType()));
            }
        }
        return result;
    }

    private String getReasonText(Integer type) {
        if (type == null) return "";
        return switch (type) {
            case 1 -> "不想要了";
            case 2 -> "地址填错";
            case 3 -> "商品质量问题";
            case 4 -> "其他";
            default -> "";
        };
    }
}
