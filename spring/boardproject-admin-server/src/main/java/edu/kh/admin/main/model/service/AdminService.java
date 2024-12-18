package edu.kh.admin.main.model.service;

import java.util.List;

import edu.kh.admin.main.model.dto.Board;
import edu.kh.admin.main.model.dto.Member;

public interface AdminService {

	/** 관리자 로그인
	 * @param inputMember
	 * @return
	 */
	Member login(Member inputMember);

	/** 탈퇴한 회원 목록 조회
	 * @return
	 */
	List<Member> selectWithdrawnMemberList();

	/** 탈퇴 회원 복구
	 * @param memberNo
	 * @return
	 */
	int restoreMember(int memberNo);

	/** 삭제된 게시글 목록 조회
	 * @return
	 */
	List<Board> selectDeleteBoardList();

	/** 삭제 게시글 복구
	 * @param boardNo
	 * @return
	 */
	int restoreBoard(int boardNo);

	/** 새로운 가입 회원 조회
	 * @return
	 */
	List<Member> getNewMember();

	/**최대 조회수 게시글 조회
	 * @return
	 */
	Board maxReadCount();
	
	/** 최대 좋아요 게시글 조회
	 * @return
	 */
	Board maxLikeCount();

	/** 최대 댓글수 게시글 조회
	 * @return
	 */
	Board maxCommentCount();

	/** 관리자 계정 목록 조회
	 * @return
	 */
	List<Member> adminAccountList();

	/** 관리자 계정 발급
	 * @param member
	 * @return
	 */
	String createAdminAccount(Member member);


}