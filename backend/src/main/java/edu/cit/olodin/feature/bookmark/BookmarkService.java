package edu.cit.olodin.feature.bookmark;

import edu.cit.olodin.feature.user.User;
import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.feature.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
                    throw new AuthException("Listing already bookmarked", "BOOKMARK_EXISTS");
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
        User user = getCurrentUser();

        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new AuthException(
                        "Bookmark not found",
                        "BOOKMARK_NOT_FOUND"
                ));

        if (!bookmark.getUserId().equals(user.getId())) {
            throw new AuthException(
                    "You are not allowed to delete this bookmark",
                    "AUTH_UNAUTHORIZED"
            );
        }

        bookmarkRepository.delete(bookmark);
    }
}
