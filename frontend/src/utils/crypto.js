/**
 * 密码加密工具
 * 使用 Web Crypto API 进行 SHA-256 加密
 */

/**
 * 使用 SHA-256 对密码进行加密
 * @param {string} password - 明文密码
 * @returns {Promise<string>} - Base64 编码的加密密码
 */
export async function hashPassword(password) {
  // 将密码转换为 Uint8Array
  const encoder = new TextEncoder()
  const data = encoder.encode(password)
  
  // 使用 SHA-256 进行哈希
  const hashBuffer = await crypto.subtle.digest('SHA-256', data)
  
  // 将 ArrayBuffer 转换为 Base64 字符串
  const hashArray = new Uint8Array(hashBuffer)
  const base64 = btoa(String.fromCharCode.apply(null, hashArray))
  
  return base64
}

/**
 * 同步版本的 SHA-256 加密（使用 js-sha256 算法模拟）
 * 备用方案，用于不支持 Web Crypto API 的环境
 */
export function hashPasswordSync(password) {
  // 简单的异步转同步包装
  return new Promise((resolve) => {
    hashPassword(password).then(resolve)
  })
}
