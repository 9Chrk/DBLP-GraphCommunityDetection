# DBLP Graph Community Detection

![Java](https://img.shields.io/badge/Langage-Java-F4C27A?style=flat-square&logo=openjdk&logoColor=3A2E2A)
![Apache Maven](https://img.shields.io/badge/Compilation-Apache%20Maven-E8B4BC?style=flat-square&logo=apache-maven&logoColor=4A1F2B)
![Licence : MIT](https://img.shields.io/badge/Licence-MIT-B7E4C7?style=flat-square&logoColor=1F3D2E)

DBLP Graph Community Detection est un **outil d’analyse des communautés d’auteurs scientifiques**, développé en **Java**. Il lit les publications de la base DBLP et construit des graphes pour repérer les groupes d’auteurs reliés par leurs publications.

Le fichier XML est traité publication par publication pour éviter de charger l’ensemble du document en mémoire. Deux analyses sont proposées : les composantes connexes du graphe de co-publication et les composantes fortement connexes d’un graphe orienté filtré.

> Projet académique ULB — INFO-F203.
> Algorithmique 2 · 2025–2026

---

## 📖 Sommaire

- [Vue d’ensemble](#vue-densemble)
- [Fonctionnalités](#fonctionnalites)
- [Prérequis](#prerequis)
- [Compilation et lancement](#compilation-et-lancement)
- [Données d’entrée](#donnees-dentree)
- [Résultats](#resultats)
- [Algorithmes utilisés](#algorithmes-utilises)
- [Structure du projet](#structure-du-projet)
- [Tests](#tests)
- [Notes de complexité](#notes-de-complexite)
- [Documentation](#documentation)

---

<a id="vue-densemble"></a>

## 🔎 Vue d’ensemble

Ce projet traite le fichier XML [DBLP](https://dblp.org/) en **traitement en flux (online)** afin d'éviter de charger en mémoire le snapshot complet (~4 Go).  
Deux tâches d'analyse de graphes indépendantes sont implémentées :

| Tâche | Type de graphe | Problème |
|------|-----------------|----------|
| Tâche 1 | Graphe non orienté de co-publication | Maintenir les communautés en ligne (composantes connexes) |
| Tâche 2 | Graphe orienté filtré (seuil >= 6) | Identifier les communautés fortement connexes |

---

<a id="fonctionnalites"></a>

## ✨ Fonctionnalités

- Traitement en flux (online) publication par publication
- Construction d'un graphe non orienté de co-publication
- Maintien des communautés (composantes connexes) pour la Tâche 1
- Comptage en ligne des paires ordonnées A -> B pour la Tâche 2
- Analyse des communautés dans le graphe orienté filtré
- Résultats écrits dans `results/task1/` et `results/task2/`
- Tests unitaires JUnit 5

---

<a id="prerequis"></a>

## 🧰 Prérequis

- Java 17+
- Maven 3.8+
- Python 3 + `pip` (pour les histogrammes)

---

<a id="compilation-et-lancement"></a>

## ▶️ Compilation et lancement

### Compilation

```powershell
mvn clean package
```

Cela génère `target/dblp-community-analysis.jar` (JAR exécutable).

Si vous voulez lancer le projet exactement avec `java -jar dblp-community-analysis.jar ...`, copiez d’abord le JAR à la racine du dépôt :

```powershell
Copy-Item target\dblp-community-analysis.jar .\dblp-community-analysis.jar
```

### Exécution (Tâche 1 par défaut)

```powershell
java -jar target\dblp-community-analysis.jar data\external\dblp.xml.gz data\external\dblp.dtd
```

### Exécution de la Tâche 2

```powershell
java -jar target\dblp-community-analysis.jar data\external\dblp.xml.gz data\external\dblp.dtd --task=2
```

Vous pouvez aussi préciser un dossier de sortie :

```powershell
java -jar target\dblp-community-analysis.jar data\external\dblp.xml.gz data\external\dblp.dtd --task=2 --outputDir=results/task2
```

### Exécution via Maven (optionnelle)

```powershell
mvn --% exec:java -Dexec.args="data/external/dblp.xml.gz data/external/dblp.dtd --task=2"
```

### Visualisation des histogrammes (script Python)

Après avoir généré les fichiers CSV (`results/task1/community_size_histogram.csv` et/ou `results/task2/task2_community_sizes.csv`), installez d'abord les dépendances Python :

```powershell
pip install -r requirements.txt
```

Puis exécutez le script :

```powershell
python scripts/plot_histograms.py
```

Les figures PNG sont écrites dans `results/task1/` et `results/task2/`.

---

<a id="donnees-dentree"></a>

## 🗃️ Données d’entrée

| Fichier | Description |
|------|-------------|
| `data/external/dblp.xml.gz` | Snapshot XML DBLP (compressé gzip, ~4 Go décompressé) |
| `data/external/dblp.dtd` | DTD nécessaire au parseur SAX |

Téléchargez la dernière version depuis <https://dblp.org/xml/> et placez les deux fichiers dans `data/external/`.  
Voir [`data/README.md`](data/README.md) pour les détails.

---

<a id="resultats"></a>

## 📊 Résultats

| Chemin | Contenu |
|------|---------|
| `results/task1/community_size_histogram.csv` | Histogramme des tailles des composantes (Tâche 1) |
| `results/task2/task2_community_sizes.csv` | Histogramme des tailles des composantes fortement connexes (`community_size,count`) |
| Terminal (stdout) | Top 10 des plus grandes composantes avec leur taille, leur diamètre et les auteurs |

---

<a id="algorithmes-utilises"></a>

## ⚙️ Algorithmes utilisés

| Algorithme | Paquet | Complexité temporelle | Complexité mémoire |
|-----------|---------|------------------|--------------------|
| BFS / DFS (composantes connexes) | `task1` | O(V + E) | O(V) |
| Union-Find (optionnel, Tâche 1) | `util` | O(α(V) · E) | O(V) |
| Kosaraju / Tarjan (CFC) | `task2` | O(V + E) | O(V) |

> Les choix exacts et les preuves de complexité sont détaillés dans le rapport (`docs/report/`).

---

<a id="structure-du-projet"></a>

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
├── scripts/
│   └── plot_histograms.py   ← script Python pour visualiser les histogrammes de taille de communauté
│
└── src/
    ├── main/java/be/ulb/dblp/
    │   ├── Main.java                           ← point d'entrée
    │   ├── parsing/                            ← analyseur XML DBLP SAX/StAX
    │   │    └── DblpPublicationGenerator.java  ← implémentation du parseur SAX
    │   │
    │   ├── task1/            ← algorithme et lanceur de la Tâche 1
    │   ├── task2/            ← algorithme et lanceur de la Tâche 2
    │   └── example/
    │       └── ExampleParser.java  ← exemple d'analyse en flux
    │
    └── test/java/be/ulb/dblp/
```

---

<a id="tests"></a>

## 🧪 Tests

```bash
mvn test
```

Les tests unitaires sont dans `src/test/java/be/ulb/dblp/`.

---

<a id="notes-de-complexite"></a>

## 🧮 Notes de complexité

L'approche en flux garantit que l'usage mémoire est borné par la taille des structures de graphe, et non par la taille brute du XML.  
L'analyse complète de complexité (temps et mémoire) pour chaque algorithme est fournie dans le rapport du projet.

---

<a id="documentation"></a>

## 📄 Documentation

Le rapport est disponible dans [`docs/report/rapport.pdf`](docs/report/rapport.pdf). Il détaille les choix d'implémentation et la complexité des algorithmes utilisés.
