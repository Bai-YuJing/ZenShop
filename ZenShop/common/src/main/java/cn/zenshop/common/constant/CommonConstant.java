package cn.zenshop.common.constant;

import java.time.LocalDateTime;

public class CommonConstant {
    public static final String DEFAULT_PASSWORD="123456";

    /**
     * 手机号正则：1开头的11位数字
     */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 管理员头像
     */
    public static final String ADMIN_AVATAR_PATH="admin/avatar/";
    /**
     * 管理员头像
     */
    public static final String BUSINESS_LOGO="business/logo/";
    /**
     * 管理员头像
     */
    public static final String USER_AVATAR="user/avatar/";

    /**
     * 管理员头像
     */
    public static final String PRODUCT_IMG="business/product/";

    /**
     * 未删除
     */
    public static final Integer IS_NOT_DELETE=1;

    /**
     * 删除
     */
    public static final Integer FULL_SHOP_CATEGORY=20;

    /**
     * 销量
     */
    public static final Integer SALES=0;
    /**
     * 库存
     */
    public static final Integer STOCK=0;

    /**
     * 上架
     */
    public static final Integer ON_SALE=1;

    /**
     * 下架
     */
    public static final Integer NOT_ON_SALE=0;
    /**
     * 下架
     */
    public static final Integer IS_STOP=0;
    /**
     * 启用
     */
    public static final Integer IS_NOT_STOP=1;

    /**
     * 默认地址
     */
    public static final Integer IS_DEFAULT = 1;


    /**
     * 默认地址
     */
    public static final Integer IS_NOT_DEFAULT = 0;
}
