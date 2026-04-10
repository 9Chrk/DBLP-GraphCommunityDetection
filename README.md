# Analyse de communautés DBLP
![Java](https://img.shields.io/badge/Langage-Java-F4C27A?style=flat-square&logo=openjdk&logoColor=3A2E2A)
![Apache Maven](https://img.shields.io/badge/Compilation-Apache%20Maven-E8B4BC?style=flat-square&logo=apache-maven&logoColor=4A1F2B)
![Licence : MIT](https://img.shields.io/badge/Licence-MIT-B7E4C7?style=flat-square&logoColor=1F3D2E)


Un projet Java pour l'analyse en streaming des réseaux de collaboration DBLP, incluant la détection de composantes connexes sur des graphes de co-signature non orientés et l'analyse de communautés fortement connexes sur des graphes de collaboration dirigés filtrés.

> **Cours :** Algorithmique 2 (INFO-F203) — BA2, Q2 — Université Libre de Bruxelles  
> **Année académique :** 2025-2026


---

## Vue d'ensemble

Ce projet traite le jeu de données XML [DBLP](https://dblp.org/) en mode **streaming** (SAX/StAX) afin d'éviter de charger en mémoire l'export complet (~4 Go).  
Deux tâches d'analyse de graphes indépendantes sont implémentées :

| Tâche | Type de graphe | Problème |
|------|-----------------|----------|
| Tâche 1 | Graphe de co-signature non orienté | Trouver toutes les composantes connexes |
| Tâche 2 | Graphe de collaboration dirigé filtré | Trouver toutes les composantes fortement connexes (communautés) |

---

## Fonctionnalités

- ✅ Parseur XML en flux (sans chargement DOM complet)
- ✅ Construction d'un graphe non orienté à partir des co-signatures DBLP
- ✅ Détection des composantes connexes (Tâche 1)
- ✅ Construction d'un graphe dirigé avec filtres configurables
- ✅ Détection des composantes fortement connexes (Tâche 2)
- ✅ Résultats écrits dans `results/task1/` et `results/task2/`
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
│   ├── README.md            ← comment obtenir le jeu de données
│   └── external/            ← placez <dblp.xml.gz> ici (ignoré par Git)
│       └── dblp.dtd         ← fichier DTD pour le parseur (fourni)
│
├── results/
│   ├── task1/               ← fichiers de sortie pour la Tâche 1
│   └── task2/               ← fichiers de sortie pour la Tâche 2
│
└── src/
    ├── main/java/be/ulb/dblp/
    │   ├── Main.java                           ← point d'entrée
    │   ├── io/                                 ← utilitaires de lecture/ecriture
    │   ├── model/                              ← classes du domaine (Graph, Node, Edge)
    │   ├── parsing/                            ← analyseur XML DBLP SAX/StAX
    │   │    └── DblpPublicationGenerator.java  ← implémentation du parseur SAX
    │   │
    │   ├── task1/            ← algorithme et lanceur de la Tâche 1
    │   ├── task2/            ← algorithme et lanceur de la Tâche 2
    │   ├── util/             ← utilitaires partagés (timing, formatage, ...)
    │   └── example/
    │       └── ExampleParser.java  ← exemple d'analyse en flux
    │
    └── test/java/be/ulb/dblp/
```

---

## ⚙️ Algorithmes utilisés

| Algorithme | Paquet | Complexité temporelle | Complexité mémoire |
|-----------|---------|------------------|--------------------|
| BFS / DFS (composantes connexes) | `task1` | O(V + E) | O(V) |
| Union-Find (optionnel, Tâche 1) | `util` | O(α(V) · E) | O(V) |
| Kosaraju / Tarjan (CFC) | `task2` | O(V + E) | O(V) |

> Les choix exacts et les preuves de complexité sont détaillés dans le rapport (`docs/report/`).

---

## 🛠️ Compiler et exécuter

### Prérequis

- Java 17+
- Maven 3.8+

### Compilation

```bash
mvn clean package
```

Cela génère `target/dblp-community-analysis.jar` (JAR exécutable).

### Exécution

```bash
java -jar target/dblp-community-analysis.jar \
     data/external/dblp.xml.gz \
     data/external/dblp.dtd
```

### Exécution via Maven (sans construire le JAR au préalable)

```bash
mvn exec:java -Dexec.args="data/external/dblp.xml.gz data/external/dblp.dtd"
```

---

## 💾 Données d'entrée

| Fichier | Description |
|------|-------------|
| `dblp.xml.gz` | Export XML DBLP complet (compressé gzip, ~4 Go décompressé) |
| `dblp.dtd` | DTD nécessaire au parseur SAX |

Téléchargez la dernière version depuis <https://dblp.org/xml/> et placez les deux fichiers dans `data/external/`.  
Voir [`data/README.md`](data/README.md) pour les détails.

---

## Fichiers de sortie

| Chemin | Contenu |
|------|---------|
| `results/task1/components.txt` | Une composante connexe par ligne (liste d'identifiants auteurs) |
| `results/task1/stats.txt` | Statistiques de synthèse (nombre de composantes, tailles, ...) |
| `results/task2/scc.txt` | Une composante fortement connexe par ligne |
| `results/task2/stats.txt` | Statistiques de synthèse |

*Exemple de noms de fichiers de sortie (à adapter selon votre implémentation).* 

---

## Tests

```bash
mvn test
```

Les tests unitaires sont dans `src/test/java/be/ulb/dblp/`.

---

## Rapport

Le rapport est disponible dans [`docs/report/rapport.pdf`](docs/report/rapport.pdf). Il détaille les choix d'implémentation et la complexité des algorithmes utilisés.

---

## Notes de complexité

L'approche en flux garantit que l'usage mémoire est borné par la taille des structures de graphe, et non par la taille brute du XML.  
L'analyse complète de complexité (temps et mémoire) pour chaque algorithme est fournie dans le rapport du projet.
