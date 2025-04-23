package com.elbialy.book.book;

import com.elbialy.book.common.PageResponse;
import com.elbialy.book.exceptions.OperationNotPermittedException;
import com.elbialy.book.history.BookTransactionHistory;
import com.elbialy.book.history.BookTransactionHistoryRepository;
import com.elbialy.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private static final String UPLOAD_DIR = "D:\\upload";
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    public Integer save(@Valid BookRequest bookRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.save(Book.builder()
                .isbn(bookRequest.getIsbn())
                .owner(user)
                .shareable(bookRequest.isShareable())
                .synopsis(bookRequest.getSynopsis())
                .title(bookRequest.getTitle())
                .authorName(bookRequest.getAuthorName())
                .archived(false)
                .build());

        return book.getId();

    }

    public BookResponse findById(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return BookResponse.builder()
                .rate(book.getRate())
                .isbn(book.getIsbn())
                .shareable(book.isShareable())
                .synopsis(book.getSynopsis())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .archived(book.isArchived())
                .bookId(bookId)
                .owner(book.getOwner().getFullName())
                .build();
    }


    public PageResponse<BookResponse> getAll(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<Book> books = bookRepository.findAllBooks(pageable,user.getId());
        return pageResponse(books);

    }

    public PageResponse<BookResponse> getByOwner(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<Book> books = bookRepository.findAllBooksByOwner(pageable,user.getId());
        return pageResponse(books);

    }

    public PageResponse<BorrowedBookResponse> getBorrowed(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowed(pageable,user.getId());

        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.getContent().stream().map(Mapper::bookToBorrowedBookResponse).toList();

        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );


    }
    public static PageResponse<BookResponse> pageResponse(Page<Book> books){
        List<BookResponse> bookResponses = books.getContent().stream().map(Mapper::bookToBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> getReturned(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<BookTransactionHistory> returnedBooks = bookTransactionHistoryRepository.findAllReturned(pageable,user.getId());
        List<BorrowedBookResponse> allReturnedBooks = returnedBooks.getContent().stream().map(Mapper::bookToBorrowedBookResponse).collect(Collectors.toList());
        return new PageResponse<>(
                allReturnedBooks,
                returnedBooks.getNumber(),
                returnedBooks.getSize(),
                returnedBooks.getTotalElements(),
                returnedBooks.getTotalPages(),
                returnedBooks.isFirst(),
                returnedBooks.isLast()
        );

    }

    public Integer updateShareable(Integer bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        User user = (User) authentication.getPrincipal();
        if(!Objects.equals(user.getId(),book.getOwner().getId())){

            throw new OperationNotPermittedException("You cannot update books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;

    }

    public Integer updateArchive(Integer bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        User user = (User) authentication.getPrincipal();
        if(!Objects.equals(user.getId(),book.getOwner().getId())){
            throw new OperationNotPermittedException("You cannot update books archive status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(int bookId, User principal) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException(
                "Book not found"));
        if (book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("Book is not shareable or archived");
        }
        if (Objects.equals(book.getOwner().getId(),principal.getId())){
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        boolean isBorrowed = bookTransactionHistoryRepository.findByBook(book.getId(),principal.getId());
        if(isBorrowed){
            throw new OperationNotPermittedException("Book is already borrowed");
        }
        return  bookTransactionHistoryRepository.save(
                BookTransactionHistory.builder().book(book)
                        .user(principal)
                        .returned(false)
                        .returnApproved(false)
                        .build()
        ).getId();



    }

    public Integer returnedBook(int bookId, User principal) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException(
                "Book not found"));
        if (book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("Book is not shareable or archived");
        }
        if (Objects.equals(book.getOwner().getId(),principal.getId())){
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        BookTransactionHistory bookTransactionHistory =
                bookTransactionHistoryRepository.findHistoryByBook(book).orElseThrow(()-> new EntityNotFoundException(
                        "Book not found"));
        if(bookTransactionHistory.isReturned()){
            throw new OperationNotPermittedException("Book is already returned");
        }
        if(bookTransactionHistory.isReturnApproved()){
            throw new OperationNotPermittedException("Book is already approved for return");
        }
        bookTransactionHistory.setReturned(true);
        bookTransactionHistoryRepository.save(bookTransactionHistory);
        return bookTransactionHistory.getId();


    }

    public Integer returnedApproved(int bookId, User principal) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if (book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("Book is not shareable or archived");
        }
        if (Objects.equals(book.getOwner().getId(),principal.getId())){
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        BookTransactionHistory bookTransactionHistory =
                bookTransactionHistoryRepository.findHistoryByBook(book).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if(!bookTransactionHistory.isReturned()){
            throw new OperationNotPermittedException("Book is not returned");
        }
        if(bookTransactionHistory.isReturnApproved()){
            throw new OperationNotPermittedException("Book is already approved for return");
        }
        bookTransactionHistory.setReturnApproved(true);
        bookTransactionHistoryRepository.save(bookTransactionHistory);
        return bookTransactionHistory.getId();
    }

    public void saveBookCover(Integer bookId, MultipartFile cover, User principal) throws IOException {
        if(cover==null){
            throw new IOException("there is a problem in upload");
        }
        if(cover.getOriginalFilename()==null){
            throw new IOException("there is a problem in upload");
        }
        String sanitizedFilename = Paths.get(cover.getOriginalFilename()).getFileName().toString()
                .replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        int dotIndex = sanitizedFilename.lastIndexOf('.');
        String baseName = (dotIndex == -1) ? sanitizedFilename : sanitizedFilename.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : sanitizedFilename.substring(dotIndex);
        String fileName = baseName + "_" + bookId + extension;
        var savedCover = new File(UPLOAD_DIR + File.separator + fileName);
        if (!Objects.equals(savedCover.getParent(), UPLOAD_DIR)){ // security check
            throw new SecurityException("Unsafe file path");
        }
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if(!Objects.equals(book.getOwner().getId(), principal.getId())){
            throw new OperationNotPermittedException("You cannot upload cover for book you don't own");
        }
        if (savedCover.exists()){
            throw new IOException("Cover already exists");
        }
        cover.transferTo(savedCover);
        book.setBookCover(savedCover.getAbsolutePath());
        bookRepository.save(book);

    }
}


