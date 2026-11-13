package com.example.testit;

import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BookRepositoryITTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    BookRepository bookRepository;

    private Book livre(String titre, Status status, Member emprunteur) {
        var book = new Book(titre, "auteur");
        book.setStatus(status);
        book.setBorrower(emprunteur);
        return em.persist(book);
    }

    @Test
    public void findByMemberAndStatusNeRetourneQueLesLivresDuMembreAvecCeStatut() {

        //Given
        var alice = em.persist(new Member("alice"));
        var bob = em.persist(new Member("bob"));
        var aliceEnCours = livre("A1", Status.EMPRUNTE, alice);
        livre("A2", Status.RENDU, alice);
        livre("B1", Status.EMPRUNTE, bob);
        livre("Libre", Status.DISPONIBLE, null);
        em.flush();
        em.clear();

        //WHen
        var result = bookRepository.findByMemberAndStatus(alice.getId(), Status.EMPRUNTE);

        //Then
        Assertions.assertThat(result)
                .extracting(Book::getId)
                .containsExactly(aliceEnCours.getId());
    }

}
