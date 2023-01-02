package org.example.entities;


import javax.persistence.*;
import java.util.List;

@Entity
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adresse;

    @OneToMany(mappedBy = "agence")
    private List<Compte> comptes;

    // Getter and Setter

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdresse() {

        return adresse;
    }

    public void setAdresse(String adresse) {

        this.adresse = adresse;
    }

    public List<Compte> getComptes() {

        return comptes;
    }

    public void setComptes(List<Compte> comptes) {

        this.comptes = comptes;
    }
}
