package edu.kh.admin.main.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.kh.admin.main.model.dto.Board;
import edu.kh.admin.main.model.dto.Member;
import edu.kh.admin.main.model.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 관리자 로그인
 * @return
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // 쿠키교환 설정
@RequestMapping("admin")
@RequiredArgsConstructor
@Slf4j
@SessionAttributes({"loginMember"})
public class AdminController {

	private final AdminService service;
	
	@PostMapping("login")
	public Member login(@RequestBody Member inputMember,
						 Model model,
						 HttpSession session){
		Member loginMember = service.login(inputMember);
		
		if(loginMember == null) return null;
		
		  model.addAttribute("loginMember", loginMember);
		//  세션에 올라감
	
		return loginMember;
	}
	
	@GetMapping("logout")
	public ResponseEntity<String> logout(HttpSession session){
		// responseEntity
		// Spring에서 제공하는 Http 응답 데이터를 커스터마이징 할 수 있도록 지원하는 클래스
		// Http 상대코드, 헤더(요청,응답), 응답 본문(responsebody)을 모두 설정 가능
		
		try {
			//log.info(session.getAttribute("loginMember").toString());
			session.invalidate(); // 세션 무효화 처리
			return ResponseEntity
					.status(HttpStatus.OK)
					.body("로그아웃이 완료되었습니다");
			
		} catch(Exception e){
			// 세션 무효화 중 예외 발생한 경우
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR) // 서버에서 발생한 오류 500번
					.body("로그 아웃 처리 중 문제가 발생했습니다 : " + e.getMessage());
					
		}

		
	}
	// ------------------------ 복구 -------------------------------
	
	@GetMapping("withdrawnMemberList")
	public ResponseEntity<Object> selectWithdrawnMemberList() {
		// 성공 시 List<Member> 반환, 실패시 String 반환 -> Object 사용
		// (참고)ResponseEntity<?> : 반환값을 특정할 수 없을 때 사용 가능
		try {
			List<Member> withdrawnMemberList = service.selectWithdrawnMemberList();
			//log.info(withdrawnMemberList.toString());
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(withdrawnMemberList);
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("탈퇴한 회원 목록 조회 중 문제가 발생했음 : " + e.getMessage());
		}
		
	}
	
	@PutMapping("restoreMember")
	public ResponseEntity<String> restoreMember(@RequestBody Member member) {
		
		try {
			int result = service.restoreMember(member.getMemberNo());

			if(result > 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(member.getMemberNo() + "회원 복구 완료");
			} else {
				// result == 0
				// -> 업데이트 된 행이 없음
				// == 탈퇴하지 않았거나, memberNo을 잘못 보냄
				// 400 -> 요청 구문이 잘못되었거나 유효하지 않음
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("유효하지 않은 memberNo : " +member.getMemberNo());
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("탈퇴 회원 복구 중 문제가 발생했습니다 : " + e.getMessage());
		}
	}
	
	@GetMapping("deleteBoardList")
	public ResponseEntity<Object> deleteBoardList() {
		// 성공 시 List<Member> 반환, 실패시 String 반환 -> Object 사용
		// (참고)ResponseEntity<?> : 반환값을 특정할 수 없을 때 사용 가능
		try {
			
			List<Board> deleteBoardList = service.deleteBoardList();
			log.info(deleteBoardList.toString());
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(deleteBoardList);
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("삭제한 보드 목록 조회 중 문제가 발생했음 : " + e.getMessage());
		}
	}
	
	@PutMapping("restoreBoard")
	public ResponseEntity<String> restoreBoard(@RequestBody Board board) {
		try {
			int result = service.restoreBoard(board.getBoardNo());

			if(result > 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(board.getBoardNo() + "번째 게시글 복구 완료");
			} else {
				// result == 0
				// -> 업데이트 된 행이 없음
				// == 탈퇴하지 않았거나, memberNo을 잘못 보냄
				// 400 -> 요청 구문이 잘못되었거나 유효하지 않음
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("유효하지 않은 boardNo : " +board.getBoardNo());
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("게시글 복구 중 문제가 발생했습니다 : " + e.getMessage());
		}
	}
}
