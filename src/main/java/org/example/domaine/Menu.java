package org.example.domaine;

import org.example.entities.Agence;
import org.example.entities.Client;
import org.example.entities.Compte;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.sound.midi.Soundbank;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Menu {

    // Création de mon EntityManager
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("banque_bdd");

    private static EntityManager em = emf.createEntityManager();

    // Préparation d'un affichage pour mon menu
    public static void aff_menu() {
        System.out.println("1 - Créer tout");
        System.out.println("2 - Créer un compte");
        System.out.println("3 - Ajouter un client à un compte");
        System.out.println("4 - Afficher les informations");
        System.out.println("5 - Exit");


    }

    // Méthode static me permettant de lancer mon menu
    public static void exo() {
        System.out.println("######### TP BANQUE #########");
       // em.getTransaction().begin();
        Scanner sc = new Scanner(System.in);
        int choix = 0;
        do {
            try{
                // afficheAll();
                aff_menu(); // Utilisation de l'affichage pour mon menu
                choix = sc.nextInt();
                switch (choix) {
                    case 1 :
                        creation(); // Méthode pour créer une agence, un compte et un client
                        break;
                    case 2 :
                        create_compte(); // Méthode pour créer un compte (dans une agence existante et pour un client existant)
                        break;
                    case 3 :
                        addClient(); // Rajout d'un titulaire (client existant) pour un compte existant
                        break;
                    case 4 :
                        // em.getTransaction().commit();
                        afficheAll();  // Affichage des informations
                        // em.getTransaction().begin();
                        break;
                    case 5 :
                        System.out.println("Aurevoir");
                        //  em.getTransaction().commit();
                        em.close();
                        emf.close();
                        break;
                    default:
                        System.out.println("Commande invalide - Réessayer");
                        break;
                }

            }catch (InputMismatchException ex) {    // Prise en compte si l'utilisateur tape autre chose qu'un entier
                System.out.println("Veuillez saisir un entier");
                aff_menu();
            }

        } while(choix != 5); // Pour quitter mon menu l'utilisateur doit taper 5
    }

    public static  void create_compte() {
        em.getTransaction().begin();  // Début de ma transaction
        // Récupération des informations par ID (client et agence)
        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez l'ID du client : ");
        Long id_c = sc.nextLong();
        Client client = em.find(Client.class, id_c);
        System.out.println("Entrez l'ID de l'agence : ");
        Long id_a = sc.nextLong();
        Agence agence = em.find(Agence.class, id_a);
        // Début de création d'un nouveau compte
        System.out.println("Entrez le libellé du compte : ");
        String libelle = sc.next();
        System.out.println("Entrez l'IBAN : ");
        String iban = sc.next();
        System.out.println("Entrez le solde du compte : ");
        double solde = sc.nextDouble();
        Compte compte = new Compte();
        compte.setLibelle(libelle);
        compte.setIban(iban);
        compte.setSolde(solde);
        // Ajout de ce compte à l'agence sélectionnée, Ajout du client sélectionné comme titulaire du compte
        compte.setAgence(agence);
        List<Client> listecl = new ArrayList<>();
        listecl.add(client);
        compte.setClients(listecl);
        //compte.getClients().add(client);
        //client.getComptes().add(compte);
        List<Compte> malistecmp = client.getComptes();
        malistecmp.add(compte);
        client.setComptes(malistecmp);
        em.persist(compte);
        // Commit de ma transaction
        em.getTransaction().commit();
    }

    public static void addClient(){
        em.getTransaction().begin(); // Début de ma transaction
        // Récupération du compte et du client par ID
        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez l'ID du compte : ");
        Long id_a = sc.nextLong();
        Compte compte = em.find(Compte.class, id_a);
        System.out.println("Entrez l'ID du client : ");
        Long id_b = sc.nextLong();
        Client client = em.find(Client.class, id_b);
        // Ajout du client sélectionné à la liste des titulaires du compte
        List<Client> newliste = compte.getClients();
        newliste.add(client);
        compte.setClients(newliste);
        em.persist(compte);
        List<Compte> comptes = client.getComptes();
        comptes.add(compte);
        client.setComptes(comptes);
        em.persist(client);
        // Commit de ma transaction
        em.getTransaction().commit();
    }


    public static void afficheAll() {
        em.getTransaction().begin(); // Début de ma transaction
        System.out.println("######### Affichage des informations #########");
        Query query = em.createQuery("select a from Agence a");
        List<Agence> agences = query.getResultList();
        for(Agence a : agences) {
            System.out.println("####################");
            System.out.println("Agence avec l'ID : "+a.getId()+"à l'adresse : "+a.getAdresse());
            for(Compte c : a.getComptes()) {
                System.out.println("\tCompte avec l'ID : "+c.getId()+" libellé : "+c.getLibelle()+" solde : "+c.getSolde());
                System.out.println("\t\tTitulaire(s) du compte : ");
                for(Client cl : c.getClients()) {
                    System.out.println("\t\tClient avec l'ID : "+cl.getId()+" nom : "+cl.getNom()+" prénom : "+cl.getPrenom());
                }
            }
            System.out.println("####################");
        }
        em.getTransaction().commit(); // Commit de ma transaction
    }



    public static void creation() {
        em.getTransaction().begin(); // Début de ma transaction
        Scanner sc = new Scanner(System.in);
        // Création de mon agence
        System.out.println("Entrez l'adresse de l'agence");
        String adresse = sc.nextLine();
        Agence agence = new Agence();
        agence.setAdresse(adresse);
        em.persist(agence);
        // Création de mon client
        Client client = new Client();
        System.out.println("Entrez le nom");
        String nom = sc.next();
        System.out.println("Entrez le prénom");
        String prenom = sc.next();
        System.out.println("Entrez la date de naissance (format YYYY-MM-DD)");
        String date_s = sc.next();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(date_s);
        }catch(ParseException e) {
            throw new RuntimeException(e);
        }
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setDateDeNaissance(date);
        em.persist(client);
        // Création de mon compte
        System.out.println("Entrez le libellé du compte : ");
        String libelle = sc.next();
        System.out.println("Entrez l'IBAN");
        String iban = sc.next();
        System.out.println("Entrez le solde du compte : ");
        double solde = sc.nextDouble();
        Compte compte = new Compte();
        compte.setLibelle(libelle);
        compte.setIban(iban);
        compte.setSolde(solde);
        // Relation entre Agence, Compte et Client créés
        compte.setAgence(agence);
        List<Client> malistedeclient = new ArrayList<>();
        malistedeclient.add(client);
        //compte.getClients().add(client);
        compte.setClients(malistedeclient);
        List<Compte> malistedecompte = new ArrayList<>();
        malistedecompte.add(compte);
        client.setComptes(malistedecompte);
        //client.getComptes().add(compte);
        em.persist(compte);
        List<Compte> listecmpagence = new ArrayList<>();
        listecmpagence.add(compte);
        agence.setComptes(listecmpagence);
        em.persist(agence);
        em.getTransaction().commit(); // Commit de ma transaction
    }
}
