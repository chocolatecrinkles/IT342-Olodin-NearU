package edu.cit.olodin.controller;

import edu.cit.olodin.entity.Bookmark;
import edu.cit.olodin.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/bookmarks")
@CrossOrigin(origins = "http://localhost:3000")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> addBookmark(@RequestBody Map<String, Long> body) {
        bookmarkService.addBookmark(body.get("listingId"));
        return  ResponseEntity.ok("Bookmarked");
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public List<Bookmark> getBookmarks() {
        return bookmarkService.getMyBookmarks();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long id) {
        bookmarkService.removeBookmark(id);
        return ResponseEntity.ok("Deleted");
    }
}
