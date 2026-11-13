package com.example.testit;

import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookServiceIT {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    public void testFindByMemberAndStatus() {
        var alice = memberRepository.save(new Member("alice"));
        var bob = memberRepository.save(new Member("bob"));

        var aliceEmprunte = new Book("Charles", "auteur");
        aliceEmprunte.setStatus(Status.EMPRUNTE);
        aliceEmprunte.setBorrower(alice);
        aliceEmprunte = bookRepository.save(aliceEmprunte);

        var aliceRendu = new Book("Lewis", "auteur");
        aliceRendu.setStatus(Status.RENDU);
        aliceRendu.setBorrower(alice);
        bookRepository.save(aliceRendu);

        var bobEmprunte = new Book("Max", "auteur");
        bobEmprunte.setStatus(Status.EMPRUNTE);
        bobEmprunte.setBorrower(bob);
        bookRepository.save(bobEmprunte);

        var libre = new Book("Libre", "auteur");
        bookRepository.save(libre);

        var result = bookRepository.findByMemberAndStatus(alice.getId(), Status.EMPRUNTE);

        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.getFirst().getId()).isEqualTo(aliceEmprunte.getId());
        Assertions.assertThat(result.getFirst().getTitle()).isEqualTo("Charles");
        Assertions.assertThat(result.getFirst().getStatus()).isEqualTo(Status.EMPRUNTE);
    }
}
