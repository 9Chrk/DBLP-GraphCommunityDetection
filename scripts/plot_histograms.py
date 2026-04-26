from pathlib import Path
import math
import re

import matplotlib.pyplot as plt


PROJECT_ROOT = Path(__file__).resolve().parents[1]
RESULTS_DIR = PROJECT_ROOT / "results"

TASKS = (
    (
        "task1_aggregated",
        RESULTS_DIR / "task1" / "community_size_histogram.csv",
        RESULTS_DIR / "task1" / "community_size_histogram_aggregated",
        "Histogramme des tailles des communautés - Tâche 1",
        True,
        False,
        "Taille des communautés (classes logarithmiques)",
    ),
    (
        "task1_log_scale",
        RESULTS_DIR / "task1" / "community_size_histogram.csv",
        RESULTS_DIR / "task1" / "community_size_histogram_log_scale",
        "Histogramme des tailles des communautés - Tâche 1",
        False,
        True,
        "Taille des communautés",
    ),
    (
        "task2",
        RESULTS_DIR / "task2" / "task2_community_sizes.csv",
        RESULTS_DIR / "task2" / "task2_community_sizes",
        "Histogramme des tailles des communautés - Tâche 2",
        False,
        False,
        "Taille des communautés",
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


def aggregate_log_bins(sizes, counts):
    bins = {}

    for size, count in zip(sizes, counts):
        if size <= 0:
            continue

        bin_index = int(math.log2(size))
        bins[bin_index] = bins.get(bin_index, 0) + count

    labels = []
    aggregated_counts = []

    for bin_index in sorted(bins):
        start = 2 ** bin_index
        end = (2 ** (bin_index + 1)) - 1

        if start == end:
            label = str(start)
        else:
            label = f"{start}-{end}"

        labels.append(label)
        aggregated_counts.append(bins[bin_index])

    return labels, aggregated_counts


def plot_histogram(task_name, csv_path, output_base, title, use_log_bins, use_log_xscale, xlabel):
    if not csv_path.exists():
        print(f"Fichier introuvable, ignoré: {csv_path}")
        return

    sizes, counts = read_histogram(csv_path)
    if not sizes:
        print(f"Aucune donnée exploitable dans: {csv_path}")
        return

    plt.figure(figsize=(10, 6))

    if use_log_bins:
        labels, plot_counts = aggregate_log_bins(sizes, counts)
        plt.bar(labels, plot_counts)
        plt.xticks(rotation=45, ha="right")
    else:
        plt.bar(sizes, counts)

    plt.xlabel(xlabel)

    if use_log_xscale:
        plt.xscale("log")

    plt.ylabel("Nombre de communautés")
    plt.yscale("log")
    plt.title(title)
    plt.tight_layout()
    plt.savefig(f"{output_base}.png", dpi=300)
    plt.close()


for task_name, input_path, output_base, title, use_log_bins, use_log_xscale, xlabel in TASKS:
    plot_histogram(task_name, input_path, output_base, title, use_log_bins, use_log_xscale, xlabel)