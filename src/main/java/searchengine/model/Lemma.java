package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table
public class Lemma {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="site_id")
    private int siteId;

    private String lemma;
    private int frequency;
//    private Set<Book> pages=new HashSet<>();
}
