package com.example.board_admin.dto.response;

import com.example.board_admin.dto.ArticleCommentDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ArticleCommentClientResponse(
        @JsonProperty("_embedded") Embedded embedded,
        @JsonProperty("page") Page page
) {

    public static ArticleCommentClientResponse of() {
        return new ArticleCommentClientResponse(
                new ArticleCommentClientResponse.Embedded(List.of()),
                new ArticleCommentClientResponse.Page(1, 0, 1, 0)
        );
    }

    public static ArticleCommentClientResponse of(List<ArticleCommentDto> articleCommentDtos) {
        return new ArticleCommentClientResponse(
                new ArticleCommentClientResponse.Embedded(articleCommentDtos),
                new ArticleCommentClientResponse.Page(articleCommentDtos.size(), articleCommentDtos.size(), 1, 0)
        );
    }

    public List<ArticleCommentDto> articleComments() { return this.embedded().articleComments();}

    public record Embedded(List<ArticleCommentDto> articleComments){}

    public record Page(
            int size,
            long totalElements,
            int totalPage,
            int number
    ) { }
}
