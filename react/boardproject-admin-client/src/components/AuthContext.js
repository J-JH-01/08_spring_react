import axios from "axios";
import { createContext, useState } from "react";

export const AuthContext = createContext();
// Context는 React에서 컴포넌트 계층 구조(트리)를 통해 데이터를 효율적으로 전달하기 위한 메커니즘
// 컴포넌트 간에 전역 상태를 공유할 수 있는 컨텍스트를 생성

// Context는 Provider 제공자, Consumer 소비자

// 전역 상태 제공자(Provider) 정의
export const AuthProvider = ({children}) => {

  const [email,setEmail] = useState('');
  const [password,setPassword] = useState('');
  const [user,setUser] = useState(() => {
    const storeUser = localStorage.getItem('userData');
    return storeUser ? JSON.parse(storeUser) : null;
  });
  // 이거 설정안하면 새로고침하면 정보가 날라가버림

  // 이메일 입력 핸들러
  const changeInputEmail = (e) =>{
    setEmail(e.target.value);
  };

  // 비밀번호 입력 핸들러
  const changeInputPw = (e) =>{
    setPassword(e.target.value);
  };

  // 로그인 처리 함수
  const handleLogin = async (e) => {
    e.preventDefault();

    // 비동기 로그인 요청
    //console.log(email);
    //console.log(password);

    try {
      const response = await axios.post(
        //'요청주소', body 데이터, 헤더 영역
        'http://localhost:8080/admin/login',
        { memberEmail : email, 
          memberPw : password
        },
        {'Content-Type' : 'application/json'}
      )
      
      console.log(response);

      const adminInfo = response.data;
      
      if(adminInfo.length === 0){alert('이메일 혹은 비밀번호 불일치!');return;}

      // 브라우저에서 현재 로그인한 관리자 정보를 기억하도록 해야함

      /** 아래의 둘다 관리자 도구 키면 쿠키 위쪽에 한글로 적혀있음
       * 
       * localstorage :
       * - 브라우저를 닫아도 데이터가 영구적으로 유지
       * - 브라우저 전역에서 사용 (모든 탭과 창에서 공유됨)
       * 
       * sessionstorage : 
       * - 브라우저 탭 또는 창을 닫으면 데이터가 즉시 삭제
       * - 현재 탭 또는 창에만 데이터가 유지됨
       * 
       */

      const currentTime = new Date().getTime();
      const expirationTime = currentTime + 60 * 60 * 1000; // 1시간 후

      // 데이터와 만료시간을 localStorage 저장
      // * localStorgage는 만료시간을 저장하는 기능 내장 x (필요 시 따로 직접 구현)
      localStorage.setItem('userData', JSON.stringify(adminInfo));
      localStorage.setItem('expirationTime', expirationTime);

      // 상태에 세팅
      setUser(adminInfo);

      // 만료시간을 확인하기 위해 타이머 설정
      setTimeout( () => {
        // 만료 시간이 지나면 localStorage에 있는 데이터 삭제
        localStorage.removeItem('userData');
        localStorage.removeItem('expirationTime');
        alert("재로그인 해주세요 :)");
        window.location.href = '/';

      }, 60 * 60 * 1000); // 1시간 후



    } catch(error){
      console.log('로그인 중 예외 발생 : ', error);
    }
    
  };

  // 로그아웃 처리 함수
  const handleLogout = async (e) =>{
    e.preventDefault();

    try{
      await axios.get("http://localhost:8080/admin/logout");
      // get 비동기 요청
      // /admin/logout

      // 1. setUser(null);
      setUser(null);
      // 2. localStorage 비우기
      localStorage.removeItem('userData');
      if(localStorage.getItem('expirationTime') !=null) localStorage.removeItem('expirationTime');
      
      window.location.href = '/';
    }catch{
      alert("로그아웃 실패");
    }
  };
  
  // user 라는 상태와, 여러가지 이벤트핸들러(함수)를 묶어서
  // Provider를 통해 하위(자식) 컴포넌트로 데이터를 전달함

  const globalState = {
    user,
    changeInputEmail,
    changeInputPw,
    handleLogin,
    handleLogout
  };

  return(
    //AuthContext.Provider : 데이털르 제공하는 역할
    // 하위 컴포넌트는 이 provider가 제공하는 데이터를 사용 (소비 == Consumer) 할 수 있음
    <AuthContext.Provider value={globalState}>
      {children}
    </AuthContext.Provider>
  );
}