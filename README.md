# DBLP Community Analysis
![Java](https://img.shields.io/badge/Language-Java-F4C27A?style=flat-square&logo=openjdk&logoColor=3A2E2A)
![Apache Maven](https://img.shields.io/badge/Build-Apache%20Maven-E8B4BC?style=flat-square&logo=apache-maven&logoColor=4A1F2B)
![License: MIT](https://img.shields.io/badge/License-MIT-B7E4C7?style=flat-square&logoColor=1F3D2E)


A Java project for streaming analysis of DBLP collaboration networks, including connected-component detection on undirected co-authorship graphs and strongly connected community analysis on filtered directed collaboration graphs.

> **Course:** Algorithmique 2 (INFO-F203) — BA2, Q2 — Université Libre de Bruxelles  
> **Academic year:** 2025–2026


---

## Overview

This project processes the [DBLP](https://dblp.org/) XML dataset in **streaming** fashion (SAX/StAX) to avoid loading the full ~4 GB dump into memory.  
Two independent graph analysis tasks are implemented:

| Task | Graph type | Problem |
|------|-----------|---------|
| Task 1 | Undirected co-authorship graph | Find all connected components |
| Task 2 | Filtered directed collaboration graph | Find all strongly connected components (communities) |

---

## Features

- ✅ Streaming XML parser (no full-DOM load)
- ✅ Undirected graph construction from DBLP co-authorship data
- ✅ Connected-component detection (Task 1)
- ✅ Directed graph construction with configurable filters
- ✅ Strongly connected component detection (Task 2)
- ✅ Results written to `results/task1/` and `results/task2/`
- ✅ JUnit 5 unit tests

---

## 📂 Project Structure

```
dblp-graph-community-detection/
├── pom.xml
├── README.md
├── .gitignore
├── LICENSE
├── docs/
│   ├── report/              ← PDF report
│   └── subject/
│       └── Projet_Algorithmique_2_2026.pdf
├── data/
│   ├── README.md            ← how to obtain the dataset
│   └── external/            ← place dblp.xml.gz + dblp.dtd here (git-ignored)
├── results/
│   ├── task1/               ← output files for Task 1
│   └── task2/               ← output files for Task 2
└── src/
    ├── main/java/be/ulb/dblp/
    │   ├── Main.java         ← entry point
    │   ├── io/               ← file reading / writing helpers
    │   ├── model/            ← Graph, Node, Edge domain classes
    │   ├── parsing/          ← DBLP XML SAX/StAX parser
    │   ├── task1/            ← Task 1 algorithm & runner
    │   ├── task2/            ← Task 2 algorithm & runner
    │   └── util/             ← shared helpers (timing, formatting, …)
    └── test/java/be/ulb/dblp/
```

---

## ⚙️ Algorithms Used

| Algorithm | Package | Time complexity | Space complexity |
|-----------|---------|----------------|-----------------|
| BFS / DFS (connected components) | `task1` | O(V + E) | O(V) |
| Union-Find (optional, Task 1) | `util` | O(α(V) · E) | O(V) |
| Kosaraju / Tarjan (SCC) | `task2` | O(V + E) | O(V) |

> Exact choices and complexity proofs are detailed in the report (`docs/report/`).

---

## 🛠️ Build and Run

### Prerequisites

- Java 17+
- Maven 3.8+

### Build

```bash
mvn clean package
```

This produces `target/dblp-community-analysis.jar` (executable fat-jar).

### Run

```bash
java -jar target/dblp-community-analysis.jar \
     data/external/dblp.xml.gz \
     data/external/dblp.dtd
```

### Run via Maven (without building the jar first)

```bash
mvn exec:java -Dexec.args="data/external/dblp.xml.gz data/external/dblp.dtd"
```

---

## 💾 Input Data

| File | Description |
|------|-------------|
| `dblp.xml.gz` | Full DBLP XML dump (gzip-compressed, ~4 GB uncompressed) |
| `dblp.dtd` | DTD required by the SAX parser |

Download the latest snapshot from <https://dblp.org/xml/> and place both files in `data/external/`.  
See [`data/README.md`](data/README.md) for details.

---

## �? Output Files

| Path | Content |
|------|---------|
| `results/task1/components.txt` | One connected component per line (list of author IDs) |
| `results/task1/stats.txt` | Summary statistics (number of components, sizes, …) |
| `results/task2/scc.txt` | One strongly connected component per line |
| `results/task2/stats.txt` | Summary statistics |

just an example!!!

---

## Testing

```bash
mvn test
```

Unit tests are located under `src/test/java/be/ulb/dblp/`.

---

## Report

The report is available at [`docs/report/rapport.pdf`](docs/report/rapport.pdf), it details the implementation choices and the complexity of the algorithms used.

---

## Complexity Notes

The streaming approach ensures that memory usage is bounded by the size of the graph data structures, not the raw XML.  
Full complexity analysis (time and space) for each algorithm is provided in the project report.
