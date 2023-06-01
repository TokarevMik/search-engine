package searchengine.model;

import jakarta.persistence.*;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lemma {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinTable(name = "index",
            joinColumns = @JoinColumn(name = "lemma_id"),
            inverseJoinColumns = @JoinColumn(name = "page_id")
    )
    private Set<Book> pages=new HashSet<>();
}
