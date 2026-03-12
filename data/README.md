# Data directory

This directory contains input datasets required to run the project.

## External datasets

Large datasets such as the official DBLP XML dump are **NOT** versioned in Git.  
Place them manually in:

```
data/external/
```

Expected files:

| File | Description | Download |
|------|-------------|---------|
| `dblp.xml.gz` | Full DBLP XML dump (gzip-compressed) | <https://dblp.org/xml/dblp.xml.gz> |
| `dblp.dtd` | DTD file for the XML parser | <https://dblp.org/xml/dblp.dtd> |

> ⚠️ The uncompressed XML file is approximately 4 GB.  
> The parser reads it in **streaming mode** — it is never fully loaded into memory.
> 
> `dblp.dtd` is included because it is required for deterministic offline parsing in the provided project setup and was distributed with the assignment materials.