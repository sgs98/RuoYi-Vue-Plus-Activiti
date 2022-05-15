import request from "@/utils/request";
/**
 * 分页查询
 * @param {条件} query
 * @returns
 */
export function list(query) {
  return request({
    url: '/workflow/definition/list',
    method: 'get',
    params: query
  })
}

/**
 * 分页查询
 * @param {条件} query
 * @returns
 */
 export function hisList(query) {
    return request({
      url: '/workflow/definition/hisList',
      method: 'get',
      params: query
    })
}

/**
 *
 * @param {流程定义id} definitionId
 * @returns
 */
export function updateProcDefState(definitionId) {
  return request({
    url: '/workflow/definition/updateProcDefState/'+definitionId,
    method: 'put'
  })
}

/**
 * 按流程部署id删除
 * @param {流程部署id} deploymentId
 * @returns
 */
 export function del(deploymentId,definitionId) {
  return request({
    url: `/workflow/definition/${deploymentId}/${definitionId}`,
    method: 'delete'
  })
}

/**
 * 通过zip或xml部署流程定义
 * @returns
 */
export function deployProcessFile(data) {
  return request({
    url: '/workflow/definition/deployByFile',
    method: 'post',
    data: data
  })
}

/**
 *
 * @param {流程定义设置} definitionId
 * @returns
 */
export function setting(definitionId) {
  return request({
    url: '/workflow/definition/setting/'+definitionId,
    method: 'get'
  })
}

/**
 *
 * @param {查看xml} definitionId
 * @returns
 */
export function getXml(definitionId) {
  return request({
    url: '/workflow/definition/getXml/'+definitionId,
    method: 'get'
  })
}



