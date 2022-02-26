import request from "@/utils/request";
/**
 * 分页查询
 * @param {条件} query
 * @returns
 */
export function list(query) {
  return request({
    url: '/workflow/model/list',
    method: 'get',
    params: query
  })
}

/**
 * 新增模型定义
 * @param {传输的数据}} data
 * @returns
 */
export function add(data) {
  console.log(data)
  return request({
    url: '/workflow/model',
    method: 'post',
    data: data
  })
}

/**
 * 按id删除模型
 * @param {模型id} id
 * @returns
 */
export function del(id) {
  return request({
    url: '/workflow/model/' + id,
    method: 'delete'
  })
}

/**
 * 流程部署
 * @param {模型id} id
 * @returns
 */
 export function deploy(id) {
  return request({
    url: '/workflow/model/deploy/' + id,
    method: 'post'
  })
}
/**
 * 将流程定义转换为模型
 * @param {流程定义id} processDefinitionId
 * @returns
 */
 export function convertToModel(processDefinitionId) {
  return request({
    url: '/workflow/model/convertToModel/' + processDefinitionId,
    method: 'get'
  })
}
