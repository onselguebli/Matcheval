package com.matcheval.stage.interfaces;

import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.model.SiteExterne;
import com.matcheval.stage.model.Users;

import java.util.List;

public interface IOffreService {
    boolean diffuserOffre(OffreEmploi offre, SiteExterne site);
    public OffreEmploi publierEtDiffuserOffre(OffreEmploi offre, Users recruteur);
    public void extraireCandidaturesDepuisSitesExternes(OffreEmploi offre);

}
