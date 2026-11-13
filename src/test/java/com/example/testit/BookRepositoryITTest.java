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
class BookRepositoryITTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        // la base est partagée entre les tests : on repart d'une base vide
        bookRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    public void testFindByMemberAndStatus() {
        var alice = memberRepository.save(new Member("alice"));
        var bob = memberRepository.save(new Member("bob"));

        var aliceEmprunte = new Book("A1", "auteur");
        aliceEmprunte.setStatus(Status.EMPRUNTE);
        aliceEmprunte.setBorrower(alice);
        aliceEmprunte = bookRepository.save(aliceEmprunte);

        var aliceRendu = new Book("A2", "auteur");
        aliceRendu.setStatus(Status.RENDU);
        aliceRendu.setBorrower(alice);
        bookRepository.save(aliceRendu);

        var bobEmprunte = new Book("B1", "auteur");
        bobEmprunte.setStatus(Status.EMPRUNTE);
        bobEmprunte.setBorrower(bob);
        bookRepository.save(bobEmprunte);

        var libre = new Book("Libre", "auteur");
        bookRepository.save(libre);

        var result = bookRepository.findByMemberAndStatus(alice.getId(), Status.EMPRUNTE);

        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.getFirst().getId()).isEqualTo(aliceEmprunte.getId());
        Assertions.assertThat(result.getFirst().getTitle()).isEqualTo("A1");
        Assertions.assertThat(result.getFirst().getStatus()).isEqualTo(Status.EMPRUNTE);
    }
}
