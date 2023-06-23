package com.main.server.domain.bookmark.controller;

import com.main.server.domain.bookmark.dto.BookmarkDto;
import com.main.server.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    @PostMapping
    public int postBookmark(@Valid @RequestBody BookmarkDto bookmarkDto) {


        return bookmarkService.updateBookmark(bookmarkDto);

    }
}
