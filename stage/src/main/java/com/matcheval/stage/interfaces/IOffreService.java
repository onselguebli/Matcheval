package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.OffreEmploiDTO;
import com.matcheval.stage.dto.OffreWithCandidaturesDTO;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.model.SiteExterne;
import com.matcheval.stage.model.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOffreService {
    boolean diffuserOffre(OffreEmploi offre, SiteExterne site);
    OffreEmploi publierEtDiffuserOffre(OffreEmploi offre, Users recruteur);
    void extraireCandidaturesDepuisSitesExternes(OffreEmploi offre);
    ResponseEntity<List<OffreWithCandidaturesDTO>> getOffresWithCandidatures(String recruteurEmail);
    List<OffreEmploiDTO> getOffresByRecruteurEmail(String email);
    OffreEmploiDTO modifierOffreEtSynchroniser(Long id, OffreEmploiDTO dto);

}
