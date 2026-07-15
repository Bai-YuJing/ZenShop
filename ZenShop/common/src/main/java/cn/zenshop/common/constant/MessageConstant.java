package cn.zenshop.common.constant;

public class MessageConstant {

    // ==================== 登录相关 ====================
    public static final String NOT_LOGIN="未登录";
    public static final String NAME_OR_PWD_NULL="用户名或密码为空";
    public static final String NAME_NULL="用户名不能为空";
    public static final String USER_NAME_NOT_FOUND="用户名不存在";
    public static final String USER_NOT_FOUND="用户不存在";
    public static final String PASSWORD_ERROR="密码错误";
    public static final String NEW_PASSWORD_IS_NULL="新密码不能为空";
    public static final String OLD_PASSWORD_IS_NULL="旧密码不能为空";
    public static final String CAPTCHA_NULL="验证码为空";
    public static final String CAPTCHA_ERROR="验证码错误";
    public static final String CAPTCHA_NOT_FOUND="验证码过期";
    public static final String USER_NAME_SAME="用户名已存在";
    public static final String ACCOUNT_IS_STOP="账号被停用";

    // ==================== 商家注册 ====================
    public static final String PHONE_NUMBER_ERROR = "手机号格式错误";
    public static final String PHONE_NOT_REGISTER = "手机号未注册";
    public static final String PHONE_SAME="手机号已注册";
    public static final String PHONE_NULL = "手机号不能为空";
    public static final String EMAIL_ERROR = "邮箱格式错误";
    public static final String STATUS_IS_STOP="账号被禁用";

    // ==================== 店铺相关 ====================
    public static final String SHOP_NAME_NULL = "店铺名称不能为空";
    public static final String SHOP_NAME_SAME="店铺名称已存在";
    public static final String SHOP_NOT_FOUND="店铺不存在";
    public static final String SHOP_CATEGORY_FULL="分类输入已达上限,请删除一些后尝试";

    // ==================== 商品相关 ====================
    public static final String PRODUCT_NAME_NULL = "商品名称不能为空";
    public static final String PRODUCT_PRICE_NULL = "商品价格不能为空";
    public static final String CATEGORY_NULL = "分类不能为空";
    public static final String CATEGORY_NOT_ON_SALE = "该商品分类已下架，无法上架";

    // ==================== 地址相关 ====================
    public static final String PROVINCE_NULL = "省份不能为空";
    public static final String CITY_NULL = "城市不能为空";
    public static final String DISTRICT_NULL = "区县不能为空";
    public static final String DETAILED_ADDRESS_NULL = "详细地址不能为空";
    public static final String ADDRESS_LIMIT = "地址数量已达上限，请删除一些后尝试";


    // ==================== 通用 ====================
    public static final String UN_KNOW_ERROR="参数异常";
    public static final String UN_KNOW_STATUS="未知状态";

    // ==================== 购物车相关 ====================
    public static final String PRODUCT_NOT_FOUND = "商品不存在";
    public static final String PRODUCT_NOT_ON_SALE = "商品已下架";
    public static final String CART_ITEM_NOT_FOUND = "购物车中未找到该商品";

    // ==================== 其他 ====================
    public static final String ADMIN_AVATAR="";

    // ==================== 订单相关 ====================
    public static final String ORDER_NOT_FOUND = "订单不存在";
    public static final String ORDER_STATUS_ERROR = "订单状态异常";
    public static final String ORDER_EMPTY_CART = "购物车为空，无法下单";
    public static final String PRODUCT_STOCK_NOT_ENOUGH = "库存不足";
    public static final String ADDRESS_NULL = "请先添加收货地址";

    // ==================== 优惠券相关 ====================
    public static final String COUPON_NOT_FOUND = "优惠券不存在";
    public static final String COUPON_OFFLINE = "优惠券已下架";
    public static final String COUPON_SOLD_OUT = "优惠券已抢完";
    public static final String COUPON_ALREADY_CLAIMED = "您已领取过该优惠券";
}
