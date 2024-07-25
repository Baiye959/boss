import request from '@/utils/request';

/**
 * 调用后端接口增加用户
 * @param {*} userDTO
 */
export function add(userDTO) {
    return request({
        url: '/user',
        method: 'post',
        data: userDTO
    });
}

/**
 * 调用后端接口更新用户
 * @param {*} userDTO
 */
export function update(userDTO) {
    return request({
        url: '/user',
        method: 'put',
        data: userDTO
    });
}

/**
 * 调用后端接口根据id取得用户
 * @param {*} id
 */
export function findById(id) {
    return request({
        url: '/user/' + id,
        method: 'get'
    });
}

/**
 * 查询用户信息
 * @param {*} userQuery
 */
export function search(userQuery) {
    return request({
        url: '/user',
        method: 'get',
        params: userQuery
    });
}

/**
 * 删除用户
 * @param id 当前行的id
 * @param version 当前版本
 */
export function remove(id, version) {
    return request({
        url: '/optimism/user',
        method: 'delete',
        params: { id: id, version: version }
    });
}

/**
 * 批量删除用户
 * @param idVersions 用户的id和版本信息
 */
export function batchRemove(idVersions) {
    return request({
        url: '/batch/user',
        method: 'delete',
        data: idVersions
    });
}