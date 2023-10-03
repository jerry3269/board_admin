package com.example.board_admin.controller;

import com.example.board_admin.dto.response.ArticleCommentResponse;
import com.example.board_admin.service.ArticleCommentManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/management/article-comments")
@Controller
public class ArticleCommentManagementController {

    private final ArticleCommentManagementService articleCommentManagementService;

    @GetMapping
    public String articleComments(Model model) {
        model.addAttribute(
                "comments",
                articleCommentManagementService.getArticleComments().stream().map(ArticleCommentResponse::from).toList()
        );
        return "management/article-comments";
    }

    @ResponseBody
    @GetMapping("/{commentId}")
    public ArticleCommentResponse articleComment(@PathVariable Long commentId) {
        return ArticleCommentResponse.from(articleCommentManagementService.getArticleComment(commentId));
    }

    @PostMapping("/{commentId}")
    public String deleteArticleComment(@PathVariable Long commentId) {
        articleCommentManagementService.deleteArticleComment(commentId);
        return "redirect:/management/article-comments";
    }
}
