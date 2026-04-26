# Répertoire des données

Ce dossier contient les jeux de données d'entrée nécessaires pour exécuter le projet.

## Jeux de données externes

Les jeux de données volumineux, comme l'export XML officiel de DBLP, ne sont **PAS** versionnés dans Git.  
Placez-les manuellement dans :

```text
data/external/
```

Fichiers attendus :

| Fichier       | Description                                 | Téléchargement                     |
|---------------|---------------------------------------------|------------------------------------|
| `dblp.xml.gz` | Export XML DBLP complet (compressé en gzip) | <https://dblp.org/xml/dblp.xml.gz> |
| `dblp.dtd`    | Fichier DTD utilisé par le parseur XML      | <https://dblp.org/xml/dblp.dtd>    |

> ⚠️ Le fichier XML décompressé fait environ 4 Go.  
> Le parseur le lit en **mode flux** : il n'est jamais chargé entièrement en mémoire.
>
> `dblp.dtd` est inclus, car il est nécessaire pour une analyse hors ligne déterministe dans la configuration fournie, et il était distribué avec les supports du projet.
