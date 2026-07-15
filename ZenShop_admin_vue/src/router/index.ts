import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import LoginView from '../views/LoginView.vue'
import AdminHome from '../views/AdminHome.vue'
import AdminUser from '../views/AdminViews/User.vue'
import AdminBusiness from '../views/AdminViews/Business.vue'
import AdminAdmin from '../views/AdminViews/Admin.vue'
import AdminInfo from '../views/utilsView/AdminInfo.vue'
import AvatarReview from '../views/AdminViews/AvatarReview.vue'
import Coupon from '../views/AdminViews/Coupon.vue'
import Statistics from '../views/AdminViews/Statistics.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/admin',
      component: AdminHome,
      redirect: '/admin/user',
      children: [
        {
          path: 'user',
          name: 'adminUser',
          component: AdminUser,
          meta: { title: '用户管理' },
        },
        {
          path: 'avatar-review',
          name: 'avatarReview',
          component: AvatarReview,
          meta: { title: '头像审核' },
        },
        {
          path: 'business',
          name: 'adminBusiness',
          component: AdminBusiness,
          meta: { title: '商家管理' },
        },
        {
          path: 'coupon',
          name: 'adminCoupon',
          component: Coupon,
          meta: { title: '优惠券管理' },
        },
        {
          path: 'statistics',
          name: 'adminStatistics',
          component: Statistics,
          meta: { title: '统计报表' },
        },
        {
          path: 'admin',
          name: 'adminManagement',
          component: AdminAdmin,
          meta: { title: '管理员管理' },
        },
        {
          path: 'adminInfo',
          name: 'adminInfo',
          component: AdminInfo,
          meta: { title: '个人信息' },
        },
      ],
    },
  ],
})

// 路由守卫：未携带 token 则重定向到登录页
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/') {
    ElMessage.warning('未登录，请先登录')
    return { path: '/' }
  }
})

export default router
