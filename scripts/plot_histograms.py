from pathlib import Path
import re

import matplotlib.pyplot as plt


PROJECT_ROOT = Path(__file__).resolve().parents[1]
RESULTS_DIR = PROJECT_ROOT / "results"

TASKS = (
    (
        "task1",
        RESULTS_DIR / "task1" / "community_size_histogram.csv",
        RESULTS_DIR / "task1" / "community_size_histogram",
        "Histogramme des tailles des communautés - Tâche 1",
    ),
    (
        "task2",
        RESULTS_DIR / "task2" / "task2_community_sizes.csv",
        RESULTS_DIR / "task2" / "task2_community_sizes",
        "Histogramme des tailles des communautés - Tâche 2",
    ),
)


def read_histogram(csv_path):
    sizes = []
    counts = []

    with csv_path.open(encoding="utf-8") as file_handle:
        lines = [line.strip() for line in file_handle if line.strip()]

    if not lines:
        return sizes, counts

    for row in lines[1:]:
        fields = [field.strip() for field in re.split(r"[;,]", row)]
        if len(fields) < 2:
            continue
        sizes.append(int(fields[0]))
        counts.append(int(fields[1]))

    return sizes, counts


def plot_histogram(csv_path, output_base, title):
    if not csv_path.exists():
        print(f"Fichier introuvable, ignoré: {csv_path}")
        return

    sizes, counts = read_histogram(csv_path)
    if not sizes:
        print(f"Aucune donnée exploitable dans: {csv_path}")
        return

    plt.figure(figsize=(10, 6))
    plt.bar(sizes, counts)
    plt.xlabel("Taille des communautés")
    plt.ylabel("Nombre de communautés")
    plt.title(title)
    plt.tight_layout()
    plt.savefig(f"{output_base}.png", dpi=300)
    plt.savefig(f"{output_base}.pdf")
    plt.close()


for _, input_path, output_base, title in TASKS:
    plot_histogram(input_path, output_base, title)