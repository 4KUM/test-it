package com.example.testit;

import com.example.testit.adapter.member.CurrentMemberService;
import com.example.testit.model.Book;
import com.example.testit.model.Status;
import com.example.testit.service.BookService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CurrentMemberService currentMemberService;

    @Test
    void itShouldCreateABookThenBorrowIt() throws Exception {

        var book = new Book("Charles", "Leclerc");
        book.setId(1L);
        Mockito.when(bookService.createBook("Charles", "Leclerc")).thenReturn(book);

        //Création d'un livre
        mockMvc.perform(MockMvcRequestBuilders.post("/books").content("""
                        {
                            "title":"Charles",
                            "author":"Leclerc"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Charles"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value("Leclerc"));

        Mockito.verify(bookService).createBook("Charles", "Leclerc");

        //emrunt d'un livre
        var emprunte = new Book("Charles", "Leclerc");
        emprunte.setId(1L);
        emprunte.setStatus(Status.EMPRUNTE);
        Mockito.when(currentMemberService.getCurrentMemberId()).thenReturn(Optional.of(1L));
        Mockito.when(bookService.borrowBook(1L, 1L)).thenReturn(emprunte);

        mockMvc.perform(MockMvcRequestBuilders.post("/books/1/borrow"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("EMPRUNTE"));

        Mockito.verify(bookService).borrowBook(1L, 1L);
    }
}
