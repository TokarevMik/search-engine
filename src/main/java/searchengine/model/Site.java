package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "site")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    int id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')",nullable = false)
    private  Status status = Status.FAILED;
    private Date status_time;
    @Column(columnDefinition="TEXT")
    private String last_error;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "site",cascade = CascadeType.ALL)
    List<Page> listPages;
    public void setListPages(List<Page> listPages) {
        if(listPages!=null){
            listPages.forEach(a->a.setSite(this));
        }
        this.listPages = listPages;
        /*this.listPages = listPages;
        listPages.forEach(p -> p.setSite(this));*/
    }

}
