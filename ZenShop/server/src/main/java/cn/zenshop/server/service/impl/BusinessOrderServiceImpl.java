package cn.zenshop.server.service.impl;

import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.model.dto.OrderPageDto;
import cn.zenshop.model.entity.BusinessRating;
import cn.zenshop.model.entity.Order;
import cn.zenshop.model.entity.OrderDetail;
import cn.zenshop.model.entity.Product;
import cn.zenshop.model.entity.ProductImage;
import cn.zenshop.model.vo.BusinessOrderItemVo;
import cn.zenshop.model.vo.BusinessOrderVo;
import cn.zenshop.server.mapper.BusinessRatingMapper;
import cn.zenshop.server.mapper.OrderDetailMapper;
import cn.zenshop.server.mapper.OrderMapper;
import cn.zenshop.server.mapper.ProductImageMapper;
import cn.zenshop.server.mapper.ProductMapper;
import cn.zenshop.server.service.BusinessOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BusinessOrderServiceImpl implements BusinessOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private BusinessRatingMapper businessRatingMapper;

    private IPage<BusinessOrderVo> getOrdersByStatus(OrderPageDto pageDto, List<Integer> statusList) {
        Long businessId = ThreadLocalContext.get();

        // 1. 查询总数
        long total = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getBusinessId, businessId)
                .eq(Order::getIsDelete, 1)
                .in(statusList != null && !statusList.isEmpty(), Order::getStatus, statusList));

        // 2. 查询分页订单ID
        long offset = (long) Math.max(0, pageDto.getPage() - 1) * pageDto.getSize();
        long limit = pageDto.getSize();
        List<Long> orderIds = orderMapper.selectBusinessOrderIds(pageDto, businessId, statusList, offset, limit);

        // 3. Java 分组组装
        List<BusinessOrderVo> records = groupOrders(orderIds);

        Page<BusinessOrderVo> page = new Page<>(pageDto.getPage(), pageDto.getSize(), total);
        page.setRecords(records);
        return page;
    }

    private List<BusinessOrderVo> groupOrders(List<Long> orderIds) {
        List<BusinessOrderVo> records = new ArrayList<>();
        if (orderIds == null || orderIds.isEmpty()) return records;

        Map<Long, Order> orderMap = orderMapper.selectList(
                new LambdaQueryWrapper<Order>().in(Order::getId, orderIds).eq(Order::getIsDelete, 1))
                .stream().collect(Collectors.toMap(Order::getId, o -> o));

        List<OrderDetail> details = orderDetailMapper.selectList(
                new LambdaQueryWrapper<OrderDetail>().in(OrderDetail::getOrderId, orderIds));

        List<Long> productIds = details.stream().map(OrderDetail::getProductId).distinct().collect(Collectors.toList());

        Map<Long, Product> productMap = productIds.isEmpty()
                ? new HashMap<>()
                : productMapper.selectBatchIds(productIds).stream()
                        .collect(Collectors.toMap(Product::getId, p -> p));

        List<ProductImage> images = productIds.isEmpty()
                ? new ArrayList<>()
                : productImageMapper.selectList(
                        new LambdaQueryWrapper<ProductImage>()
                                .in(ProductImage::getProductId, productIds)
                                .select(ProductImage::getProductId, ProductImage::getUrl)
                                .orderByAsc(ProductImage::getId));
        Map<Long, String> imageMap = images.stream()
                .collect(Collectors.toMap(ProductImage::getProductId, ProductImage::getUrl, (a, b) -> a));

        // 6. 查询评价信息
        Map<String, BusinessRating> ratingMap = new HashMap<>();
        if (!details.isEmpty()) {
            List<String> orderNos = orderMap.values().stream().map(Order::getOrderNo).collect(Collectors.toList());
            List<BusinessRating> ratings = businessRatingMapper.selectList(
                    new LambdaQueryWrapper<BusinessRating>()
                            .in(BusinessRating::getOrderNo, orderNos));
            ratingMap = ratings.stream()
                    .collect(Collectors.toMap(r -> r.getOrderNo() + "_" + r.getProductId(), r -> r));
        }

        Map<Long, List<OrderDetail>> detailGroup = details.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

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

            List<OrderDetail> itemDetails = detailGroup.getOrDefault(oid, new ArrayList<>());
            List<BusinessOrderItemVo> items = new ArrayList<>();
            for (OrderDetail od : itemDetails) {
                Product p = productMap.get(od.getProductId());
                BusinessOrderItemVo item = new BusinessOrderItemVo();
                item.setProductId(od.getProductId());
                item.setProductName(p != null ? p.getName() : null);
                item.setProductImage(imageMap.get(od.getProductId()));
                item.setPrice(od.getPrice());
                item.setQuantity(od.getQuantity());
                // 评价信息
                BusinessRating rating = ratingMap.get(order.getOrderNo() + "_" + od.getProductId());
                if (rating != null) {
                    item.setScore(rating.getScore());
                    item.setRatingContent(rating.getContent());
                    item.setRated(true);
                } else {
                    item.setRated(false);
                }
                items.add(item);
            }
            vo.setItems(items);
            records.add(vo);
        }
        return records;
    }

    @Override
    public IPage<BusinessOrderVo> getCompletedOrders(OrderPageDto pageDto) {
        return getOrdersByStatus(pageDto, List.of(3));
    }

    @Override
    public IPage<BusinessOrderVo> getUnfinishedOrders(OrderPageDto pageDto) {
        return getOrdersByStatus(pageDto, List.of(0, 1, 2));
    }
}
