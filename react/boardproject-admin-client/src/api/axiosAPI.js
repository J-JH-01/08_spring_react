// axiosAPI.js
import { default as axios } from 'axios'

export const axiosAPI = axios.create({
  baseURL :  'http://localhost:8080',
  headers : {'Content-Type' : 'application/json'},
  withCredentials : true // 쿠키 포함 설정 (서버와 클라이언트 둘다 동의 필요)
  // 서버에서도 credential 허용 설정 필요함
});