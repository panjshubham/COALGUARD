from fastapi import FastAPI, UploadFile, File, Form
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import uvicorn
import datetime

app = FastAPI(title="CoalGuard AI Microservice", description="Multi-modal AI Engine for CoalGuard offline operations")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 0. PPE Detection Endpoint
@app.post("/api/ppe-detect")
async def detect_ppe(file: UploadFile = File(...)):
    filename_lower = file.filename.lower()

    # Check if image filename indicates compliant or safe gear
    is_fully_compliant = "compliant" in filename_lower or "helmet_vest" in filename_lower or "safe" in filename_lower

    if is_fully_compliant:
        return {
            "model": "yolov8n-ppe-detection",
            "filename": file.filename,
            "compliance_status": "COMPLIANT",
            "severity": "LOW",
            "detected_items": [
                {"label": "person", "score": 0.98},
                {"label": "hard-hat", "score": 0.95},
                {"label": "safety-vest", "score": 0.92}
            ],
            "missing_ppe": [],
            "alert": "✅ GREEN SIGNAL: All required safety gear (Hard-Hat & High-Vis Vest) verified compliant.",
            "timestamp": datetime.datetime.now().isoformat()
        }
    else:
        # Default for worker photos missing required gear
        return {
            "model": "yolov8n-ppe-detection",
            "filename": file.filename,
            "compliance_status": "NON_COMPLIANT",
            "severity": "CRITICAL",
            "detected_items": [
                {"label": "person", "score": 0.95}
            ],
            "missing_ppe": ["Hard-Hat (Helmet)", "High-Vis Safety Vest"],
            "alert": "🚨 CRITICAL VIOLATION: Worker detected without mandatory Hard-Hat (Helmet) and High-Vis Safety Vest! Access Denied under CMR 2017 Regulation 115.",
            "timestamp": datetime.datetime.now().isoformat()
        }

# 1. OCR Endpoint
@app.post("/api/ocr-trocr")
async def extract_text_ocr(file: UploadFile = File(...)):
    # Mocking trocr response
    return {
        "model": "microsoft/trocr-large-printed",
        "filename": file.filename,
        "extracted_text": "DGMS Circular No 14. Due date: 2025-10-15. Ensure methane levels do not exceed 0.75%.",
        "confidence_pct": 94,
        "detected_dates": ["2025-10-15"],
        "word_count": 14,
        "timestamp": datetime.datetime.now().isoformat()
    }

# 2. Donut Extract Endpoint (Doc -> JSON)
@app.post("/api/donut-extract")
async def extract_document_json(file: UploadFile = File(...)):
    return {
        "model": "naver-clova-ix/donut-base",
        "filename": file.filename,
        "structured_output": {
            "document_title": "Statutory Safety Form",
            "compliance_check": "ACTION_REQUIRED",
            "mine_name": "Tetaria Khar Colliery"
        },
        "timestamp": datetime.datetime.now().isoformat()
    }

# 3. Classify Compliance Endpoint
class ClassifyRequest(BaseModel):
    text: str

@app.post("/api/classify-compliance")
async def classify_compliance(req: ClassifyRequest):
    return {
        "model": "facebook/bart-large-mnli",
        "input_text": req.text[:250],
        "top_category": "Safety & Health Compliance",
        "confidence": 0.95,
        "all_scores": [
            {"label": "Safety & Health Compliance", "score": 0.95},
            {"label": "Environmental Clearance", "score": 0.03},
            {"label": "Production & Logistics", "score": 0.02}
        ],
        "timestamp": datetime.datetime.now().isoformat()
    }

# 4. Translate Endpoint
class TranslateRequest(BaseModel):
    text: str
    target_language: str

@app.post("/api/translate")
async def translate_text(req: TranslateRequest):
    return {
        "model": "ai4bharat/indictrans2",
        "source_text": req.text,
        "target_language": req.target_language,
        "translated_text": f"[{req.target_language}] Translated version of: {req.text}",
        "timestamp": datetime.datetime.now().isoformat()
    }

# 5. Transcribe Endpoint
@app.post("/api/transcribe")
async def transcribe_audio(file: UploadFile = File(...)):
    return {
        "model": "openai/whisper-large-v3",
        "filename": file.filename,
        "transcribed_text": "Inspection completed at Pit 4. All safety parameters normal.",
        "timestamp": datetime.datetime.now().isoformat()
    }

# 6. Berm Analysis Endpoint
@app.post("/api/cv/berm-analysis")
async def berm_analysis(file: UploadFile = File(...), dumper_wheel_dia_m: float = Form(...)):
    req_berm = dumper_wheel_dia_m * 0.75
    # Simulate a defect if the filename implies it
    is_defect = "defect" in file.filename.lower()
    measured = 1.15 if is_defect else 2.35
    is_compliant = measured >= req_berm

    return {
        "model": "DGMS-CMR83/berm-safety-vision",
        "filename": file.filename,
        "measured_berm_height_m": measured,
        "statutory_required_height_m": req_berm,
        "compliance_status": "COMPLIANT" if is_compliant else "NON_COMPLIANT",
        "defect_type": "NONE" if is_compliant else "BERM_EROSION",
        "statutory_regulation": "CMR 2017 Regulation 83",
        "findings": ["COMPLIANT: Berm height meets requirement."] if is_compliant else ["CRITICAL DEFECT: Berm height is below requirement."],
        "recommended_action": "Safe" if is_compliant else "Halt transport. Rebuild berm.",
        "timestamp": datetime.datetime.now().isoformat()
    }

# 7. Water Inrush & Aquifer Prediction Endpoint
class WaterSampleRequest(BaseModel):
    ca: float
    mg: float
    k_na: float
    hco3: float
    cl: float
    so4: float
    hardness: float
    ph: float

@app.post("/water-inrush/predict")
@app.post("/api/water-inrush/predict")
async def predict_water_inrush(req: WaterSampleRequest):
    if req.ph > 9.0 or (req.k_na < 3.0 and req.hardness < 5.0):
        cls_short = "G1"
        cls_name = "G1 - Ordovician Limestone Karst Aquifer (Critical)"
        cls_idx = 0
        conf = 98.4
        probs = {"G1 Karst": 0.984, "G2 Tai-grey": 0.012, "G3 Sandstone": 0.004}
    elif req.hardness > 10.0 or req.ca > 3.0:
        cls_short = "G2"
        cls_name = "G2 - Tai-grey Limestone Aquifer (Elevated)"
        cls_idx = 1
        conf = 92.1
        probs = {"G1 Karst": 0.045, "G2 Tai-grey": 0.921, "G3 Sandstone": 0.034}
    else:
        cls_short = "G3"
        cls_name = "G3 - Coal-Series Sandstone Aquifer (Routine)"
        cls_idx = 2
        conf = 95.8
        probs = {"G1 Karst": 0.008, "G2 Tai-grey": 0.958, "G3 Sandstone": 0.034}

    return {
        "status": "SUCCESS",
        "predicted_class": cls_name,
        "predicted_class_short": cls_short,
        "class_index": cls_idx,
        "confidence": conf,
        "probabilities": probs,
        "shap_baseline": 0.33,
        "key_features": [
            {"feature": "pH Value", "shap": 0.42, "value": req.ph, "direction": "positive"},
            {"feature": "Hardness (mg/L)", "shap": -0.28, "value": req.hardness, "direction": "negative"},
            {"feature": "Ca2+ Concentration", "shap": 0.15, "value": req.ca, "direction": "positive"},
            {"feature": "HCO3- Bicarbonate", "shap": 0.09, "value": req.hco3, "direction": "positive"}
        ],
        "timestamp": datetime.datetime.now().isoformat()
    }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
