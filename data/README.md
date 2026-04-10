# Repertoire des donnees

Ce dossier contient les jeux de donnees d'entree necessaires pour executer le projet.

## Jeux de donnees externes

Les jeux de donnees volumineux, comme l'export XML officiel de DBLP, ne sont **PAS** versionnes dans Git.  
Placez-les manuellement dans :

```text
data/external/
```

Fichiers attendus :

| Fichier | Description | Telechargement |
|------|-------------|---------|
| `dblp.xml.gz` | Export XML DBLP complet (compresse en gzip) | <https://dblp.org/xml/dblp.xml.gz> |
| `dblp.dtd` | Fichier DTD utilise par le parseur XML | <https://dblp.org/xml/dblp.dtd> |

> ⚠️ Le fichier XML decompresse fait environ 4 Go.  
> Le parseur le lit en **mode flux** : il n'est jamais charge entierement en memoire.
>
> `dblp.dtd` est inclus car il est necessaire pour une analyse hors ligne deterministe dans la configuration fournie, et il etait distribue avec les supports du projet.
