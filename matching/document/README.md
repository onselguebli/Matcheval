# CV Keyword Extraction & Matching API

Microservice to extract keywords from a CV (PDF) and match it with a job description (text).
Built with FastAPI + KeyBERT + Sentence-Transformers.

## Run locally

### 1) Python (no Docker)
```
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8080
```
Open Swagger UI: http://localhost:8080/docs

### 2) Docker
```
docker build -t cv-matching-api .
docker run -p 8080:8080 cv-matching-api
```

## API Endpoints

- GET /health
- POST /extract-keywords (multipart: file, lang_stopwords?, top_n?)
- POST /match (multipart: file, job_text, lang_stopwords?, top_n?)


## Optimized Build (This package)
- Multilingual matching model: `paraphrase-multilingual-MiniLM-L12-v2`
- Keyword extraction with KeyBERT (`all-MiniLM-L6-v2`)
- OCR fallback via Tesseract + pdf2image
- Preloaded models on startup
- LRU cache for embeddings
- Better error handling

### Run locally
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000

### Docker
docker build -t cv-matching-api:latest .
docker run --rm -p 8000:8000 cv-matching-api:latest

### Test (curl)
curl -X POST "http://localhost:8000/extract" -F "file=@sample_cv.pdf" -F "top_n=10"
curl -X POST "http://localhost:8000/match" -F "file=@sample_cv.pdf" -F "job_description=Data Scientist with NLP and Python"

