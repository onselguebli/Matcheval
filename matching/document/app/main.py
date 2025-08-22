# app/main.py
from __future__ import annotations
from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
import io
import re
import json

# --- PDF -> texte (avec fallback OCR optionnel) ---
from pypdf import PdfReader

def _extract_text_pdf(file_bytes: bytes) -> str:
    try:
        reader = PdfReader(io.BytesIO(file_bytes))
        pages = [p.extract_text() or "" for p in reader.pages]
        return "\n".join(pages).strip()
    except Exception:
        return ""

def _extract_text_ocr(file_bytes: bytes) -> str:
    # OCR est optionnel: si pdf2image/pytesseract ne sont pas installés, on ignore
    try:
        from pdf2image import convert_from_bytes
        import pytesseract
        images = convert_from_bytes(file_bytes)
        ocr_texts = [pytesseract.image_to_string(img) for img in images]
        return "\n".join(ocr_texts).strip()
    except Exception:
        return ""

def extract_text_with_fallback(file_bytes: bytes) -> str:
    text = _extract_text_pdf(file_bytes)
    if text and len(text.split()) >= 20:
        return text
    ocr = _extract_text_ocr(file_bytes)
    return ocr if ocr else text

# --- Langue & embeddings ---
try:
    from langdetect import detect
except Exception:
    detect = None  # on gère l'absence proprement

from sentence_transformers import SentenceTransformer, util

_embedder: Optional[SentenceTransformer] = None
def get_embedder() -> SentenceTransformer:
    global _embedder
    if _embedder is None:
        # Multilingue FR/EN, bon compromis perf/qualité
        _embedder = SentenceTransformer("paraphrase-multilingual-MiniLM-L12-v2")
    return _embedder

def text_similarity(a: str, b: str) -> float:
    if not a.strip() or not b.strip():
        return 0.0
    model = get_embedder()
    emb = model.encode([a, b], convert_to_tensor=True, normalize_embeddings=True)
    sim = util.cos_sim(emb[0], emb[1]).cpu().item()
    return float(max(0.0, min(1.0, sim)))

# --- Banque de compétences (simple, extensible) ---
SKILL_BANK = {
    # data / eng
    "python","pandas","numpy","scikit-learn","sklearn","pytorch","tensorflow","keras",
    "sql","spark","hadoop","airflow","docker","kubernetes","git","linux","bash",
    "power bi","tableau","excel","etl","api","rest","fastapi","flask","django",
    "ml","machine learning","deep learning","nlp","transformers","opencv",
    "gcp","aws","azure","bigquery","redshift","s3","emr","databricks",
    # fr
    "apprentissage automatique","apprentissage profond","traitement du langage naturel",
    "visualisation", "ingénierie des données", "modélisation",
    # soft data terms
    "feature engineering","mle","mlops","time series","recommendation","classification","clustering","regression",
}

LANG_BANK = {
    "english","anglais","french","français","francais","arabic","arabe","german","allemand","italian","italien","spanish","espagnol"
}

# --- Extraction basique depuis texte CV (regex + lookup) ---
EMAIL_RE = re.compile(r"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}")
PHONE_RE = re.compile(r"(\+?\d[\d\s\-()]{7,})")
YEAR_EXPERIENCE_RE = re.compile(r"(\d+)\s*(?:ans|years|year|année|annees|années)", re.IGNORECASE)

def detect_language_safe(text: str) -> Optional[str]:
    try:
        if detect is None:
            return None
        return detect(text)
    except Exception:
        return None

def normalize(s: str) -> str:
    return re.sub(r"\s+", " ", s.strip().lower())

def extract_skills(text: str) -> List[str]:
    t = normalize(text)
    found = set()
    # multi-words d’abord
    for sk in sorted(SKILL_BANK, key=lambda x: -len(x)):
        if sk in t:
            found.add(sk)
    # présence de versions FR/EN proches
    if "scikit learn" in t or "scikitlearn" in t:
        found.add("scikit-learn")
    return sorted(found)

def extract_languages(text: str) -> List[str]:
    t = normalize(text)
    found = set()
    for lg in LANG_BANK:
        if lg in t:
            # normaliser label
            if lg in {"anglais","english"}: found.add("english")
            elif lg in {"français","francais","french"}: found.add("french")
            elif lg in {"arabe","arabic"}: found.add("arabic")
            elif lg in {"allemand","german"}: found.add("german")
            elif lg in {"italien","italian"}: found.add("italian")
            elif lg in {"espagnol","spanish"}: found.add("spanish")
    return sorted(found)

def extract_years_of_experience(text: str) -> Optional[float]:
    # Heuristique simple: max des nombres suivis de "ans/years"
    years = []
    for m in YEAR_EXPERIENCE_RE.finditer(text):
        try:
            years.append(int(m.group(1)))
        except Exception:
            pass
    return float(max(years)) if years else None

def guess_location(text: str) -> Optional[str]:
    # très simple; à adapter à ta donnée
    t = normalize(text)
    candidates = [
        "tunis","sousse","sfax","monastir","nabeul","paris","lyon","marseille",
        "remote","hybrid","on-site","onsite","tunisia","france","montréal","montreal","london"
    ]
    hits = [c for c in candidates if c in t]
    return hits[0] if hits else None

# --- Pydantic Schemas ---
class JobPosting(BaseModel):
    title: str = Field(..., description="Titre du poste")
    description: str = Field(..., description="Description complète")
    skills_required: List[str] = Field(default_factory=list, description="Compétences requises")
    min_years_experience: Optional[float] = Field(None, description="Années d'expérience minimales")
    location: Optional[str] = Field(None, description="Localisation souhaitée (ex: Paris, Remote)")
    languages_required: List[str] = Field(default_factory=list, description="Langues requises (english, french, ...)")

class ExtractedCV(BaseModel):
    filename: Optional[str] = None
    detected_language: Optional[str] = None
    name: Optional[str] = None  # (à compléter si tu veux un NER)
    email: Optional[str] = None
    phone: Optional[str] = None
    skills: List[str] = []
    languages: List[str] = []
    years_experience: Optional[float] = None
    location: Optional[str] = None
    raw_text_chars: int = 0

class MatchCriteriaScores(BaseModel):
    title_match: float
    skills_match: float
    experience_match: float
    location_match: float
    languages_match: float

class MatchResult(BaseModel):
    filename: str
    score_overall: float
    criteria: MatchCriteriaScores
    missing_skills: List[str]
    extra_skills: List[str]
    cv_extracted: ExtractedCV

class BatchMatchResponse(BaseModel):
    job_received: JobPosting
    results: List[MatchResult]

# --- FastAPI App ---
app = FastAPI(
    title="CV Matching API (Final)",
    version="2.0.0",
    description="Extraction + Matching multi-critères (titre, compétences, expérience, localisation, langues) sur plusieurs CVs."
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], allow_credentials=True,
    allow_methods=["*"], allow_headers=["*"]
)

@app.get("/health")
def health():
    return {"status": "ok"}

# --------- Extraction brute d’un CV (pour inspection) ----------
@app.post("/extract", response_model=ExtractedCV)
async def extract_endpoint(file: UploadFile = File(...)):
    if file.content_type not in ("application/pdf","application/octet-stream","application/x-pdf"):
        raise HTTPException(status_code=415, detail="Please upload a PDF.")
    raw = await file.read()
    text = extract_text_with_fallback(raw)
    if not text or len(text.strip()) < 20:
        raise HTTPException(status_code=422, detail="PDF contains no readable text.")

    lang = detect_language_safe(text)
    emails = EMAIL_RE.findall(text)
    phones = PHONE_RE.findall(text)
    skills = extract_skills(text)
    languages = extract_languages(text)
    years = extract_years_of_experience(text)
    loc = guess_location(text)

    return ExtractedCV(
        filename=file.filename,
        detected_language=lang,
        email=emails[0] if emails else None,
        phone=phones[0] if phones else None,
        skills=skills,
        languages=languages,
        years_experience=years,
        location=loc,
        raw_text_chars=len(text)
    )

# --------- Matching multi-CV contre 1 offre ----------
@app.post("/match-multiple", response_model=BatchMatchResponse)
async def match_multiple_endpoint(
    files: List[UploadFile] = File(..., description="Plusieurs CVs (PDF)"),
    job_json: str = Form(..., description="Objet JSON JobPosting")
):
    """
    Exemple job_json:
    {
      "title": "Data Scientist",
      "description": "We need a DS with NLP, Python, SQL ...",
      "skills_required": ["python","sql","nlp","docker"],
      "min_years_experience": 2,
      "location": "Paris",
      "languages_required": ["english","french"]
    }
    """
    try:
        job = JobPosting(**json.loads(job_json))
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Invalid job_json: {e}")

    if not files:
        raise HTTPException(status_code=400, detail="No files received.")

    # embeddings du titre + description de l'offre
    job_title = job.title.strip()
    job_desc = job.description.strip()

    results: List[MatchResult] = []
    for f in files:
        if f.content_type not in ("application/pdf","application/octet-stream","application/x-pdf"):
            raise HTTPException(status_code=415, detail=f"Not a PDF: {f.filename}")

        raw = await f.read()
        text = extract_text_with_fallback(raw)
        if not text or len(text.strip()) < 20:
            # CV illisible -> score 0
            extracted = ExtractedCV(
                filename=f.filename, raw_text_chars=len(text or ""), skills=[], languages=[]
            )
            results.append(MatchResult(
                filename=f.filename,
                score_overall=0.0,
                criteria=MatchCriteriaScores(
                    title_match=0.0, skills_match=0.0, experience_match=0.0, location_match=0.0, languages_match=0.0
                ),
                missing_skills=job.skills_required,
                extra_skills=[],
                cv_extracted=extracted
            ))
            continue

        # ---- Extraction CV ----
        lang = detect_language_safe(text)
        emails = EMAIL_RE.findall(text)
        phones = PHONE_RE.findall(text)
        skills_cv = extract_skills(text)
        languages_cv = extract_languages(text)
        years_cv = extract_years_of_experience(text)
        loc_cv = guess_location(text)

        extracted = ExtractedCV(
            filename=f.filename,
            detected_language=lang,
            email=emails[0] if emails else None,
            phone=phones[0] if phones else None,
            skills=skills_cv,
            languages=languages_cv,
            years_experience=years_cv,
            location=loc_cv,
            raw_text_chars=len(text)
        )

        # ---- Scoring par critère ----
        # 1) Titre / description vs contenu CV (similarité sémantique)
        title_sim = text_similarity(job_title, text)
        # bonus si la desc match aussi
        desc_sim = text_similarity(job_desc, text)
        title_match = max(title_sim, desc_sim)

        # 2) Compétences (Jaccard simple)
        required = {normalize(s) for s in job.skills_required}
        cvset = {normalize(s) for s in skills_cv}
        inter = required.intersection(cvset)
        skills_match = (len(inter) / len(required)) if required else 1.0
        missing_skills = sorted(required - inter)
        extra_skills = sorted(cvset - required)

        # 3) Expérience
        if job.min_years_experience is None or job.min_years_experience <= 0:
            experience_match = 1.0
        else:
            if years_cv is None:
                experience_match = 0.0
            else:
                # 1.0 si >= requis, sinon proportionnel
                experience_match = max(0.0, min(1.0, years_cv / job.min_years_experience))

        # 4) Localisation (exacte ou sémantique simple)
        if not job.location:
            location_match = 1.0
        else:
            job_loc = normalize(job.location)
            cv_loc = normalize(loc_cv) if loc_cv else ""
            if not cv_loc:
                # s'il y a "remote" dans l'offre et des indices similaires dans CV -> 0.7
                if "remote" in job_loc and ("remote" in normalize(text) or "work from home" in normalize(text)):
                    location_match = 0.7
                else:
                    location_match = 0.0
            else:
                location_match = 1.0 if job_loc in cv_loc or cv_loc in job_loc else text_similarity(job_loc, cv_loc)

        # 5) Langues
        req_langs = {normalize(l) for l in job.languages_required}
        cv_langs = {normalize(l) for l in languages_cv}
        lang_inter = req_langs.intersection(cv_langs)
        languages_match = (len(lang_inter) / len(req_langs)) if req_langs else 1.0

        # ---- Aggrégation pondérée ----
        # Ajuste les poids selon tes priorités
        WEIGHTS = {
            "title": 0.25,
            "skills": 0.40,
            "experience": 0.20,
            "location": 0.10,
            "languages": 0.05,
        }
        overall = (
            WEIGHTS["title"] * title_match +
            WEIGHTS["skills"] * skills_match +
            WEIGHTS["experience"] * experience_match +
            WEIGHTS["location"] * location_match +
            WEIGHTS["languages"] * languages_match
        )

        results.append(MatchResult(
            filename=f.filename,
            score_overall=round(float(overall), 4),
            criteria=MatchCriteriaScores(
                title_match=round(title_match, 4),
                skills_match=round(skills_match, 4),
                experience_match=round(experience_match, 4),
                location_match=round(location_match, 4),
                languages_match=round(languages_match, 4),
            ),
            missing_skills=missing_skills,
            extra_skills=extra_skills,
            cv_extracted=extracted
        ))

    # trier décroissant
    results.sort(key=lambda r: r.score_overall, reverse=True)

    return BatchMatchResponse(job_received=job, results=results)
