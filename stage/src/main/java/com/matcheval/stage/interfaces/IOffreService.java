package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.OffreWithCandidaturesDTO;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.model.SiteExterne;
import com.matcheval.stage.model.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOffreService {
    boolean diffuserOffre(OffreEmploi offre, SiteExterne site);
    public OffreEmploi publierEtDiffuserOffre(OffreEmploi offre, Users recruteur);
    public void extraireCandidaturesDepuisSitesExternes(OffreEmploi offre);
    ResponseEntity<List<OffreWithCandidaturesDTO>> getOffresWithCandidatures(String recruteurEmail);
    public OffreEmploi modifierOffreEtSynchroniser(Long id, OffreEmploi updatedData);
    public List<OffreEmploi> getOffresByRecruteurEmail(String email);
}
