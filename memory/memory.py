import json
from pathlib import Path

FILE = Path(__file__).parent / "memories.json"

def load():
    if not FILE.exists():
        return []
    return json.loads(FILE.read_text(encoding="utf-8")).get("memories", [])

def save(text, kind="general"):
    data = load()
    data.append({"text": text, "kind": kind})
    FILE.write_text(
        json.dumps({"version": 1, "memories": data},
                   ensure_ascii=False, indent=2),
        encoding="utf-8"
    )

def search(query):
    q = query.lower()
    return [m for m in load() if q in m["text"].lower()]
