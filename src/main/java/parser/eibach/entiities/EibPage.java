package parser.eibach.entiities;

import javax.persistence.*;

@Entity
@Table(name = "pages")
public class EibPage {

    @Id
    @Column(name = "PAGE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "PAGE_URL")
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
