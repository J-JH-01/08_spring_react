package edu.kh.admin.main.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.admin.main.model.dto.Member;
import edu.kh.admin.main.model.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AdminServiceImpl implements AdminService{

	private final AdminMapper mapper;
	private final BCryptPasswordEncoder bcrypt;
	
	// 관리자 로그인 
	@Override
	public Member login(Member inputMember) {
		
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		if(loginMember == null) return null;
		
		if( !bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) return null;
		
		// 비밀번호 들고가면 대참사임 ㅠㅠ 보안 중요
		loginMember.setMemberPw(null);
		
		return loginMember;
	}
	
}
