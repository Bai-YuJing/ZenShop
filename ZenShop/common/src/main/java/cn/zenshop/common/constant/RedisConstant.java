package cn.zenshop.common.constant;

public class RedisConstant {
    public static final String ADMIN_CAPTCHA_KEY="ZenShop:login:admin:captcha:";
    public static final Integer ADMIN_CAPTCHA_KEY_TTL=60;
    public static final String BUSINESS_CAPTCHA_KEY="ZenShop:login:business:captcha:";
    public static final Integer BUSINESS_CAPTCHA_KEY_TTL=60;

    public static final String ADMIN_TOKEN="ZenShop:login:admin:token:";
    public static final String BUSINESS_TOKEN="ZenShop:login:business:token:";
    public static final Integer ADMIN_TOKEN_TTL=60*60;
    public static final Integer BUSINESS_TOKEN_TTL=60*60*2;

    public static final String CATEGORY_KEY="ZenShop:CATEGORY";
    public static final Integer CATEGORY_KEY_TTL=60*60*12;

    public static final String BUSINESS_MODIFY_REVIEW_KEY="ZenShop:business:review:modify:";
    public static final Integer BUSINESS_MODIFY_REVIEW_KEY_TTL=60*30;

    public static final String BUSINESS_PRODUCT_SALES_KEY="ZenShop:business:product:sales:";

    public static final String BUSINESS_PRODUCT_STOCK_KEY="ZenShop:business:product:stock:";

    public static final String USER_CAPTCHA_KEY="ZenShop:login:user:captcha:";
    public static final Integer USER_CAPTCHA_KEY_TTL=60;

    public static final String USER_TOKEN="ZenShop:login:user:token:";
    public static final Integer USER_TOKEN_TTL=60*60*24;

    public static final String USER_AVATAR="ZenShop:login:user:avatar:";

    public static final String TOP_TEN="ZenShop:user:product:top10:";
    public static final Integer TOP_TEN_TTL=60*30;

    public static final String PRODUCT_INFO="ZenShop:user:product:";
    public static final Integer PRODUCT_INFO_TTL=60*30;

    public static final String USER_CART_KEY="ZenShop:user:cart:";
    public static final Integer USER_CART_TTL=60*60*24*7;
}
