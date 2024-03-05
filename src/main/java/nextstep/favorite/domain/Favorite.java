package nextstep.favorite.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import nextstep.member.domain.Member;

@Entity
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    private Long source;

    private Long target;

    public Favorite() {
    }

    public Favorite(Member member, Long source, Long target) {
        this.member = member;
        this.source = source;
        this.target = target;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", member=" + member +
                ", source=" + source +
                ", target=" + target +
                '}';
    }
}
