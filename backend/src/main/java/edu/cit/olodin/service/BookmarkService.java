package edu.cit.olodin.service;

import edu.cit.olodin.entity.Bookmark;
import edu.cit.olodin.entity.User;
import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.repository.BookmarkRepository;
import edu.cit.olodin.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.security.Security;
import java.util.List;

@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository, UserRepository userRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", "AUTH_USER_NOT_FOUND"));
    }

    public void addBookmark(Long listingId) {
        User user = getCurrentUser();

        bookmarkRepository.findByUserIdAndListingId(user.getId(), listingId)
                .ifPresent( b-> {
                    throw new RuntimeException("Already bookmarked");
                });

        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(user.getId());
        bookmark.setListingId(listingId);

        bookmarkRepository.save(bookmark);
    }

    public List<Bookmark> getMyBookmarks() {
        User user = getCurrentUser();
        return bookmarkRepository.findByUserId(user.getId());
    }

    public void removeBookmark(Long id) {
        bookmarkRepository.deleteById(id);
    }
}
