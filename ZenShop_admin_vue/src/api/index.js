// src/api/index.js
import request from '@/utils/request'

// ============ 用户模块 ============
export const adminApi = {
    getcaptcha:()=>request.get('/admin/login/getCaptcha'),
    login:(data)=>request.post('/admin/login',data),

    list:(params)=> {
      const query = Object.entries(params)
        .filter(([_, v]) => v != null)
        .map(([k, v]) => `${k}=${v}`)
        .join('&')
      return request.get(`/admin/list?${query}`)
    },
    remove:(id)=>request.delete('/admin/delete', { id }),
    getByID:(id)=>request.get('/admin/getone', { id }),
    add:(data)=>request.post('/admin/addAdminUser', data),
    getNowUser:()=>request.get('/admin/getNowUser'),
    update:(params)=>request.put('/admin/update',params),
    logout:()=>request.get('/admin/logout',),
}

// ============ 用户头像审核 ============
export const userApi = {
    pendingAvatar:()=> request.get('/admin/user/pendingAvatar'),
    reviewAvatar:(id, status)=> request.put(`/admin/user/avatar/${id}`, null, { params: { status } }),
}

// ============ 用户管理（前台用户） ============
export const userManageApi = {
    page:(params)=> request.get('/admin/user/page', params),
    getById:(id)=> request.get(`/admin/user/${id}`),
    update:(data)=> request.put('/admin/user/update', data),
    updateStatus:(id, status)=> request.patch(`/admin/user/status/${id}`, null, { params: { status } }),
    remove:(id)=> request.delete(`/admin/user/${id}`),
    ratings:(userId)=> request.get(`/admin/user/ratings/${userId}`),
}

// ============ 商家模块 ============
export const businessApi = {
    getCategory:()=>request.get('/admin/business/getCategory'),
    page:(params)=> request.post('/admin/business/page', params),
    getById:(id)=> request.get('/admin/business/get', { id }),
    getUpdateInfo:(id)=> request.get('/admin/business/getUpdateInfo', { id }),
    audit:(id, status, reason)=> request.put(`/admin/business/updateStatus/${id}`, null, { params: reason ? { status, reason } : { status } }),
    getReason:(id, type)=> request.get(`/admin/business/getReason/${id}`, { type }),
    updateBusiness:(id)=> request.put('/admin/business/updateBusiness', null, { params: { id } }),

}

// ============ 优惠券模块 ============
export const couponApi = {
    page:(params)=> request.get('/admin/coupon/page', params),
    getById:(id)=> request.get(`/admin/coupon/${id}`),
    create:(data)=> request.post('/admin/coupon/create', data),
    update:(data)=> request.put('/admin/coupon/update', data),
    remove:(id)=> request.delete(`/admin/coupon/${id}`),
}

// ============ 统计报表 ============
export const statsApi = {
    user:(range = '7d')=> request.get('/admin/dashboard/user', { range }),
    business:(range = '7d')=> request.get('/admin/dashboard/business', { range }),
}

