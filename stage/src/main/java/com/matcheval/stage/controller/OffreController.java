package com.matcheval.stage.controller;

import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.model.Users;
import com.matcheval.stage.repo.OffreRepo;
import com.matcheval.stage.repo.UserRepo;
import com.matcheval.stage.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("recruiter")
public class OffreController {

    @Autowired
    private OffreService offreService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    OffreRepo offreRepo;

    @PostMapping("/offre/publier")
    public OffreEmploi publierOffre(@RequestBody OffreEmploi offer, Principal principal) {
        String email = principal.getName();
        Users recruteur = userRepo.findByEmail(email);
        if (recruteur == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return offreService.publierEtDiffuserOffre(offer, recruteur);
    }
    @GetMapping("/importer/{offreId}")
    public ResponseEntity<String> importerCandidatures(@PathVariable Long offreId) {
        OffreEmploi offre = offreRepo.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        offreService.extraireCandidaturesDepuisSitesExternes(offre);
        return ResponseEntity.ok("Importation des candidatures lancée pour l'offre ID " + offreId);
    }
    @PutMapping("/offre/{id}")
    public ResponseEntity<OffreEmploi> modifierOffre(
            @PathVariable Long id,
            @RequestBody OffreEmploi dto) {
        OffreEmploi updated = offreService.modifierOffreEtSynchroniser(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/offresByrecruteur/{email}")
    public ResponseEntity<List<OffreEmploi>> getOffresByRecruteurEmail(@PathVariable String email) {
            List<OffreEmploi> offres = offreService.getOffresByRecruteurEmail(email);
            return ResponseEntity.ok(offres);
    }

}

