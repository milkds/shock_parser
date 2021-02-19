package parser.summit.entities;

import javax.persistence.*;

@Entity
@Table(name = "pages")
public class SumPage {

    @Id
    @Column(name = "PAGE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "PAGE_URL")
    private String url;

    @Column(name = "PAGE_PART")
    private String part;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
