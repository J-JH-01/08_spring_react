import React, { useEffect, useState } from "react";
import { axiosAPI } from "../api/axiosAPI";

export default function Restore() {
  const [withdrawnMembers,setWithdrawnMembers] = useState([]); // 탈퇴 회원 목록을 서버에서 받아서 저장할 변수
  const [deleteBoard,setDeleteBoard] = useState([]); // 삭제 게시글 목록
  const [loading,setLoading] = useState(true); // 로딩 상태

  // 탈퇴한 회원 목록 조회용 비동기 요청 함수
  const getWithdrawnMemberList = async() => {
    try {
      const resp = await axiosAPI.get("/admin/withdrawnMemberList");

      if(resp.status === 200){
        setWithdrawnMembers(resp.data);
      }

    } catch (error) {
      console.log("탈퇴 회원 목록 조회 중 에러 발생 : " + error);
    }
  }

  // 탈퇴한 회원 복구 비동기 요청 함수
  const restoreMember = async (member) => {
    if(window.confirm(member.memberNickname + "님을 탈퇴 복구 시키겠습니까?")){
      try {
        const response = await axiosAPI.put("/admin/restoreMember", {memberNo : member.memberNo});

        console.log(response);

        if(response.status === 200){
          alert("복구되었습니다");
          getWithdrawnMemberList();
        }
      } catch (error) {
        console.log(error);
      }
    }
  }
  // -----------------------------------------------------------------------------------------------

  // 삭제한 게시글 목록 조회용 비동기 요청 함수
  const getDeleteBoardList = async() => {
    try {
      const resp = await axiosAPI.get("/admin/deleteBoardList");

      if(resp.status === 200){
        setDeleteBoard(resp.data);
      }

    } catch (error) {
      console.log("탈퇴 회원 목록 조회 중 에러 발생 : " + error);
    }
  }

  // 삭제한 게시글 복무 비동기 요청 함수
  const restoreBoard = async(board) => {
    if(window.confirm(board.boardNo + "번째 작성글을 복구 시키겠습니까?")){
      try {
        const response = await axiosAPI.put("/admin/restoreBoard", {boardNo : board.boardNo});

        console.log(response);

        if(response.status === 200){
          alert("복구되었습니다");
          getDeleteBoardList();
        }
      } catch (error) {
        console.log(error);
      }
    }
  }

  /*
  userEffect : React의 함수형 컴포넌트에서 특정 작업(sdie effect(==부수효과)을 처리하기 위해 사용하는 Hook)
  컴포넌트가 렌더링 되거나 상태가 변결 될 때 실행되는 작업을 정의 할 수 있다

    useEffect(() => {
      // 실행한 작업(side effect)

    }, [의존성 배열])  이게 기본형


    // 두번째 인자로 전달된 배열([])은 의존성 (dependency Array)임
    // 의존성 배열은 이 useEffect가 언제 실행될지를 결정함
    // -> 의존성 배열이 빈 배열일 경우, 이 useEffect는 컴포넌트가 처음 렌더링 될 때 딱 한번만 실행
  */

  // Restore 컴포넌트가 처음 렌더링 될때 getWithdrawnMemberList(),getDeleteBoardList 함수를 실행
  useEffect(() => {
    getWithdrawnMemberList();
    getDeleteBoardList();
  }, [])

  // withdrawnMembers 또는 deleteBoard 상태가 변결 될 때 마다 실행
  useEffect(() => {
    if(withdrawnMembers!=null && deleteBoard != null){
      setLoading(false)
    }
  }, [withdrawnMembers, deleteBoard])


  if(loading){
    return <h1>loading...</h1>
  } else {
    return (
      <div className="menu-box">
        <RestoreMember withdrawnMembers = {withdrawnMembers} restoreMember = {restoreMember}/>
        <RestoreBoard deleteBoard = {deleteBoard} restoreBoard = {restoreBoard}/>
      </div>
    );
  }
};


const RestoreMember = ({withdrawnMembers, restoreMember}) => {
  return (
    <section className="section-border">
      <h2>탈퇴 회원 복구</h2>

      <h3>탈퇴한 회원 목록</h3>

      {
        withdrawnMembers.length === 0 ? 
        (<p>탈퇴한 회원이 없습니다</p>) :
        (withdrawnMembers.map((member,index) => { // 이건 꺼낸게 아니라 리스트에서 거낸걸 멤버,인덱스로하는
          return(
            <ul className="ul-border" key={index}>
              {/* 키 안넣으면 에러나서 넣어주는것 */}
              <li>회원 번호 : {member.memberNo}</li>
              <li>회원 이메일 : {member.memberEmail}</li>
              <li>회원 닉네임 : {member.memberNickname}</li>
              <button className="restoreBtn" onClick={() => {restoreMember(member)}}>복구</button>
            </ul>
          )
        }))
      }

    </section>
  );
};


const RestoreBoard = ({deleteBoard, restoreBoard}) => {
  return(
    <section className="section-border">
        <h2>삭제 게시글 복구</h2>

        <h3>삭제된 게시글 목록</h3>

        {
          deleteBoard.length === 0 ?
          (<p>삭제된 게시글이 없습니다</p>):
          (deleteBoard.map((board,index) => {
            return(
              <ul className="ul-border" key={index}>
                <li>게시글 번호 : {board.boardNo}</li>
                <li>게시글 타이틀 : {board.boardTitle}</li>
                <li>게시글 작성자  : {board.memberNickname}</li>
                <button className="restoreBtn" onClick={() => {restoreBoard(board)}}>복구</button>
              </ul>
            )
          })
          ) 

        }
    </section>
  )
};