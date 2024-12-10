package edu.kh.admin.main.controller;

import java.util.Enumeration;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.kh.admin.main.model.dto.Member;
import edu.kh.admin.main.model.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 관리자 로그인
 * @return
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
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
	public void logout(HttpSession session) {
	    // 세션에서 loginMember 가져오기
	    Member loginMember = (Member) session.getAttribute("loginMember");

	    if (loginMember != null) {
	        log.info("Logged in member: " + loginMember.toString());
	        session.invalidate();
	    } else {
	        log.info("No loginMember found in session");
	    }
	}
	
}
