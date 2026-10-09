import os
import requests
from flask import Flask, request, jsonify

app = Flask(__name__)

API_KEY = os.environ.get("OPENAI_API_KEY")

@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json() or {}
    message = data.get("message", "").strip()

    if not message:
        return jsonify({"error": "message is required"}), 400

    if not API_KEY:
        return jsonify({"error": "OPENAI_API_KEY is not configured"}), 500

    response = requests.post(
        "https://api.openai.com/v1/responses",
        headers={
            "Authorization": "Bearer " + API_KEY,
            "Content-Type": "application/json; charset=utf-8"
        },
        json={
            "model": "gpt-5-mini",
            "input": message
        },
        timeout=60
    )

    return jsonify(response.json()), response.status_code

if __name__ == "__main__":
    app.run(host="127.0.0.1", port=5000)
