package com.example.board_admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/admin/members")
@Controller
public class AdminAccountController {

    @GetMapping
    public String members() {
        return "admin/members";
    }

}
