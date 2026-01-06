package com.matcheval.stage.config;

import com.matcheval.stage.model.*;
import com.matcheval.stage.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@Configuration
public class DataSeeder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Bean
    CommandLineRunner seedData(
            UserRepo userRepo,
            OffreRepo offreRepo,
            CandidatureRepo candidatureRepo,
            CvRepo cvRepo,
            SiteExterneRepo siteExterneRepo,
            OffreSiteExterneRepo offreSiteExterneRepo
    ) {
        return args -> {

            // -------------------- USERS --------------------
            if (!userRepo.existsByEmail("admin1@matcheval.com")) {
                Users admin = new Users();
                admin.setFirstname("Admin1");
                admin.setLastname("Matcheval");
                admin.setEmail("admin1@matcheval.com");
                admin.setPassword(encoder.encode("Admin123!"));
                admin.setRole(Roles.ADMIN);
                admin.setCivility(Civility.SINGLE);
                admin.setCreatedAt(new Date());
                admin.setEnabled(true);
                admin.setLocked(false);
                userRepo.save(admin);
            }

            Users manager = userRepo.findByEmail("manager1@matcheval.com");
            if (manager == null) {
                manager = new Users();
                manager.setFirstname("Manager1");
                manager.setLastname("Matcheval");
                manager.setEmail("manager1@matcheval.com");
                manager.setPassword(encoder.encode("Manager123!"));
                manager.setRole(Roles.MANAGER);
                manager.setCivility(Civility.SINGLE);
                manager.setCreatedAt(new Date());
                manager.setEnabled(true);
                manager.setLocked(false);
                manager = userRepo.save(manager);
            }

            Users recruiter = userRepo.findByEmail("recruteur1@matcheval.com");
            if (recruiter == null) {
                recruiter = new Users();
                recruiter.setFirstname("Recruteur1");
                recruiter.setLastname("Matcheval");
                recruiter.setEmail("recruteur1@matcheval.com");
                recruiter.setPassword(encoder.encode("Recruiter123!"));
                recruiter.setRole(Roles.RECRUITER);
                recruiter.setCivility(Civility.SINGLE);
                recruiter.setCreatedAt(new Date());
                recruiter.setEnabled(true);
                recruiter.setLocked(false);
                recruiter.setManager(manager);
                recruiter = userRepo.save(recruiter);
            }

            // -------------------- OFFRE EMPLOI --------------------
            String titreOffre = "Software Engineer – Développement Web Full Stack";
            OffreEmploi offre;

            if (!offreRepo.existsByTitreAndRecruteurEmail(titreOffre, recruiter.getEmail())) {
                offre = new OffreEmploi();
                offre.setTitre(titreOffre);
                offre.setDescription("Nous recherchons un(e) Software Engineer passionné(e) pour rejoindre notre équipe.");
                offre.setExigences("Angular, Spring Boot, SQL, Docker. 1 an d'expérience. Français/Anglais.");
                offre.setLocalisation("Tunisie, Ariana");
                offre.setStatut("OUVERTE");
                offre.setDatePublication(new Date());
                offre.setDateExpiration(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // +30 jours
                offre.setTypeOffre(TypeOffre.IT); // adapte si besoin
                offre.setRecruteur(recruiter);
                offre = offreRepo.save(offre);
            } else {
                offre = offreRepo.findFirstByTitreAndRecruteurEmail(titreOffre, recruiter.getEmail());
            }

            // -------------------- CANDIDATURES + CV (resources) --------------------
            seedCandidatureWithCvFromResource(candidatureRepo, cvRepo, offre,
                    "Ons", "ElGuebli", "ons.elguebli@example.com",
                    "seed-cvs/cv (1).pdf", "cv (1).pdf"
            );

            seedCandidatureWithCvFromResource(candidatureRepo, cvRepo, offre,
                    "Ahmed", "BenAli", "ahmed.benali@example.com",
                    "seed-cvs/Cv_Ons_ElGuebli_Fr.pdf", "Cv_Ons_ElGuebli_Fr.pdf"
            );

            seedCandidatureWithCvFromResource(candidatureRepo, cvRepo, offre,
                    "Fedi", "Torkhani", "fedi.torkhani@example.com",
                    "seed-cvs/Fadi_Tarkhani-CV.pdf", "Fadi_Tarkhani-CV.pdf"
            );

            seedCandidatureWithCvFromResource(candidatureRepo, cvRepo, offre,
                    "Samira", "Derouich", "samira.derouich@example.com",
                    "seed-cvs/DEROUICH-SAMIRA-CV.pdf", "DEROUICH-SAMIRA-CV.pdf"
            );

            // -------------------- SITES EXTERNES --------------------
            SiteExterne linkedin = seedSite(siteExterneRepo, "LinkedIn", "https://api.linkedin.com", new Date());
            SiteExterne indeed   = seedSite(siteExterneRepo, "Indeed", "https://api.indeed.com", new Date());
            SiteExterne welcome  = seedSite(siteExterneRepo, "WelcomeToTheJungle", "https://api.welcometothejungle.com", new Date());

            // -------------------- OFFRE -> SITES (diffusion) --------------------
            seedOffreDiffusion(offreSiteExterneRepo, offre, linkedin, "DIFFUSEE");
            seedOffreDiffusion(offreSiteExterneRepo, offre, indeed, "EN_ATTENTE");
            seedOffreDiffusion(offreSiteExterneRepo, offre, welcome, "ECHEC"); // exemple
        };
    }

    private void seedCandidatureWithCvFromResource(
            CandidatureRepo candidatureRepo,
            CvRepo cvRepo,
            OffreEmploi offre,
            String nom,
            String prenom,
            String email,
            String classpathPdf,
            String originalFilename
    ) throws IOException {

        if (candidatureRepo.existsByOffreIdAndCandidatEmail(offre.getId(), email)) return;

        Candidature c = new Candidature();
        c.setOffre(offre);
        c.setCandidatNom(nom);
        c.setCandidatPrenom(prenom);
        c.setCandidatEmail(email);
        c.setDateSoumission(LocalDateTime.now().minusDays(1));
        c.setStatut("EN_ATTENTE");
        c.setCommentaire("Candidature seed (demo).");
        c = candidatureRepo.save(c);

        byte[] pdfBytes = new ClassPathResource(classpathPdf).getInputStream().readAllBytes();

        CV cv = new CV();
        cv.setCandidature(c);
        cv.setOriginalFilename(originalFilename);
        cv.setContentType("application/pdf");
        cv.setDateUpload(new Date());
        cv.setData(pdfBytes);
        cv.setSize((long) pdfBytes.length);
        cv.setContenuTexte(null);

        cv = cvRepo.save(cv);

        c.setCv(cv);
        candidatureRepo.save(c);
    }

    private SiteExterne seedSite(SiteExterneRepo siteRepo, String nom, String apiConfig, Date dateA) {
        if (siteRepo.existsByNom(nom)) return siteRepo.findByNom(nom);

        SiteExterne s = new SiteExterne();
        s.setNom(nom);
        s.setApiConfig(apiConfig);
        s.setDateA(dateA);
        return siteRepo.save(s);
    }

    private void seedOffreDiffusion(
            OffreSiteExterneRepo repo,
            OffreEmploi offre,
            SiteExterne site,
            String statut
    ) {
        if (repo.existsByOffreIdAndSiteExterneId(offre.getId(), site.getId())) return;

        OffreSiteExterne ose = new OffreSiteExterne();
        ose.setOffre(offre);
        ose.setSiteExterne(site);
        ose.setDateDiffusion(LocalDateTime.now());
        ose.setStatutDiffusion(statut);

        repo.save(ose);
    }
}
