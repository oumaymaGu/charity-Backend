    package tn.example.charity.Entity;


    import com.fasterxml.jackson.annotation.JsonIgnore;
    import lombok.*;

    import javax.persistence.*;

    @Entity
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public class Logestique {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long idlogestique;
        private String ressourceName;
        private float quantity;
        @ManyToOne
        @JoinColumn(name = "event_id")
        @JsonIgnore
        private Event event;



    }
