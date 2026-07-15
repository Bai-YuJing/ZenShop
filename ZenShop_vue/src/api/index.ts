import request from '@/utils/request'
import type { register } from 'node:module'

/** 店铺状态 */
export interface ShopStatus {
  id: string
  name: string | null
  description: string | null
  logo: string | null
  phone: string
  email: string | null
  categoryName: string | null
  categoryId: number
  rating: number | null
  /** 0-待提交 1-待审核 2-正常 3-冻结 4-已关闭 5-审核未通过 6-修改审核 */
  status: number
  provinceId: number | null
  cityId: number | null
  districtId: number | null
  detailedAddress: string | null
  lastLoginTime: string | null
  createdTime: string
  updatedTime: string
}

// ============ 商家账号模块 ============
export const businessApi = {
  getcaptcha: (phone: string) => request.get('/business/business/getCaptcha', { phone }),
  getCategory: () => request.get('/business/business/getCategory'),
  register: (parms:{}) => request.post('/business/business/register',parms),
  login: (parms:{}) => request.post('/business/business/login',parms),
}

// ============ 用户端公共模块 ============
export const userIndexApi = {
  getCategory: () => request.get('/user/index/category'),
  /** 查询商家列表 */
  getBusinessPage: (dto: {}) => request.post('/user/business/page', dto),
  /** 商品分页查询 */
  getProductPage: (dto: {}) => request.post('/user/product/page', dto),
  /** 商品详情 */
  getProductDetail: (id: number) => request.get('/user/product/product', { id }),
  /** 销量Top10 */
  getTop10: () => request.get('/user/index/top10'),
  /** 商家详情 */
  getBusinessDetail: (id: string) => request.get(`/user/business/detail/${id}`),
  /** 根据商家ID和分类查询商品 */
  getProductsByBusiness: (params: {}) => request.get('/user/product/listByBusiness', params),
  /** 商品评价列表 */
  getProductRatings: (params: {}) => request.get('/user/product/ratings', params),
}

// ============ 用户地址模块 ============
export const userAddressApi = {
  add: (dto: {}) => request.post('/user/address/add', dto),
  update: (dto: {}) => request.put('/user/address/update', dto),
  getList: () => request.get('/user/address/getAddress'),
  remove: (id: number) => request.delete(`/user/address/delete/${id}`),
  setDefault: (id: number) => request.put(`/user/address/default/${id}`),
}

// ============ 支付模块 ============
export const payApi = {
  pay: (dto: {}) => request.post('/user/order/pay', dto),
}

// ============ 订单模块 ============
export const orderApi = {
  list: (params: {}) => request.get('/user/order/list', params),
  statusCounts: () => request.get('/user/order/statusCounts'),
  detail: (orderId: number) => request.get(`/user/order/detail/${orderId}`),
  complete: (orderId: number) => request.put(`/user/order/complete/${orderId}`),
  cancel: (orderId: number, reasonType: number) => request.put(`/user/order/cancel/${orderId}`, {}, { params: { reasonType } }),
  unreviewed: (params: {}) => request.get('/user/order/unreviewed', params),
  rate: (dto: {}) => request.post('/user/order/rate', dto),
  getRate: (orderId: number) => request.get(`/user/order/ratings/${orderId}`),
  preview: (dto: {}) => request.post('/user/order/preview', dto),
  submit: (dto: {}) => request.post('/user/order/create', dto),
}

// ============ 优惠券模块 ============
export const couponApi = {
  list: () => request.get('/user/coupon/list'),
  all: () => request.get('/user/coupon/all'),
  claim: (couponId: number) => request.post(`/user/coupon/claim/${couponId}`),
}

// ============ 购物车模块 ============
export const cartApi = {
  add: (params: {}) => request.post('/user/cart/add', {}, { params }),
  list: () => request.get('/user/cart/list'),
  update: (params: {}) => request.put('/user/cart/update', {}, { params }),
  remove: (productId: number) => request.delete(`/user/cart/remove/${productId}`),
  clear: () => request.delete('/user/cart/clear'),
}

// ============ 用户账号模块 ============
export const userApi = {
  getCaptcha: (phone: string) => request.get('/user/user/getCaptcha', { phone }),
  register: (params: {}) => request.post('/user/user/register', params),
  login: (params: {}) => request.post('/user/user/login', params),
  /** 获取当前登录用户信息 */
  getUser: () => request.get('/user/info/getUser'),
  /** 更新用户信息 */
  updateUser: (params: {}) => request.put('/user/info/update', params),
  /** 修改密码 */
  updatePassword: (params: {}) => request.patch('/user/info/updatePassword', params),
  /** 上传头像 */
  uploadAvatar: (formData: any) =>
    request.post('/user/info/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
}
/** 商品分类管理（商家自定义分类） */
export interface ShopCategoryDto {
  name: string
  sort: number
}
export const businessCategoryApi = {
  /** 新增分类 */
  add: (dto: {}) =>
    request.post('/business/shop/category/addCategory', dto),
  /** 获取所有分类（可传 name 搜索） */
  getList: (params?: { name?: string }) =>
    request.get('/business/shop/category/getAll', params),
  /** 修改分类 */
  update: (dto: {}) =>
    request.put('/business/shop/category/updateCategory', dto),
  /** 删除分类 */
  remove: (id: number) =>
    request.delete(`/business/shop/category/deleteCategory/${id}`),
  /** 更新分类状态 */
  updateStatus: (id: number, status: number) =>
    request.put(`/business/shop/category/updateStatus/${id}`, {}, { params: { status } }),
}

/** 商品管理 */
export const businessProductApi = {
  /** 分页查询 */
  page: (dto: {}) =>
    request.post('/business/shop/product/page', dto),
  /** 添加商品 */
  add: (dto: {}) =>
    request.post('/business/shop/product/addProduct', dto),
  /** 编辑商品 */
  update: (dto: {}) =>
    request.put('/business/shop/product/update', dto),
  /** 上架/下架 */
  updateStatus: (id: number, status: number) =>
    request.put(`/business/shop/product/updateStatus/${id}`, {}, { params: { status } }),
  /** 上传商品图片 */
  uploadImage: (formData: any) =>
    request.post('/business/shop/product/img', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
}

export const businessStatsApi = {
  orderStats: (params: {}) => request.get('/business/shop/statistics/order', params),
  ratingStats: (params: {}) => request.get('/business/shop/statistics/rating', params),
}

export const adminApi = {
  businessStats: (params: {}) => request.get('/admin/dashboard/business', params),
}

export const businessOrderApi = {
  completed: (params: {}) => request.get('/business/shop/orders/completed', params),
  unfinished: (params: {}) => request.get('/business/shop/orders/unfinished', params),
  deliver: (id: number) => request.put(`/business/shop/order/deliver/${id}`),
  cancelList: (params: {}) => request.get('/business/shop/order/cancelList', params),
  reviewCancel: (orderId: number, approved: boolean, reasonType?: number) =>
    request.put(`/business/shop/order/reviewCancel/${orderId}`, {}, { params: { approved, ...(reasonType != null ? { reasonType } : {}) } }),
}

export const businessRatingApi = {
  page: (dto: {}) => request.post('/business/shop/product/ratings', dto),
}

export const businessShopApi = {
  getStatus: () => request.get('/business/shop/getStatus'),
  getShop: () => request.get<ShopStatus>('/business/shop/getShop'),
  register: (parms:{}) => request.put('/business/shop/review',parms),
  /** 获取审核未通过 / 冻结原因 */
  getReason: (id: string, type: number) =>
    request.get<string>(`/business/shop/getReason/${id}`, { type }),
  /** 退出登录 */
  logout: () => request.get('/business/shop/logout'),
  /** 更新店铺信息 */
  updateBusiness: (parms: {}) => request.put('/business/shop/updateBusiness', parms),
  /** 更新店铺状态（解冻：status=1） */
  updateStatus: (id: string, status: number) =>
    request.put(`/business/shop/updateStatus/${id}`, {}, { params: { status } }),
  /** 关店 */
  close: () => request.put('/business/shop/close'),
  /** 开店 */
  open: () => request.put('/business/shop/open'),
}
