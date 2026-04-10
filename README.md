# Analyse de communautes DBLP
![Java](https://img.shields.io/badge/Langage-Java-F4C27A?style=flat-square&logo=openjdk&logoColor=3A2E2A)
![Apache Maven](https://img.shields.io/badge/Compilation-Apache%20Maven-E8B4BC?style=flat-square&logo=apache-maven&logoColor=4A1F2B)
![License: MIT](https://img.shields.io/badge/Licence-MIT-B7E4C7?style=flat-square&logoColor=1F3D2E)


Un projet Java pour l'analyse en streaming des reseaux de collaboration DBLP, incluant la detection de composantes connexes sur des graphes de co-signature non orientes et l'analyse de communautes fortement connexes sur des graphes de collaboration diriges filtres.

> **Cours :** Algorithmique 2 (INFO-F203) — BA2, Q2 — Universite Libre de Bruxelles  
> **Annee academique :** 2025-2026


---

## Vue d'ensemble

Ce projet traite le jeu de donnees XML [DBLP](https://dblp.org/) en mode **streaming** (SAX/StAX) afin d'eviter de charger en memoire le dump complet (~4 Go).  
Deux taches d'analyse de graphes independantes sont implementees :

| Tache | Type de graphe | Probleme |
|------|-----------------|----------|
| Tache 1 | Graphe de co-signature non oriente | Trouver toutes les composantes connexes |
| Tache 2 | Graphe de collaboration dirige filtre | Trouver toutes les composantes fortement connexes (communautes) |

---

## Fonctionnalites

- ✅ Parseur XML en flux (sans chargement DOM complet)
- ✅ Construction d'un graphe non oriente a partir des co-signatures DBLP
- ✅ Detection des composantes connexes (Tache 1)
- ✅ Construction d'un graphe dirige avec filtres configurables
- ✅ Detection des composantes fortement connexes (Tache 2)
- ✅ Resultats ecrits dans `results/task1/` et `results/task2/`
- ✅ Tests unitaires JUnit 5

---

## 📂 Structure du projet

```text
dblp-graph-community-detection/
├── pom.xml
├── README.md
├── .gitignore
├── LICENSE
│
├── docs/
│   ├── report/
│   └── subject/
│       └── Projet_Algorithmique_2_2026.pdf
│
├── data/
│   ├── README.md            ← comment obtenir le jeu de donnees
│   └── external/            ← placez <dblp.xml.gz> ici (ignore par Git)
│       └── dblp.dtd         ← fichier DTD pour le parseur (fourni)
│
├── results/
│   ├── task1/               ← fichiers de sortie pour la Tache 1
│   └── task2/               ← fichiers de sortie pour la Tache 2
│
└── src/
    ├── main/java/be/ulb/dblp/
    │   ├── Main.java                           ← point d'entree
    │   ├── io/                                 ← utilitaires de lecture/ecriture
    │   ├── model/                              ← classes domaine (Graph, Node, Edge)
    │   ├── parsing/                            ← analyseur XML DBLP SAX/StAX
    │   │    └── DblpPublicationGenerator.java  ← implementation du parseur SAX
    │   │
    │   ├── task1/            ← algorithme et lanceur de la Tache 1
    │   ├── task2/            ← algorithme et lanceur de la Tache 2
    │   ├── util/             ← utilitaires partages (timing, formatage, ...)
    │   └── example/
    │       └── ExampleParser.java  ← exemple d'analyse en flux
    │
    └── test/java/be/ulb/dblp/
```

---

## ⚙️ Algorithmes utilises

| Algorithme | Paquet | Complexite temps | Complexite memoire |
|-----------|---------|------------------|--------------------|
| BFS / DFS (composantes connexes) | `task1` | O(V + E) | O(V) |
| Union-Find (optionnel, Tache 1) | `util` | O(α(V) · E) | O(V) |
| Kosaraju / Tarjan (CFC) | `task2` | O(V + E) | O(V) |

> Les choix exacts et les preuves de complexite sont detailles dans le rapport (`docs/report/`).

---

## 🛠️ Compiler et executer

### Prerequis

- Java 17+
- Maven 3.8+

### Compilation

```bash
mvn clean package
```

Cela genere `target/dblp-community-analysis.jar` (JAR executable).

### Execution

```bash
java -jar target/dblp-community-analysis.jar \
     data/external/dblp.xml.gz \
     data/external/dblp.dtd
```

### Execution via Maven (sans construire le jar au prealable)

```bash
mvn exec:java -Dexec.args="data/external/dblp.xml.gz data/external/dblp.dtd"
```

---

## 💾 Donnees d'entree

| Fichier | Description |
|------|-------------|
| `dblp.xml.gz` | Export XML DBLP complet (compresse gzip, ~4 Go decompresse) |
| `dblp.dtd` | DTD necessaire au parseur SAX |

Telechargez la derniere version depuis <https://dblp.org/xml/> et placez les deux fichiers dans `data/external/`.  
Voir [`data/README.md`](data/README.md) pour les details.

---

## Fichiers de sortie

| Chemin | Contenu |
|------|---------|
| `results/task1/components.txt` | Une composante connexe par ligne (liste d'identifiants auteurs) |
| `results/task1/stats.txt` | Statistiques de synthese (nombre de composantes, tailles, ...) |
| `results/task2/scc.txt` | Une composante fortement connexe par ligne |
| `results/task2/stats.txt` | Statistiques de synthese |

*Exemple de noms de fichiers de sortie (a adapter selon votre implementation).* 

---

## Tests

```bash
mvn test
```

Les tests unitaires sont dans `src/test/java/be/ulb/dblp/`.

---

## Rapport

Le rapport est disponible dans [`docs/report/rapport.pdf`](docs/report/rapport.pdf). Il detaille les choix d'implementation et la complexite des algorithmes utilises.

---

## Notes de complexite

L'approche en flux garantit que l'usage memoire est borne par la taille des structures de graphe, et non par la taille brute du XML.  
L'analyse complete de complexite (temps et memoire) pour chaque algorithme est fournie dans le rapport du projet.
