package com.example.board_admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/management/article-comments")
@Controller
public class ArticleCommentManagementController {

    @GetMapping
    public String articleComments() {
        return "management/articleComments";
    }

}
