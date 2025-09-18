package com.ticket.homepage.controller;

import com.ticket.homepage.model.User;
import com.ticket.homepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.loginUser(username, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            
            // 예매 진행 중이었다면 예매 페이지로 리다이렉트
            String concertId = (String) session.getAttribute("concertId");
            if (concertId != null) {
                session.removeAttribute("concertId");
                return "redirect:/booking/" + concertId;
            }
            
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String name,
                          @RequestParam String phone,
                          RedirectAttributes redirectAttributes) {
        
        // 비밀번호 확인
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/register";
        }
        
        // 입력값 검증
        if (username.trim().isEmpty() || email.trim().isEmpty() || 
            password.trim().isEmpty() || name.trim().isEmpty() || phone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "모든 필드를 입력해주세요.");
            return "redirect:/register";
        }
        
        try {
            User user = userService.registerUser(username, email, password, name, phone);
            redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
