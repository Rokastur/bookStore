package com.dematic.bookStore.services;

import com.dematic.bookStore.controller.utility.BookAuthorDTO;
import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.entities.ScienceJournal;
import com.dematic.bookStore.repositories.BookRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookTypeConversionOperations {

    private final BookRepository bookRepository;

    public BookTypeConversionOperations(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /*
     * if book is a scienceJournal and dto contains not null science index, update books science index.
     * if book is scienceJournal and dto contains null scienceIndex, convert to book.
     * else if book is not a scienceJournal but dto contains not null science index, convert to scienceJournal.
     * */
    public Book updateScienceIndexOrConvertBookType(Book book, BookAuthorDTO dto) {
        if (book instanceof ScienceJournal) {
            if (dto.getScienceIndex() != null) {
                ((ScienceJournal) book).setScienceIndex(dto.getScienceIndex());
            } else {
                book = transformToBook(book);
            }
        } else if (dto.getScienceIndex() != null) {
            book = transformToScienceJournal(book, dto.getScienceIndex());
        }
        return book;
    }

    /*
     * if book is a antiqueBook and dto contains release year within threshold, update books release year.
     * if book is antiqueBook and dto contains release year after the threshold, convert to book.
     * else if book is not a scienceJournal but dto contains release year within threshold, convert to antiqueBook.
     * */
    public Book updateReleaseYearOrConvertBookType(Book book, BookAuthorDTO dto) {
        boolean withinThreshold = false;
        if (dto.getReleaseYear() != null) {
            withinThreshold = dto.getReleaseYear().isBefore(LocalDate.parse("1900-01-01"));
        }
        if (book instanceof AntiqueBook) {
            if (withinThreshold) {
                ((AntiqueBook) book).setReleaseYear(dto.getReleaseYear());
            } else {
                book = transformToBook(book);
            }
        } else if (dto.getReleaseYear() != null && withinThreshold) {
            book = transformToAntiqueBook(book, dto.getReleaseYear());
        }
        return book;
    }

    public Book transformToBook(Book book) {
        bookRepository.deleteById(book.getBarcode());
        return new Book(book.getBarcode(), book.getTitle(), book.getQuantity(), book.getUnitPrice(), book.getAuthors());
    }

    public AntiqueBook transformToAntiqueBook(Book book, LocalDate releaseYear) {
        bookRepository.deleteById(book.getBarcode());
        return new AntiqueBook(book, releaseYear);
    }

    public ScienceJournal transformToScienceJournal(Book book, Integer scienceIndex) {
        bookRepository.deleteById(book.getBarcode());
        return new ScienceJournal(book, scienceIndex);
    }
}
