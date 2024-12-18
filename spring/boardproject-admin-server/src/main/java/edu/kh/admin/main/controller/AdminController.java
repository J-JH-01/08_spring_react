package edu.kh.admin.main.controller;

import java.util.List;

import org.springframework.http.HttpStatus;import org.springframework.http.HttpStatusCode;
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

@RestController
@CrossOrigin(origins = "http://localhost:3000" ,allowCredentials = "true")
@RequestMapping("admin")
@RequiredArgsConstructor
@Slf4j
@SessionAttributes({"loginMember"})
public class AdminController {
	
	private final AdminService service;
	
	
	/** 관리자 로그인
	 * @param inputMember
	 * @param model
	 * @return
	 */
	@PostMapping("login")
	public Member login(@RequestBody Member inputMember,
						Model model ) {
		
		Member loginMember = service.login(inputMember);
		
		if(loginMember == null) {
			return null;
		}
		
		model.addAttribute(loginMember);
		return loginMember;
	}
	
	/** 관리자 로그아웃
	 * @param session
	 * @return
	 */
	@GetMapping("logout")
	public ResponseEntity<String> logout(HttpSession session) {

		// ResponseEntity 
		// Spring 에서 제공하는 Http 응답 데이터를 커스터마이징 할 수 있도록 지원하는 클래스
		// HTTP 상태코드, 헤더, 응답 본문(body)을 모두 설정 가능
		
		try {
			session.invalidate(); // 세션 무효화 처리
			return ResponseEntity
					.status(HttpStatus.OK) // 200번
					.body("로그아웃이 완료되었습니다");
			
		} catch (Exception e) {
			// 세션 무효화 중 예외 발생한 경우
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR) // 서버에서 발생한 오류 500번
					.body("로그아웃 처리 중 문제가 발생했습니다 : " + e.getMessage());
			
		}
		
	}
	
	
	// ---------- 복구 -------------------
	
	@GetMapping("withdrawnMemberList")
	public ResponseEntity<Object> selectWithdrawnMemberList() {
		// 성공 시 List<Member> 반환, 실패 시 String 반환 -> Object 사용
		// (참고) ResponseEntity<?> : 반환값을 특정할 수 없을 때 사용 가능
		
		try {
			List<Member> withdrawnMemberList = service.selectWithdrawnMemberList();
			return ResponseEntity.status(HttpStatus.OK).body(withdrawnMemberList);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("탈퇴한 회원 목록 조회 중 문제가 발생했음 : " + e.getMessage());
		}
	}
	
	@PutMapping("restoreMember")
	public ResponseEntity<String> restoreMember(@RequestBody Member member) {
		
		try {
			
			int result = service.restoreMember(member.getMemberNo());
			// 	int result = service.restoreMember(100);
			
			if(result > 0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(member.getMemberNo() + " 회원 복구 완료");
				
			} else {
				// result == 0
				// -> 업데이트 된 행이 없음
				// == 탈퇴하지 않았거나, memberNo를 잘못 보냄.
				// 400 -> 요청 구문이 잘못되었거나 유효하지 않음.
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("유효하지 않은 memberNo : " + member.getMemberNo());
			}
			
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("탈퇴 회원 복구 중 문제가 발생했습니다 : " + e.getMessage());
		}
		
	}
	
	

	/**
	 * 삭제된 게시글 목록 조회
	 * 
	 * @return
	 */
	@GetMapping("deleteBoardList")
	public ResponseEntity<Object> selectDeleteBoardList() {

		try {
			List<Board> deleteBoardList = service.selectDeleteBoardList();
			return ResponseEntity.status(HttpStatus.OK).body(deleteBoardList);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("삭제된 게시글 목록 조회 중 문제가 발생했습니다: " + e.getMessage());
		}
	}

	/**
	 * 삭제된 게시글 복구
	 * 
	 * @param board
	 * @return
	 */
	@PutMapping("restoreBoard")
	public ResponseEntity<String> restoreBoard(@RequestBody Board board) {
		try {
			int result = service.restoreBoard(board.getBoardNo());
			// int result = service.restoreBoard(5000); // 없는번호거나 삭제안된 게시물번호로 테스트

			if (result > 0) {
				return ResponseEntity.status(HttpStatus.OK).body(board.getBoardNo() + " 게시글 복구 완료");
			} else {
				// result == 0
				// -> 업데이트가 안되었음 == 삭제하지 않았거나 없는 boardNo 잘못 보냄(잘못된 요청)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 (요청 구문이 잘못되었거나 유효하지 않음)
						.body("유효하지 않은 boardNo : " + board.getBoardNo());
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("삭제된 게시글 복구 중 문제가 발생했습니다: " + e.getMessage());
		}

	}
	
	// 새로운 가입 회원 조회
	@GetMapping("newMember")
	public ResponseEntity<List<Member>> getNewMember() {
		try {
			
			List<Member> newMemberList = service.getNewMember();
			return ResponseEntity.status(HttpStatus.OK).body(newMemberList);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	// 최대 조회수 게시글 조회
	@GetMapping("maxReadCount")
	public ResponseEntity<Object> maxReadCount() {
		try {
			Board board = service.maxReadCount();
			return ResponseEntity.status(HttpStatus.OK).body(board);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	
	
	// 최대 좋아요 게시글 조회
	@GetMapping("maxLikeCount")
	public ResponseEntity<Object> maxLikeCount() {

		try {
			Board board = service.maxLikeCount();
			return ResponseEntity.status(HttpStatus.OK).body(board);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// 최대 댓글수 게시글 조회
	@GetMapping("maxCommentCount")
	public ResponseEntity<Object> maxCommentCount() {
		try {
			Board board = service.maxCommentCount();
			return ResponseEntity.status(HttpStatus.OK).body(board);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}


	/** 관리자 계정 목록 조회
	 * @return
	 */
	@GetMapping("adminAccountList")
	public ResponseEntity<Object> adminAccountList() {
		try {
			List<Member> adminList = service.adminAccountList();
			return ResponseEntity.status(HttpStatus.OK).body(adminList);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	/** 관리자 계정 발급
	 * @return
	 */
	@PostMapping("createAdminAccount")
	public ResponseEntity<String> createAdminAccount(@RequestBody Member member) {
		try {
			
			String accountPw = service.createAdminAccount(member);
			
			if(accountPw != null) {
				// 201(자원이 성공적으로 생성되었음을 나타냄)
				return ResponseEntity.status(HttpStatus.CREATED).body(accountPw);
			} else {
				// 204 (콘텐츠 없음)
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
					.body(null);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}