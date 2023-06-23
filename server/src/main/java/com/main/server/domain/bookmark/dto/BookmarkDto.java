package com.main.server.domain.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookmarkDto {
    private long memberId;
    private long boardId;
}
