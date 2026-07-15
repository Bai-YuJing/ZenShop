import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/user/login',
    },
    // ===== 用户端 =====
    {
      path: '/user/index',
      component: () => import('../views/User/index.vue'),
      redirect: '/user/index/home',
      children: [
        {
          path: 'home',
          name: 'user-home',
          meta: { title: '首页' },
          component: () => import('../views/User/views/Home.vue'),
        },
        {
          path: 'cart',
          name: 'user-cart',
          meta: { title: '购物车' },
          component: () => import('../views/User/views/Cart.vue'),
        },
        {
          path: 'profile',
          name: 'user-profile',
          meta: { title: '个人中心' },
          component: () => import('../views/User/views/Profile.vue'),
        },
      ],
    },
    {
      path: '/user/login',
      name: 'user-login',
      meta: { title: '登录' },
      component: () => import('../views/User/UserLogin.vue'),
    },
    {
      path: '/user/info',
      name: 'user-info',
      meta: { title: '个人信息' },
      component: () => import('../views/User/views/UserInfo.vue'),
    },
    {
      path: '/user/address',
      name: 'user-address',
      meta: { title: '地址管理' },
      component: () => import('../views/User/views/AddressManage.vue'),
    },
    {
      path: '/user/orders',
      name: 'user-orders',
      meta: { title: '历史订单' },
      component: () => import('../views/User/views/OrderList.vue'),
    },
    {
      path: '/user/order/detail',
      name: 'user-order-detail',
      meta: { title: '订单详情' },
      component: () => import('../views/User/views/OrderDetail.vue'),
    },
    {
      path: '/user/payment',
      name: 'user-payment',
      meta: { title: '支付' },
      component: () => import('../views/User/views/Payment.vue'),
    },
    {
      path: '/user/review',
      name: 'user-review',
      meta: { title: '评价商品' },
      component: () => import('../views/User/views/SubmitReview.vue'),
    },
    {
      path: '/user/business',
      name: 'user-business-detail',
      meta: { title: '商家详情' },
      component: () => import('../views/User/views/BusinessDetail.vue'),
    },
    {
      path: '/user/coupon',
      name: 'user-coupon',
      meta: { title: '领券中心' },
      component: () => import('../views/User/views/CouponGrab.vue'),
    },
    {
      path: '/user/order/confirm',
      name: 'user-order-confirm',
      meta: { title: '确认订单' },
      component: () => import('../views/User/views/OrderConfirm.vue'),
    },
    {
      path: '/user/product',
      name: 'user-product',
      meta: { title: '商品详情' },
      component: () => import('../views/User/views/ProductDetail.vue'),
    },
    {
      path: '/user/search',
      name: 'user-search',
      meta: { title: '搜索' },
      component: () => import('../views/User/views/SearchResult.vue'),
    },
    {
      path: '/user/shop-list',
      name: 'user-shop-list',
      meta: { title: '商家列表' },
      component: () => import('../views/User/views/ShopList.vue'),
    },
    {
      path: '/user/password',
      name: 'user-password',
      meta: { title: '修改密码' },
      component: () => import('../views/User/views/ChangePassword.vue'),
    },
    // ===== 商家端 =====
    {
      path: '/business/login',
      name: 'business-login',
      meta: { title: '商家登录' },
      component: () => import('../views/Business/BusinessLogin.vue'),
    },
    {
      path: '/business/center',
      component: () => import('../views/Business/BusinessCenter.vue'),
      redirect: '/business/center/profile',
      children: [
        {
          path: 'profile',
          name: 'business-profile',
          meta: { title: '商家管理平台' },
          component: () => import('../views/Business/views/Profile.vue'),
        },
        {
          path: 'profile/edit',
          name: 'business-profile-edit',
          component: () => import('../views/Business/views/EditProfile.vue'),
        },
        {
          path: 'employee',
          name: 'business-employee',
          component: () => import('../views/Business/views/EmployeeManage.vue'),
        },
        {
          path: 'category',
          name: 'business-category',
          component: () => import('../views/Business/views/CategoryManage.vue'),
        },
        {
          path: 'product',
          name: 'business-product',
          component: () => import('../views/Business/views/ProductManage.vue'),
        },
        {
          path: 'statistics',
          name: 'business-statistics',
          meta: { title: '统计报表' },
          component: () => import('../views/Business/views/Statistics.vue'),
        },
        {
          path: 'review',
          name: 'business-review-manage',
          meta: { title: '商品评价' },
          component: () => import('../views/Business/views/ReviewManage.vue'),
        },
        {
          path: 'order',
          redirect: '/business/center/order/done',
          children: [
            {
              path: 'done',
              name: 'business-order-done',
              meta: { title: '已完成订单' },
              component: () => import('../views/Business/views/OrderDone.vue'),
            },
            {
              path: 'pending',
              name: 'business-order-pending',
              meta: { title: '未完成订单' },
              component: () => import('../views/Business/views/OrderPending.vue'),
            },
            {
              path: 'cancel',
              name: 'business-order-cancel',
              meta: { title: '取消管理' },
              component: () => import('../views/Business/views/CancelManage.vue'),
            },
          ],
        },
      ],
    },
    {
      path: '/business/register',
      name: 'business-register',
      component: () => import('../views/Business/BusinessViews/Register.vue'),
    },
    {
      path: '/business/Review',
      name: 'business-review',
      component: () => import('../views/Business/BusinessViews/Review.vue'),
    },
    {
      path: '/business/thaw',
      name: 'business-thaw',
      component: () => import('../views/Business/BusinessViews/Thaw.vue'),
    },
  ],
})

// 用户端路由拦截 - 未登录跳转登录页
router.beforeEach((to, from, next) => {
  const userToken = localStorage.getItem('token')
  const bizToken = localStorage.getItem('authentication')
  if (to.path.startsWith('/user/') && !to.path.includes('/login')) {
    if (!userToken) return next('/user/login')
  }
  if (to.path.startsWith('/business/') && !to.path.includes('/login') && !to.path.includes('/register') && !to.path.includes('/Review') && !to.path.includes('/thaw')) {
    if (!bizToken) return next('/business/login')
  }
  next()
})

router.afterEach((to) => {
  const title = to.meta?.title
  if (title) document.title = title as string
})

export default router
